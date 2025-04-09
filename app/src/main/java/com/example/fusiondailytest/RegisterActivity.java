package com.example.fusiondailytest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email, password;
    private Button registerBtn;
    private TextView loginLink;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ui);

        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerBtn = findViewById(R.id.signUpBtn);
        loginLink = findViewById(R.id.loginLink);
        progressDialog = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                if (!validateInput(emailText, passwordText)) return;

                registerBtn.setEnabled(false);  // Disable button to prevent multiple clicks
                progressDialog.setMessage("Registering...");
                progressDialog.show();

                auth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            registerBtn.setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                                // Navigate to MainActivity after successful registration
                                startActivity(new Intent(RegisterActivity.this, QuestionnaireActivity.class));
                                finish();  // Close RegisterActivity to prevent user from returning to it
                            } else {
                                String errorMessage = getFirebaseErrorMessage(task.getException());
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        loginLink.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, QuestionnaireActivity.class)));
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getFirebaseErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            return ((FirebaseAuthException) exception).getMessage();
        }
        return "Registration Failed! Please try again.";
    }
}

