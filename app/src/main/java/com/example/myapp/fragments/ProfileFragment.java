package com.example.myapp.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapp.R;
import com.example.myapp.activities.LoginActivity;
import com.example.myapp.activities.ProfileActivity;
import com.example.myapp.api.GetProfileTask;
import com.example.myapp.api.LogoutAllTask;
import com.example.myapp.api.LogoutTask;
import com.example.myapp.api.RefreshTokenTask;
import com.example.myapp.api.UpdateProfileTask;
import com.example.myapp.api.VerifyEmailTask;
import com.example.myapp.auth.SessionManager;
import com.example.myapp.model.User;
import com.example.myapp.utils.NetworkUtils;
import com.example.myapp.utils.PasswordValidator;

public class ProfileFragment extends Fragment implements
        GetProfileTask.GetProfileCallback,
        UpdateProfileTask.UpdateProfileCallback,
        LogoutTask.LogoutCallback,
        LogoutAllTask.LogoutAllCallback,
        VerifyEmailTask.VerifyEmailCallback,
        RefreshTokenTask.RefreshTokenCallback {

    // UI Components
    private TextView tvUserName, tvUserEmail, tvVerificationStatus;
    private LinearLayout layoutEditProfile, layoutChangePassword, layoutVerifyEmail,
            layoutRefreshToken, layoutLogout, layoutLogoutAll, layoutDeleteAccount;
    private SwitchCompat switchNotifications, switchTheme;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupClickListeners();

        sessionManager = new SessionManager(getContext());

        // Load user data
        loadUserData();

        return view;
    }

    private void initViews(View view) {
        // Header
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvVerificationStatus = view.findViewById(R.id.tvVerificationStatus);

        // Menu Items
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutVerifyEmail = view.findViewById(R.id.layoutVerifyEmail);
        layoutRefreshToken = view.findViewById(R.id.layoutRefreshToken);
        layoutLogout = view.findViewById(R.id.layoutLogout);
        layoutLogoutAll = view.findViewById(R.id.layoutLogoutAll);
        layoutDeleteAccount = view.findViewById(R.id.layoutDeleteAccount);

        // Switches
        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchTheme = view.findViewById(R.id.switchTheme);
    }

    private void setupClickListeners() {
        layoutEditProfile.setOnClickListener(v -> showEditProfileDialog());

        layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        layoutVerifyEmail.setOnClickListener(v -> requestVerification());

        layoutRefreshToken.setOnClickListener(v -> refreshToken());

        layoutLogout.setOnClickListener(v -> logout());

        layoutLogoutAll.setOnClickListener(v -> logoutAll());

        layoutDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());

        // Switch listeners
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(getContext(), 
                isChecked ? "Notifications enabled" : "Notifications disabled", 
                Toast.LENGTH_SHORT).show();
        });

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(getContext(), 
                isChecked ? "Dark theme enabled" : "Light theme enabled", 
                Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        if (sessionManager.isLoggedIn()) {
            // Load from session first
            tvUserName.setText(sessionManager.getFullName());
            tvUserEmail.setText(sessionManager.getUserEmail());
            updateVerificationStatus(sessionManager.isEmailVerified());

            // Then fetch fresh data from server
            if (NetworkUtils.isNetworkAvailable(getContext())) {
                new GetProfileTask(this, sessionManager.getAccessToken()).execute();
            }
        }
    }

    private void updateVerificationStatus(boolean isVerified) {
        if (isVerified) {
            tvVerificationStatus.setText("Verified");
            tvVerificationStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvVerificationStatus.setText("Not Verified");
            tvVerificationStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Profile");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        final android.widget.EditText etFirstName = view.findViewById(R.id.etFirstName);
        final android.widget.EditText etLastName = view.findViewById(R.id.etLastName);

        // Pre-fill current data
        if (currentUser != null) {
            etFirstName.setText(currentUser.getFirstName());
            etLastName.setText(currentUser.getLastName());
        } else {
            etFirstName.setText(sessionManager.getFirstName());
            etLastName.setText(sessionManager.getLastName());
        }

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();

            if (firstName.isEmpty() && lastName.isEmpty()) {
                Toast.makeText(getContext(), "No changes to update", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!NetworkUtils.isNetworkAvailable(getContext())) {
                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                return;
            }

            showProgress("Updating profile...");
            new UpdateProfileTask(this, sessionManager.getAccessToken())
                    .execute(firstName, lastName);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Password");

        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        final android.widget.EditText etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        final android.widget.EditText etNewPassword = view.findViewById(R.id.etNewPassword);
        final android.widget.EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        builder.setView(view);
        builder.setPositiveButton("Change", (dialog, which) -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (currentPassword.isEmpty()) {
                Toast.makeText(getContext(), "Current password is required", Toast.LENGTH_SHORT).show();
                return;
            }

            PasswordValidator.ValidationResult validation = 
                PasswordValidator.validate(newPassword);
            if (!validation.isValid()) {
                Toast.makeText(getContext(), validation.getErrorMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                return;
            }

            if (!NetworkUtils.isNetworkAvailable(getContext())) {
                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                return;
            }

            showProgress("Changing password...");
            new UpdateProfileTask(this, sessionManager.getAccessToken())
                    .execute(currentPassword, newPassword);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void requestVerification() {
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Sending verification email...");
        new VerifyEmailTask(this, sessionManager.getAccessToken()).execute();
    }

    private void refreshToken() {
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Refreshing token...");
        new RefreshTokenTask(this, sessionManager.getAccessToken(),
                sessionManager.getRefreshToken()).execute();
    }

    private void logout() {
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    showProgress("Logging out...");
                    new LogoutTask(this, sessionManager.getAccessToken(),
                            sessionManager.getRefreshToken()).execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logoutAll() {
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Logout All Devices")
                .setMessage("Are you sure you want to logout from all devices?")
                .setPositiveButton("Logout All", (dialog, which) -> {
                    showProgress("Logging out from all devices...");
                    new LogoutAllTask(this, sessionManager.getAccessToken()).execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone!")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Toast.makeText(getContext(), 
                        "Please contact support to delete your account", 
                        Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ========== Callback Implementations ==========

    @Override
    public void onGetProfileSuccess(User user) {
        hideProgress();
        this.currentUser = user;

        tvUserName.setText(user.getFullName());
        tvUserEmail.setText(user.getEmail());
        updateVerificationStatus(user.isEmailVerified());

        // Update session
        sessionManager.updateUserInfo(
            user.getFirstName(),
            user.getLastName(),
            user.isEmailVerified()
        );
    }

    @Override
    public void onGetProfileError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Failed to load profile: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetProfileNetworkError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Network error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdateProfileSuccess(User user) {
        hideProgress();
        this.currentUser = user;

        tvUserName.setText(user.getFullName());
        updateVerificationStatus(user.isEmailVerified());

        // Update session
        sessionManager.updateUserInfo(
            user.getFirstName(),
            user.getLastName(),
            user.isEmailVerified()
        );

        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateProfileError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Failed to update profile: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdateProfileNetworkError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Network error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVerifyEmailSuccess(String message) {
        hideProgress();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        loadUserData(); // Refresh data
    }

    @Override
    public void onVerifyEmailError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Failed to send verification: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVerifyEmailNetworkError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Network error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRefreshTokenSuccess(String newAccessToken, String newRefreshToken) {
        hideProgress();
        sessionManager.updateTokens(newAccessToken, newRefreshToken);
        Toast.makeText(getContext(), "Token refreshed successfully", Toast.LENGTH_SHORT).show();
        loadUserData(); // Reload profile with new token
    }

    @Override
    public void onRefreshTokenError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Failed to refresh token: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRefreshTokenNetworkError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Network error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogoutSuccess(String message) {
        hideProgress();
        sessionManager.logoutUser();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    @Override
    public void onLogoutError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Logout error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogoutNetworkError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Network error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogoutAllSuccess(String message) {
        hideProgress();
        sessionManager.logoutUser();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    @Override
    public void onLogoutAllError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Logout all error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogoutAllNetworkError(String error) {
        hideProgress();
        Toast.makeText(getContext(), "Network error: " + error, Toast.LENGTH_LONG).show();
    }

    // ========== Helper Methods ==========

    private void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
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
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
