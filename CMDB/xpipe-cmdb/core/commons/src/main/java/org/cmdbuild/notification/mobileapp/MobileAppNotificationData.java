/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.notification.mobileapp;

import com.google.common.base.Joiner;
import java.time.ZonedDateTime;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.notification.NotificationCommonData;
import org.cmdbuild.notification.NotificationStatus;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

/**
 *
 * @author afelice
 */
public interface MobileAppNotificationData extends NotificationCommonData {

    @Nullable
    ZonedDateTime getBeginDate();

    int getErrorCount();

    @Nullable
    ZonedDateTime getSentDate();

    @Nullable
    String getAuthInfo();

    NotificationStatus getStatus();

    String getStatusAsString();
    
    default String serializeNotificationStatus(NotificationStatus status) {
        return serializeEnum(status);
    }

    public static String topicListToString(@Nullable List<String> list) {
        return CmConvertUtils.fromListToString(list);
    }

    public static List<String> parseTopicListAsStrings(String topicList) {
        return CmConvertUtils.toListOfStrings(topicList);
    }         

    @Nullable
    public static NotificationStatus parseNotificationStatus(@Nullable String status) {
        return parseEnumOrNull(status, NotificationStatus.class);
    }  
    
}
