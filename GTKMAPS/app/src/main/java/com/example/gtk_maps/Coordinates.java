package com.example.gtk_maps;

import androidx.room.Entity;

import java.io.Serializable;
public class Coordinates implements Serializable {
    private double lat;
    private double lon;

    public Coordinates() {}

    public Coordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
