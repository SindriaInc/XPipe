/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.notification.mobileapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auth.oauth2.GoogleCredentials;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.notification.NotificationStatus;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class FirebaseSender implements MobileAppNotificationSender {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public MobileAppNotificationData sendNotification(MobileAppNotificationData notificationData) {
        checkNotBlank(notificationData.getAuthInfo());
        checkNotBlank(notificationData.getSubject());
        checkNotBlank(notificationData.getContent());
        final String topics = notificationData.getTo();
        checkNotBlank(topics);

        // Identify serviceAccount used
        String serviceAccountJson = notificationData.getAuthInfo();
        String serviceAccountId = identifyServiceAccount(serviceAccountJson);
        logger.debug("trying to send Firebase msg with serviceAccount =< {} >, topic =< {} >, mobile app notification =< {} >", serviceAccountId, topics, notificationData);

        // Init Firebase Admin SDK for fgiven Google Firebase project
        FirebaseApp firebaseApp = initFirebaseApp(serviceAccountJson, serviceAccountId);

        // Define Message payload
        final Notification msgToSend = Notification.builder()
                .setTitle(notificationData.getSubject())
                .setBody(notificationData.getContent())
                .build();

        List<String> topicList = MobileAppNotificationData.parseTopicListAsStrings(topics);
        // NotificationStatus.NS_SENT only if all notifications are sent correctly
        NotificationStatus globalStatus = topicList.stream().map(t -> buildAndSend(firebaseApp, t, notificationData, msgToSend).getStatus()).reduce(NotificationStatus.NS_SENT, NotificationStatus::merge);

        return MobileAppNotificationDataImpl.copyOf(notificationData)
                .withSentDate(now())
                .withStatus(globalStatus)
                .build();
    }

    private MobileAppNotificationData buildAndSend(FirebaseApp firebaseApp, String topic,
            MobileAppNotificationData notificationData, Notification msgToSend) {
        // Setting default sound for ios notification
        ApnsConfig apnsConfig = ApnsConfig.builder().setAps(Aps.builder().setSound("default").build()).build();

        Message.Builder messageBuilder = Message.builder()
                .setTopic(topic)
                .setApnsConfig(apnsConfig)
                .setNotification(msgToSend);

        if (MapUtils.isNotEmpty(notificationData.getMeta())) {
            notificationData.getMeta().forEach((k, v) -> logger.debug("adding this meta to firebase data -> key: {} - value: {}", k, v));
            messageBuilder.putAllData(notificationData.getMeta());
        }

        // Send a message to the devices subscribed to the provided token/topic.
        return sendFirebaseNotification(messageBuilder, topic, notificationData, firebaseApp);
    }

    /**
     * (Eventually) Delete the initialized Firebase resources
     *
     * @param mobileAppNotificationData
     */
    @Override
    public boolean release(MobileAppNotificationData mobileAppNotificationData) {
        checkNotBlank(mobileAppNotificationData.getAuthInfo());

        String serviceAccountJson = mobileAppNotificationData.getAuthInfo();
        final String serviceAccountId = identifyServiceAccount(serviceAccountJson);

        final Optional<FirebaseApp> foundInstance = FirebaseApp.getApps().stream()
                .filter(a -> a.getName().equals(serviceAccountId))
                .findAny();

        foundInstance.ifPresent(firebaseApp -> release(firebaseApp));

        return foundInstance.isPresent();
    }

    /**
     * (Eventually) Delete all initialized Firebase resources
     */
    @Override
    public void releaseAll() {
        FirebaseApp.getApps().stream().forEach(firebaseApp -> release(firebaseApp));
    }

    private void release(FirebaseApp firebaseApp) {
        firebaseApp.delete();
        logger.info("released FirebaseApp for serviceAccount =< {} >", firebaseApp.getName());
    }

    private String identifyServiceAccount(String serviceAccountJson) {
        ServiceAccountClient client = CmJsonUtils.fromJson(serviceAccountJson, ServiceAccountClient.class);

        checkNotNull(client.getProjectId(), "Invalid serviceAccount json, project_id is missing");
        checkNotNull(client.getClientId(), "Invalid serviceAccount json, client_id is missing");

        return format("%s_%s", client.getProjectId(), client.getClientId());
    }

    /**
     * Send a message to the devices subscribed to the provided topic
     *
     * @param messageBuilder
     * @param notificationData
     */
    private MobileAppNotificationData sendFirebaseNotification(Message.Builder messageBuilder, String topic, MobileAppNotificationData notificationData,
            FirebaseApp firebaseApp) {

        String serviceAccountId = firebaseApp.getName();
        try {
            // Response is a message ID string.
            String sentMessageID = FirebaseMessaging.getInstance(firebaseApp).send(messageBuilder.build());
            logger.debug("sent Firebase msg with serviceAccount =< {} >, topic =< {} >, mobile app notification =< {} >, generated messageID =< {} >", serviceAccountId, topic, notificationData, sentMessageID);

            return MobileAppNotificationDataImpl.builder()
                    .withSubject(notificationData.getSubject())
                    .withContent(notificationData.getContent())
                    .withStatus(NotificationStatus.NS_SENT)
                    .build();

        } catch (FirebaseMessagingException ex) {
            logger.error("error sending Firebase msg with serviceAccount =< {} >, topic =< {} >, mobile app notification =< {} >", serviceAccountId, topic, notificationData, ex);
        }

        return MobileAppNotificationDataImpl.builder()
                .withStatus(NotificationStatus.NS_ERROR)
                .build();
    }

    private FirebaseApp initFirebaseApp(String serviceAccountJson, final String serviceAccountId) {

        // Avoid error "FirebaseApp name [DEFAULT] already exists": build it only if not
        // already existent, see https://stackoverflow.com/a/44407983
        return FirebaseApp.getApps().stream()
                .filter(a -> a.getName().equals(serviceAccountId))
                .findAny()
                .orElseGet(() -> buildFirebaseApp(serviceAccountJson, serviceAccountId));
    }

    private FirebaseApp buildFirebaseApp(String serviceAccountJson, String serviceAccountId) {
        FirebaseApp firebaseApp = null;
        try ( InputStream serviceAccountJsonStream = IOUtils.toInputStream(serviceAccountJson)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountJsonStream)) // Raises IOException
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options, serviceAccountId);
            logger.info("initialized FirebaseApp for serviceAccount =< {} >", serviceAccountId);
        } catch (IOException ex) {
            logger.error("error initializing FirebaseApp for serviceAccount =< {} >", serviceAccountId, ex);
        }

        return firebaseApp;
    }

} // end FirebaseSender class

class ServiceAccountClient {

    private final String clientId;
    private final String projectId;

    public ServiceAccountClient(
            @JsonProperty(value = "client_id") String clientId,
            @JsonProperty(value = "project_id") String projectId
    ) {
        this.clientId = clientId;
        this.projectId = projectId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getProjectId() {
        return projectId;
    }

} // end ServiceAccountClient class
