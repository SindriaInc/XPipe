/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.notification.mobileapp;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.notification.NotificationProvider;
import org.cmdbuild.notification.NotificationStatus;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class MobileAppNotificationProvider implements NotificationProvider<MobileAppNotificationData> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final MobileAppNotificationSender mobileAppNotificationSender;
    private final MobileConfiguration mobileConfiguration;
    
    public MobileAppNotificationProvider(MobileAppNotificationSender mobileNotificationSender, MobileConfiguration mobileConfiguration) {
        this.mobileAppNotificationSender = mobileNotificationSender;
        this.mobileConfiguration = mobileConfiguration;
        
    }
    
    @Override
    public String getNotificationProviderName() {
        return MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP;
    }

    @Override
    public MobileAppNotificationData sendNotification(MobileAppNotificationData notificationData) {
        String authInfo = mobileConfiguration.getMobileNotificationAuthInfo();
        checkNotNull(authInfo);
        
        MobileAppNotificationDataImpl.MobileAppNotificationDataImplBuilder mobileAppNotificationData = MobileAppNotificationDataImpl.builder()
                .withTopics(notificationData.getTo())
                .withSubject(notificationData.getSubject())
                .withContent(notificationData.getContent())
                .withMeta(notificationData.getMeta())
                .withAuthInfo(authInfo);
        
        if (!mobileConfiguration.isMobileEnabled()) {
            logger.warn("trying to send Firebase msg, but mobile is disabled");
            return mobileAppNotificationData
                    .withStatus(NotificationStatus.NS_ERROR)
                    .build();
        }
        
        return mobileAppNotificationSender.sendNotification(mobileAppNotificationData.build());
    }
    
    public boolean releaseSender(MobileAppNotificationData mobileAppNotificationData) {
        return mobileAppNotificationSender.release(mobileAppNotificationData);
    }
}
