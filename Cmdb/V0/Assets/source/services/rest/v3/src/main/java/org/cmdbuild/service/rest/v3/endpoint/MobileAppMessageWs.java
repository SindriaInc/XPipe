package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import org.cmdbuild.common.utils.FilteringOptions;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData.MOBILE_APP_MESSAGE_ATTR_SOURCE_NAME;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData.MOBILE_APP_MESSAGE_ATTR_SOURCE_TYPE;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData.MOBILE_APP_MESSAGE_ATTR_STATUS;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData.MOBILE_APP_MESSAGE_ATTR_TARGET;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData.MOBILE_APP_MESSAGE_ATTR_TIMESTAMP;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageDataImpl;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_ARCHIVED;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.plugin.DummyPluginService;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.failure;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("sessions/current/mobile/messages")
@Produces(APPLICATION_JSON)
public class MobileAppMessageWs {

    private final MobileConfiguration mobileConfiguration;
    private final MobileAppService foundService;

    public MobileAppMessageWs(MobileAppService service, MobileConfiguration mobileConfiguration) {
        checkNotNull(mobileConfiguration);
        this.foundService = service;
        this.mobileConfiguration = mobileConfiguration;
    }

    @GET
    @Path("")
    public Object getMessages(WsQueryOptions options) {
        try {
            return response(getService().getMessagesForCurrentUser(options.getQuery().mapAttrNames(map(
                    "sourceType", MOBILE_APP_MESSAGE_ATTR_SOURCE_TYPE,
                    "status", MOBILE_APP_MESSAGE_ATTR_STATUS,
                    "timestamp", MOBILE_APP_MESSAGE_ATTR_TIMESTAMP,
                    "sourceName", MOBILE_APP_MESSAGE_ATTR_SOURCE_NAME,
                    "target", MOBILE_APP_MESSAGE_ATTR_TARGET
            ))).map(options.isDetailed() ? this::serializeDetailedMessage : this::serializeBasicMessage));
        } catch (UnsupportedOperationException unsExc) {
            return failureWith(unsExc.getMessage());
        }
    }

    @POST
    @Path("")
    public Object sendMessage(WsMessageData message) {
        try {
            return response(serializeDetailedMessage(getService().sendMessage(message.toMobileAppMessageData())));
        } catch (UnsupportedOperationException unsExc) {
            return failureWith(unsExc.getMessage());
        }
    }

    @PUT
    @Path("{recordId}")
    public Object updateMessage(@QueryParam("recordId") Long recordId, WsMessageData message) {
        checkArgument(equal(message.status, MAMS_ARCHIVED));
        try {
            getService().archiveMessagesForCurrentUser(list(recordId));
            return success();
        } catch (UnsupportedOperationException unsExc) {
            return failureWith(unsExc.getMessage());
        }
    }

    @DELETE
    @Path("{recordId}")
    public Object deleteMessage(@QueryParam("recordId") Long recordId) {
        try {
            getService().deleteMessagesForCurrentUser(list(recordId));
            return success();
        } catch (UnsupportedOperationException unsExc) {
            return failureWith(unsExc.getMessage());
        }
    }

    private FluentMap<String, Object> serializeBasicMessage(MobileAppMessage message) {
        return map(
                "_id", message.getId(),
                "messageId", message.getMessageId(),
                "subject", message.getSubject(),
                "target", message.getTarget(),
                "sourceType", serializeEnum(message.getSourceType()),
                "sourceName", message.getSourceName(),
                "sourceDescription", message.getSourceDescription(),
                "timestamp", toIsoDateTimeLocal(message.getTimestamp()),
                "status", serializeEnum(message.getStatus()),
                "_isNew", message.isNewMessage()
        );
    }

    private FluentMap<String, Object> serializeDetailedMessage(MobileAppMessage message) {
        return map(serializeBasicMessage(message)).with(
                "content", message.getContent(),
                "meta", message.getMeta()
        );
    }

    private Object failureWith(String errMsg) {
        return Response.status(Response.Status.BAD_REQUEST).entity(failure().with("messages", errMsg)).build();
    }

    /**
     * This can't be done in class constructor since configuration is not
     * already correctly initialized. So a dynamic initialization, in each
     * endpoint, is done.
     */
    private MobileAppService getService() {
        if (mobileConfiguration.isMobileEnabled()) {
            return checkNotNull(foundService);
        } else {
            return new DummyMobileAppService();
        }
    }

    public static class WsMessageData {

        private final String target, subject, content;
        private final Map<String, String> meta;
        private final MobileAppMessageStatus status;

        public WsMessageData(@JsonProperty("status") String status, @JsonProperty("target") String target, @JsonProperty("subject") String subject, @JsonProperty("content") String content, @JsonProperty("meta") Map<String, String> meta) {
            this.status = parseEnumOrNull(status, MobileAppMessageStatus.class);
            this.target = target;
            this.subject = subject;
            this.content = content;
            this.meta = meta;
        }

        private MobileAppMessageData toMobileAppMessageData() {
            return MobileAppMessageDataImpl.builder().withTarget(target).withSubject(subject).withMeta(meta).withContent(content).build();
        }

    }

    class DummyMobileAppService implements MobileAppService, DummyPluginService {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public PagedElements<MobileAppMessage> getMessagesForCurrentUser(FilteringOptions options) {
            return throwError("trying to get mobile app notification msg, but mobile is disabled");
        }

        @Override
        public MobileAppMessage sendMessage(MobileAppNotificationData notification) {
            return throwError("trying to send mobile app notification msg, but mobile is disabled");
        }

        @Override
        public MobileAppMessage sendMessage(MobileAppMessageData message) {
            return throwError("trying to send mobile app notification msg, but mobile is disabled");
        }

        @Override
        public boolean releaseSender(MobileAppNotificationData mobileAppNotificationData) {
            return throwError("trying to release mobile app notification sender, but mobile is disabled");
        }

        @Override
        public void archiveMessagesForCurrentUser(List<Long> recordIds) {
            throwError("trying to archive mobile app notification message, but mobile is disabled");
        }

        @Override
        public void deleteMessagesForCurrentUser(List<Long> recordIds) {
            throwError("trying to delete mobile app notification message, but mobile is disabled");
        }

        private <T> T throwError(String errMsg) throws UnsupportedOperationException {
            logger.warn(marker(), errMsg);
            throw new UnsupportedOperationException(errMsg);
        }

    } // end DummyMobileAppService class

} // end MobileAppMessageWs class
