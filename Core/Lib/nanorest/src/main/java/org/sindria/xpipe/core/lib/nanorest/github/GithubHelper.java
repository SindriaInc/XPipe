package org.sindria.xpipe.core.lib.nanorest.github;

import org.json.JSONArray;
import org.sindria.xpipe.core.lib.nanorest.config.AppConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

public class GithubHelper {

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

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HttpResponse<String> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(GithubHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "X-GitHub-Api-Version", "2022-11-28",
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

    public static String getForLogs(String uri) {
        try {

            HttpClient client = HttpClient.newBuilder()
                    .build();


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GithubHelper.baseUrl + uri))
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .header("Authorization", "Bearer " + GithubHelper.token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 302) {

                String redirectUrl = response.headers().firstValue("Location").orElseThrow();
                System.out.println("Redirecting to: " + redirectUrl);

                HttpRequest redirectedRequest = HttpRequest.newBuilder()
                        .uri(URI.create(redirectUrl))
                        .GET()
                        .build();

                response = client.send(redirectedRequest, HttpResponse.BodyHandlers.ofString());
            }
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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
                            "X-GitHub-Api-Version", "2022-11-28",
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
                            "X-GitHub-Api-Version", "2022-11-28",
                            "Authorization", "Bearer " + GithubHelper.token
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
                    .uri(new URI(GithubHelper.baseUrl + uri))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(data.toString()))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "X-GitHub-Api-Version", "2022-11-28",
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
                            "X-GitHub-Api-Version", "2022-11-28",
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

    /**
     * Download file
     */
    public static JSONObject downloadFile(String uri, String filePath) {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(GithubHelper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Authorization", "Bearer " + GithubHelper.token
                    )
                    .GET()
                    .build();

            HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(filePath)));

            System.out.print(response.headers().toString());
            if (response.statusCode() == 200) {
                System.out.println("File downloaded successfully: " + filePath);
                return new JSONObject(response.body());
            } else {
                System.out.println("Failed to download file. HTTP Status: " + response.statusCode());
                return new JSONObject(response.body());
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return new JSONObject("");
        }
    }

}