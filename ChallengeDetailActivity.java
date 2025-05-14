package com.example.ecotrack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChallengeDetailActivity extends AppCompatActivity {

    private ImageView badgeImage, uniqueImage;
    private TextView tvTopic, tvDetails, tvMessage;
    private Button yesButton, noButton;
    private DatabaseReference userChallengesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Initialize UI components
        badgeImage = findViewById(R.id.badgeImage);
        uniqueImage = findViewById(R.id.uniqueImage);
        tvTopic = findViewById(R.id.tvTopic);
        tvDetails = findViewById(R.id.tvDetails);
        tvMessage = findViewById(R.id.tvMessage);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        // Get the challenge object passed via Intent (you still need this)
        Challenge challenge = (Challenge) getIntent().getSerializableExtra("challenge");

        // Get the current authenticated user ID directly from Firebase Authentication
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set up Firebase references
        userChallengesRef = FirebaseDatabase.getInstance()
                .getReference("Registered Users")
                .child(userId)
                .child("Challenges")
                .child(challenge.getBadgeId());

        DatabaseReference challengesMetadataRef = FirebaseDatabase.getInstance()
                .getReference("ChallengesMetadata")
                .child(challenge.getBadgeId());

        // Fetch challenge details from Firebase
        challengesMetadataRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String topic = task.getResult().child("topic").getValue(String.class);
                String details = task.getResult().child("details").getValue(String.class);
                String imageName = task.getResult().child("imageName").getValue(String.class);

                // Set the challenge details
                challenge.setTopic(topic);
                challenge.setDetails(details);
                challenge.setImageName(imageName);

                // Update UI with the fetched data
                tvTopic.setText(topic);
                tvDetails.setText(details);
            }
        });

        // Mark the challenge as completed when the "Yes" button is clicked
        yesButton.setOnClickListener(v -> {
            // Update the UI to reflect the completed challenge
            badgeImage.setImageResource(R.drawable.colored_badge);
            int imageResId = getResources().getIdentifier(challenge.getImageName(), "drawable", getPackageName());
            if (imageResId != 0) {
                uniqueImage.setImageResource(imageResId);
                uniqueImage.setVisibility(ImageView.VISIBLE);
            }
            yesButton.setVisibility(Button.GONE);
            noButton.setVisibility(Button.GONE);
            tvDetails.setVisibility(TextView.GONE);
            tvMessage.setText("Congratulations! You've completed this challenge!");
            tvMessage.setVisibility(TextView.VISIBLE);

            // Now update the challenge completion status in Firebase
            userChallengesRef.child("completed").setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        // Log success (or show a toast to the user)
                        System.out.println("Challenge marked as completed");
                    })
                    .addOnFailureListener(e -> {
                        // Log failure (or show an error message to the user)
                        System.err.println("Failed to update challenge: " + e.getMessage());
                    });
        });

        // Mark the challenge as not completed when the "No" button is clicked
        noButton.setOnClickListener(v -> {
            badgeImage.setImageResource(R.drawable.uncolored_badge_new);
            yesButton.setVisibility(Button.GONE);
            noButton.setVisibility(Button.GONE);
            tvDetails.setVisibility(TextView.GONE);
            tvMessage.setText("Keep going! You're doing great, don't give up!");
            tvMessage.setVisibility(TextView.VISIBLE);
        });

        // Close the activity when the back button is clicked
        findViewById(R.id.tB).setOnClickListener(v -> finish());
    }
}




















