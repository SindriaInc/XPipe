/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.api.inner;

import org.cmdbuild.notification.mobileapp.MobileAppNotificationSender;
import org.springframework.stereotype.Component;
import org.cmdbuild.api.NotificationApi;

/**
 * Methods on notifications
 * 
 * @author afelice
 */
@Component
public class NotificationApiImpl implements NotificationApi {
    
    private final MobileAppNotificationSender mobileAppNotificationSender;

    public NotificationApiImpl(MobileAppNotificationSender mobileAppNotificationSender) {
        this.mobileAppNotificationSender = mobileAppNotificationSender;
    }
    
    @Override
    public void releaseMobileAppResources() {
        mobileAppNotificationSender.releaseAll();
    }
}
