package com.example.myapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;
import com.example.myapp.api.GetProfileTask;
import com.example.myapp.api.LogoutAllTask;
import com.example.myapp.api.LogoutTask;
import com.example.myapp.api.RefreshTokenTask;
import com.example.myapp.api.UpdateProfileTask;
import com.example.myapp.api.VerifyEmailTask;
import com.example.myapp.auth.SessionManager;
import com.example.myapp.model.ApiResponse;
import com.example.myapp.model.User;
import com.example.myapp.utils.NetworkUtils;
import com.example.myapp.utils.PasswordValidator;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity 
        implements GetProfileTask.GetProfileCallback,
                   UpdateProfileTask.UpdateProfileCallback,
                   LogoutTask.LogoutCallback,
                   RefreshTokenTask.RefreshTokenCallback,
                   LogoutAllTask.LogoutAllCallback,
                   VerifyEmailTask.VerifyEmailCallback {
    
    private TextView tvEmail, tvUserId, tvVerificationStatus;
    private EditText etFirstName, etLastName, etCurrentPassword, etNewPassword;
    private Button btnUpdateProfile, btnChangePassword, btnVerifyEmail, 
                   btnLogout, btnLogoutAll, btnRefreshToken;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        // Initialize views
        tvEmail = findViewById(R.id.tvEmail);
        tvUserId = findViewById(R.id.tvUserId);
        tvVerificationStatus = findViewById(R.id.tvVerificationStatus);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogoutAll = findViewById(R.id.btnLogoutAll);
        btnRefreshToken = findViewById(R.id.btnRefreshToken);

        // Load profile
        loadProfile();

        // Setup click listeners
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        btnVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestVerification();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnLogoutAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAll();
            }
        });

        btnRefreshToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshToken();
            }
        });
    }

    private void loadProfile() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }



        showProgress("Loading profile...");
        new GetProfileTask(this, sessionManager.getAccessToken()).execute();
    }

    private void updateProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "No changes to update", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Updating profile...");
        new UpdateProfileTask(this, sessionManager.getAccessToken())
            .execute(firstName, lastName);
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        PasswordValidator.ValidationResult validation = 
            PasswordValidator.validate(newPassword);
        if (!validation.isValid()) {
            etNewPassword.setError(validation.getErrorMessage());
            etNewPassword.requestFocus();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Changing password...");
        new UpdateProfileTask(this, sessionManager.getAccessToken())
            .execute(currentPassword, newPassword);
    }

    private void requestVerification() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Sending verification email...");
        new VerifyEmailTask(this, sessionManager.getAccessToken()).execute();
    }

    private void logout() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Logging out...");
        new LogoutTask(this, sessionManager.getAccessToken(), 
                       sessionManager.getRefreshToken()).execute();
    }

    private void logoutAll() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Logging out from all devices...");
        new LogoutAllTask(this, sessionManager.getAccessToken()).execute();
    }

    private void refreshToken() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Refreshing token...");
        new RefreshTokenTask(this, sessionManager.getAccessToken(), 
                             sessionManager.getRefreshToken()).execute();
    }

    @Override
    public void onGetProfileSuccess(User user) {
        hideProgress();
        this.currentUser = user;
        
        tvEmail.setText("Email: " + user.getEmail());
        tvUserId.setText("User ID: " + user.getId());
        tvVerificationStatus.setText("Verified: " + (user.isEmailVerified() ? "✅" : "❌"));
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());
        
        // Update session
        sessionManager.updateUserInfo(user.getFirstName(), user.getLastName(), 
                                     user.isEmailVerified());
    }

    @Override
    public void onGetProfileError(String error) {
        hideProgress();
        Toast.makeText(this, "Failed to load profile: " + error, Toast.LENGTH_LONG).show();
    }


@Override
public void onGetProfileNetworkError(String error) {
    runOnUiThread(() ->
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    );
}


    @Override
    public void onUpdateProfileSuccess(User user) {
        hideProgress();
        this.currentUser = user;
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        
        // Update session
        sessionManager.updateUserInfo(user.getFirstName(), user.getLastName(), 
                                     user.isEmailVerified());
        
        // Clear password fields
        etCurrentPassword.setText("");
        etNewPassword.setText("");
    }

    @Override
    public void onUpdateProfileError(String error) {
        hideProgress();
        Toast.makeText(this, "Failed to update profile: " + error, Toast.LENGTH_LONG).show();
    }

@Override
public void onUpdateProfileNetworkError(String error) {
    hideProgress();
    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
}
    @Override
    public void onVerifyEmailSuccess(String message) {
        hideProgress();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        loadProfile(); // Refresh profile
    }

    @Override
    public void onVerifyEmailError(String error) {
        hideProgress();
        Toast.makeText(this, "Failed to send verification: " + error, Toast.LENGTH_LONG).show();
    }

@Override
public void onVerifyEmailNetworkError(String error) {
    hideProgress();
    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
}
    @Override
     public void onLogoutSuccess(String message) {
        hideProgress();
        sessionManager.logoutUser();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    @Override
    public void onLogoutError(String error) {
        hideProgress();
        Toast.makeText(this, "Logout error: " + error, Toast.LENGTH_LONG).show();
    }

@Override
public void onLogoutNetworkError(String error) {
    hideProgress();
    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
}
    @Override
    public void onLogoutAllSuccess(String message) {
        hideProgress();
        sessionManager.logoutUser();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    @Override
    public void onLogoutAllError(String error) {
        hideProgress();
        Toast.makeText(this, "Logout all error: " + error, Toast.LENGTH_LONG).show();
    }

@Override
public void onLogoutAllNetworkError(String error) {
    hideProgress();
    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
}

    @Override
    public void onRefreshTokenSuccess(String newAccessToken, String newRefreshToken) {
        hideProgress();
        sessionManager.updateTokens(newAccessToken, newRefreshToken);
        Toast.makeText(this, "Token refreshed successfully", Toast.LENGTH_SHORT).show();
        loadProfile(); // Reload profile with new token
    }

    @Override
    public void onRefreshTokenError(String error) {
        hideProgress();
        Toast.makeText(this, "Failed to refresh token: " + error, Toast.LENGTH_LONG).show();
    }


@Override
public void onRefreshTokenNetworkError(String error) {
    hideProgress();
    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
}


    private void showProgress(String message) { if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
