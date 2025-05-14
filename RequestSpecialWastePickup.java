package com.example.ecotrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RequestSpecialWastePickup extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001; // Request code for image selection

    private EditText emailEditText, addressEditText, cityEditText, stateEditText,
            postcodeEditText, wasteTypeEditText;
    private ImageView uploadedImageView;
    private Button uploadPhotoButton, submitRequestButton;

    // URI to store the selected image
    private Uri imageUri = null;

    // Firebase references for database and storage
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_special_waste_pickup); // Link to corresponding XML layout

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
        wasteTypeEditText = findViewById(R.id.wasteTypeEditText);

        uploadedImageView = findViewById(R.id.uploadedImageView);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        submitRequestButton = findViewById(R.id.submitRequestButton);

        //return to fragment if press back
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get userId
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase Realtime Database and Storage references
        databaseRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(UID).child("Request Special Waste Pickup");
        //initialize firebase storage
        storageRef = FirebaseStorage.getInstance().getReference("special_waste_pickup_images");

        // Set click listener for the upload photo button
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });

        // Set click listener for the submit request button
        submitRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRequest();
            }
        });
    }

    // Opens gallery to select an image
    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*"); // Only show images
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

    // Submits the special waste pickup request
    private void submitRequest() {
        String email = emailEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String postcode = postcodeEditText.getText().toString().trim();
        String wasteType = wasteTypeEditText.getText().toString().trim();

        // Validate user input
        if (email.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() ||
                postcode.isEmpty() || wasteType.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageAndSaveData(email, address, city, state, postcode, wasteType); // Upload image and save data
        } else {
            saveRequestToDatabase(email, address, city, state, postcode, wasteType, null); // Save data without image
        }
    }

    // Uploads the image to Firebase Storage and saves the data in Firebase Realtime Database
    private void uploadImageAndSaveData(String email, String address, String city,
                                        String state, String postcode, String wasteType) {
        String fileName = System.currentTimeMillis() + ".jpg"; // Generate a unique file name
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnCompleteListener(urlTask -> {
                        if (urlTask.isSuccessful()) {
                            String imageUrl = urlTask.getResult().toString();
                            saveRequestToDatabase(email, address, city, state, postcode, wasteType, imageUrl); // Save data with image URL
                        } else {
                            Toast.makeText(RequestSpecialWastePickup.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(RequestSpecialWastePickup.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Saves the request details in Firebase Realtime Database
    private void saveRequestToDatabase(String email, String address, String city,
                                       String state, String postcode, String wasteType,
                                       String imageUrl) {

        // Generate a unique database key
        String requestId = databaseRef.push().getKey();
        if (requestId == null) {
            Toast.makeText(this, "Failed to get database key", Toast.LENGTH_SHORT).show();
            return;
        }

        SpecialWastePickupModel model = new SpecialWastePickupModel(
                email, address, city, state, postcode, wasteType, imageUrl, System.currentTimeMillis()
        );

        databaseRef.child(requestId).setValue(model)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Request submitted", Toast.LENGTH_SHORT).show();
                        sendConfirmationEmail(email); // Send confirmation email
                        finish(); // Close activity
                    } else {
                        Toast.makeText(this, "Failed to submit request: " + task.getException(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Sends a confirmation email to the user
    private void sendConfirmationEmail(String recipientEmail) {
        String subject = "Your request is received";
        String message = "Hello,\n\nWe have received your Special Waste Pickup request and will handle it shortly.\n\nThank you,\nEcoTrack Support Team";

        JavaMailAPI mailAPI = new JavaMailAPI(
                this,
                "motian23333@yahoo.com",  // Sender's email
                "zkzcdhdahwizjofk",       // App-specific password
                recipientEmail,           // Recipient's email
                subject,
                message
        );
        mailAPI.execute(); // Send email asynchronously
    }
}