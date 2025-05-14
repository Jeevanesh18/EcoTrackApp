package com.example.ecotrack;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageProfileActivity extends AppCompatActivity {
    private EditText editTextName, editTextAddress, editTextCity, editTextState, editTextPostcode;
    private String Name, Address, City, State, Postcode;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        //return to profile fragment if press back
        findViewById(R.id.TBMainAct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextName = findViewById(R.id.edittext_manage_profile_name);
        editTextAddress = findViewById(R.id.edittext_manage_profile_address);
        editTextCity = findViewById(R.id.edittext_manage_profile_city);
        editTextState = findViewById(R.id.edittext_manage_profile_state);
        editTextPostcode = findViewById(R.id.edittext_manage_profile_postcode);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showProfile(firebaseUser);

        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {

        if (TextUtils.isEmpty(Name)) {
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
        } else if (TextUtils.isEmpty(Address)) {
            editTextAddress.setError("Address is required!");
            editTextAddress.requestFocus();
        } else if (TextUtils.isEmpty(City)) {
            editTextCity.setError("City is required!");
            editTextCity.requestFocus();
        } else if (TextUtils.isEmpty(State)) {
            editTextState.setError("State is required!");
            editTextState.requestFocus();
        } else if (TextUtils.isEmpty(Postcode)) {
            editTextPostcode.setError("Postcode is required!");
            editTextPostcode.requestFocus();
        } else {
            Name = editTextName.getText().toString();
            Address = editTextAddress.getText().toString();
            City = editTextCity.getText().toString();
            State = editTextState.getText().toString();
            Postcode = editTextPostcode.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(Address, City, State, Postcode);

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String UID = firebaseUser.getUid();

            referenceProfile.child(UID).child("User Info").setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(Name)
                                .build();

                        firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.reload().addOnCompleteListener(reloadTask -> {
                                        if (reloadTask.isSuccessful()) {
                                            Toast.makeText(ManageProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                                            finish();
                                        } else {
                                            Toast.makeText(ManageProfileActivity.this, "Failed to reload profile.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        });
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

    private void showProfile(FirebaseUser firebaseUser) {
        String UID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        referenceProfile.child(UID).child("User Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                if (readUserDetails != null) {
                    Name = firebaseUser.getDisplayName();
                    Address = readUserDetails.Address;
                    City = readUserDetails.City;
                    State = readUserDetails.State;
                    Postcode = readUserDetails.Postcode;

                    editTextName.setText(Name);
                    editTextAddress.setText(Address);
                    editTextCity.setText(City);
                    editTextState.setText(State);
                    editTextPostcode.setText(Postcode);
                } else {
                    Toast.makeText(ManageProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}