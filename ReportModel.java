package com.example.ecotrack;
//report app issue
public class ReportModel {

    private String email;       // The reporter's email address
    private String problem; // Description of the reported issue
    private long time;          // Timestamp of when the report was created

    // Default constructor required for Firebase deserialization
    public ReportModel() {
    }

    // Parameterized constructor for creating a new report instance
    public ReportModel(String email, String description, long time) {
        this.email = email;
        this.problem = description;
        this.time = time;
    }

    // Getter method for email
    public String getEmail() {
        return email;
    }

    // Getter method for description
    public String getDescription() {
        return problem;
    }

    // Getter method for timestamp
    public long getTime() {
        return time;
    }
}