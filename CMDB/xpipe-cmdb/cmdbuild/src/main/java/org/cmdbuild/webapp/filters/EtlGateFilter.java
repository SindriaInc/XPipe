/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.IOException;
import static java.lang.String.format;
import static java.util.Collections.list;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import javax.activation.DataSource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.gate.EtlGateService;
import org.cmdbuild.etl.gate.inner.EtlGate;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.hasContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.cmdbuild.utils.ws3.api.Ws3WarningSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import static org.cmdbuild.auth.utils.AuthUtils.checkAuthorized;
import static org.cmdbuild.etl.gate.EtlGateService.ETLGATE_REQUEST_METHOD;
import static org.cmdbuild.etl.gate.EtlGateService.ETLGATE_REQUEST_PATH;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_COMPLETED;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.getOutputDataFromMessage;

@Configuration
public class EtlGateFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EtlGateService gateService;
    @Autowired
    private Ws3WarningSource warningHandlerService;
    @Autowired
    private ExceptionMapper<Exception> exceptionHandlerService;
    @Autowired
    private WaterwayService waterwayService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        new Helper((HttpServletRequest) request, (HttpServletResponse) response).doInner();
    }

    private class Helper {

        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private DataSource output;
        private int statusCode;

        private Helper(HttpServletRequest request, HttpServletResponse response) {
            this.request = checkNotNull(request);
            this.response = checkNotNull(response);
        }

        private void doInner() {
            logger.debug("etl gate filter doFilter BEGIN");
            String requestUrl = "<undefined>";
            try {

                requestUrl = request.getRequestURI().substring(request.getContextPath().length());

                String contentType = request.getContentType();
                byte[] data = toByteArray(request.getInputStream());

                String context, gateCode, path;
                Matcher matcher = Pattern.compile("/?services/etl/gate/(public|private)/([^/]+)/?(.*)").matcher(requestUrl);
                checkArgument(matcher.matches(), "invalid request path syntax for etl gate =< %s >", requestUrl);
                context = checkNotBlank(matcher.group(1));
                gateCode = checkNotBlank(matcher.group(2));
                path = nullToEmpty(matcher.group(3));

                EtlGate gate = gateService.getByCodeOrNull(gateCode);

                if (gate == null) {
                    logger.error("gate not found for code =< {} >", gateCode);
                    statusCode = SC_NOT_FOUND;
                    return;
                }

                switch (context) {
                    case "public" ->
                        checkAuthorized(gate.getAllowPublicAccess(), "access denied for gate =< %s >", gate.getCode());
                    case "private" ->
                        checkAuthorized(gateService.currentUserCanAccess(gate), "access denied for gate =< %s >", gate.getCode());
                    default ->
                        throw new EtlException("invalid etl gate context =< %s >", context);
                }

                if (!gate.isEnabled()) {
                    logger.error("gate is not enabled = {}", gate);
                    statusCode = SC_NOT_FOUND;
                    return;
                }

                DataSource dataSource = newDataSource(data, contentType);
//                dataSource = handleMultipart(dataSource);

//                JobRun job = gateService.receive(gate.getCode(), dataSource, mapOf(String.class, String.class).with(
//                        ETLGATE_REQUEST_METHOD, request.getMethod(),
//                        ETLGATE_REQUEST_PATH, path
//                ).accept(m -> {
//                    list(request.getHeaderNames()).forEach(n -> m.put(format("header_%s", n), request.getHeader((String) n)));
//                    request.getParameterMap().forEach((k, v) -> m.put(format("param_%s", k), v.length == 1 ? v[0] : toStringNotBlank(v)));
//                }));
//                JobRun job =
                WaterwayMessage message = waterwayService.submitRequest(gate.getCode(), dataSource, mapOf(String.class, String.class).with(
                        ETLGATE_REQUEST_METHOD, request.getMethod(),
                        ETLGATE_REQUEST_PATH, path
                ).accept(m -> {
                    list(request.getHeaderNames()).forEach(n -> m.put(format("header_%s", n), request.getHeader((String) n)));
                    request.getParameterMap().forEach((k, v) -> m.put(format("param_%s", k), v.length == 1 ? v[0] : toStringNotBlank(v)));
                }));
                //TODO reload message, check for output ??
                if (message.hasStatus(WMS_COMPLETED)) {
//                    output = job.getOutputData();
                    output = getOutputDataFromMessage(message);
                }
                handleOutput();
                statusCode = SC_OK;
            } catch (Exception ex) {
                logger.error("error in etl gate filter while processing request url =< {} >", requestUrl, ex);
                Response r = exceptionHandlerService.toResponse(ex);
                statusCode = r.getStatus();
                output = newDataSource(toJson(r.getEntity()), "application/json");
            } finally {
                logger.debug("etl gate filter doFilter END");
                printResponse();
            }
        }
//
//        private DataSource handleMultipart(DataSource dataSource) throws MessagingException {//TODO improve this, handle multipart datasource
//            if (isMultipart(dataSource)) {
//                MimeMultipart mimeMultipart = new MimeMultipart(dataSource);
//                if (mimeMultipart.getCount() == 1) {
//                    dataSource = handleMultipart(toDataSource(mimeMultipart.getBodyPart(0).getDataHandler()));
//                }
//            }
//            return dataSource;
//        }

        private void printResponse() {
            try {
                response.setStatus(statusCode);
                if (output != null) {
                    response.setContentType(getContentType(output));//TODO duplicate code, use same response handling code as other ws 
                    copy(output.getInputStream(), response.getOutputStream());
                }
            } catch (Exception ex) {
                logger.warn("error writing response", ex);
            }
        }

        private void handleOutput() {
            if (output == null) {
                output = newDataSource("{\"success\":true}", "application/json");
            }
            try {
                if (hasContentType(output, "application/json")) {
                    JsonNode json = fromJson(readToString(output), JsonNode.class);
                    if (!json.has("success")) {
                        json = fromJson(toJson(map("success", true, "data", json)), JsonNode.class);
                    }
                    List<Object> warningJsonMessages = warningHandlerService.getWarningJsonMessages();
                    if (!warningJsonMessages.isEmpty()) {
                        ((ObjectNode) json).withArray("messages").addAll(warningJsonMessages.stream().map(o -> fromJson(toJson(o), JsonNode.class)).collect(toList()));
                    }
                    output = newDataSource(toJson(json), "application/json");
                }
            } catch (Exception ex) {
                logger.warn("error processing messages in json response", ex);
            }
        }
    }

}
