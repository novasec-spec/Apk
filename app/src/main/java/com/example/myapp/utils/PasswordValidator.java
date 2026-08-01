package com.example.myapp.utils;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {
    
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;

        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public String getErrorMessage() {
            return String.join("\n", errors);
        }

        public void addError(String error) {
            this.valid = false;
            this.errors.add(error);
        }
    }

    public static ValidationResult validate(String password) {
        ValidationResult result = new ValidationResult();
        
        if (password == null || password.isEmpty()) {
            result.addError("Password is required");
            return result;
        }

        if (password.length() < 8) {
            result.addError("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            result.addError("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            result.addError("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            result.addError("Password must contain at least one number");
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            result.addError("Password must contain at least one special character (@$!%*?&)");
        }

        return result;
    }

    public static ValidationResult validateEmail(String email) {
        ValidationResult result = new ValidationResult();
        
        if (email == null || email.isEmpty()) {
            result.addError("Email is required");
            return result;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            result.addError("Invalid email format");
        }

        return result;
    }
}
