package com.example.ecotrack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class FeedbackActivity extends AppCompatActivity {
    private RatingBar myRatingBar;
    private EditText textInput;
    private Button btnSubmitFeedback;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //return to EcoService fragment when press back
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get userid from Firebase Auth
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //String uid = userId.getInstance().getUserId();

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users").child(uid).child("Feedback");

        // Initialize the view
        myRatingBar = findViewById(R.id.myRatingBar);
        textInput = findViewById(R.id.text_input);
        btnSubmitFeedback = findViewById(R.id.btn_submit_request);

        // Set the click event of the submit button
        btnSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = textInput.getText().toString().trim();
                float rating = myRatingBar.getRating();

                if (feedback.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Please provide your feedback.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create Feedback object
                    Feedback feedbackData = new Feedback(rating, feedback);

                    //create unique ID for each feedback entry
                    String feedbackId = databaseReference.push().getKey();
                    // Store feedback in Firebase
                    databaseReference.child(feedbackId).setValue(feedbackData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(FeedbackActivity.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();

                                    // Clear the input box and rating column
                                    textInput.setText("");
                                    myRatingBar.setRating(0);
                                } else {
                                    Toast.makeText(FeedbackActivity.this, "Failed to submit feedback. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    // Define Feedback class
    public static class Feedback {
        public float rating;
        public String comments;

        // Default constructor required for Firebase
        public Feedback() {}

        public Feedback(float rating, String comments) {
            this.rating = rating;
            this.comments = comments;
        }
    }
}