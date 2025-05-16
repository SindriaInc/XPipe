package org.cmdbuild.config;

public interface EmailNotificationConfiguration {

	public boolean isEmailNotificationEnabled();

	public String getEmailDmsAccount();

	public String getEmailDmsTemplate();

	public String getEmailDmsDestination();

	public int getEmailDmsSilence();

}
