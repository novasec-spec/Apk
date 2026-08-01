package com.example.myapp.api;

import android.os.AsyncTask;

import com.example.myapp.model.ApiResponse;
import com.example.myapp.utils.Constants;
import com.example.myapp.utils.JsonParser;
import com.example.myapp.utils.NetworkUtils;
import org.json.JSONObject;

public class ForgotPasswordTask extends AsyncTask<String, Void, String> {
    
    public interface ForgotPasswordCallback {
        void onForgotPasswordSuccess(String message);
        void onForgotPasswordError(String error);
        void onForgotPasswordNetworkError(String error);
    }

    private ForgotPasswordCallback callback;
    private Exception exception;

    public ForgotPasswordTask(ForgotPasswordCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String email = params[0];

        try {
            JSONObject request = JsonParser.createRequest("email", email);
            String url = Constants.BASE_URL + Constants.API_EMAIL_FORGOT_PASSWORD;
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
            callback.onForgotPasswordNetworkError(exception != null ? 
                exception.getMessage() : "Unknown network error");
            return;
        }

        ApiResponse<JSONObject> response = ApiResponse.fromJson(result);
        
        if (!response.isSuccess()) {
            callback.onForgotPasswordError(response.getErrorMessage());
            return;
        }

        callback.onForgotPasswordSuccess(response.getMessage());
    }
}
