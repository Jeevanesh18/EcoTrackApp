package com.example.ecotrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ModifyPasswordActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmNewPassword;
    private TextView textViewAuthenticatedYes, textViewAuthenticatedNo;
    private Button buttonChangePassword, buttonAuthenticate;
    private String userCurrentPassword;

    private RelativeLayout modifyPasswordSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        //return profile fragment if press back
        findViewById(R.id.TBMainAct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextCurrentPassword = findViewById(R.id.editText_change_password_current);
        editTextNewPassword = findViewById(R.id.editText_change_password_new);
        editTextConfirmNewPassword = findViewById(R.id.editText_change_password_new_confirm);
        textViewAuthenticatedYes = findViewById(R.id.textView_change_password_authenticated_yes);
        textViewAuthenticatedNo = findViewById(R.id.textView_change_password_authenticated_no);
        buttonChangePassword = findViewById(R.id.button_change_password);
        buttonAuthenticate = findViewById(R.id.button_change_password_authenticate);
        modifyPasswordSection = findViewById(R.id.enableModify);

        editTextNewPassword.setEnabled(false);
        editTextConfirmNewPassword.setEnabled(false);
        buttonChangePassword.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(ModifyPasswordActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            // Create an intent to start MainActivity
            Intent intent = new Intent(ModifyPasswordActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add an extra to indicate that UserProfileFragment should be shown
            intent.putExtra("SHOW_USER_PROFILE", true);

            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }

        ImageView imageViewShowHidePasswordModify = findViewById(R.id.imageView_show_hide_password_modify);
        imageViewShowHidePasswordModify.setImageResource(R.drawable.eye_close);
        imageViewShowHidePasswordModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextCurrentPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editTextCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePasswordModify.setImageResource(R.drawable.eye_close);
                } else {
                    editTextCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePasswordModify.setImageResource(R.drawable.eye_open);
                }
            }
        });
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCurrentPassword = editTextCurrentPassword.getText().toString();

                if (TextUtils.isEmpty(userCurrentPassword)) {
                    editTextCurrentPassword.setError("Authentication requires your current password!");
                    editTextCurrentPassword.requestFocus();
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userCurrentPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                textViewAuthenticatedYes.setVisibility(TextView.VISIBLE);
                                textViewAuthenticatedNo.setVisibility(TextView.INVISIBLE);
                                modifyPasswordSection.setVisibility(RelativeLayout.VISIBLE);
                                editTextNewPassword.setEnabled(true);
                                editTextConfirmNewPassword.setEnabled(true);
                                buttonChangePassword.setEnabled(true);

                                editTextCurrentPassword.setEnabled(false);
                                buttonAuthenticate.setEnabled(false);

                                buttonChangePassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePassword(firebaseUser);
                                    }
                                });
                            } else {
                                textViewAuthenticatedNo.setVisibility(TextView.VISIBLE);
                                textViewAuthenticatedYes.setVisibility(TextView.INVISIBLE);
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser) {
        String userNewPassword = editTextNewPassword.getText().toString();
        String userConfirmNewPassword = editTextConfirmNewPassword.getText().toString();

        if (TextUtils.isEmpty(userNewPassword)) {
            editTextNewPassword.setError("A new password is required!");
            editTextNewPassword.requestFocus();
        } else if (TextUtils.isEmpty(userConfirmNewPassword)) {
            editTextConfirmNewPassword.setError("Please re-enter your new password!");
            editTextConfirmNewPassword.requestFocus();
        } else if (!userNewPassword.equals(userConfirmNewPassword)) {
            editTextConfirmNewPassword.setError("Passwords do not match!");
            editTextConfirmNewPassword.requestFocus();
        } else if (userNewPassword.equals(userCurrentPassword)) {
            editTextNewPassword.setError("The new password cannot be the same as the old password!");
            editTextNewPassword.requestFocus();
        } else if (userNewPassword.length() <= 6) {
            editTextNewPassword.setError("Your password must be more than 6 characters.");
            editTextNewPassword.requestFocus();
        } else if (!userNewPassword.matches(".*[a-zA-Z].*")) {
            editTextNewPassword.setError("Your password must contain at least one letter.");
            editTextNewPassword.requestFocus();
        } else if (!userNewPassword.matches(".*\\d.*")) {
            editTextNewPassword.setError("Your password must contain at least one digit.");
            editTextNewPassword.requestFocus();
        } else if (!userNewPassword.matches(".*[!@#$%^&*_].*")) {
            editTextNewPassword.setError("Your password must contain at least one special character (!@#$%^&*_).");
            editTextNewPassword.requestFocus();
        } else {
            firebaseUser.updatePassword(userNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ModifyPasswordActivity.this, "Your password has been successfully updated!", Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }
}