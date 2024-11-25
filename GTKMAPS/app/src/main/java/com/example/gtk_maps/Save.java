package com.example.gtk_maps;

import java.util.ArrayList;

public class Save {

    private ArrayList<Place> savedPlaces;
    private Address startAddress;
    private String title;
    private int distance;
    private ArrayList<String> categories;
    private String transport;
    private String date;

    public Save(){}

    public Save(String title,String transport,String date, int distance, ArrayList<String> categories, ArrayList<Place> savedPlaces,Address startAddress){
        this.title = title;
        this.transport = transport;
        this.date = date;
        this.distance = distance;
        this.categories = new ArrayList<>();
        this.categories.addAll(categories);
        this.savedPlaces = new ArrayList<>();
        this.savedPlaces.addAll(savedPlaces);
        this.startAddress = startAddress;
    }
    public ArrayList<Place> getSavedPlaces() {
        return savedPlaces;
    }

    public void setSavedPlaces(ArrayList<Place> savedPlaces) {
        this.savedPlaces = savedPlaces;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }
    /*public String getCategoriesAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(String category: categories)
            stringBuilder.append(category).append("/n");
        return stringBuilder.toString();
    }*/

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public Address getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(Address startAddress) {
        this.startAddress = startAddress;
    }
}
