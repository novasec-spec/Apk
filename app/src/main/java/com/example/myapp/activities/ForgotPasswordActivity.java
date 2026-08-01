package com.example.myapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;
import com.example.myapp.api.ForgotPasswordTask;
import com.example.myapp.utils.NetworkUtils;
import com.example.myapp.utils.PasswordValidator;

public class ForgotPasswordActivity extends AppCompatActivity 
        implements ForgotPasswordTask.ForgotPasswordCallback {
    
    private EditText etEmail;
    private Button btnSubmit;
    private TextView tvBackToLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptForgotPassword();
            }
        });

        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void attemptForgotPassword() {
        String email = etEmail.getText().toString().trim();

        PasswordValidator.ValidationResult validation = 
            PasswordValidator.validateEmail(email);
        if (!validation.isValid()) {
            etEmail.setError(validation.getErrorMessage());
            etEmail.requestFocus();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Sending reset link...");
        new ForgotPasswordTask(this).execute(email);
    }

    @Override
    public void onForgotPasswordSuccess(String message) {
        hideProgress();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onForgotPasswordError(String error) {
        hideProgress();
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onForgotPasswordNetworkError(String error) {
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
