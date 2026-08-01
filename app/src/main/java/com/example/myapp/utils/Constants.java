package com.example.myapp.utils;

public class Constants {
    // API Endpoints
    public static final String BASE_URL = "https://mybackendapp-v8b0.onrender.com"; // For Android emulator
    // public static final String BASE_URL = "http://localhost:3000"; // For physical device
    
    public static final String API_AUTH_REGISTER = "/api/auth/register";
    public static final String API_AUTH_LOGIN = "/api/auth/login";
    public static final String API_AUTH_REFRESH = "/api/auth/refresh";
    public static final String API_AUTH_LOGOUT = "/api/auth/logout";
    public static final String API_AUTH_LOGOUT_ALL = "/api/auth/logout-all";
    
    public static final String API_USERS_ME = "/api/users/me";
    
    public static final String API_EMAIL_VERIFY_REQUEST = "/api/email/verify-email/request";
    public static final String API_EMAIL_VERIFY = "/api/email/verify-email";
    public static final String API_EMAIL_FORGOT_PASSWORD = "/api/email/forgot-password";
    public static final String API_EMAIL_RESET_PASSWORD = "/api/email/reset-password";
    
    public static final String API_HEALTH = "/health";
    
    // Shared Preferences
    public static final String PREF_NAME = "MyAppPrefs";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_FIRST_NAME = "user_first_name";
    public static final String KEY_USER_LAST_NAME = "user_last_name";
    public static final String KEY_USER_IS_VERIFIED = "user_is_verified";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_REMEMBER_ME = "remember_me";
    
    // HTTP Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BEARER = "Bearer ";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_USER_AGENT = "User-Agent";
    
    // Content Types
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    // Timeouts (in milliseconds)
    public static final int TIMEOUT_CONNECT = 15000;
    public static final int TIMEOUT_READ = 15000;
    
    // Request Codes
    public static final int REQUEST_CODE_LOGIN = 1001;
    public static final int REQUEST_CODE_REGISTER = 1002;
    public static final int REQUEST_CODE_FORGOT_PASSWORD = 1003;
    public static final int REQUEST_CODE_RESET_PASSWORD = 1004;
    public static final int REQUEST_CODE_PROFILE = 1005;
}
