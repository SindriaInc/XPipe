package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.CHAT_ACCESS_AUTHORITY;
import org.cmdbuild.chat.ChatMessage;
import static org.cmdbuild.chat.ChatMessage.CHAT_MESSAGE_ATTR_SOURCE_NAME;
import static org.cmdbuild.chat.ChatMessage.CHAT_MESSAGE_ATTR_SOURCE_TYPE;
import static org.cmdbuild.chat.ChatMessage.CHAT_MESSAGE_ATTR_STATUS;
import static org.cmdbuild.chat.ChatMessage.CHAT_MESSAGE_ATTR_TARGET;
import static org.cmdbuild.chat.ChatMessage.CHAT_MESSAGE_ATTR_THREAD;
import static org.cmdbuild.chat.ChatMessage.CHAT_MESSAGE_ATTR_TIMESTAMP;
import static org.cmdbuild.chat.ChatMessage.CHAT_MESSAGE_ATTR_TYPE;
import org.cmdbuild.chat.ChatMessageData;
import org.cmdbuild.chat.ChatMessageDataImpl;
import org.cmdbuild.chat.ChatMessageStatus;
import static org.cmdbuild.chat.ChatMessageStatus.CMS_ARCHIVED;
import org.cmdbuild.chat.ChatService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("sessions/current/messages")
@Produces(APPLICATION_JSON)
public class ChatMessageWs {

    private final ChatService service;

    public ChatMessageWs(ChatService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path("")
    public Object getMessages(WsQueryOptions options) {
        return response(service.getMessagesForCurrentUser(options.getQuery().mapAttrNames(map(
                "sourceType", CHAT_MESSAGE_ATTR_SOURCE_TYPE,
                "type", CHAT_MESSAGE_ATTR_TYPE,
                "status", CHAT_MESSAGE_ATTR_STATUS,
                "timestamp", CHAT_MESSAGE_ATTR_TIMESTAMP,
                "sourceName", CHAT_MESSAGE_ATTR_SOURCE_NAME,
                "target", CHAT_MESSAGE_ATTR_TARGET,
                "thread", CHAT_MESSAGE_ATTR_THREAD
        ))).map(options.isDetailed() ? this::serializeDetailedMessage : this::serializeBasicMessage));
    }

    @PUT
    @Path("{recordId}")
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object updateMessage(@QueryParam("recordId") Long recordId, WsMessageData message) {
        checkArgument(equal(message.status, CMS_ARCHIVED));
        service.archiveMessagesForCurrentUser(list(recordId));
        return success();
    }

    @POST
    @Path("")
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object sendMessage(WsMessageData message) {
        return response(serializeDetailedMessage(service.sendMessage(message.toChatMessageData())));
    }

    @DELETE
    @Path("{recordId}")
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object deleteMessage(@QueryParam("recordId") Long recordId) {
        service.deleteMessagesForCurrentUser(list(recordId));
        return success();
    }

    private FluentMap<String, Object> serializeBasicMessage(ChatMessage message) {
        return map(
                "_id", message.getId(),
                "messageId", message.getMessageId(),
                "subject", message.getSubject(),
                "target", message.getTarget(),
                "thread", message.getThread(),
                "sourceType", serializeEnum(message.getSourceType()),
                "sourceName", message.getSourceName(),
                "sourceDescription", message.getSourceDescription(),
                "timestamp", toIsoDateTimeLocal(message.getTimestamp()),
                "type", serializeEnum(message.getType()),
                "status", serializeEnum(message.getStatus()),
                "_isNew", message.isNewMessage()
        );
    }

    private FluentMap<String, Object> serializeDetailedMessage(ChatMessage message) {
        return map(serializeBasicMessage(message)).with(
                "content", message.getContent(),
                "meta", message.getMeta()
        );
    }

    public static class WsMessageData {

        private final String target, subject, content, thread;
        private final Map<String, String> meta;
        private final ChatMessageStatus status;

        public WsMessageData(@JsonProperty("status") String status, @JsonProperty("target") String target, @JsonProperty("subject") String subject, @JsonProperty("content") String content, @JsonProperty("thread") String thread, @JsonProperty("meta") Map<String, String> meta) {
            this.status = parseEnumOrNull(status, ChatMessageStatus.class);
            this.target = target;
            this.subject = subject;
            this.content = content;
            this.thread = thread;
            this.meta = meta;
        }

        private ChatMessageData toChatMessageData() {
            return ChatMessageDataImpl.builder().withTarget(target).withSubject(subject).withThread(thread).withMeta(meta).withContent(content).build();
        }

    }
}
