package com.example.myapp.model;

import org.json.JSONObject;

public class AuthResponse {
    private User user;
    private String accessToken;
    private String refreshToken;

    public AuthResponse() {
        // Default constructor
    }

    public AuthResponse(User user, String accessToken, String refreshToken) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public boolean hasTokens() {
        return accessToken != null && !accessToken.isEmpty() &&
               refreshToken != null && !refreshToken.isEmpty();
    }

    // Parse from JSON
    public static AuthResponse fromJson(JSONObject json) {
        if (json == null) return null;
        AuthResponse response = new AuthResponse();
        
        JSONObject userJson = json.optJSONObject("user");
        if (userJson != null) {
            response.setUser(User.fromJson(userJson));
        }
        
        response.setAccessToken(json.optString("accessToken", null));
        response.setRefreshToken(json.optString("refreshToken", null));
        
        return response;
    }
}
