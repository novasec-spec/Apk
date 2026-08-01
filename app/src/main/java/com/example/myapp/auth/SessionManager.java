package com.example.myapp.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapp.utils.Constants;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String email, String firstName, 
                                   String lastName, boolean isVerified,
                                   String accessToken, String refreshToken, 
                                   boolean rememberMe) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putString(Constants.KEY_USER_FIRST_NAME, firstName);
        editor.putString(Constants.KEY_USER_LAST_NAME, lastName);
        editor.putBoolean(Constants.KEY_USER_IS_VERIFIED, isVerified);
        editor.putString(Constants.KEY_ACCESS_TOKEN, accessToken);
        editor.putString(Constants.KEY_REFRESH_TOKEN, refreshToken);
        editor.putBoolean(Constants.KEY_REMEMBER_ME, rememberMe);
        editor.apply();
    }

    public void updateTokens(String accessToken, String refreshToken) {
        editor.putString(Constants.KEY_ACCESS_TOKEN, accessToken);
        editor.putString(Constants.KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public void updateUserInfo(String firstName, String lastName, boolean isVerified) {
        if (firstName != null) {
            editor.putString(Constants.KEY_USER_FIRST_NAME, firstName);
        }
        if (lastName != null) {
            editor.putString(Constants.KEY_USER_LAST_NAME, lastName);
        }
        editor.putBoolean(Constants.KEY_USER_IS_VERIFIED, isVerified);
        editor.apply();
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getAccessToken() {
        return pref.getString(Constants.KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return pref.getString(Constants.KEY_REFRESH_TOKEN, null);
    }

    public String getUserEmail() {
        return pref.getString(Constants.KEY_USER_EMAIL, null);
    }

    public String getUserId() {
        return pref.getString(Constants.KEY_USER_ID, null);
    }

    public String getFirstName() {
        return pref.getString(Constants.KEY_USER_FIRST_NAME, null);
    }

    public String getLastName() {
        return pref.getString(Constants.KEY_USER_LAST_NAME, null);
    }

    public boolean isEmailVerified() {
        return pref.getBoolean(Constants.KEY_USER_IS_VERIFIED, false);
    }

    public boolean getRememberMe() {
        return pref.getBoolean(Constants.KEY_REMEMBER_ME, false);
    }

    public String getFullName() {
        String firstName = getFirstName();
        String lastName = getLastName();
        if (firstName == null && lastName == null) return "";
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName + " " + lastName;
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
