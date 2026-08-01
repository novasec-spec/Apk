package com.example.myapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;
import com.example.myapp.api.RegisterTask;
import com.example.myapp.utils.NetworkUtils;
import com.example.myapp.utils.PasswordValidator;

public class RegisterActivity extends AppCompatActivity implements RegisterTask.RegisterCallback {
    
    private EditText etEmail, etPassword, etConfirmPassword, etFirstName, etLastName;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void attemptRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        // Validate email
        PasswordValidator.ValidationResult emailValidation = 
            PasswordValidator.validateEmail(email);
        if (!emailValidation.isValid()) {
            etEmail.setError(emailValidation.getErrorMessage());
            etEmail.requestFocus();
            return;
        }

        // Validate password
        PasswordValidator.ValidationResult passwordValidation = 
            PasswordValidator.validate(password);
        if (!passwordValidation.isValid()) {
            etPassword.setError(passwordValidation.getErrorMessage());
            etPassword.requestFocus();
            return;
        }

        // Check password match
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Check network
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        // Show progress
        showProgress("Creating account...");

        // Call API
        new RegisterTask(this).execute(email, password, firstName, lastName);
    }

    @Override
    public void onRegisterSuccess(String message) {
        hideProgress();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onRegisterError(String error) {
        hideProgress();
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegisterNetworkError(String error) {
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
