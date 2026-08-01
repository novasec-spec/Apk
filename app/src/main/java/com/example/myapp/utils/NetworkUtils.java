package com.example.myapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String makeRequest(String urlString, String method, String body, String accessToken) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(Constants.TIMEOUT_CONNECT);
            connection.setReadTimeout(Constants.TIMEOUT_READ);
            connection.setRequestProperty(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
            connection.setRequestProperty(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_JSON);
            connection.setRequestProperty(Constants.HEADER_USER_AGENT, "MyApp-Android/1.0");
            
            if (accessToken != null && !accessToken.isEmpty()) {
                connection.setRequestProperty(Constants.HEADER_AUTHORIZATION, 
                    Constants.HEADER_BEARER + accessToken);
            }

            if (body != null && !body.isEmpty() && 
                (method.equals("POST") || method.equals("PUT") || method.equals("PATCH"))) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.getBytes("UTF-8"));
                    os.flush();
                }
            }

            int responseCode = connection.getResponseCode();
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode == 401) {
                // Token expired - handle refresh
                return "{\"success\":false,\"message\":\"Token expired\",\"error\":\"UNAUTHORIZED\"}";
            }

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\":false,\"message\":\"Network error: " + e.getMessage() + "\"}";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
