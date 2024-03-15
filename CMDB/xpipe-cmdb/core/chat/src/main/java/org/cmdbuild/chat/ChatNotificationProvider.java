/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.chat.ChatMessageSourceType.CMST_SYSTEM;
import static org.cmdbuild.chat.ChatService.CHAT_NOTIFICATION_SOURCE_TYPE;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_CHAT;
import org.cmdbuild.notification.NotificationCommonData;
import org.cmdbuild.notification.NotificationProvider;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatNotificationProvider implements NotificationProvider<NotificationCommonData> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ChatService chatService;
    private final OperationUserSupplier operationUser;

    public ChatNotificationProvider(ChatService chatService, OperationUserSupplier operationUser) {
        this.chatService = checkNotNull(chatService);
        this.operationUser = checkNotNull(operationUser);
    }

    @Override
    public String getNotificationProviderName() {
        return NOTIFICATION_PROVIDER_CHAT;
    }

    @Override
    @Nullable
    public NotificationCommonData sendNotification(NotificationCommonData notificationData) {
        checkArgument(notificationData.hasNotificationProvider(NOTIFICATION_PROVIDER_CHAT), "invalid notification provider for chat = %s", notificationData);
        logger.debug("send notification = {}", notificationData);
        toListOfStrings(notificationData.getTo()).forEach(target -> {
            chatService.sendMessageAs(buildChatMessage(notificationData, target), 
                                      parseEnumOrDefault(notificationData.getMeta(CHAT_NOTIFICATION_SOURCE_TYPE), CMST_SYSTEM), 
                                      operationUser.getUsername(), operationUser.getUser().getLoginUser().getDescription());
        });
        return null;//TODO message to email (optional)
    }

    private static ChatMessageData buildChatMessage(NotificationCommonData notificationData, String target) {
        return ChatMessageDataImpl.builder()
                .withContent(notificationData.getContent())
                .withSubject(notificationData.getSubject())
                .withTarget(target)
                .withMeta(notificationData.getMeta())
                .build();
    }

}
