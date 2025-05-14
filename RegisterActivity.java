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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterName, editTextRegisterEmail, editTextRegisterPassword, editTextRegisterConfirmPassword, editTextRegisterAddress, editTextRegisterCity, editTextRegisterState, editTextRegisterPostcode;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.TBMainAct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextRegisterName = findViewById(R.id.editText_register_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterPassword = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPassword = findViewById(R.id.editText_register_confirm_password);
        editTextRegisterAddress = findViewById(R.id.editText_register_address);
        editTextRegisterCity = findViewById(R.id.editText_register_city);
        editTextRegisterState = findViewById(R.id.editText_register_state);
        editTextRegisterPostcode = findViewById(R.id.editText_register_postcode);

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName = editTextRegisterName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textPassword = editTextRegisterPassword.getText().toString();
                String textConfirmPassword = editTextRegisterConfirmPassword.getText().toString();
                String textAddress = editTextRegisterAddress.getText().toString();
                String textCity = editTextRegisterCity.getText().toString();
                String textState = editTextRegisterState.getText().toString();
                String textPostcode = editTextRegisterPostcode.getText().toString();

                if (TextUtils.isEmpty(textName)) {
                    editTextRegisterName.setError("Name is required!");
                    editTextRegisterName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    editTextRegisterEmail.setError("Email is required!");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    editTextRegisterEmail.setError("Valid email is required!");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    editTextRegisterPassword.setError("Password is required!");
                    editTextRegisterPassword.requestFocus();
                } else if (textPassword.length() <= 6) {
                    editTextRegisterPassword.setError("Your password must be more than 6 characters.");
                    editTextRegisterPassword.requestFocus();
                } else if (!textPassword.matches(".*[a-zA-Z].*")) {
                    editTextRegisterPassword.setError("Your password must contain at least one letter.");
                    editTextRegisterPassword.requestFocus();
                } else if (!textPassword.matches(".*\\d.*")) {
                    editTextRegisterPassword.setError("Your password must contain at least one digit.");
                    editTextRegisterPassword.requestFocus();
                } else if (!textPassword.matches(".*[!@#$%^&*_].*")) {
                    editTextRegisterPassword.setError("Your password must contain at least one special character (!@#$%^&*_).");
                    editTextRegisterPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    editTextRegisterConfirmPassword.setError("Password Confirmation is required!");
                    editTextRegisterConfirmPassword.requestFocus();
                } else if (!textPassword.equals(textConfirmPassword)) {
                    editTextRegisterConfirmPassword.setError("Passwords do not match!");
                    editTextRegisterConfirmPassword.requestFocus();
                } else if (TextUtils.isEmpty(textAddress)) {
                    editTextRegisterAddress.setError("Address is required!");
                    editTextRegisterAddress.requestFocus();
                } else if (TextUtils.isEmpty(textCity)) {
                    editTextRegisterCity.setError("City is required!");
                    editTextRegisterCity.requestFocus();
                } else if (TextUtils.isEmpty(textState)) {
                    editTextRegisterState.setError("State is required!");
                    editTextRegisterState.requestFocus();
                } else if (TextUtils.isEmpty(textPostcode)) {
                    editTextRegisterPostcode.setError("Postcode is required!");
                    editTextRegisterPostcode.requestFocus();
                } else {
                    registerUser(textName, textEmail, textPassword, textAddress, textCity, textState, textPostcode);
                }
            }
        });
    }

    private void registerUser(String textName, String textEmail, String textPassword, String textAddress, String textCity, String textState, String textPostcode) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textAddress, textCity, textState, textPostcode);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).child("User Info").setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Create challenges for the new user
                                createChallenges(firebaseUser.getUid());

                                firebaseUser.sendEmailVerification();

                                auth.signOut();

                                Toast.makeText(RegisterActivity.this, "User Registration Successful! Please verify your email!", Toast.LENGTH_LONG).show();

                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "User Registration Failed! Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editTextRegisterPassword.setError("Your password is not strong enough. Please use a combination of letters, numbers, and special characters.");
                        editTextRegisterPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextRegisterEmail.setError("Your email is either invalid or already in use. Please re-enter it.");
                        editTextRegisterEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        editTextRegisterEmail.setError("This email is already registered. Please use a different email address.");
                        editTextRegisterEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void createChallenges(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        DatabaseReference challengesRef = databaseReference.child(userId).child("Challenges");

        for (int i = 1; i <= 12; i++) {
            String badgeId = "badge" + i;
            challengesRef.child(badgeId).child("completed").setValue(false)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Challenge " + badgeId + " created successfully.");
                        } else {
                            Log.e(TAG, "Error creating challenge " + badgeId + ": " + task.getException().getMessage());
                        }
                    });
        }
    }
}
