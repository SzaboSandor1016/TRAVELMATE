package com.example.gtk_maps;

import androidx.room.Entity;

import java.io.Serializable;

public class Address implements Serializable {

    private String city;
    private String street;
    private String houseNumber;

    public Address(){}

    public Address(String city,String street,String houseNumber){
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
    }

    public String AddressAsString(){
        StringBuilder address = new StringBuilder();
        if (this.city != null && this.street != null && this.houseNumber != null) {
            address.append(this.city).append(", ").append(this.street).append(" ").append(this.houseNumber);
            return address.toString();
        }
        if (this.city != null && this.street != null){
            address.append(this.city).append(", ").append(this.street);
            return address.toString();
        }
        return "unknown";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
