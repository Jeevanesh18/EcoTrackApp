package com.example.ecotrack;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private Context context;
    private List<Challenge> challengeList;

    public ChallengeAdapter(Context context, List<Challenge> challengeList) {
        this.context = context;
        this.challengeList = challengeList;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false);
        Log.d("ChallengeAdapter", "onCreateViewHolder called");
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        // Log the size of challengeList to ensure it's not empty
        Log.d("ChallengeAdapter", "challengeList size: " + challengeList.size());
        Challenge challenge = challengeList.get(position);

        // Fetch the userId dynamically from Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userChallengesRef = FirebaseDatabase.getInstance()
                .getReference("Registered Users")
                .child(userId)
                .child("Challenges")
                .child(challenge.getBadgeId());

        // Fetch completion status from Firebase
        userChallengesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Boolean isCompleted = task.getResult().child("completed").getValue(Boolean.class);
                if (isCompleted != null) {
                    challenge.setCompleted(isCompleted);
                } else {
                    challenge.setCompleted(false);  // Default if no completion data
                }

                // Load images based on completion status
                int imageResId = context.getResources().getIdentifier(challenge.getImageName(), "drawable", context.getPackageName());

                if (challenge.isCompleted()) {
                    holder.badgeBackground.setImageResource(R.drawable.colored_badge);
                    if (imageResId != 0) {
                        holder.uniqueImage.setImageResource(imageResId);
                        holder.uniqueImage.setVisibility(View.VISIBLE);
                    } else {
                        holder.uniqueImage.setVisibility(View.GONE); // Fallback if no image
                    }
                    holder.itemView.setOnClickListener(null); // Disable click if completed
                } else {
                    holder.badgeBackground.setImageResource(R.drawable.uncolored_badge_new);
                    holder.uniqueImage.setVisibility(View.GONE);

                    holder.itemView.setOnClickListener(v -> {
                        // Go to Challenge detail activity
                        Intent intent = new Intent(context, ChallengeDetailActivity.class);
                        intent.putExtra("challenge", challenge);
                        intent.putExtra("userId", userId);
                        context.startActivity(intent);
                    });
                }
            } else {
                // Default view if no completion data exists or Firebase query fails
                holder.badgeBackground.setImageResource(R.drawable.uncolored_badge_new);
                holder.uniqueImage.setVisibility(View.GONE);
            }
            // Notify the adapter that the data has changed after Firebase query completes
            notifyItemChanged(position);
        }).addOnFailureListener(e -> {
            // Log error in case Firebase query fails
            System.err.println("Firebase query failed: " + e.getMessage());
            holder.badgeBackground.setImageResource(R.drawable.uncolored_badge_new);
            holder.uniqueImage.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        ImageView badgeBackground;
        ImageView uniqueImage;
        FrameLayout badgeContainer;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeBackground = itemView.findViewById(R.id.badgeBackground);
            uniqueImage = itemView.findViewById(R.id.uniqueImage);
            badgeContainer = (FrameLayout) itemView;
        }
    }
}





