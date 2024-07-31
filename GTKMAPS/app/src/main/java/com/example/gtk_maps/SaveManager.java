package com.example.gtk_maps;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SaveManager extends Activity {

    private static SaveManager instance;
    private Context context;
    //private ArrayList<String> categories;
    private static String[] fileNames ={"preSave.txt","savedTitles.txt","savedLabels.txt","savedCoordinates.txt",
            "savedCategories.txt","savedPlaceNames.txt","savedCuisine.txt","savedOpeningHours.txt","savedCharges.txt"};
    //TODO ADD HERE

    private static String[] toReturn = {"titles","labels","coordinates","categories","placeNames","cuisines","openingHours","charges"};
    //TODO ADD HERE
    public SaveManager(Context context){
        this.context=context.getApplicationContext();
        File dir = new File(context.getFilesDir(), "saved");
        if(!dir.exists()){
            dir.mkdir();
        }
    }

    public static SaveManager getInstance(Context context){
        if (instance == null && context!=null) {
            instance = new SaveManager(context);
        }
        return instance;
    }

    private String concatStringArrayList(ArrayList<String> arrayList){
        StringBuilder cArrayList = new StringBuilder(arrayList.get(0));
        for(int i=1; i<arrayList.size();i++){
            cArrayList.append(";;").append(arrayList.get(i));
        }
        return cArrayList.toString();

    }

    private ArrayList<String> readStorage(String storageName) {
        ArrayList<String> fileContent = new ArrayList<>();
        File dir = new File(context.getFilesDir(), "saved");
        File gpxfile = new File(dir, storageName);

        if (gpxfile.exists()) {
            try {
                FileReader reader = new FileReader(gpxfile);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    fileContent.add(line);
                    // If you want to add newline characters between lines, uncomment the next line
                    //content.append(System.lineSeparator());
                }

                reader.close();
                // Now 'content' contains the content read from the file
                // You can use 'content.toString()' to get it as a String
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // File does not exist, handle accordingly
            // For example, you can throw an exception, log a message, etc.
        }
        Log.d("fileContent", String.valueOf(fileContent));
        return fileContent;
    }

    private void writeStorage(String storageName, String content, boolean isPre){
        File dir = new File(context.getFilesDir(), "saved");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, storageName);
            FileWriter writer = new FileWriter(gpxfile, !isPre);
            writer.write(content);
            if (!content.equals("") || !isPre)
                writer.write("\n");


            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void addSearch(String name,String label, Map<String,ArrayList<String>> saveDetails) {

        //private static String[] usefulTags= {"place","transportMode","distance","categories"};

        writeStorage("saved_"+toReturn[0]+".txt", name,false);
        writeStorage("saved_"+ toReturn[1]+".txt", label,false);

        for (int i=2; i< toReturn.length; i++){
            writeStorage("saved_"+ toReturn[i]+".txt", concatStringArrayList(saveDetails.get(toReturn[i])), false);
        }

    }

    public Map<String,ArrayList<String>> getSearches(){
        Map<String,ArrayList<String>> savedMap= new HashMap<>();

        for (int i=0; i<toReturn.length;i++){
            savedMap.put(toReturn[i],readStorage("saved_"+toReturn[i]+".txt"));
        }

        return savedMap;
    }

    public void removeSavedSearch(String toRemove){

        Map<String,ArrayList<String>> savedSearches = getSearches();
        int removeIndex =savedSearches.get(toReturn[1]).indexOf(toRemove);

        for (int i=0; i<toReturn.length; i++){
            savedSearches.get(toReturn[i]).remove(removeIndex);
        }

        for(int i=0; i< savedSearches.size(); i++){
            ArrayList<String> entry = savedSearches.get(toReturn[i]);
            writeStorage("saved_"+toReturn[i]+".txt","",true);
            for (String entryElement: entry){
                writeStorage("saved_"+toReturn[i]+".txt",entryElement,false);
            }
        }
    }
}
