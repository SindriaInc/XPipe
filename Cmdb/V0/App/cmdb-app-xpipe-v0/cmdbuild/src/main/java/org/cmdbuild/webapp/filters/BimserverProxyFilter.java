/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.cmdbuild.config.UiConfigurationImpl;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.webapp.utils.ProxyUtils.proxyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.cmdbuild.config.BimConfiguration;

@Configuration
public class BimserverProxyFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BimConfiguration configuration;
    @Autowired
    private UiConfigurationImpl config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("bimserver proxy filter doFilter BEGIN");
        try {
            checkArgument(configuration.isBimserverEnabled(), "bimserver is not enabled");
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            String requestPath = httpServletRequest.getRequestURI().replaceFirst(Pattern.quote(((HttpServletRequest) request).getContextPath()) + "/services/bimserver/?", "");
            logger.debug("bimserver proxy filter, processing request = {}", requestPath);
            switch (requestPath) {
                case "stream":
                    logger.debug("forward websocket stream request");
                    filterChain.doFilter(request, response);
                    break;
//                case "x.getbimserveraddress":
//                    String baseUrl = config.getBaseUrl();
//                    if (equal(baseUrl, AUTO_URL)) {
//                        String pattern = "^(.*)/services/bimserver/.*";
//                        Matcher matcher = Pattern.compile(pattern).matcher(httpServletRequest.getRequestURL().toString());
//                        checkArgument(matcher.find(), "resource request url = %s does not match this filter url pattern = %s", httpServletRequest.getRequestURL(), pattern);
//                        baseUrl = checkNotNull(trimToNull(matcher.group(1)), "request url parsing failed, using pattern = '%s'", pattern);
//                    }
//                    httpServletResponse.setContentType("application/json");
//                    httpServletResponse.getWriter().write(toJson(map("address", baseUrl + "/services/bimserver")));
//                    break;
                default:
                    String requestUrl = checkNotBlank(configuration.getUrl(), "bimserver url not configured").replaceFirst("/$", "") + "/" + requestPath;
                    if (isNotBlank(httpServletRequest.getQueryString())) {
                        requestUrl += "?" + httpServletRequest.getQueryString();
                    }
                    HttpUriRequest httpRequest = buildRequest(requestUrl, httpServletRequest);
                    logger.debug("forward call to bimserver url = {}", requestUrl);
                    proxyRequest(httpRequest, httpServletResponse);
                    break;
            }

            logger.debug("bimserver proxy filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in bimserver proxy filter", ex);
            throw runtime(ex, "error processing bimserver proxy filter");//TODO return properly formatted json response
        }

    }

    private HttpUriRequest buildRequest(String requestUrl, HttpServletRequest httpServletRequest) throws IOException {//TODO some duplicate code, merge with proxy utils
        String method = httpServletRequest.getMethod();
        switch (method.toUpperCase()) {
            case HttpGet.METHOD_NAME:
                return new HttpGet(requestUrl);
            case HttpPost.METHOD_NAME:
                HttpPost httpPost = new HttpPost(requestUrl);
                httpPost.setHeader("Content-Type", httpServletRequest.getContentType());
                if (nullToEmpty(httpServletRequest.getContentType()).toLowerCase().contains("application/x-www-form-urlencoded")) {
                    httpPost.setEntity(new StringEntity(getOnlyElement(httpServletRequest.getParameterMap().keySet())));//TODO improve this; thiw works only for single-entry form post without value, a degenerate case used by bimserver
                } else if (nullToEmpty(httpServletRequest.getContentType()).toLowerCase().contains("application/json")) {
                    String payload = readToString(httpServletRequest.getInputStream());
                    try {
                        if (payload.contains("org.bimserver.AuthInterface")) {
                            JsonNode requestNode = fromJson(payload, JsonNode.class).get("request");
                            if (requestNode.get("interface").asText().equals("org.bimserver.AuthInterface") && requestNode.get("method").asText().equals("login")) {
                                payload = toJson(map("request", map("interface", "org.bimserver.AuthInterface", "method", "login", "parameters", map("username", configuration.getUsername(), "password", configuration.getPassword()))));
                            }
                        }
                    } catch (Exception ex) {
                        logger.error("error processing bim proxied payload = {}", abbreviate(payload), ex);
                    }
                    httpPost.setEntity(new StringEntity(payload));
                } else {
                    httpPost.setEntity(new ByteArrayEntity(toByteArray(httpServletRequest.getInputStream())));
                }
                return httpPost;
            default:
                throw unsupported("unsupported method = %s", method);
        }
    }

}
