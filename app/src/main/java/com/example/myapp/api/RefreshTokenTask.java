package com.example.myapp.api;

import android.os.AsyncTask;

import com.example.myapp.model.ApiResponse;
import com.example.myapp.utils.Constants;
import com.example.myapp.utils.JsonParser;
import com.example.myapp.utils.NetworkUtils;

import org.json.JSONObject;

public class RefreshTokenTask extends AsyncTask<Void, Void, String> {
    
    public interface RefreshTokenCallback {
        void onRefreshTokenSuccess(String newAccessToken, String newRefreshToken);
        void onRefreshTokenError(String error);
        void onRefreshTokenNetworkError(String error);
    }

    private RefreshTokenCallback callback;
    private String accessToken;
    private String refreshToken;
    private Exception exception;

    public RefreshTokenTask(RefreshTokenCallback callback, 
                           String accessToken, String refreshToken) {
        this.callback = callback;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONObject request = JsonParser.createRequest("refreshToken", refreshToken);
            String url = Constants.BASE_URL + Constants.API_AUTH_REFRESH;
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
            callback.onRefreshTokenNetworkError(exception != null ? 
                exception.getMessage() : "Unknown network error");
            return;
        }

        ApiResponse<JSONObject> response = ApiResponse.fromJson(result);
        
        if (!response.isSuccess()) {
            callback.onRefreshTokenError(response.getErrorMessage());
            return;
        }

        JSONObject data = response.getData();
        String newAccessToken = data.optString("accessToken", null);
        String newRefreshToken = data.optString("refreshToken", null);

        if (newAccessToken != null && newRefreshToken != null) {
            callback.onRefreshTokenSuccess(newAccessToken, newRefreshToken);
        } else {
            callback.onRefreshTokenError("Invalid token response");
        }
    }
}
