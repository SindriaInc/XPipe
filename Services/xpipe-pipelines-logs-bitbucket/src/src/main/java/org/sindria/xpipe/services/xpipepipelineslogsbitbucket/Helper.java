package org.sindria.xpipe.services.xpipepipelineslogsbitbucket;

import org.sindria.xpipe.lib.nanoREST.helpers.BaseHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class Helper extends BaseHelper {

    /**
     * baseUrl
     */
    protected static String baseUrl = "https://api.bitbucket.org/2.0";

    protected static String encodedAuth = "";

    /**
     * Make get request
     */
    public static JSONObject get(String uri) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {
            System.out.println(new URI(Helper.baseUrl + uri).toString());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Basic " + Helper.encodedAuth
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
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Basic " + Helper.encodedAuth
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
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Basic " + Helper.encodedAuth
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
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Authorization", "Basic " + Helper.encodedAuth
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