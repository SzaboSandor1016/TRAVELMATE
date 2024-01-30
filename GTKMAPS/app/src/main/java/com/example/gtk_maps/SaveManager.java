package com.example.gtk_maps;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SaveManager extends Activity {

    private Context context;
    private String address, place, transportMode, distance, name;
    private ArrayList<String> categories;

    public SaveManager(Context context){
        this.context=context.getApplicationContext();
        File dir = new File(context.getFilesDir(), "saved");
        if(!dir.exists()){
            dir.mkdir();
        }
    }

    public void setAddress(String city, String street, String houseNumber) {
        String formattedAddress = "";
        if (!city.equals("")){
            formattedAddress= formattedAddress+city;
        }
        if (!street.equals("")){
            formattedAddress= formattedAddress+ ", "+ street;
        }
        if (!houseNumber.equals("")){
            formattedAddress= formattedAddress + " "+ houseNumber +".";
        }
        this.address= formattedAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories= categories;
    }
    public void resetAddressAndPlace(){
        this.address= null;
        this.place= null;
    }

private String generateID(String address, String place, String transportMode, String distance){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm.ss");
        String currentDate = sdf.format(new Date());
        if (address!=null) {
            return String.format("%a%b%c-%d", address.substring(0, 3),transportMode.substring(0, 2), distance, currentDate);
        }
        return String.format("%a%b%c-%d", place.substring(0, 3),transportMode.substring(0, 2), distance, currentDate);

    }

    private String concatCategories(ArrayList<String> categories){
        StringBuilder cCategories = new StringBuilder(new String());
        cCategories.append(categories.get(0));
        for(int i=1; i<categories.size();i++){
            cCategories.append(",").append(categories.get(i));
        }
        return cCategories.toString();

    }
    private String concatCoordinates(ArrayList<String> coordinates){
        StringBuilder concatCoordinates = new StringBuilder(coordinates.get(0));
        for (int i=1; i< coordinates.size(); i++){
            concatCoordinates.append(";;").append(coordinates.get(i));
        }
        return concatCoordinates.toString();
    }
    private String concatCategoryLabels(ArrayList<String> categories) {
        StringBuilder concatCategoryLabels= new StringBuilder(new String());
        for (int i=0; i< categories.size(); i++){
            concatCategoryLabels.append(categories.get(i)).append(";;");
        }
        return concatCategoryLabels.toString();
    }
    private String concatNames(ArrayList<String> names) {
        StringBuilder concatNames= new StringBuilder(new String());
        for (int i=0; i< names.size(); i++){
            concatNames.append(names.get(i)).append(";;");
        }
        return concatNames.toString();
    }
    private String generateSaveRecord(String name, String recordLabel, String recordCoordinates, String recordCategories, String recordNames){
        return name+ "//"+ recordLabel+ "//"+ recordCoordinates+ "//"+ recordCategories + "//" + recordNames;
    }
    private String generateLabel(String address, String place, String transportMode, String distance, ArrayList<String>categories){
        String shownCategories;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String currentDate = sdf.format(new Date());
        if (categories!=null) {
            shownCategories= concatCategories(categories);
        }else{
            shownCategories="";
        }
        if (address==null)
            return place + ";;"+ transportMode+ ";;"+ distance+ ";;"+shownCategories+ ";;"+ currentDate;
        else
            return address + ";;"+ transportMode+ ";;"+ distance+ ";;"+shownCategories+ ";;"+ currentDate;
    }
    private String readStorage(String storageName) {
        String fileContent = new String();
        File dir = new File(context.getFilesDir(), "saved");
        File gpxfile = new File(dir, storageName);

        if (gpxfile.exists()) {
            try {
                FileReader reader = new FileReader(gpxfile);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                    // If you want to add newline characters between lines, uncomment the next line
                    //content.append(System.lineSeparator());
                }

                reader.close();
                fileContent= content.toString();
                // Now 'content' contains the content read from the file
                // You can use 'content.toString()' to get it as a String
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // File does not exist, handle accordingly
            // For example, you can throw an exception, log a message, etc.
        }
        Log.d("fileContent", fileContent);
        return fileContent;
    }

    private void writeStorage(String storageName, String content){
        File dir = new File(context.getFilesDir(), "saved");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, storageName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(content);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void preAddSearch(){
        //String id= generateID(address,place,transportMode,distance);
        String label= generateLabel(address, place, transportMode,distance, categories);
        writeStorage("preSave.txt", label);
    }
    /*public void saveQrCodeContent(String name, String place, String[] coordinates){
        String concatCoordinates = concatCoordinates(coordinates);
        String label = generateLabel("",place, "","",null);
        String saveRecord = generateSaveRecord(name,label,concatCoordinates);
        String savedSearches= readStorage("savedSearches.txt");
        savedSearches += saveRecord + "::";
        writeStorage("savedSearches.txt",savedSearches);
    }*/
    public void addSearch(String name,ArrayList<String> coordinates, ArrayList<String> categories,ArrayList<String> names){

        String concatCoordinates = concatCoordinates(coordinates);
        String concatCategories = concatCategoryLabels(categories);
        String concatNames = concatNames(names);
        String preSavedLabel = readStorage("preSave.txt");
        String savedSearches = readStorage("savedSearches.txt");
        String saveRecord = generateSaveRecord(name, preSavedLabel, concatCoordinates, concatCategories, concatNames);
        Log.d("fileContent", preSavedLabel);
        savedSearches += saveRecord + "::";
        writeStorage("savedSearches.txt", savedSearches);
    }
    public void addSelectedSearch(String name,ArrayList<String> coordinates, ArrayList<String> categories,ArrayList<String> names, ArrayList<String> selectedCategories) {

        String concatCoordinates = concatCoordinates(coordinates);
        String concatCategories = concatCategoryLabels(categories);
        String concatNames = concatNames(names);
        String preSavedLabel = readStorage("preSave.txt");
        String savedSearches = readStorage("savedSearches.txt");
        String[] labelArray= preSavedLabel.split(";;");
        String label= labelArray[0]+";;"+labelArray[1]+";;"+labelArray[2]+";;"+concatCategories(selectedCategories)+";;"+labelArray[4];
        String saveRecord = generateSaveRecord(name, label, concatCoordinates, concatCategories, concatNames);
        Log.d("fileContent", preSavedLabel);
        savedSearches += saveRecord + "::";
        writeStorage("savedSearches.txt", savedSearches);
    }

    private String[] getSearches(){
        String savedSearches=readStorage("savedSearches.txt");
        Log.d("getSearches", savedSearches);
        return savedSearches.split("::");
    }
    public ArrayList<String> getSearchNames(){
        String[] searches = getSearches();
        Log.d("searches", searches[0]);
        ArrayList<String> searchLabels = new ArrayList<>();
        for (String search : searches) {
            String[] record = search.split("//");
            if (record.length > 1)
                searchLabels.add(record[0]);
        }
        return searchLabels;
    }
    public ArrayList<String> getSearchLabels(){
        String[] searches = getSearches();
            Log.d("searches", searches[0]);
            ArrayList<String> searchLabels = new ArrayList<>();
        for (String search : searches) {
            String[] record = search.split("//");
            if (record.length > 1)
                searchLabels.add(record[1]);
        }
            return searchLabels;
    }
    public ArrayList<String> getSearchCoordinates() {
        String[] searches = getSearches();
        ArrayList<String> searchCoordinates = new ArrayList<>();
        for (String search : searches) {
            String[] record = search.split("//");
            if (record.length > 1)
                searchCoordinates.add(record[2]);
        }
        return searchCoordinates;
    }
    public ArrayList<String> getSearchCategories() {
        String[] searches = getSearches();
        ArrayList<String> searchCategories = new ArrayList<>();
        for (String search : searches) {
            String[] record = search.split("//");
            if (record.length > 1)
                searchCategories.add(record[3]);
        }
        return searchCategories;
    }
    public ArrayList<String> getSearchPlaceNames() {
        String[] searches = getSearches();
        ArrayList<String> searchNames = new ArrayList<>();
        for (String search : searches) {
            String[] record = search.split("//");
            if (record.length > 1)
                searchNames.add(record[4]);
        }
        return searchNames;
    }
    public void removeSavedSearch(String savedSearchLabel){
        StringBuilder removedSearchArray= new StringBuilder(new String());

        ArrayList<String> searchNames = getSearchNames();
        ArrayList<String> searchLabels = getSearchLabels();
        ArrayList<String> searchCoordinates = getSearchCoordinates();
        ArrayList<String> searchCategories = getSearchCategories();
        ArrayList<String> searchPlaceNames = getSearchPlaceNames();

        searchNames.remove(searchLabels.indexOf(savedSearchLabel));
        searchCoordinates.remove(searchLabels.indexOf(savedSearchLabel));
        searchCategories.remove(searchLabels.indexOf(savedSearchLabel));
        searchPlaceNames.remove(searchLabels.indexOf(savedSearchLabel));
        searchLabels.remove(savedSearchLabel);

        for (int i=0; i< searchLabels.size(); i++){
            removedSearchArray.append(generateSaveRecord(searchNames.get(i), searchLabels.get(i), searchCoordinates.get(i), searchCategories.get(i), searchPlaceNames.get(i))).append("::");
        }
        writeStorage("savedSearches.txt", removedSearchArray.toString());
        Log.d("savedSearches", removedSearchArray.toString());
    }
}
