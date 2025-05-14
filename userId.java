package com.example.ecotrack;
//class to store userId since it uniquely identify the users
public class userId {
    private static userId instance;
    private String userId;

    private userId(){ }

    public static userId getInstance(){
        if (instance == null){
            instance = new userId();
        }
        return instance;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
}
