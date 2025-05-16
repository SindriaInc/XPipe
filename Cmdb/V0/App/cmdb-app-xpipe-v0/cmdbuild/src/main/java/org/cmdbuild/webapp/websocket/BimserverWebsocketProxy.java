/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.websocket;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.cmdbuild.spring.SpringIntegrationUtils.applicationContext;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.config.BimConfiguration;

@ServerEndpoint(value = "/services/bimserver/stream")
public class BimserverWebsocketProxy {

    private static final int MAX_BIMSERVER_TO_CLIENT_MESSAGE_SIZE = 32 * 1024 * 1024; //32MiB

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Cache<String, BimserverWebsocketClient> WEBSOCKET_CLIENTS_BY_UI_SESSION_ID = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

    private final BimConfiguration configuration;

    public BimserverWebsocketProxy() {
        configuration = applicationContext().getBean(BimConfiguration.class);//TODO fix this, autowire spring config
        logger.info("ready");
    }

    @OnOpen
    public void onOpen(Session session) {
        try {
            logger.debug("ui session opened = {}", session.getId());
            BimserverWebsocketClient websocketClient = new BimserverWebsocketClient(session);
            WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.put(session.getId(), websocketClient);
            logger.debug("active sessions = {}", WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.size());
        } catch (Exception ex) {
            logger.warn("error processing open session event", ex);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            logger.trace("ui server text message received, session = {}, message = {}", session.getId(), abbreviate(message));
            getBimserverWebsocketClient(session).sendMessage(message);
        } catch (Exception ex) {
            logger.error("error processing message event", ex);
        }
    }

    @OnMessage
    public void onMessage(Session session, byte[] message) {
        try {
            logger.trace("ui server binary message received, session = {}, message = {}", session.getId(), byteCountToDisplaySize(message.length));
            getBimserverWebsocketClient(session).sendMessage(message);
        } catch (Exception ex) {
            logger.error("error processing message event", ex);
        }
    }

    @OnMessage
    public void onMessage(Session session, PongMessage message) {
        logger.debug("pong message received from ui, session = {}, message = {}", session.getId(), message);
        // TODO Handle pong messages
    }

    @OnClose
    public void onClose(Session session) {
        try {
            logger.debug("ui session closed = {}", session.getId());
            getBimserverWebsocketClient(session).closeConnection();
            WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.invalidate(session.getId());
            logger.debug("active sessions = {}", WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.size());
        } catch (Exception ex) {
            logger.error("error processing close session event", ex);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.warn("ui session error, session = {}", session.getId(), throwable);
    }

    private BimserverWebsocketClient getBimserverWebsocketClient(Session session) {
        return checkNotNull(WEBSOCKET_CLIENTS_BY_UI_SESSION_ID.getIfPresent(session.getId()), "bim server client not found for ui server session = %s", session.getId());
    }

    private class BimserverWebsocketClient {

        private final Session uiServerSession;
        private Session bimserverClientSession;

        public BimserverWebsocketClient(Session session) throws URISyntaxException, IOException, DeploymentException {
            uiServerSession = checkNotNull(session);

            ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            client.getProperties().put("org.glassfish.tyrus.incomingBufferSize", MAX_BIMSERVER_TO_CLIENT_MESSAGE_SIZE); //see https://tyrus-project.github.io/documentation/1.13.1/user-guide.html#d0e1197
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        logger.debug("bimserver client session opened = {}", session.getId());
                        bimserverClientSession = session;
                    } catch (Exception ex) {
                        logger.error("error processing bimserver open session event", ex);
                    }

                    session.addMessageHandler(new MyTextMessageHandler());
                    session.addMessageHandler(new MyBinaryMessageHandler());
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    try {
                        logger.debug("bimserver client session closed, session = {}, reason =< {} >", session.getId(), closeReason);
                        try {
                            uiServerSession.close(closeReason);
                        } finally {
                            closeConnection();
                        }
                    } catch (Exception ex) {
                        logger.error("error processing bimserver close session event", ex);
                    }
                }

                @Override
                public void onError(Session session, Throwable throwable) {
                    logger.error("bimserver client websocket error for session = {}", session.getId(), throwable);
                }

            }, cec, new URI(configuration.getUrl().replaceFirst("http", "ws") + "/stream"));
            checkNotNull(bimserverClientSession, "unable to open bimserver websocket session");
        }

        private void sendMessage(String message) throws IOException {
            logger.debug("send text message to bimserver, session = {} message =< {} >", bimserverClientSession.getId(), abbreviate(message));
            bimserverClientSession.getBasicRemote().sendText(message);
        }

        private void sendMessage(byte[] message) throws IOException {
            logger.debug("send binary message to bimserver, session = {} message = {}", bimserverClientSession.getId(), byteCountToDisplaySize(message.length));
            bimserverClientSession.getBasicRemote().sendBinary(ByteBuffer.wrap(message));
        }

        private void closeConnection() throws IOException {
            if (bimserverClientSession != null) {
                try {
                    if (bimserverClientSession.isOpen()) {
                        bimserverClientSession.close();
                    }
                } catch (Exception ex) {
                    logger.debug("error closing websocket connection", ex);
                }
            }
            bimserverClientSession = null;
        }

        private class MyTextMessageHandler implements MessageHandler.Whole<String> {

            @Override
            public void onMessage(String msg) {
                try {
                    logger.debug("bimserver client text message received, session = {}, message =< {} >", bimserverClientSession.getId(), abbreviate(msg));
                    uiServerSession.getBasicRemote().sendText(msg);
                } catch (Exception ex) {
                    logger.error("error processing bimserver text message", ex);
                }
            }

        }

        private class MyBinaryMessageHandler implements MessageHandler.Whole<byte[]> {

            @Override
            public void onMessage(byte[] msg) {
                try {
                    logger.debug("bimserver client binary message received, session = {}, message = {}", bimserverClientSession.getId(), byteCountToDisplaySize(msg.length));
                    uiServerSession.getBasicRemote().sendBinary(ByteBuffer.wrap(msg));
                } catch (Exception ex) {
                    logger.error("error processing bimserver text message", ex);
                }
            }

        }

    }

}
