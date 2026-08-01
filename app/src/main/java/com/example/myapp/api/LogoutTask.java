package com.example.myapp.api;

import android.os.AsyncTask;

import com.example.myapp.model.ApiResponse;
import com.example.myapp.utils.Constants;
import com.example.myapp.utils.JsonParser;
import com.example.myapp.utils.NetworkUtils;
import org.json.JSONObject;

public class LogoutTask extends AsyncTask<Void, Void, String> {
    
    public interface LogoutCallback {
        void onLogoutSuccess(String message);
        void onLogoutError(String error);
        void onLogoutNetworkError(String error);
    }

    private LogoutCallback callback;
    private String accessToken;
    private String refreshToken;
    private Exception exception;

    public LogoutTask(LogoutCallback callback, String accessToken, String refreshToken) {
        this.callback = callback;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONObject request = JsonParser.createRequest("refreshToken", refreshToken);
            String url = Constants.BASE_URL + Constants.API_AUTH_LOGOUT;
            return NetworkUtils.makeRequest(url, "POST", request.toString(), accessToken);
        } catch (Exception e) {
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback == null) return;

        if (result == null) {
            callback.onLogoutNetworkError(exception != null ? 
                exception.getMessage() : "Unknown network error");
            return;
        }

        ApiResponse<JSONObject> response = ApiResponse.fromJson(result);
        
        if (!response.isSuccess()) {
            callback.onLogoutError(response.getErrorMessage());
            return;
        }

        callback.onLogoutSuccess(response.getMessage());
    }
}
