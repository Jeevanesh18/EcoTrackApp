package com.example.ecotrack.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecotrack.Challenge;
import com.example.ecotrack.ChallengeAdapter;
import com.example.ecotrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EcoChallengeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChallengeAdapter challengeAdapter;
    private List<Challenge> challengeList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eco_challenge, container, false);
        Log.d("EcoChallengeFragment", "Fragment is being created!");


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));  // 3 columns
        fetchChallenges();

        return view;
    }

    private void fetchChallenges() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ChallengesMetadata");  // Correct path here
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                challengeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Challenge challenge = snapshot.getValue(Challenge.class);
                    challengeList.add(challenge);
                }
                Log.d("EcoChallengeFragment", "Challenges added: " + challengeList.size());
                Log.d("EcoChallengeFragment", "Challenge list size before setting adapter: " + challengeList.size());
                challengeAdapter = new ChallengeAdapter(getContext(), challengeList);
                recyclerView.setAdapter(challengeAdapter);
                Log.d("EcoChallengeFragment", "Adapter is being set");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch challenges.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}


