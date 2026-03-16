package com.example.ai_interviewer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailET, passwordET;
    Button loginBtn, registerBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> {

            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                emailET.setError("Enter Email");
                return;
            }

            if(TextUtils.isEmpty(password)){
                passwordET.setError("Enter Password");
                return;
            }

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){

                            Toast.makeText(LoginActivity.this,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT).show();

                           startActivity(new Intent(LoginActivity.this,
                                    MainActivity.class));

                            finish();

                        }else{

                            Toast.makeText(LoginActivity.this,
                                    "Login Failed",
                                    Toast.LENGTH_SHORT).show();

                        }

                    });

        });

        registerBtn.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this,
                        RegisterActivity.class)));
    }
}