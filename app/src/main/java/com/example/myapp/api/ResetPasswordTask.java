package com.example.myapp.api;

import android.os.AsyncTask;

import com.example.myapp.model.ApiResponse;
import com.example.myapp.utils.Constants;
import com.example.myapp.utils.JsonParser;
import com.example.myapp.utils.NetworkUtils;
import org.json.JSONObject;

public class ResetPasswordTask extends AsyncTask<String, Void, String> {
    
    public interface ResetPasswordCallback {
        void onResetPasswordSuccess(String message);
        void onResetPasswordError(String error);
        void onResetPasswordNetworkError(String error);
    }

    private ResetPasswordCallback callback;
    private Exception exception;

    public ResetPasswordTask(ResetPasswordCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String token = params[0];
        String newPassword = params[1];

        try {
            JSONObject request = JsonParser.createRequest(
                "token", token,
                "newPassword", newPassword
            );

            String url = Constants.BASE_URL + Constants.API_EMAIL_RESET_PASSWORD;
            return NetworkUtils.makeRequest(url, "POST", request.toString(), null);
        } catch (Exception e) {
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback == null) return;

        if (result == null) {
            callback.onResetPasswordNetworkError(exception != null ? 
                exception.getMessage() : "Unknown network error");
            return;
        }

        ApiResponse<JSONObject> response = ApiResponse.fromJson(result);
        
        if (!response.isSuccess()) {
            callback.onResetPasswordError(response.getErrorMessage());
            return;
        }

        callback.onResetPasswordSuccess(response.getMessage());
    }
}
