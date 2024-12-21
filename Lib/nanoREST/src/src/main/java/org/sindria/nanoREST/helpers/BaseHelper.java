package org.sindria.nanoREST.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.sindria.nanoREST.requests.Request;

public class BaseHelper {

    public static JSONObject rd(Request object) {
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().serializeNulls().create();
        var obj = gson.toJson(object);
        System.out.println(obj);
        return new JSONObject(obj);

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        var obj = gson.toJson(object);
//        System.out.println(obj);
//        return new JSONObject(obj);
    }


}
