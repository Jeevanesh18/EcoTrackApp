package com.example.ecotrack.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.ecotrack.GuidelineAdapter;
import com.example.ecotrack.GuidelineDetailActivity;
import com.example.ecotrack.GuidelineItem;
import com.example.ecotrack.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecycleGuidelineFragment extends Fragment {

    private RecyclerView recyclerView;
    private GuidelineAdapter adapter;
    private List<GuidelineItem> guidelineItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycle_guideline, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        EditText searchBar = view.findViewById(R.id.searchBar);

        // Fetch data from Firebase
        fetchGuidelineItems();

        // Setup RecyclerView
        adapter = new GuidelineAdapter(guidelineItems, selectedItem -> {
            // Navigate to details
            Intent intent = new Intent(getActivity(), GuidelineDetailActivity.class);
            intent.putExtra("itemName", selectedItem.getItemName());
            intent.putExtra("itemIntro", selectedItem.getItemIntro());
            intent.putExtra("itemTips", selectedItem.getTips());
            intent.putExtra("imageRes", selectedItem.getImageURL());
            intent.putExtra("videoUrl", selectedItem.getVideoURL());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Setup search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void fetchGuidelineItems() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("guideline");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                guidelineItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    GuidelineItem item = itemSnapshot.getValue(GuidelineItem.class);
                    guidelineItems.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filterItems(String query) {
        List<GuidelineItem> filteredList = new ArrayList<>();
        for (GuidelineItem item : guidelineItems) {
            if (item.getItemName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter = new GuidelineAdapter(filteredList, selectedItem -> {
            Intent intent = new Intent(getActivity(), GuidelineDetailActivity.class);
            intent.putExtra("itemName", selectedItem.getItemName());
            intent.putExtra("itemIntro", selectedItem.getItemIntro());
            intent.putExtra("itemTips", selectedItem.getTips());
            intent.putExtra("imageRes", selectedItem.getImageURL());
            intent.putExtra("videoUrl", selectedItem.getVideoURL());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}




