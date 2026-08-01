package com.example.myapp.api;

import android.os.AsyncTask;

import com.example.myapp.model.ApiResponse;
import com.example.myapp.utils.Constants;
import com.example.myapp.utils.NetworkUtils;
import org.json.JSONObject;

public class VerifyEmailTask extends AsyncTask<Void, Void, String> {
    
    public interface VerifyEmailCallback {
        void onVerifyEmailSuccess(String message);
        void onVerifyEmailError(String error);
        void onVerifyEmailNetworkError(String error);
    }

    private VerifyEmailCallback callback;
    private String accessToken;
    private Exception exception;

    public VerifyEmailTask(VerifyEmailCallback callback, String accessToken) {
        this.callback = callback;
        this.accessToken = accessToken;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String url = Constants.BASE_URL + Constants.API_EMAIL_VERIFY_REQUEST;
            return NetworkUtils.makeRequest(url, "POST", null, accessToken);
        } catch (Exception e) {
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback == null) return;

        if (result == null) {
            callback.onVerifyEmailNetworkError(exception != null ? 
                exception.getMessage() : "Unknown network error");
            return;
        }

        ApiResponse<JSONObject> response = ApiResponse.fromJson(result);
        
        if (!response.isSuccess()) {
            callback.onVerifyEmailError(response.getErrorMessage());
            return;
        }

        callback.onVerifyEmailSuccess(response.getMessage());
    }
}
