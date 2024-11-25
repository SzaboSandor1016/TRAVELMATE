package com.example.gtk_maps;

import java.io.Serializable;
import java.util.ArrayList;

public class ShareDetails implements Serializable {

    private String transportMode;
    private String startName;
    private String date;
    private ArrayList<String> categories;
    private int distance;
    private Address address;


    public ShareDetails(){}

    public ShareDetails(String transportMode, String startName, String date, ArrayList<String> categories, int distance, Address address) {
        this.transportMode = transportMode;
        this.startName = startName;
        this.categories = new ArrayList<>();
        this.categories.addAll(categories);
        this.distance = distance;
        this.address = address;
        this.date = date;

    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}