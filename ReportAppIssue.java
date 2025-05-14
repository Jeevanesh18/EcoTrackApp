package com.example.ecotrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class ReportAppIssue extends AppCompatActivity {

    private EditText emailEditText, problemEditText;
    private Button submitButton;

    // Firebase Realtime Database reference
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_app_issue); // Link to the corresponding XML layout

        //return to fragment when press back
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 1. Initialize UI components
        emailEditText = findViewById(R.id.email_address);
        problemEditText = findViewById(R.id.problem_description);
        submitButton = findViewById(R.id.button);

        //get user id
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 2. Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(UID).child("Report App Issue");

        // 3. Set up submit button click event
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = emailEditText.getText().toString().trim();
                String problemDescription = problemEditText.getText().toString().trim();

                // Validate user input
                if (userEmail.isEmpty()) {
                    Toast.makeText(ReportAppIssue.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (problemDescription.isEmpty()) {
                    Toast.makeText(ReportAppIssue.this, "Please describe the issue", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 3.1 Store the report data in Firebase
                storeReportData(userEmail, problemDescription);

                // 3.2 Send confirmation email to the user
                sendConfirmationEmail(userEmail);
                finish();
            }
        });
    }

    // Store the user's report data in Firebase Realtime Database
    private void storeReportData(String email, String description) {
        // Generate a unique key for the report
        String reportId = databaseRef.push().getKey();
        if (reportId == null) {
            Toast.makeText(this, "Failed to get database key", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a data model object
        ReportModel report = new ReportModel(email, description, System.currentTimeMillis());

        // Save the data in the "reports" node under the generated key
        databaseRef.child(reportId).setValue(report)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ReportAppIssue.this, "Thanks for your report!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReportAppIssue.this, "Failed to store report: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Send a confirmation email to the user using JavaMailAPI
    private void sendConfirmationEmail(String recipientEmail) {
        // Email subject and message
        String subject = "Your report is received";
        String message = "Hello,\n\nThank you for reporting your issue. We have received your report and will get back to you soon.\n\nBest regards,\nEcoTrack Support";

        // Initialize and execute JavaMailAPI for asynchronous email sending
        JavaMailAPI mailAPI = new JavaMailAPI(
                this,
                "motian23333@yahoo.com",   // Sender's Yahoo email
                "zkzcdhdahwizjofk",        // Application-specific password
                recipientEmail,            // Recipient email
                subject,
                message
        );
        mailAPI.execute();
    }
}
