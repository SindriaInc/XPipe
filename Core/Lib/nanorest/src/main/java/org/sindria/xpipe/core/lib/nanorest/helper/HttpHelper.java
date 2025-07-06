package org.sindria.xpipe.core.lib.nanorest.helper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;
import org.json.JSONObject;

public class HttpHelper {

    private final String baseUrl;
    private final String authHeader;
    private final HttpClient client;

    public HttpHelper(String baseUrl, String authType, String username, String password) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();

        if ("Basic".equalsIgnoreCase(authType)) {
            String encodedAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            this.authHeader = "Basic " + encodedAuth;
        } else if ("Bearer".equalsIgnoreCase(authType)) {
            this.authHeader = "Bearer " + password;
        } else {
            this.authHeader = null;
        }
    }

    private HttpRequest.Builder createRequest(String uri, Map<String, String> headers) throws URISyntaxException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(this.baseUrl + uri))
                .version(HttpClient.Version.HTTP_2);

        if (this.authHeader != null) {
            builder.header("Authorization", this.authHeader);
        }

        if (headers != null) {
            headers.forEach(builder::header);
        }

        return builder;
    }

    private JSONObject sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        try {
            return new JSONObject(response.body());
        } catch (Exception e) {
            return new JSONObject().put("status", response.statusCode()).put("message", response.body());
        }
    }

    public JSONObject get(String uri, Map<String, String> headers) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = createRequest(uri, headers).GET().build();
        return sendRequest(request);
    }

    public JSONObject post(String uri, Object data, Map<String, String> headers) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = createRequest(uri, headers)
                .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                .header("Content-Type", "application/json")
                .build();
        return sendRequest(request);
    }

    public JSONObject put(String uri, Object data, Map<String, String> headers) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = createRequest(uri, headers)
                .PUT(HttpRequest.BodyPublishers.ofString(data.toString()))
                .header("Content-Type", "application/json")
                .build();
        return sendRequest(request);
    }

    public JSONObject delete(String uri, Map<String, String> headers) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = createRequest(uri, headers).DELETE().build();
        return sendRequest(request);
    }
}