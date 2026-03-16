package com.example.ai_interviewer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText emailET, passwordET;
    Button registerBtn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        registerBtn = findViewById(R.id.registerBtn);

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(v -> {

            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailET.setError("Enter Email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordET.setError("Enter Password");
                return;
            }

            if (password.length() < 6) {
                passwordET.setError("Password must be 6+ characters");
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(RegisterActivity.this,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        } else {

                            Toast.makeText(RegisterActivity.this,
                                    "Registration Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    });

        });

    }
}