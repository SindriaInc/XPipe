package org.cmdbuild.config;

import org.cmdbuild.auth.login.PasswordManagementConfiguration;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.password")
public class PasswordManagementConfigurationImpl implements PasswordManagementConfiguration {

    @ConfigValue(key = "enable-password-change-management", description = "enable password management rules, expiration, etc", defaultValue = FALSE)
    private boolean passwordValidationEnabled;

    @ConfigValue(key = "allow_password_change", description = "allow users to change their own password", defaultValue = TRUE)
    private boolean passwordChangeEnabled;

    @ConfigValue(key = "differ-from-username", defaultValue = TRUE)
    private boolean differentFromUsername;

    @ConfigValue(key = "differ-from-previous", defaultValue = TRUE)
    private boolean differentFromPrevious;

    @ConfigValue(key = "differ-from-previous-count", defaultValue = "3")
    private int differentFromPreviousCount;

    @ConfigValue(key = "require-digit", defaultValue = FALSE)
    private boolean requireDigit;

    @ConfigValue(key = "require-lowercase", defaultValue = FALSE)
    private boolean requireLowercase;

    @ConfigValue(key = "require-uppercase", defaultValue = FALSE)
    private boolean requireUppercase;

    @ConfigValue(key = "min-length", defaultValue = "6")
    private int passwordMinLength;

    @ConfigValue(key = "max-password-age-days", defaultValue = "365")
    private int maxPasswordAgeDays;

    @ConfigValue(key = "forewarning-days", defaultValue = "7")
    private int forewarningDays;

    @ConfigValue(key = "expireServiceUserPassword", description = "if true, password expiration affects `service` users", defaultValue = FALSE)
    private boolean expireServiceUserPassword;

    @Override
    public boolean isPasswordManagementEnabled() {
        return passwordValidationEnabled;
    }

    @Override
    public boolean isPasswordChangeEnabled() {
        return passwordChangeEnabled;
    }

    @Override
    public boolean isServiceUsersPasswordExpirationEnabled() {
        return expireServiceUserPassword;
    }

    @Override
    public boolean getDifferentFromUsername() {
        return differentFromUsername;
    }

    @Override
    public boolean getDifferentFromPrevious() {
        return differentFromPrevious;
    }

    @Override
    public int getDifferentFromPreviousCount() {
        return differentFromPreviousCount;
    }

    @Override
    public boolean requireDigit() {
        return requireDigit;
    }

    @Override
    public boolean requireLowercase() {
        return requireLowercase;
    }

    @Override
    public boolean requireUppercase() {
        return requireUppercase;
    }

    @Override
    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    @Override
    public int getMaxPasswordAgeDays() {
        return maxPasswordAgeDays;
    }

    @Override
    public int getForewarningDays() {
        return forewarningDays;
    }

}
