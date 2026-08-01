package com.example.myapp.api;

import android.os.AsyncTask;

import com.example.myapp.model.ApiResponse;
import com.example.myapp.model.User;
import com.example.myapp.utils.Constants;
import com.example.myapp.utils.JsonParser;
import com.example.myapp.utils.NetworkUtils;

import org.json.JSONObject;

public class UpdateProfileTask extends AsyncTask<String, Void, String> {
    
    public interface UpdateProfileCallback {
        void onUpdateProfileSuccess(User user);
        void onUpdateProfileError(String error);
        void onUpdateProfileNetworkError(String error);
    }

    private UpdateProfileCallback callback;
    private String accessToken;
    private Exception exception;

    public UpdateProfileTask(UpdateProfileCallback callback, String accessToken) {
        this.callback = callback;
        this.accessToken = accessToken;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            JSONObject request = new JSONObject();
            
            // Check if updating profile or password
            if (params.length == 2 && params[0] != null && params[1] != null) {
                // Password change
                request.put("currentPassword", params[0]);
                request.put("newPassword", params[1]);
            } else {
                // Profile update
                String firstName = params[0];
                String lastName = params.length > 1 ? params[1] : "";
                if (firstName != null && !firstName.isEmpty()) {
                    request.put("firstName", firstName);
                }
                if (lastName != null && !lastName.isEmpty()) {
                    request.put("lastName", lastName);
                }
            }

            String url = Constants.BASE_URL + Constants.API_USERS_ME;
            return NetworkUtils.makeRequest(url, "PATCH", request.toString(), accessToken);
        } catch (Exception e) {
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback == null) return;

        if (result == null) {
            callback.onUpdateProfileNetworkError(exception != null ? 
                exception.getMessage() : "Unknown network error");
            return;
        }

        ApiResponse<JSONObject> response = ApiResponse.fromJson(result);
        
        if (!response.isSuccess()) {
            callback.onUpdateProfileError(response.getErrorMessage());
            return;
        }

        JSONObject data = response.getData();
        if (data.has("user")) {
            JSONObject userJson = data.optJSONObject("user");
            User user = User.fromJson(userJson);
            if (user != null) {
                callback.onUpdateProfileSuccess(user);
            } else {
                callback.onUpdateProfileError("Failed to parse user data");
            }
        } else {
            callback.onUpdateProfileError("Invalid response format");
        }
    }
}
