package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_VIEW_AUTHORITY;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.buildMessageReference;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.serializeHistoryRecord;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.toDataSource;
import org.cmdbuild.etl.waterway.storage.EtlMessage;
import static org.cmdbuild.etl.waterway.storage.EtlMessage.ETL_MESSAGE_ATTR_ATTACHMENTS;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.v3.endpoint.AuditWs.serializeErrors;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("etl/messages/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
public class EtlMessageWs {

    private final WaterwayService service;
    private final CardMapperService cardMapperService;

    public EtlMessageWs(WaterwayService service, CardMapperService cardMapperService) {
        this.service = checkNotNull(service);
        this.cardMapperService = checkNotNull(cardMapperService);
    }

    @GET
    @Path(EMPTY)
    public Object readMessages(WsQueryOptions wsQueryOptions) {
        DaoQueryOptions daoQueryOptions = DaoQueryOptionsImpl.copyOf(wsQueryOptions.getQuery()).withAttrs(cardMapperService.getClasseForModelOrBuilder(EtlMessage.class).getCoreAttributes().stream().map(Attribute::getName).filter(not(ETL_MESSAGE_ATTR_ATTACHMENTS::equals)).collect(toSet())).build(); // skip attachments here
        return response(service.getMessages(wsQueryOptions.isDetailed() ? wsQueryOptions.getQuery() : daoQueryOptions).map(m -> serializeMessage(m, wsQueryOptions.isDetailed())));
    }

    @GET
    @Path("{messageReference}/")
    public Object read(@PathParam("messageReference") String messageReference) {
        return response(serializeMessage(service.getMessage(messageReference), true));
    }

    @GET
    @Path("{messageReference}/attachments/{attachmentId}")
    public Object readAttachment(@PathParam("messageReference") String messageReference, @PathParam("attachmentId") String attachmentId) {
        return toDataSource(service.getMessageAttachmentLoadData(messageReference, attachmentId));
    }

    @POST
    @Path("{messageReference}/retry")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object retryFailedMessage(@PathParam("messageReference") String messageReference) {
        throw new UnsupportedOperationException();
//        return response(serializeMessage(service.getMessage(messageReference), true));
    }

    @GET
    @Path("stats")
    public Object readMessagesStats() {
        return response(map(service.getMessagesStats().getMessageCountByStatus()).mapKeys(CmConvertUtils::serializeEnum));
    }

    private Object serializeMessage(WaterwayMessage message, boolean detailed) {
        return map(
                "_id", buildMessageReference(message.getStorageCode(), message.getMessageId()),
                "messageId", message.getMessageId(),
                "status", serializeEnum(message.getStatus()),
                "queue", message.getQueue(),
                "storage", message.getStorage(),
                "_queueKey", message.getQueueKey(),
                "_storageKey", message.getStorageKey(),
                "_queueCode", message.getQueueCode(),
                "_storageCode", message.getStorageCode(),
                "nodeId", message.getNodeId(),
                "timestamp", toIsoDateTimeLocal(message.getTimestamp()),
                "transactionId", message.getTransactionId()
        ).accept(b -> {
            if (detailed) {
                b.put("meta", message.getMeta(),
                        "history", message.getHistory(),
                        "errors", serializeErrors(message.getErrors()),
                        "logs", message.getLogs());
                b.put("_historyRecords", list(message.getHistoryRecords()).map(h -> map(
                        "_id", h.getMessageKey(),
                        "_value", serializeHistoryRecord(h),
                        "messageId", h.getMessageId(),
                        "status", serializeEnum(h.getStatus()),
                        "queue", h.getQueueKey(),
                        "storage", h.getStorageKey(),
                        "_queueKey", h.getQueueKey(),
                        "_storageKey", h.getStorageKey(),
                        "_queueCode", h.getQueueCode(),
                        "_storageCode", h.getStorageCode(),
                        "nodeId", h.getNodeId(),
                        "timestamp", toIsoDateTimeLocal(h.getTimestamp()),
                        "transactionId", h.getTransactionId()
                )));
                b.put("attachments", list(message.getAttachments()).sorted(WaterwayMessageAttachment::getName).map(a -> map(
                        "_id", a.getName(),
                        "name", a.getName(),
                        "type", serializeEnum(a.getType()),
                        "storage", serializeEnum(a.getStorage()),
                        "_contentType", a.getContentType(),
                        "_byteSize", a.getByteSize(),
                        "meta", a.getMeta()).accept(m -> {
                    switch (a.getStorage()) {
                        case WMAS_REFERENCE -> {
                            m.put("value", a.getText());
                        }
                    }
                })
                ));
            }
        });
    }
}
