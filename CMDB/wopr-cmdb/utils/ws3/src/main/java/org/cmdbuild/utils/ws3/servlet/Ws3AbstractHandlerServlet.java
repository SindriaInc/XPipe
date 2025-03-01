/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.servlet;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.activation.DataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.function.Function.identity;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaFileCleaner;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletRequestContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmMultipartUtils.isPlaintext;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.utils.ws3.api.Ws3ResourceRepository;
import org.cmdbuild.utils.ws3.api.Ws3WarningSource;
import org.cmdbuild.utils.ws3.inner.Ws3Part;
import org.cmdbuild.utils.ws3.inner.Ws3RequestHandler;
import org.cmdbuild.utils.ws3.inner.Ws3RequestHandlerImpl;
import org.cmdbuild.utils.ws3.inner.Ws3ResponseHandler;
import org.cmdbuild.utils.ws3.inner.Ws3ResponsePrinter;
import org.cmdbuild.utils.ws3.inner.Ws3RestRequest;
import org.cmdbuild.utils.ws3.inner.Ws3RestRequestImpl;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class Ws3AbstractHandlerServlet extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Ws3RequestHandler handler;
    private ExceptionMapper exceptionHandler;

    private DiskFileItemFactory diskFileItemFactory;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            WebApplicationContext applicationContext = checkNotNull(WebApplicationContextUtils.getWebApplicationContext(getServletContext()), "application context not available");
            Ws3ResourceRepository repository = applicationContext.getBean(Ws3ResourceRepository.class);
            exceptionHandler = applicationContext.getBean(ExceptionMapper.class);//TODO improve this
            Ws3WarningSource warningSource = applicationContext.getBean(Ws3WarningSource.class);//TODO improve this
            handler = new Ws3RequestHandlerImpl(repository, warningSource);

            diskFileItemFactory = DiskFileItemFactory.builder()
                    .setFileCleaningTracker(JakartaFileCleaner.getFileCleaningTracker(getServletContext()))
                    .get();
        } catch (Exception ex) {
            logger.error("error starting ws3 rest servlet", ex);
            throw ex;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Ws3ResponseHandler responseHelper;
        Ws3ResponsePrinter responsePrinter;
        try {
            responseHelper = handleRequest(request, response);
            responsePrinter = responseHelper.prepareResponse();
        } catch (Exception ex) {
            logger.debug("error processing ws3 rest request", ex);
            handleError(request, response, ex);
            return;
        }
        try {
            responsePrinter.printResponse(response);
        } catch (IOException ex) {
            logger.warn("write error printing ws3 rest response: {}", ex.toString());
            logger.debug("write error printing ws3 rest response", ex);
        } catch (Exception ex) {
            logger.error("error printing ws3 rest response", ex);
        }
    }

    protected JakartaServletFileUpload getMultipartHelper() {
        return new JakartaServletFileUpload(checkNotNull(diskFileItemFactory, "file upload handler not ready"));
    }

    protected abstract Ws3ResponseHandler handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    protected Ws3RestRequest buildWs3RestRequest(HttpServletRequest request, String resourceUri) throws IOException {
        try {
            Map<String, String> headers = list(request.getHeaderNames()).collect(toMap(identity(), request::getHeader));

            Map<String, List<String>> params = map(request.getParameterMap()).mapValues(v -> asList(v));

            String payload;
            Map<String, Ws3Part> parts = map();
            if (isPlaintext(request.getContentType())) {
                payload = IOUtils.toString(request.getReader());
                parts.put(Ws3Part.DEFAULT_PART, new Ws3Part(newDataSource(payload, request.getContentType()), Ws3Part.DEFAULT_PART, emptyMap()));
            } else {
                if (JakartaServletFileUpload.isMultipartContent(new JakartaServletRequestContext(request))) {
                    logger.trace("processing multipart request");
                    AtomicInteger i = new AtomicInteger(0);
                    List<FileItem> items = getMultipartHelper().parseRequest(request);
                    items.forEach(part -> {
                        DataSource data = newDataSource(part::getInputStream, part.getContentType(), part.getName());
                        String partName = part.getFieldName();
                        if (isBlank(partName)) {
                            partName = format("part_%s", i.getAndIncrement());
                        }
                        logger.trace("processing multipart element =< {} > ( {} {} )", partName, FileUtils.byteCountToDisplaySize(part.getSize()), part.getContentType());
                        Map<String, String> partHeaders = list(part.getHeaders().getHeaderNames()).collect(toMap(identity(), h -> part.getHeaders().getHeader(h)));
                        parts.put(partName, new Ws3Part(data, partName, partHeaders));
                    });
                    payload = parts.values().stream().filter(p -> isPlaintext(p.getDataSource().getContentType())).findFirst().map(p -> readToString(p.getDataSource())).orElse(null);
                } else {
                    ServletInputStream in = request.getInputStream();
                    DataSource dataSource;
                    if (in == null) {
                        dataSource = newDataSource(in, request.getContentType());
                    } else {
                        dataSource = newDataSource(new byte[]{}, request.getContentType());
                    }
                    parts.put(Ws3Part.DEFAULT_PART, new Ws3Part(dataSource, Ws3Part.DEFAULT_PART, headers));
                    payload = null;
                }
            }

            return new Ws3RestRequestImpl(request, resourceUri, params, parts, headers, payload);

        } catch (FileUploadException ex) {
            throw new Ws3Exception(ex);
        }
    }

    protected Ws3RequestHandler getHandler() {
        return checkNotNull(handler, "handler not ready");
    }

    protected void handleError(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        try {
            if (exceptionHandler != null) {
                Response resp = exceptionHandler.toResponse(ex);
                checkArgument(isContentType(resp.getMediaType().toString(), "application/json"));
                response.setContentType("application/json");
                response.setCharacterEncoding(UTF_8.name());
                response.setStatus(resp.getStatus());
                String payload = toJson(resp.getEntity());
                response.getWriter().write(payload);
            } else {
                response.setStatus(500);
                //TODO
            }
        } catch (Exception exx) {
            logger.error("error handling ws3 error", exx);
        }
    }
}
