package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github;

import org.json.JSONArray;
import org.sindria.xpipe.lib.nanoREST.config.AppConfig;
import org.sindria.xpipe.lib.nanoREST.helpers.BaseHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.BitbucketHelper;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.GithubHelper;

public class GithubHelper extends BaseHelper {

    /**
     * baseUrl
     */
    protected static String baseUrl = "https://api.github.com";

    /**
     * token
     */
    protected static String token = AppConfig.config.getApp().getGithub().getToken();


    /**
     * Make GET request
     */
    public static Object get(String uri) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(GithubHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Bearer " + GithubHelper.token
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
    public static JSONObject post(String uri, Object data) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(GithubHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Bearer " + GithubHelper.token
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
                    .uri(new URI(GithubHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Bearer " + GithubHelper.token
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
     * Make patch request
     */
    public static JSONObject patch(String uri, Object data) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(GithubHelper.baseUrl + uri))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(data.toString()))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Bearer " + GithubHelper.token
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
                    .uri(new URI(GithubHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Bearer " + GithubHelper.token
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