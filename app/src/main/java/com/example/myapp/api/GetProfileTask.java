package com.example.myapp.api;

import android.os.AsyncTask;

import com.example.myapp.model.ApiResponse;
import com.example.myapp.model.User;
import com.example.myapp.utils.Constants;
import com.example.myapp.utils.NetworkUtils;

import org.json.JSONObject;

public class GetProfileTask extends AsyncTask<Void, Void, String> {
    
    public interface GetProfileCallback {
        void onGetProfileSuccess(User user);
        void onGetProfileError(String error);
        void onGetProfileNetworkError(String error);
    }

    private GetProfileCallback callback;
    private String accessToken;
    private Exception exception;

    public GetProfileTask(GetProfileCallback callback, String accessToken) {
        this.callback = callback;
        this.accessToken = accessToken;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String url = Constants.BASE_URL + Constants.API_USERS_ME;
            return NetworkUtils.makeRequest(url, "GET", null, accessToken);
        } catch (Exception e) {
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback == null) return;

        if (result == null) {
            callback.onGetProfileNetworkError(exception != null ? 
                exception.getMessage() : "Unknown network error");
            return;
        }

        ApiResponse<JSONObject> response = ApiResponse.fromJson(result);
        
        if (!response.isSuccess()) {
            callback.onGetProfileError(response.getErrorMessage());
            return;
        }

        JSONObject data = response.getData();
        if (data.has("user")) {
            JSONObject userJson = data.optJSONObject("user");
            User user = User.fromJson(userJson);
            if (user != null) {
                callback.onGetProfileSuccess(user);
            } else {
                callback.onGetProfileError("Failed to parse user data");
            }
        } else {
            callback.onGetProfileError("Invalid response format");
        }
    }
}
