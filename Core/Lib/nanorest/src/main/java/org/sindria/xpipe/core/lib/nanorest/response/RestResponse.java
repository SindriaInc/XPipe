package org.sindria.xpipe.core.lib.nanorest.response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public final class RestResponse {

    public final int code;

    public final boolean success;

    public final String message;

    public final HashMap<String, Object> data;


    public RestResponse(int code, boolean success, String message, HashMap<String, Object> data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean getSuccess() {
        return success;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public JSONObject serialize() throws JSONException {
        try {
            return new JSONObject(this);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new JSONException(e);
        }
    }


}
