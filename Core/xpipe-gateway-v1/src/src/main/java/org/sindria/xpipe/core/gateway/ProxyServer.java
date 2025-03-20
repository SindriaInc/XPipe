package org.sindria.xpipe.core.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ProxyServer {

    private static final int PORT = 8080;
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Set<String> publicRoutes = new HashSet<>();
    private static Set<String> privateRoutes = new HashSet<>();

    public static void main(String[] args) throws IOException {
        loadRoutes();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/v1", new MiddlewareHandler());
        server.setExecutor(null);
        System.out.println("Proxy Server running on port " + PORT);
        server.start();
    }

    private static void loadRoutes() {
        try {
            File publicRoutesFile = new File("config/public_routes.json");
            File privateRoutesFile = new File("config/private_routes.json");

            if (publicRoutesFile.exists()) {
                publicRoutes = new HashSet<>(Arrays.asList(objectMapper.readValue(publicRoutesFile, String[].class)));
            }

            if (privateRoutesFile.exists()) {
                privateRoutes = new HashSet<>(Arrays.asList(objectMapper.readValue(privateRoutesFile, String[].class)));
            }

            System.out.println("Loaded Public Routes: " + publicRoutes);
            System.out.println("Loaded Private Routes: " + privateRoutes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class MiddlewareHandler implements HttpHandler {

        /**
         * response
         */
        private RestResponse response;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            URI requestUri = exchange.getRequestURI();
            String fullPath = requestUri.getPath();

            String authToken = exchange.getRequestHeaders().getFirst("Authorization");

            if (privateRoutes.contains(fullPath)) {
                if (authToken == null || !authToken.startsWith("Bearer ")) {
                    //sendResponse(exchange, 401, "Unauthorized: Missing Bearer token");
                    sendError(exchange, "Unauthorized: Missing Bearer token", 401);
                    return;
                }

                String token = authToken.substring(7);
                String userUid = extractUserUidFromToken(token);

                if (userUid == null || !isValidToken(token)) {
                    //sendResponse(exchange, 403, "Forbidden: Invalid token");
                    sendError(exchange, "Forbidden: Invalid token", 403);
                    return;
                }

                if (!hasPolicyAccess(userUid, fullPath, method)) {
                    //sendResponse(exchange, 403, "Forbidden: Policy access denied");
                    sendError(exchange, "Forbidden: Policy access denied", 403);
                    return;
                }
            }

            new ProxyHandler().handle(exchange);
        }

        private String extractUserUidFromToken(String token) {
            try {
                String keycloakUserInfoUrl = System.getenv("KEYCLOAK_USERINFO_URL");

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(keycloakUserInfoUrl))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    return null;
                }

                JsonNode jsonResponse = objectMapper.readTree(response.body());
                return jsonResponse.has("sub") ? jsonResponse.get("sub").asText() : null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private boolean isValidToken(String token) {
            try {
                String keycloakIntrospectionUrl = System.getenv("KEYCLOAK_INTROSPECT_URL");
                String clientId = System.getenv("KEYCLOAK_CLIENT_ID");
                String clientSecret = System.getenv("KEYCLOAK_CLIENT_SECRET");

                String requestBody = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                        + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                        + "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(keycloakIntrospectionUrl))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    return false;
                }

                JsonNode jsonResponse = objectMapper.readTree(response.body());
                return jsonResponse.get("active").asBoolean(false);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private boolean hasPolicyAccess(String userUid, String uri, String method) {
            try {
                String policyServiceUrl = System.getenv("POLICIES_SERVICE") + "/api/v1/policies/verify";

                String queryParams = "uid=" + URLEncoder.encode(userUid, StandardCharsets.UTF_8)
                        + "&uri=" + URLEncoder.encode(uri, StandardCharsets.UTF_8)
                        + "&mtd=" + URLEncoder.encode(method, StandardCharsets.UTF_8);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(policyServiceUrl + "?" + queryParams))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    return false;
                }

                JsonNode jsonResponse = objectMapper.readTree(response.body());
                return jsonResponse.has("data") &&
                        jsonResponse.get("data").has("response") &&
                        jsonResponse.get("data").get("response").get("hasAccess").asBoolean(false);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }


        /**
         * Success response
         */
        private void sendSuccess(HttpExchange exchange, String message, Integer code, HashMap<String, Object> data) throws IOException {
            this.response = new RestResponse(code, true, message, data);
            sendResponse(exchange, code, this.response.serialize().toString());
        }

        /**
         * Success response without data
         */
        private void sendSuccess(HttpExchange exchange, String message, Integer code) throws IOException {
            this.response = new RestResponse(code, true, message, new HashMap<String, Object>());
            sendResponse(exchange, code, this.response.serialize().toString());
        }


        /**
         * Error response
         */
        private void sendError(HttpExchange exchange, String message, Integer code, HashMap<String, Object> data) throws IOException {
            this.response = new RestResponse(code, false, message, data);
            sendResponse(exchange, code, this.response.serialize().toString());
        }

        /**
         * Error response without data
         */
        private void sendError(HttpExchange exchange, String message, Integer code) throws IOException {
            this.response = new RestResponse(code, false, message, new HashMap<String, Object>());
            sendResponse(exchange, code, this.response.serialize().toString());
        }


    }
}
