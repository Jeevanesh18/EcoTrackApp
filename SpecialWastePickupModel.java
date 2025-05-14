package com.example.ecotrack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SpecialWastePickupModel {

    private String email;       // User's email address
    private String address;     // Address where the waste is to be picked up
    private String city;        // City of the pickup location
    private String state;       // State of the pickup location
    private String postcode;    // Postal code of the pickup location
    private String wasteType;   // Type of waste to be picked up
    private String imageUrl;    // URL of the uploaded image (optional)
    private long timestamp;     // Time when the request was submitted

    // Default constructor required for Firebase deserialization
    public SpecialWastePickupModel() { }

    // Parameterized constructor to initialize all fields
    public SpecialWastePickupModel(String email, String address, String city,
                                   String state, String postcode, String wasteType,
                                   String imageUrl, long timestamp) {
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postcode = postcode;
        this.wasteType = wasteType;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Getter method for email
    public String getEmail() { return email; }

    // Getter method for address
    public String getAddress() { return address; }

    // Getter method for city
    public String getCity() { return city; }

    // Getter method for state
    public String getState() { return state; }

    // Getter method for postcode
    public String getPostcode() { return postcode; }

    // Getter method for waste type
    public String getWasteType() { return wasteType; }

    // Getter method for image URL
    public String getImageUrl() { return imageUrl; }

    // Getter method for timestamp
    public long getTimestamp() { return timestamp; }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(this.timestamp);  // Convert the timestamp into a Date object
        return sdf.format(date);  // Format the Date object into a string
    }
}

