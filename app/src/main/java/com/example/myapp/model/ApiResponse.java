package com.example.myapp.model;

import org.json.JSONObject;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;
    private String[] errors;

    public ApiResponse() {
        // Default constructor
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public String[] getErrors() { return errors; }
    public void setErrors(String[] errors) { this.errors = errors; }

    public boolean hasData() {
        return data != null;
    }

    public String getErrorMessage() {
        if (error != null) return error;
        if (errors != null && errors.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String e : errors) {
                sb.append(e).append("\n");
            }
            return sb.toString().trim();
        }
        return message != null ? message : "Unknown error occurred";
    }

    // Parse from JSON
    public static ApiResponse<JSONObject> fromJson(String jsonString) {
        ApiResponse<JSONObject> response = new ApiResponse<>();
        try {
            JSONObject json = new JSONObject(jsonString);
            response.setSuccess(json.optBoolean("success", false));
            response.setMessage(json.optString("message", null));
            
            if (json.has("data")) {
                Object dataObj = json.get("data");
                if (dataObj instanceof JSONObject) {
                    response.setData((JSONObject) dataObj);
                }
            }
            
            if (json.has("error")) {
                response.setError(json.optString("error", null));
            }
            
            if (json.has("errors")) {
                org.json.JSONArray errorsArray = json.optJSONArray("errors");
                if (errorsArray != null) {
                    String[] errors = new String[errorsArray.length()];
                    for (int i = 0; i < errorsArray.length(); i++) {
                        JSONObject errorObj = errorsArray.optJSONObject(i);
                        if (errorObj != null) {
                            errors[i] = errorObj.optString("message", errorObj.toString());
                        } else {
                            errors[i] = errorsArray.optString(i);
                        }
                    }
                    response.setErrors(errors);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("Failed to parse response");
            response.setError(e.getMessage());
        }
        return response;
    }
}
