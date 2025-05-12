package org.sindria.xpipe.lib.blog;

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
    protected static String baseUrl = "https://dp-fit-prod-function.azurewebsites.net";

    /**
     * Make get request
     */
    public static JSONObject get(String uri, String origin) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Origin", origin,
                            "Sec-Fetch-Site", "cross-site",
                            "Sec-Fetch-Mode", "cors",
                            "Sec-Fetch-Dest", "empty",
                            "Referer", origin
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
    public static JSONObject post(String uri, String origin, Object data) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<?> response = null;

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Helper.baseUrl + uri))
                    .version(HttpClient.Version.HTTP_2)
                    .headers(
                            "Content-Type", "application/json",
                            "Origin", origin,
                            "Sec-Fetch-Site", "cross-site",
                            "Sec-Fetch-Mode", "cors",
                            "Sec-Fetch-Dest", "empty",
                            "Referer", origin
                    )
                    .POST(HttpRequest.BodyPublishers.ofString((String) data))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                return new JSONObject("{\"competizioni\": [] }");
            }

            return new JSONObject(response.body().toString());

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            return new JSONObject("{\"competizioni\": [] }");
        }
    }


    /**
     * Get only competitions of Arzachena
     */
    public static JSONArray cleanCompetitions(JSONObject competitions) {

        String matchCountry = "Arzachena";
        JSONArray competitionsCleaned = new JSONArray();

        JSONArray collection = (JSONArray) competitions.get("competizioni");

        for (int i = 0; i < collection.length(); i++) {
            var value = collection.getJSONObject(i);

            String currentCountry = (String) value.get("citta");

            if (currentCountry.equals(matchCountry)) {
                competitionsCleaned.put(i, value);
            }
        }


//        for (int i = 0; i < competitionsCleaned.length(); i++) {
//            var entry = competitionsCleaned.getJSONObject(i);
//
//            if (entry == null) {
//                System.out.println("nullo");
//                continue;
//            }
//
//            System.out.println("entry");
////            String currentCountry = (String) value.get("citta");
////
////            if (currentCountry.equals(matchCountry)) {
////                competitionsCleaned.put(i, value);
////            }
//        }

        return competitionsCleaned;
    }
}