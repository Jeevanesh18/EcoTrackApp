package com.example.ecotrack;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Button buttonResetPassword;
    private EditText editTextPasswordResetEmail;
    private FirebaseAuth authProfile;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        findViewById(R.id.TBMainAct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        authProfile = FirebaseAuth.getInstance();
        editTextPasswordResetEmail = findViewById(R.id.editText_password_reset_email);
        buttonResetPassword = findViewById(R.id.button_password_reset);

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextPasswordResetEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    editTextPasswordResetEmail.setError("Email is required!");
                    editTextPasswordResetEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextPasswordResetEmail.setError("Valid email is required!");
                    editTextPasswordResetEmail.requestFocus();
                } else {
                    checkAndSendPasswordResetEmail(email);
                }
            }
        });
    }

    private void checkAndSendPasswordResetEmail(String email) {
        // Trim the email to avoid any whitespace issues
        email = email.trim();

        // Send the password reset email
        authProfile.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Inform the user that if the email is registered, they will receive the reset email
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "If this email is registered, a password reset link will be sent.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                editTextPasswordResetEmail.setError("No account found with this email. Please register first.");
                                editTextPasswordResetEmail.requestFocus();
                            } catch (Exception e) {
                                // Handle other errors like network issues, etc.
                                Log.e(TAG, "Error: " + e.getMessage());
                                Snackbar.make(findViewById(android.R.id.content),
                                        "Error sending password reset email. Please try again.",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

}
