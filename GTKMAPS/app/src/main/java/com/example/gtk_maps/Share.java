package com.example.gtk_maps;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Share implements Serializable {

    private String op;
    private transient String opActual;
    private String title;
    private String note;
    private Map<String,Boolean> sharedWith;
    private transient ArrayList<String> sharedWithStrings;
    private ArrayList<Place> sharedPlaces;
    private ShareDetails shareDetails;
    private transient DatabaseReference referenceToShare;

    public Share(){}

    public Share(String title, ArrayList<Place> sharedPlaces, String note, String transportMode, ArrayList<String> categories, String startName, int distance, Address address, String date) {
        this.title = title;
        this.note = note;
        this.sharedPlaces = new ArrayList<>();
        this.sharedPlaces.addAll(sharedPlaces);
        this.shareDetails = new ShareDetails(transportMode,startName,date, categories, distance,address);
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Boolean> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(Map<String, Boolean> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public ArrayList<Place> getSharedPlaces() {
        return sharedPlaces;
    }

    public void setSharedPlaces(ArrayList<Place> sharedPlaces) {
        this.sharedPlaces = sharedPlaces;
    }

    public ShareDetails getShareDetails() {
        return shareDetails;
    }

    public void setShareDetails(ShareDetails shareDetails) {
        this.shareDetails = shareDetails;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<String> getSharedWithStrings() {
        return sharedWithStrings;
    }

    public void setSharedWithStrings(ArrayList<String> sharedWithStrings) {
        this.sharedWithStrings = sharedWithStrings;
    }

    public String getOpActual() {
        return opActual;
    }

    public void setOpActual(String opActual) {
        this.opActual = opActual;
    }

    public DatabaseReference getReferenceToShare() {
        return referenceToShare;
    }

    public void setReferenceToShare(DatabaseReference referenceToShare) {
        this.referenceToShare = referenceToShare;
    }
}
