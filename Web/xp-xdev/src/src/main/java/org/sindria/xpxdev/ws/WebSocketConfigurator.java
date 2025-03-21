package org.sindria.xpxdev.ws;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();

        if (httpSession == null) {
            return;
        }
        //把HttpSession中保存的ClientIP放到ServerEndpointConfig中，关键字可以跟之前不同
        config.getUserProperties().put("ClientIP", httpSession.getAttribute("ClientIP"));
    }
}