package com.example.myapp.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonParser {
    
    public static JSONObject createRequest(String... keyValuePairs) {
        JSONObject json = new JSONObject();
        try {
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                if (i + 1 < keyValuePairs.length) {
                    json.put(keyValuePairs[i], keyValuePairs[i + 1]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getString(JSONObject json, String key) {
        try {
            return json.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public static boolean getBoolean(JSONObject json, String key, boolean defaultValue) {
        try {
            return json.getBoolean(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static int getInt(JSONObject json, String key, int defaultValue) {
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static long getLong(JSONObject json, String key, long defaultValue) {
        try {
            return json.getLong(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static String formatValidationErrors(JSONObject json) {
        if (json == null) return null;
        try {
            if (json.has("errors")) {
                org.json.JSONArray errors = json.getJSONArray("errors");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < errors.length(); i++) {
                    JSONObject error = errors.getJSONObject(i);
                    sb.append(error.optString("message", error.toString()));
                    if (i < errors.length() - 1) {
                        sb.append("\n");
                    }
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
