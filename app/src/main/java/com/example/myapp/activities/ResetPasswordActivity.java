package com.example.myapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;
import com.example.myapp.api.ResetPasswordTask;
import com.example.myapp.utils.NetworkUtils;
import com.example.myapp.utils.PasswordValidator;

public class ResetPasswordActivity extends AppCompatActivity 
        implements ResetPasswordTask.ResetPasswordCallback {
    
    private EditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private ProgressDialog progressDialog;
    private String resetToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Get token from intent
        resetToken = getIntent().getStringExtra("token");
        if (resetToken == null || resetToken.isEmpty()) {
            Toast.makeText(this, "Invalid reset token", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptResetPassword();
            }
        });
    }

    private void attemptResetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        PasswordValidator.ValidationResult validation = 
            PasswordValidator.validate(newPassword);
        if (!validation.isValid()) {
            etNewPassword.setError(validation.getErrorMessage());
            etNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Resetting password...");
        new ResetPasswordTask(this).execute(resetToken, newPassword);
    }

    @Override
    public void onResetPasswordSuccess(String message) {
        hideProgress();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onResetPasswordError(String error) {
        hideProgress();
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResetPasswordNetworkError(String error) {
        hideProgress();
        Toast.makeText(this, "Network error: " + error, Toast.LENGTH_LONG).show();
    }

    private void showProgress(String message) {
        if (progressDialog == null) {
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
}
