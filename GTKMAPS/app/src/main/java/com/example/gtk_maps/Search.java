package com.example.gtk_maps;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Search {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long distance;
    private String transport;

    public Search(long distance, String transport){
        this.transport = transport;
        this.distance = distance;
    }

    public Search() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
}
