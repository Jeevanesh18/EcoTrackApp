package com.example.ecotrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GuidelineAdapter extends RecyclerView.Adapter<GuidelineAdapter.GuidelineViewHolder> {

    private List<GuidelineItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GuidelineItem item);
    }

    public GuidelineAdapter(List<GuidelineItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GuidelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle, parent, false);
        return new GuidelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuidelineViewHolder holder, int position) {
        GuidelineItem item = itemList.get(position);

        // Set item name
        holder.itemName.setText(item.getItemName());

        // Set image resource dynamically
        int resId = holder.itemView.getContext().getResources()
                .getIdentifier(item.getImageURL(), "drawable", holder.itemView.getContext().getPackageName());
        holder.itemImage.setImageResource(resId);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, GuidelineDetailActivity.class);

            // Pass data to the GuidelineDetailActivity
            intent.putExtra("itemName", item.getItemName());
            intent.putExtra("itemIntro", item.getItemIntro());
            intent.putExtra("itemTips", item.getTips());
            intent.putExtra("imageRes", item.getImageURL()); //pass resourc ename
            intent.putExtra("videoUrl", item.getVideoURL());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class GuidelineViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;

        public GuidelineViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
        }
    }
}





