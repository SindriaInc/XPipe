/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.asyncjob;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.transformValues;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.String.format;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_HEADER;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class AsyncJobServiceImpl implements AsyncRequestJobService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executorService = Executors.newCachedThreadPool(namedThreadFactory(getClass()));
    private final Map<Long, AsyncRequestJob> jobs = new ConcurrentHashMap<>();

    private final SessionService sessionService;
    private final RequestContextService requestContextService;

    public AsyncJobServiceImpl(SessionService sessionService, RequestContextService requestContextService) {
        this.sessionService = checkNotNull(sessionService);
        this.requestContextService = checkNotNull(requestContextService);
    }

    @Override
    public AsyncRequestJob createAsyncRequest(String requestPath, HttpServletRequest request, HttpServletResponse response) {
        try {
            String method = request.getMethod();
            logger.debug("starting async request job for method = {} path =< {} >", method, requestPath);

            byte[] payload = toByteArray(request.getInputStream());

            String contentType = request.getContentType(),
                    baseUrl = request.getRequestURL().toString();

            Set<String> headersToCopyFromOriginalRequest = list("TODO").stream().map(String::toLowerCase).collect(toSet());//TODO

            Map<String, String> params = map(transformValues(filterKeys(request.getParameterMap(), k -> !ASYNC_JOB_REQUEST_PARAM.equalsIgnoreCase(k)), v -> getOnlyElement(list(v)))),
                    headers = Collections.list(request.getHeaderNames()).stream().filter(k -> headersToCopyFromOriginalRequest.contains(k.toLowerCase())).collect(toMap(identity(), k -> request.getHeader(k)));

            String sessionId = sessionService.getCurrentSessionId();
            headers.put(CMDBUILD_AUTHORIZATION_HEADER, sessionId);

            AsyncRequestJob job = new AsyncRequestJobImpl(abs((long) new Random().nextInt()));
            jobs.put(job.getId(), job);

            executorService.submit(() -> {
                try {

                    //TODO init request thread context, etc
                    MDC.put("cm_type", "req_async");//TODO improve this
                    MDC.put("cm_id", format("req_async:%s", job.getId()));//TODO improve this, add original request id (?)
                    requestContextService.initCurrentRequestContext(format("req_async_%s", job.getId()));//TODO improve this, add original request id (?)

                    logger.debug("execute async request job = {}", job);

                    HttpClient httpClient = HttpClients.custom()
                            .setDefaultHeaders(headers.entrySet().stream().map(e -> new BasicHeader(e.getKey(), e.getValue())).collect(toList()))
                            .build();

                    URIBuilder builder = new URIBuilder(baseUrl);
                    params.forEach(builder::addParameter);
                    String requestUrl = builder.build().toString();

                    HttpResponse clientResponse;
                    HttpUriRequest clientRequest;

                    logger.debug("request url = {}", requestUrl);
                    logger.debug("request params = \n\n{}\n", mapToLoggableStringLazy(params));
                    logger.debug("request headers = \n\n{}\n", mapToLoggableStringLazy(headers));

                    switch (method.toLowerCase()) {
                        case "get":
                            clientRequest = new HttpGet(requestUrl);
                            break;
                        case "delete":
                            clientRequest = new HttpDelete(requestUrl);
                            break;
                        case "post":
                            clientRequest = new HttpPost(requestUrl);
                            ((HttpPost) clientRequest).setEntity(new ByteArrayEntity(payload, ContentType.create(contentType)));//TODO charset (?)
                            break;
                        case "put":
                            clientRequest = new HttpPut(requestUrl);
                            ((HttpPut) clientRequest).setEntity(new ByteArrayEntity(payload, ContentType.create(contentType)));//TODO charset (?)
                            break;
                        default:
                            throw unsupported("unsupported method = %s", method);
                    }
                    clientResponse = httpClient.execute(clientRequest);

                    int statusCode = clientResponse.getStatusLine().getStatusCode();
                    byte[] responseContent = clientResponse.getEntity() == null ? new byte[]{} : toByteArray(clientResponse.getEntity().getContent());
                    Map<String, String> responseHeaders = list(clientResponse.getAllHeaders()).stream().collect(toMap(Header::getName, Header::getValue));

                    AsyncRequestJob completedJob = new AsyncRequestJobImpl(job, responseHeaders, responseContent, statusCode);
                    logger.debug("completed job = {} with response status code = {}", completedJob, clientResponse.getStatusLine());
                    logger.debug("response content = \n\n{}\n", new String(responseContent));//TODO improve this
                    jobs.put(completedJob.getId(), completedJob);
                } catch (Exception ex) {
                    logger.error("failed job = {}", job, ex);
                    AsyncRequestJob failedJob = new AsyncRequestJobImpl(job, emptyMap(), toJson(map("success", false, "message", ex.toString())).getBytes(), 500);//TODO improve error response
                    jobs.put(failedJob.getId(), failedJob);
                }
            });

            return job;
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    @Override
    public AsyncRequestJob getJobForCurrentUserById(long jobId) {
        AsyncRequestJob job = checkNotNull(jobs.get(jobId), "job not found for id = %s", jobId);
        //TODO validate job user
        return job;
    }

    @Override
    public void deleteJob(long jobId) {
        jobs.remove(jobId);
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executorService);
    }

    private static class AsyncRequestJobImpl implements AsyncRequestJob {

        private final long id;

        private final boolean completed;

        private final Integer statusCode;
        private final Map<String, String> responseHeaders;
        private final byte[] responseContent;

        public AsyncRequestJobImpl(Long id) {
            this.id = id;
            this.completed = false;
            this.responseContent = null;
            this.responseHeaders = emptyMap();
            this.statusCode = null;
        }

        public AsyncRequestJobImpl(AsyncRequestJob copyOf, Map<String, String> responseHeaders, byte[] responseContent, int statusCode) {
            this.id = copyOf.getId();
            this.completed = true;
            this.responseHeaders = map(responseHeaders).immutable();
            this.responseContent = checkNotNull(responseContent);
            this.statusCode = statusCode;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public boolean isCompleted() {
            return completed;
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public Map<String, String> getResponseHeaders() {
            return responseHeaders;
        }

        @Override
        public byte[] getResponseContent() {
            return checkNotNull(responseContent);
        }

        @Override
        public String toString() {
            return "AsyncRequestJob{" + "id=" + id + '}';
        }

    }

}
