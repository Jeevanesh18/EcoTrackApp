package com.example.ecotrack;

import java.io.Serializable;

public class Challenge implements Serializable {
    private String badgeId;     // Unique ID for the badge
    private String topic;       // Title of the challenge
    private String details;     // Detailed description
    private boolean completed;  // Completion status
    private String imageName;   // Unique image name for the colored badge

    // Empty constructor for Firebase
    public Challenge() {}

    // Getters and setters
    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}



