/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.utils;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.javascript.jscomp.jarjar.com.google.re2j.Matcher;
import com.google.javascript.jscomp.jarjar.com.google.re2j.Pattern;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.Completer;
import jline.console.completer.CompletionHandler;
import jline.console.history.FileHistory;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.common.http.HttpConst;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.CmExceptionUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNullSafe;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleClientHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BlockingQueue<Map<String, Object>> queue = new ArrayBlockingQueue<>(10);

    public void run(String sessionId, String baseUrl) throws Exception {
        System.out.printf("open connection to < %s > ... ", baseUrl);
        checkNotBlank(sessionId);
        String prompt = baseUrl;
        ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().configurator(new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put(HttpConst.CMDBUILD_AUTHORIZATION_HEADER, Collections.singletonList(sessionId));
            }
        }).build();
        ClientManager websocketClient = ClientManager.createClient();
        CompletableFuture connectionReadyFuture = new CompletableFuture();
        javax.websocket.Session websocketSession = websocketClient.connectToServer(new Endpoint() {
            @Override
            public void onOpen(javax.websocket.Session session, EndpointConfig config) {
                try {
                    logger.debug("websocket client session opened = {}", session.getId());
                    session.addMessageHandler(String.class, (MessageHandler.Whole<String>) (String msg) -> {
                        try {
                            logger.debug("received message =< {} >", msg);
                            Map<String, Object> payload = CmJsonUtils.fromJson(msg, CmJsonUtils.MAP_OF_OBJECTS);
                            switch (Strings.nullToEmpty(CmStringUtils.toStringOrNull(payload.get("_event")))) {
                                case "socket.session.ok":
                                    logger.debug("connection ready");
                                    connectionReadyFuture.complete(true);
                                    break;
                                case "console.response":
                                    queue.put(payload);
                                    break;
                                case "socket.error":
                                    ((CompletableFuture) connectionReadyFuture).completeExceptionally(CmExceptionUtils.runtime("error opening socket connection: %s", msg));
                                    System.out.println("\nsocket error");
                                    System.exit(1);
                                    break;
                            }
                        } catch (Exception ex) {
                            logger.error("error processing message", ex);
                        }
                    });
                    session.getBasicRemote().sendText(CmJsonUtils.toJson(CmMapUtils.map("_action", "socket.session.login", "token", sessionId)));
                    logger.debug("session login request sent");
                } catch (Exception ex) {
                    logger.error("error processing websocket open session event", ex);
                }
            }

            @Override
            public void onClose(javax.websocket.Session session, CloseReason closeReason) {
                logger.debug("websocket session closed, session = {}, reason = {}", session.getId(), closeReason);
                System.out.println("\nconnection closed");
                System.exit(1);
            }
        }, cec, new URI(checkNotBlank(baseUrl).replaceFirst("http", "ws") + "/services/websocket/v1/main"));
        connectionReadyFuture.get(5, TimeUnit.SECONDS);
        websocketSession.getBasicRemote().sendText(CmJsonUtils.toJson(CmMapUtils.map("_action", "console.open", "_id", randomId())));
        System.out.println("ready");
        ConsoleReader console = new ConsoleReader();
        console.setPrompt(String.format("%s : ", prompt));
        console.setExpandEvents(false);
        console.setHistory(new FileHistory(new File(System.getProperty("user.home"), ".cm_console_history")));
        console.setCompletionHandler(new CandidateListCompletionHandler() {
            {
                setPrintSpaceAfterFullCompletion(false);
            }
        });
        console.addCompleter(new Completer() {
            @Override
            public int complete(String buffer, int cursor, List<CharSequence> candidates) {
                logger.info("buffer =< {} > cursor = {}", buffer, cursor);
                try {
                    String part = buffer.substring(0, cursor).trim(), prefix;
                    String requestId = randomId();
                    websocketSession.getBasicRemote().sendText(toJson(map("_action", "console.autocomplete", "_id", requestId, "line", part)));
                    Map<String, Object> response = getResponse(requestId);
                    Matcher matcher = Pattern.compile("(.*)[.](.+)").matcher(part);
                    if (matcher.matches()) {
                        prefix = matcher.group(2);
                    } else {
                        prefix = "";
                    }
                    ((List<String>) response.get("candidates")).stream().map(c -> prefix + c).sorted().forEach(candidates::add);
                    return cursor - prefix.length();
                } catch (Exception ex) {
                    logger.warn("error processing autocomplete with buffer =< {} > cursor = {}", buffer, cursor, ex);
                    candidates.clear();
                    return 0;
                }
            }

        });
        String line;
        try {
            loop:
            while ((line = console.readLine()) != null) {
                ((FileHistory) console.getHistory()).flush();
                if (StringUtils.isNotBlank(line)) {
                    switch (line) {
                        case "quit":
                        case "exit":
                            break loop;
                        default:
                            try {
                            String requestId = randomId();
                            websocketSession.getBasicRemote().sendText(toJson(map("_action", "console.exec", "_id", requestId, "line", line)));
                            Map<String, Object> response = getResponse(requestId);
                            Object res = response.get("output");
                            if (res != null) {
                                System.out.printf("%s > %s\n", prompt, toStringOrNullSafe(res));
                            }
                        } catch (Exception ex) {
                            System.out.println("ERROR : " + ex.toString());
                            System.out.println();
                        }
                    }
                }
            }
            System.out.println();
        } finally {
            TerminalFactory.get().restore();
        }
    }

    private Map<String, Object> getResponse(String requestId) {
        try {
            Map<String, Object> response;
            do {
                response = queue.take();
            } while (!Objects.equal(requestId, response.get("requestId")));
            return response;
        } catch (InterruptedException ex) {
            throw runtime(ex);
        }
    }

}
