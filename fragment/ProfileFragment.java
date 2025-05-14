package com.example.ecotrack.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ecotrack.FAQSupportActivity;
import com.example.ecotrack.IntroActivity;
import com.example.ecotrack.ManageProfileActivity;
import com.example.ecotrack.ModifyPasswordActivity;
import com.example.ecotrack.NotificationCentreActivity;
import com.example.ecotrack.R;
import com.example.ecotrack.ReadWriteUserDetails;
import com.example.ecotrack.ReportAppIssue;
import com.example.ecotrack.UploadProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private TextView textViewWelcome, textViewName, textViewEmail, textViewAddress, textViewCity, textViewState, textViewPostcode;
    private FirebaseAuth authProfile;
    private ImageView imageViewProfileDp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewWelcome = rootView.findViewById(R.id.textView_show_welcome);
        textViewName = rootView.findViewById(R.id.textView_show_name);
        textViewEmail = rootView.findViewById(R.id.textView_show_email);
        textViewAddress = rootView.findViewById(R.id.textView_show_address);
        textViewCity = rootView.findViewById(R.id.textView_show_city);
        textViewState = rootView.findViewById(R.id.textView_show_state);
        textViewPostcode = rootView.findViewById(R.id.textView_show_postcode);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        // Set up toolbar
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        imageViewProfileDp = rootView.findViewById(R.id.imageView_profile_dp);

        if (firebaseUser != null) {
            if (!firebaseUser.isEmailVerified()) {
                showVerificationDialog();  // Show verification pop-up if not verified
            } else {
                showUserProfile(firebaseUser);
            }
        } else {
            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            showUserProfile(firebaseUser); // Force refresh data
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        firebaseUser.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String UID = firebaseUser.getUid();
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                referenceProfile.child(UID).child("User Info").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                        if (readUserDetails != null) {
                            String name = firebaseUser.getDisplayName();  // Get display name after reload
                            String email = firebaseUser.getEmail();

                            if (name == null || name.isEmpty()) {
                                name = "User";  // Default name if display name is not available
                            }

                            textViewWelcome.setText("Welcome, " + name + "!");
                            textViewName.setText(name);
                            textViewEmail.setText(email);
                            textViewAddress.setText(readUserDetails.Address);
                            textViewCity.setText(readUserDetails.City);
                            textViewState.setText(readUserDetails.State);
                            textViewPostcode.setText(readUserDetails.Postcode);

                            // Load profile picture from Firebase Storage
                            loadProfileImage(UID);
                        } else {
                            Toast.makeText(getActivity(), "Failed to load profile!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Failed to reload user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage(String uid) {
        // Firebase Storage reference to the profile picture
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + uid + ".png");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Glide
            Glide.with(getActivity())
                    .load(uri)
                    .placeholder(R.drawable.ic_profile)  // Placeholder image during loading
                    .error(R.drawable.ic_profile)        // Error image if loading fails
                    .into(imageViewProfileDp);
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to load profile image.", Toast.LENGTH_SHORT).show();
        });
    }

    private void showVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email to continue. Check your inbox.");

        // "Continue" button that opens the Gmail app
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);  // Opens the default email app
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Notify that this fragment has a menu
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_manage_profile) {
            startActivity(new Intent(getActivity(), ManageProfileActivity.class));
            return true;
        } else if (id == R.id.menu_modify_password) {
            startActivity(new Intent(getActivity(), ModifyPasswordActivity.class));
            return true;
        } else if (id == R.id.menu_upload_picture) {
            startActivity(new Intent(getActivity(), UploadProfileActivity.class));
            return true;
        } else if (id == R.id.menu_notification_settings) {
            startActivity(new Intent(getActivity(), NotificationCentreActivity.class));
            return true;
        } else if (id == R.id.menu_report_app_issues) {
            startActivity(new Intent(getActivity(), ReportAppIssue.class));
            return true;
        } else if (id == R.id.menu_faqs) {
            startActivity(new Intent(getActivity(), FAQSupportActivity.class));
            return true;
        } else if (id == R.id.menu_log_out) {
            showLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authProfile.signOut();
                    Intent intent = new Intent(getActivity(), IntroActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create().show();
    }
}
