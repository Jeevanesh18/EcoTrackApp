package com.example.ecotrack;
//for my report

public class Report {
    private String id;
    private String date;
    private String email;
    private String problem; // Description or waste type
    private String address;
    private String city;
    private String state;
    private String postcode;
    private String imageUrl;
    private String status;
    private String reportType; //differentiate between Overflowing Bin and Special Waste Pickup

    // Default constructor
    public Report() {}

    // Constructor to handle both types
    public Report(String id, String date, String email, String problem, String address,
                  String city, String state, String postcode, String imageUrl,
                  String status, String reportType) {
        this.id = id;
        this.date = date;
        this.email = email;
        this.problem = problem;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postcode = postcode;
        this.imageUrl = imageUrl;
        this.status = status;
        this.reportType = reportType;
    }

    // const for app issue reports
    public Report(String id,String date, String email, String problem, String status, String reportType) {
        this.id = id;
        this.date = date;
        this.email = email;
        this.problem = problem;
        this.status = status;
        this.reportType = reportType;
    }

    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getProblem() { return problem; }
    public void setProblem(String problem) { this.problem = problem; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
}




