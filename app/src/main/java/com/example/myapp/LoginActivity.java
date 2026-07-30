package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.api.LoginTask;
import com.example.myapp.auth.SessionManager;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;
    private TextView registerText;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        session = new SessionManager(this);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        loginButton.setOnClickListener(v -> login());

        registerText.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            LoginActivity.this,
                            RegisterActivity.class
                    )
            );

        });

    }

    private void login() {

        String e = email.getText().toString().trim();
        String p = password.getText().toString();

        if (e.isEmpty() || p.isEmpty()) {

            Toast.makeText(
                    this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        loginButton.setEnabled(false);

        new LoginTask(result -> {

            loginButton.setEnabled(true);

            try {

                JSONObject obj = new JSONObject(result);

                if (obj.getBoolean("success")) {

                    String accessToken =
                            obj.getString("accessToken");

                    JSONObject user =
                            obj.getJSONObject("user");

                    session.login(

                            accessToken,

                            user.getInt("id"),

                            user.getString("username")

                    );

                    startActivity(
                            new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            )
                    );

                    finish();

                } else {

                    Toast.makeText(

                            this,

                            obj.getString("message"),

                            Toast.LENGTH_LONG

                    ).show();

                }

            } catch (Exception ex) {

                Toast.makeText(

                        this,

                        ex.getMessage(),

                        Toast.LENGTH_LONG

                ).show();

            }

        }).execute(e, p);

    }

}
