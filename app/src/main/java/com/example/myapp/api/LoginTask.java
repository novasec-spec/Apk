package com.example.myapp.api;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginTask extends AsyncTask<String, Void, String> {

    public interface Callback {
        void onResult(String result);
    }

    private final Callback callback;

    public LoginTask(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            URL url = new URL(ApiClient.BASE_URL + "/auth/login");

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );

            JSONObject json = new JSONObject();

            json.put("email", params[0]);
            json.put("password", params[1]);

            BufferedWriter writer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    conn.getOutputStream()
                            )
                    );

            writer.write(json.toString());
            writer.flush();
            writer.close();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()
                            )
                    );

            StringBuilder response =
                    new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {

                response.append(line);

            }

            reader.close();

            return response.toString();

        } catch (Exception e) {

            return "{\"success\":false,\"message\":\""
                    + e.getMessage() + "\"}";

        }

    }

    @Override
    protected void onPostExecute(String result) {

        callback.onResult(result);

    }

}
