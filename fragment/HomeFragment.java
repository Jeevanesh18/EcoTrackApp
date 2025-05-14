package com.example.ecotrack.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecotrack.R;
import com.example.ecotrack.VPAdapter;
import com.example.ecotrack.ViewPagerItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private ArrayList<ViewPagerItem> viewPagerItemArrayList;
    private HashMap<String, String> events = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        viewPager2 = view.findViewById(R.id.image_slider);
        CalendarView calendarView = view.findViewById(R.id.calendar_view);

        setupViewPager();
        fetchFirebaseData();
        setupCalendar(calendarView);

        return view;
    }

    private void setupViewPager() {
        int[] images = {R.drawable.plastic, R.drawable.electric};
        String[] heading = {"Tips on plastic bottles", "Tips on carbon emission"};
        String[] desc = {
                getString(R.string.plastics_desc),
                getString(R.string.electric_desc)
        };

        viewPagerItemArrayList = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            ViewPagerItem viewPagerItem = new ViewPagerItem(images[i], heading[i], desc[i]);
            viewPagerItemArrayList.add(viewPagerItem);
        }

        VPAdapter vpAdapter = new VPAdapter(viewPagerItemArrayList);
        viewPager2.setAdapter(vpAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void fetchFirebaseData() {
        DatabaseReference regularDaysRef = FirebaseDatabase.getInstance()
                .getReference("Regular Collection Days");

        regularDaysRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String date = childSnapshot.getValue(String.class);
                    if (date != null) {
                        events.put(date, "Regular Collection Day");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load regular days.", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference specialDaysRef = FirebaseDatabase.getInstance()
                .getReference("special waste collection day");

        specialDaysRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                    String date = daySnapshot.child("date").getValue(String.class);
                    String wasteType = daySnapshot.child("waste").getValue(String.class);
                    String startingTime = daySnapshot.child("startingTime").getValue(String.class);
                    String endingTime = daySnapshot.child("endTime").getValue(String.class);

                    if (date != null) {
                        String details = "Special Collection day \n" +
                                "Waste Type: " + wasteType + "\n" +
                                "Time: " + startingTime + " - " + endingTime + "\n";
                        events.put(date, details);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load special days.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCalendar(CalendarView calendarView) {
        events.put("31/12/2024", "New Year's Eve");
        events.put("25/12/2024", "Christmas Day");

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month + 1, year);
            String event = events.get(selectedDate);

            if (event != null) {
                showEventDialog(selectedDate, event);
            } else {
                showEventDialog(selectedDate, "No events for this date.");
            }
        });
    }

    private void showEventDialog(String date, String event) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Event on " + date)
                .setMessage(event)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
