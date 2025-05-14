package com.example.ecotrack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OverflowingBinsModel {

    private String email;
    private String address;
    private String city;
    private String state;
    private String postcode;
    private String description;
    private String imageUrl;  // URL for the uploaded image
    private long timestamp;   // Time when the report was submitted
    private String id;

    // Default constructor required for Firebase deserialization
    public OverflowingBinsModel() { }

    // Parameterized constructor to initialize all fields
    public OverflowingBinsModel(String email, String address, String city, String state,
                                String postcode, String description,
                                String imageUrl, long timestamp) {
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postcode = postcode;
        this.description = description;
        this.imageUrl = imageUrl; // Optional, may be null if no image is uploaded
        this.timestamp = timestamp;
    }

    // Getter methods for all fields
    public String getEmail() { return email; } // Returns the reporter's email
    public String getAddress() { return address; } // Returns the address of the overflowing bin
    public String getCity() { return city; } // Returns the city where the bin is located
    public String getState() { return state; } // Returns the state of the location
    public String getPostcode() { return postcode; } // Returns the postal code
    public String getId(){return id; }
    public String getStatus(){return "pending"; } //hardcode status
    public String getDescription() { return description; } // Returns the description of the issue
    public String getImageUrl() { return imageUrl; } // Returns the URL of the uploaded image (if available)
    public long getTimestamp() { return timestamp; } // Returns the timestamp of the report
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(this.timestamp);  // Convert the timestamp into a Date object
        return sdf.format(date);  // Format the Date object into a string
    }
    public void setId(String id) {
        this.id = id;
    }
}
