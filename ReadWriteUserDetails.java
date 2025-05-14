package com.example.ecotrack;

public class ReadWriteUserDetails {
    public String Address, City, State, Postcode;
    public ReadWriteUserDetails(){

    };
    public ReadWriteUserDetails (String textAddress, String textCity, String textState, String textPostcode) {
        this.Address = textAddress;
        this.City = textCity;
        this.State = textState;
        this.Postcode = textPostcode;
    }
}
