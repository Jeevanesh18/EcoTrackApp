package com.example.ecotrack;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NotificationCentreActivity extends AppCompatActivity {

    private Switch switchtrashpickup, switchspecialcollection, switchchallenge, switchtips;
    private boolean trashpickupx,specialcollectionx,challengex,tipsx;
    private Button uploadButton;
    private ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_centre);

        //return back to profile fragment when press back
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize the switches and button
        switchtrashpickup = findViewById(R.id.switch_notification_1);
        switchspecialcollection = findViewById(R.id.switch_notification_2);
        switchchallenge = findViewById(R.id.switch_notification_3);
        switchtips = findViewById(R.id.switch_notification_4);
        uploadButton = findViewById(R.id.updateButton);


        //set switch states by retrieving data from fb
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notification_preferences");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean challenge = snapshot.child("challenge").getValue(Boolean.class);
                    boolean specialCollection = snapshot.child("specialcollection").getValue(Boolean.class);
                    boolean tips = snapshot.child("tips").getValue(Boolean.class);
                    boolean trashPickup = snapshot.child("trashpickup").getValue(Boolean.class);

                    switchchallenge.setChecked(challenge);
                    switchspecialcollection.setChecked(specialCollection);
                    switchtips.setChecked(tips);
                    switchtrashpickup.setChecked(trashPickup);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load preferences.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set a click listener for the upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotificationPreferences();
                finish();
            }
        });
    }

    private void saveNotificationPreferences() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference preferencesRef = FirebaseDatabase.getInstance()
                    .getReference("Registered Users")
                    .child(userId)
                    .child("notification_preferences");

            // Get the values from the switches
            Map<String, Object> preferences = new HashMap<>();
            preferences.put("trashpickup", switchtrashpickup.isChecked());
            preferences.put("specialcollection", switchspecialcollection.isChecked());
            preferences.put("challenge", switchchallenge.isChecked());
            preferences.put("tips", switchtips.isChecked());
            trashpickupx=switchtrashpickup.isChecked();
            specialcollectionx=switchspecialcollection.isChecked();
            challengex=switchchallenge.isChecked();
            tipsx=switchtips.isChecked();
            // Save the preferences to Firebase
            preferencesRef.setValue(preferences).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(NotificationCentreActivity.this, "Preferences updated successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Preferences updated successfully");
                    showLocalNotification();
                } else {
                    Toast.makeText(NotificationCentreActivity.this, "Failed to update preferences.", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Error updating preferences", task.getException());
                }
            });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            Log.e("Firebase", "No authenticated user found.");
        }
    }

    private void showLocalNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "NotificationCentreChannel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Notification Centre", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notification for preference updates");
            notificationManager.createNotificationChannel(channel);
        }

        // Tips Notification
        if (tipsx) {
            Intent intent = new Intent(this, MainActivity.class).putExtra("SHOW_HOME", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ecotrack_logo)
                    .setContentTitle("TIPS")
                    .setContentText("Use electric car")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(101, builder.build());
        }

        // Trash Pickup Notification
        if (trashpickupx) {
            Intent intent = new Intent(this, MainActivity.class).putExtra("SHOW_HOME", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 102, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ecotrack_logo)
                    .setContentTitle("Trash pickup reminder")
                    .setContentText("22/1/2025")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(102, builder.build());
        }

        // Special Collection Notification
        if (specialcollectionx) {
            Intent intent = new Intent(this, MainActivity.class).putExtra("SHOW_HOME", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 103, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ecotrack_logo)
                    .setContentTitle("Special collection day reminder")
                    .setContentText("Date: 19/1/2025 Waste: Cooking Oil")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(103, builder.build());
        }

        // Challenge Notification
        if (challengex) {

            Intent intent = new Intent(this, MainActivity.class).putExtra("SHOW_ECO_CHALLENGE", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 104, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ecotrack_logo) // Replace with your app's notification icon
                    .setContentTitle("New challenge")
                    .setContentText("Did you recycle something today?")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent); // Add the PendingIntent
            notificationManager.notify(104, builder.build()); // Use a unique ID
        }
    }
}