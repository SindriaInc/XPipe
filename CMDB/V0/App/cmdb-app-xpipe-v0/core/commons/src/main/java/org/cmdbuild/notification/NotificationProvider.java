/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification;

import javax.annotation.Nullable;

public interface NotificationProvider<T extends NotificationCommonData> {
    
    @Nullable
    T sendNotification(T notificationData);

    String getNotificationProviderName();

}
