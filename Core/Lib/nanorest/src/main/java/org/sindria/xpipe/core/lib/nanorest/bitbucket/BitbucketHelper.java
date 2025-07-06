package org.sindria.xpipe.core.lib.nanorest.bitbucket;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.config.AppConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class BitbucketHelper {

    /**
     * baseUrl
     */
    protected static String baseUrl = "https://api.bitbucket.org/2.0";

    /**
     * username
     */
    protected static String username = AppConfig.config.getApp().getBitbucket().getUsername();

    /**
     * token
     */
    protected static String token = AppConfig.config.getApp().getBitbucket().getToken();

    /**
     * encodedAuth
     */
    protected static String encodedAuth = Base64.getEncoder().encodeToString((BitbucketHelper.username + ":" + BitbucketHelper.token).getBytes());

    // Example base64
    // String encodeBytes = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());

    /**
     * Make get request
     */
    public static JSONObject get(String uri) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BitbucketHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Basic " + BitbucketHelper.encodedAuth
                    )
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body().toString());

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return new JSONObject("");
        }
    }

    /**
     * Make post request
     */
    public static JSONObject post(String uri, Object data) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BitbucketHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Basic " + BitbucketHelper.encodedAuth
                    )
                    .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body().toString());

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return new JSONObject("");
        }
    }


    /**
     * Make put request
     */
    public static JSONObject put(String uri, Object data) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BitbucketHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Basic " + BitbucketHelper.encodedAuth
                    )
                    .PUT(HttpRequest.BodyPublishers.ofString(data.toString()))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body().toString());

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return new JSONObject("");
        }
    }

    /**
     * Make delete request
     */
    public static JSONObject delete(String uri) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BitbucketHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Basic " + BitbucketHelper.encodedAuth
                    )
                    .DELETE()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.statusCode());

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return new JSONObject("");
        }
    }

}