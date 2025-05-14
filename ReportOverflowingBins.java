package com.example.ecotrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;

public class ReportOverflowingBins extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001; // Request code for image selection

    // UI components
    private EditText emailEditText, addressEditText, cityEditText, stateEditText, postcodeEditText, descriptionEditText;
    private ImageView uploadedImageView;
    private Button uploadPhotoButton, submitReportButton;

    // URI for the selected image
    private Uri imageUri = null;

    // Firebase references
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_overflowing_bins);

        //return to service fragment if press back
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Bind UI components to variables
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        cityEditText = findViewById(R.id.cityEditText);
        stateEditText = findViewById(R.id.stateEditText);
        postcodeEditText = findViewById(R.id.postcodeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        uploadedImageView = findViewById(R.id.uploadedImageView);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        submitReportButton = findViewById(R.id.submitReportButton);

        //get uid
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //initialize firebase realtime db
        databaseRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(uid).child("Report Overflowing Bins");

        //initialize fb storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("overflowing_bins_images");

        // Button to select an image from the gallery
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });

        // Button to submit the report
        submitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReport();
            }
        });
    }

    // Opens gallery to choose an image
    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handles the result of image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadedImageView.setImageURI(imageUri); // Display the selected image
        }
    }

    // Submits the report with or without an image
    private void submitReport() {
        String email = emailEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String postcode = postcodeEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (email.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || postcode.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageAndSaveData(email, address, city, state, postcode, description); // Image present
        } else {
            saveReportToDatabase(email, address, city, state, postcode, description, null); // No image
        }
    }

    // Uploads the image to Firebase Storage and saves the data to Firebase Database
    private void uploadImageAndSaveData(String email, String address, String city, String state, String postcode, String description) {
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnCompleteListener(urlTask -> {
                        if (urlTask.isSuccessful()) {
                            String imageUrl = urlTask.getResult().toString();
                            saveReportToDatabase(email, address, city, state, postcode, description, imageUrl); // Save with image URL
                        } else {
                            Toast.makeText(ReportOverflowingBins.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ReportOverflowingBins.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Saves the report to Firebase Database
    private void saveReportToDatabase(String email, String address, String city, String state, String postcode, String description, String imageUrl) {
        OverflowingBinsModel reportData = new OverflowingBinsModel(
                email, address, city, state, postcode, description, imageUrl, System.currentTimeMillis()
        );

        //create unique ID for each report, if no start from R1
        String reportId = databaseRef.push().getKey();

        databaseRef.child(reportId).setValue(reportData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ReportOverflowingBins.this, "Report submitted", Toast.LENGTH_SHORT).show();
                        sendConfirmationEmail(email); // Send confirmation email
                        finish(); // Close activity
                    } else {
                        Toast.makeText(ReportOverflowingBins.this, "Failed to submit report", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Sends a confirmation email using JavaMailAPI
    private void sendConfirmationEmail(String recipientEmail) {
        String subject = "Your report is received";
        String message = "Hello,\n\nWe have received your overflowing bin report and will handle it shortly.\n\nThank you,\nEcoTrack Support Team";

        JavaMailAPI mailAPI = new JavaMailAPI(
                this,
                "motian23333@yahoo.com", // Sender email
                "zkzcdhdahwizjofk",      // App password
                recipientEmail,
                subject,
                message
        );
        mailAPI.execute();
    }
}
