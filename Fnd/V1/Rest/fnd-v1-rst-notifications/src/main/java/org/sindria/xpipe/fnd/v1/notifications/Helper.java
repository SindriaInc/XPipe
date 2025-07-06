package org.sindria.xpipe.fnd.v1.notifications;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;


import org.sindria.xpipe.core.lib.nanorest.config.AppConfig;


public class Helper {

    /**
     * baseUrl
     */
    protected static String baseUrl = "http://172.16.10.5";

    /**
     * token
     */

    protected static String token = "secret";
//    protected static String token = AppConfig.config.getApp().getGithub().getToken();


    /**
     * Make GET request
     */
    public static Object get(String uri) {

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HttpResponse<String> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "X-Token-XPipe", Helper.token
                    )
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            return responseBody.startsWith("{") ? new JSONObject(responseBody) : new JSONArray(responseBody);

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return new JSONObject("");
        }
    }


    /**
     * Make post request
     */
    public static Object post(String uri, Object data) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "X-Token-XPipe", Helper.token
                    )
                    .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = (String) response.body();
            return responseBody.startsWith("{") ? new JSONObject(responseBody) : new JSONArray(responseBody);

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
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "X-Token-XPipe", Helper.token
                    )
                    .PUT(HttpRequest.BodyPublishers.ofString(data.toString()))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                return new JSONObject("{}");
            }

            return new JSONObject(response.body().toString());

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return new JSONObject("");
        }
    }

    /**
     * Make patch request
     */
    public static JSONObject patch(String uri, Object data) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Helper.baseUrl + uri))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(data.toString()))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "X-Token-XPipe", Helper.token
                    )
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                return new JSONObject("{}");
            }

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
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "X-Token-XPipe", Helper.token
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