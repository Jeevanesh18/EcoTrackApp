package com.example.ecotrack;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyReport extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);

        //return to fragment if press back
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize RecyclerView and report list
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        reportList = new ArrayList<Report>();
        reportAdapter = new ReportAdapter(reportList, this);
        recyclerView.setAdapter(reportAdapter);

        // Fetch reports from Firebase
        fetchReportsFromFirebase();
    }

    private void fetchReportsFromFirebase() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference binsRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(UID).child("Report Overflowing Bins");
        DatabaseReference specialPickupRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(UID).child("Request Special Waste Pickup");
        DatabaseReference appIssueRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(UID).child("Report App Issue");
        // Fetch Overflowing Bin reports
        binsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportList.clear(); // Clear existing list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String reportId = snapshot.getKey();
                    OverflowingBinsModel binReport = snapshot.getValue(OverflowingBinsModel.class);

                    if (binReport != null) {
                        Report report = new Report(
                                reportId,
                                binReport.getDate(),
                                binReport.getEmail(),
                                binReport.getDescription(),
                                binReport.getAddress(),
                                binReport.getCity(),
                                binReport.getState(),
                                binReport.getPostcode(),
                                binReport.getImageUrl(),
                                "pending", //default
                                "Overflowing Bin"
                        );
                        reportList.add(report);
                    }
                }
                reportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyReport.this, "Failed to load Overflowing Bin reports.", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch Special Waste Pickup requests
        specialPickupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String reportId = snapshot.getKey();
                    SpecialWastePickupModel pickupRequest = snapshot.getValue(SpecialWastePickupModel.class);

                    if (pickupRequest != null) {
                        Report report = new Report(
                                reportId,
                                pickupRequest.getDate(),
                                pickupRequest.getEmail(),
                                "Special Waste Pickup: " + pickupRequest.getWasteType(),
                                pickupRequest.getAddress(),
                                pickupRequest.getCity(),
                                pickupRequest.getState(),
                                pickupRequest.getPostcode(),
                                pickupRequest.getImageUrl(),
                                "pending", //default
                                "Special Waste Pickup"
                        );
                        reportList.add(report);
                    }
                }
                reportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyReport.this, "Failed to load Special Waste Pickup requests.", Toast.LENGTH_SHORT).show();
            }
        });

//        appIssueRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    String reportId = data.getKey();
//                    ReportModel appIssueReport = data.getValue(ReportModel.class);
//
//                    if (appIssueReport != null) {
//                        Report report = new Report(
//                                reportId, // Pass the report ID
//                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(appIssueReport.getTime())),
//                                appIssueReport.getEmail(),
//                                appIssueReport.getDescription(),
//                                "pending",
//                                "App Issue"
//                        );
//                        reportList.add(report);
//                    }
//                }
//                reportAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(MyReport.this, "Failed to load app issue reports.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    }

