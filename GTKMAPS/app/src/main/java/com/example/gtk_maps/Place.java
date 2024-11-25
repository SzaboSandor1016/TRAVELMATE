package com.example.gtk_maps;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.osmdroid.views.overlay.Marker;

import java.io.Serializable;
@Entity(foreignKeys = @ForeignKey(entity = Search.class, parentColumns = "id", childColumns = "searchId"))
public class Place implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long searchId;
    @Embedded
    private Coordinates coordinates;
    @Embedded
    private Address address;
    private String name;
    private String category;
    private String cuisine;
    private String openingHours;
    private String charge;

    /*public Place(double lat, double lon, String name,String category, String cuisine, String openingHours, String charge){
        this.coordinates  = new Coordinates(lat,lon);
        this.category = category;
        this.name = name;
        this.cuisine = cuisine;
        this.openingHours = openingHours;
        this.charge =charge;
    }*/

    public Place(){
        this.coordinates = new Coordinates();
        this.address = new Address();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
    public void setCoordinatesWithLatLon(double lat, double lon) {
        this.coordinates =new Coordinates(lat,lon);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSearchId() {
        return searchId;
    }

    public void setSearchId(long searchId) {
        this.searchId = searchId;
    }
}
