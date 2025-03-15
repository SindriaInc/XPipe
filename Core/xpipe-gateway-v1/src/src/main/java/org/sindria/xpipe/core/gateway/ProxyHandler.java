package org.sindria.xpipe.core.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ProxyHandler implements HttpHandler {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI requestUri = exchange.getRequestURI();
        String fullPath = requestUri.getPath();
        Map<String, String> queryParams = getQueryParams(requestUri.getQuery());

        String service = matchService(fullPath);
        if (service == null) {
            sendResponse(exchange, 400, "Invalid service");
            return;
        }

        String targetUrl = resolveServiceUrl(service) + fullPath.replace("/api/v1/" + service, "");
        String authToken = exchange.getRequestHeaders().getFirst("Authorization");

        String requestBody = readRequestBody(exchange.getRequestBody());
        HttpResponse<String> response = forwardRequest(method, targetUrl, queryParams, requestBody, authToken);
        sendResponse(exchange, response.statusCode(), response.body());
    }

    private HttpResponse<String> forwardRequest(String method, String url,
                                                Map<String, String> queryParams, String requestBody,
                                                String authToken) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url + buildQueryString(queryParams)))
                    .header("Content-Type", "application/json");

            if (authToken != null) {
                requestBuilder.header("Authorization", authToken);
            }

            switch (method) {
                case "GET" -> requestBuilder.GET();
                case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
                case "PUT" -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(requestBody));
                case "DELETE" -> requestBuilder.DELETE();
                default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            HttpRequest request = requestBuilder.build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return createFakeResponse(500, "Internal Server Error");
        }
    }

    private static HttpResponse<String> createFakeResponse(int statusCode, String body) {
        return new HttpResponse<>() {
            @Override public int statusCode() { return statusCode; }
            @Override public String body() { return body; }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
            @Override public HttpRequest request() { return null; }
            @Override public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (s, s2) -> true); }
            @Override public URI uri() { return null; }
            @Override public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }
        };
    }

    private String matchService(String uri) {
        String[] parts = uri.split("/");
        return (parts.length > 3) ? parts[3] : null;
    }

    private String resolveServiceUrl(String service) {
        return System.getenv(service.toUpperCase() + "_SERVICE");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private String readRequestBody(InputStream is) throws IOException {
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
    }

    private Map<String, String> getQueryParams(String query) {
        if (query == null || query.isEmpty()) return Map.of();
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(p -> p[0], p -> p.length > 1 ? p[1] : ""));
    }

    private String buildQueryString(Map<String, String> queryParams) {
        return queryParams.isEmpty() ? "" : "?" + queryParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
}
