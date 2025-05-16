package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.SoapConfiguration.SOAP_CONFIG_NAMESPACE;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@Primary
@ConfigComponent(SOAP_CONFIG_NAMESPACE)
public class SoapConfigurationImpl implements SoapConfiguration {

    @ConfigValue(key = SOAP_CONFIG_ENABLED, description = "enable soap service", defaultValue = FALSE)
    private boolean isSoapEnabled;

    @Override
    public boolean isSoapEnabled() {
        return isSoapEnabled;
    }

}
