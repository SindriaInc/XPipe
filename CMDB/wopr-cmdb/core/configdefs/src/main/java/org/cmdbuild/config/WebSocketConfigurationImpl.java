package org.cmdbuild.config;

import static org.cmdbuild.config.WebSocketConfiguration.WEBSOCKET_CONFIG_NAMESPACE;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@Primary
@ConfigComponent(WEBSOCKET_CONFIG_NAMESPACE)
public class WebSocketConfigurationImpl implements WebSocketConfiguration {

    @ConfigValue(key = WEBSOCKET_CONFIG_ENABLED, description = "enable websocket service", defaultValue = TRUE)
    private boolean isWebSocketEnabled;

    @Override
    public boolean isWebSocketEnabled() {
        return isWebSocketEnabled;
    }

}
