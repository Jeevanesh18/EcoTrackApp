package com.example.ecotrack;

public class GuidelineItem {
    private String itemId;
    private String itemName;
    private String itemIntro;
    private String tips;
    private String videoURL;
    private String imageURL;

    // No-argument constructor for Firebase
    public GuidelineItem() {
        // Default constructor required for calls to DataSnapshot.getValue(GuidelineItem.class)
    }

    // Constructor with parameters
    public GuidelineItem(String itemId, String itemName, String itemIntro, String tips, String videoURL, String imageURL) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemIntro = itemIntro;
        this.tips = tips;
        this.videoURL = videoURL;
        this.imageURL = imageURL;
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getItemIntro() { return itemIntro; }
    public String getTips() { return tips; }
    public String getVideoURL() { return videoURL; }
    public String getImageURL() { return imageURL; }

    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemIntro(String itemIntro) { this.itemIntro = itemIntro; }
    public void setTips(String tips) { this.tips = tips; }
    public void setVideoURL(String videoURL) { this.videoURL = videoURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
}


