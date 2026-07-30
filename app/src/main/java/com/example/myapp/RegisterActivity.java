package com.example.myapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.api.RegisterTask;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText name, username, email, password, confirm;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirmPassword);
        register = findViewById(R.id.registerButton);

        register.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String n = name.getText().toString().trim();
        String u = username.getText().toString().trim();
        String e = email.getText().toString().trim();
        String p = password.getText().toString();
        String c = confirm.getText().toString();

        if (n.isEmpty() || u.isEmpty() || e.isEmpty()
                || p.isEmpty() || c.isEmpty()) {

            Toast.makeText(this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!p.equals(c)) {

            Toast.makeText(this,
                    "Passwords don't match",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        register.setEnabled(false);

        new RegisterTask(result -> {

            register.setEnabled(true);

            try {

                JSONObject obj = new JSONObject(result);

                if (obj.getBoolean("success")) {

                    Toast.makeText(
                            this,
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                    ).show();

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

        }).execute(n, u, e, p);

    }

}
