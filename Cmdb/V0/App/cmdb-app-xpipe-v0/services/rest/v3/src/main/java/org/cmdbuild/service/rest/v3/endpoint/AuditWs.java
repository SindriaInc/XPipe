package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.time.ZonedDateTime;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestInfo;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.audit.RequestTrackingRepository;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.encode.CmPackUtils.packOrNull;
import org.cmdbuild.fault.FaultEvent;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

@Path("system/audit/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
public class AuditWs {

    private final RequestTrackingRepository store;

    public AuditWs(RequestTrackingRepository store) {
        this.store = checkNotNull(store);
    }

    @GET
    @Path("mark")
    public Object mark() {
        String mark = String.valueOf(now().toInstant().toEpochMilli());
        return map("success", true, "data", map("mark", mark));
    }

    @GET
    @Path("requests")
    public Object getRequests(@QueryParam("since") @Nullable String mark, @QueryParam("limit") @Nullable Long limit) {
        List<RequestInfo> requests;
        if (isNotBlank(mark)) {
            ZonedDateTime dateTime = toDateTime(Long.valueOf(mark));
            requests = store.getRequestsSince(dateTime);
        } else {
            requests = store.getLastRequests(firstNonNull(limit, 10l));
        }
        return response(requests.stream().map(this::serializeRequestInfo).collect(toList()));
    }

    @GET
    @Path("errors")
    public Object getErrors(@QueryParam("since") @Nullable String mark, @QueryParam("limit") @Nullable Long limit) {
        List<RequestInfo> requests;
        if (isNotBlank(mark)) {
            ZonedDateTime dateTime = toDateTime(Long.valueOf(mark));
            requests = store.getErrorsSince(dateTime);
        } else {
            requests = store.getLastErrors(firstNonNull(limit, 10l));
        }
        return response(requests.stream().map(this::serializeRequestInfo).collect(toList()));
    }

    @GET
    @Path("requests/{id}")
    public Object getRequest(@PathParam("id") String id) {
        RequestData requestData = store.getRequest(id);
        return response(serializeRequestInfo(requestData).with("client", requestData.getClient(),
                "payloadText", requestData.getPayloadText(),
                "payloadBytes", packOrNull(requestData.getPayloadBytes()),
                "payloadContentType", requestData.getPayloadContentType(),
                "payloadSize", requestData.getPayloadSize(),
                "responseText", requestData.getResponseText(),
                "responseBytes", packOrNull(requestData.getResponseBytes()),
                "responseContentType", requestData.getResponseContentType(),
                "responseSize", requestData.getResponseSize(),
                "completed", requestData.isCompleted(),
                "userAgent", requestData.getUserAgent(),
                "errors", serializeErrors(requestData.getFaultEvents()),
                "logs", nullToEmpty(requestData.getLogs()),
                "tcpDump", packOrNull(requestData.getTcpDumpBytes()),
                "requestHeaders", requestData.getRequestHeaders(),
                "responseHeaders", requestData.getResponseHeaders()));

    }

    public static Object serializeErrors(List<FaultEvent> data) {
        return data.stream().map((e) -> map("level", serializeEnum(e.getLevel()).toUpperCase(), "message", e.getMessage(), "exception", e.getStacktrace())).collect(toList());
    }

    private FluentMap<String, Object> serializeRequestInfo(RequestInfo record) {
        return map(
                "user", record.getUser(),
                "nodeId", record.getNodeId(),
                "sessionId", record.getSessionId(),
                "requestId", record.getRequestId(),
                "actionId", record.getActionId(),
                "path", record.getPath(),
                "method", record.getMethod(),
                "isSoap", record.isSoap(),
                "elapsed", record.getElapsedTimeMillis(),
                "soapActionOrMethod", record.isSoap() ? record.getSoapActionOrMethod() : null,
                "query", record.getQuery(),
                "status", record.getStatusCode(),
                "timestamp", toIsoDateTime(toDateTime(record.getTimestamp())));
    }

}
