package org.sindria.xpipe.core.lib.nanorest.facade;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.helper.HttpHelper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

class HttpFacade {
    private static HttpHelper httpHelper;

    public static void initialize(String baseUrl, String authType, String username, String password) {
        httpHelper = new HttpHelper(baseUrl, authType, username, password);
    }

    public static JSONObject get(String uri, Map<String, String> headers) throws IOException, InterruptedException, URISyntaxException {
        return httpHelper.get(uri, headers);
    }

    public static JSONObject post(String uri, Object data, Map<String, String> headers) throws IOException, InterruptedException, URISyntaxException {
        return httpHelper.post(uri, data, headers);
    }

    public static JSONObject put(String uri, Object data, Map<String, String> headers) throws IOException, InterruptedException, URISyntaxException {
        return httpHelper.put(uri, data, headers);
    }

    public static JSONObject delete(String uri, Map<String, String> headers) throws IOException, InterruptedException, URISyntaxException {
        return httpHelper.delete(uri, headers);
    }
}
