package org.cmdbuild.config;

/**
 *
 * @author ataboga
 */
public interface WebSocketConfiguration {

    final static String WEBSOCKET_CONFIG_NAMESPACE = "org.cmdbuild.services.websocket",
            WEBSOCKET_CONFIG_ENABLED = "enabled";

    boolean isWebSocketEnabled();

}
