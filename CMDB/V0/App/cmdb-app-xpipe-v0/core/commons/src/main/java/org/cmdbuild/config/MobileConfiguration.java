package org.cmdbuild.config;

/**
 *
 * @author ataboga
 */
public interface MobileConfiguration {

    boolean isMobileEnabled();

    String getMobileCustomerCode();

    String getMobileDeviceNamePrefix();
    
    String getMobileNotificationAuthInfo();

}
