package com.example.myapp.model;

import org.json.JSONObject;

public class User {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isEmailVerified;
    private boolean isActive;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;

    public User() {
        // Default constructor
    }

    public User(String id, String email, String firstName, String lastName, 
                boolean isEmailVerified, boolean isActive) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isEmailVerified = isEmailVerified;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public boolean isEmailVerified() { return isEmailVerified; }
    public void setEmailVerified(boolean emailVerified) { isEmailVerified = emailVerified; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getFullName() {
        if (firstName == null && lastName == null) return "";
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName + " " + lastName;
    }

    // Parse from JSON
    public static User fromJson(JSONObject json) {
        if (json == null) return null;
        User user = new User();
        user.setId(json.optString("id", null));
        user.setEmail(json.optString("email", null));
        user.setFirstName(json.optString("first_name", null));
        user.setLastName(json.optString("last_name", null));
        user.setEmailVerified(json.optBoolean("is_email_verified", false));
        user.setActive(json.optBoolean("is_active", true));
        user.setLastLogin(json.optString("last_login", null));
        user.setCreatedAt(json.optString("created_at", null));
        user.setUpdatedAt(json.optString("updated_at", null));
        return user;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("email", email);
            json.put("first_name", firstName);
            json.put("last_name", lastName);
            json.put("is_email_verified", isEmailVerified);
            json.put("is_active", isActive);
            if (lastLogin != null) json.put("last_login", lastLogin);
            if (createdAt != null) json.put("created_at", createdAt);
            if (updatedAt != null) json.put("updated_at", updatedAt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
