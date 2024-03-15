package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.notification")
public final class NotificationConfigImpl implements EmailNotificationConfiguration {

    @ConfigValue(key = "enable", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = "email.dms.account", defaultValue = "")
    private String emailDmsAccount;

    @ConfigValue(key = "email.dms.template", defaultValue = "")
    private String emailDmsTemplate;

    @ConfigValue(key = "email.dms.destination", defaultValue = "")
    private String emailDmsDestination;

    @ConfigValue(key = "email.dms.silence", defaultValue = "0")
    private int emailDmsSilence;

    @Override
    public boolean isEmailNotificationEnabled() {
        return isEnabled;
    }

    @Override
    public String getEmailDmsAccount() {
        return emailDmsAccount;
    }

    @Override
    public String getEmailDmsTemplate() {
        return emailDmsTemplate;
    }

    @Override
    public String getEmailDmsDestination() {
        return emailDmsDestination;
    }

    @Override
    public int getEmailDmsSilence() {
        return emailDmsSilence;
    }

}
