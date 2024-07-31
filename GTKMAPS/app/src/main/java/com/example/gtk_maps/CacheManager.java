package com.example.gtk_maps;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class CacheManager extends AppCompatActivity {
    private Context context;
    private String searchLabel;
    CacheManager(Context context){
        this.context=context;
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
                bufferedReader.close();
                fileContent= content.toString();
                // Now 'content' contains the content read from the file
                // You can use 'content.toString()' to get it as a String
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // File does not exist, handle accordingly
            // For example, you can throw an exception, log a message, etc.
            return "";
        }
        Log.d("fileContent", fileContent);
        return fileContent;
    }

    private String concatMatchCoordinates(ArrayList<String> matchCoordinates){
        StringBuilder concatCoordinates = new StringBuilder();
        for (String matchCoordinate: matchCoordinates){
            concatCoordinates.append(matchCoordinate).append(";;");
        }

        return concatCoordinates.toString();
    }

    private String concatMatchLabels(ArrayList<String> matchLabels){
        StringBuilder concatLabels = new StringBuilder();
        for (String matchLabel: matchLabels){
            concatLabels.append(matchLabel).append(";;");
        }

        return concatLabels.toString();
    }

    public boolean checkCacheFileContentIfContains(){
        boolean contains=false;
        StringBuilder incrementedCacheFileContent = new StringBuilder();
        String cacheFileContent = readStorage("searchCache.txt");
        if (cacheFileContent.length()!=0) {
            String[] splitToRecords = cacheFileContent.split("::");
            for (String record : splitToRecords) {
                String[] splitRecord = record.split("//");
                if (splitRecord[0].equals(searchLabel)) {
                    splitRecord[3] = String.valueOf(Integer.parseInt(splitRecord[3]) + 1);
                    contains = true;
                }
                String concatRecord = splitRecord[0] + "//" + splitRecord[1] + "//" + splitRecord[2] + "//" + splitRecord[3] + "//";
                incrementedCacheFileContent.append(concatRecord).append("::");
            }
            writeStorage("searchCache.txt", incrementedCacheFileContent.toString());
        }
        return contains;
    }
    public ArrayList<String> getCacheFileMatchLabels(){
        ArrayList<String> cacheFileMatchLabels= new ArrayList<>();
        String cacheFileContent = readStorage("searchCache.txt");
        String[] splitToRecords = cacheFileContent.split("::");
        for (String record: splitToRecords){
            String[] splitRecord = record.split("//");
            if (splitRecord[0].equals(searchLabel)){
                String[] splitRecordLabels= splitRecord[1].split(";;");
                cacheFileMatchLabels.addAll(Arrays.asList(splitRecordLabels));
            }
        }
        return cacheFileMatchLabels;
    }
    public ArrayList<String> getCacheFileMatchCoordinates(){
        ArrayList<String> cacheFileMatchCoordinates= new ArrayList<>();
        String cacheFileContent = readStorage("searchCache.txt");
        String[] splitToRecords = cacheFileContent.split("::");
        for (String record: splitToRecords){
            String[] splitRecord = record.split("//");
            if (splitRecord[0].equals(searchLabel)){
                String[] splitRecordCoordinates= splitRecord[2].split(";;");
                cacheFileMatchCoordinates.addAll(Arrays.asList(splitRecordCoordinates));
            }
        }
        return cacheFileMatchCoordinates;
    }

    public void setSearchLabelDetails(String nameOfPlace, String city, String street, String houseNumber){
        this.searchLabel = nameOfPlace+city+street+houseNumber;
    }


    public void writeToCacheFile(ArrayList<String> matchCoordinates, ArrayList<String> matchLabels){
        String concatMatchCoordinates = concatMatchCoordinates(matchCoordinates);
        String concatMatchLabels = concatMatchLabels(matchLabels);
        String cacheFileContent = readStorage("searchCache.txt");

        String[] splitToRecords = cacheFileContent.split("::");
        if (splitToRecords.length==10) {
            int min = Integer.parseInt(splitToRecords[0].split("//")[3]);
            for (String record : splitToRecords) {
                String[] splitRecord = record.split("//");
                if (Integer.parseInt(splitRecord[3])<min){
                    min=Integer.parseInt(splitRecord[3]);
                }
            }
            for (String record : splitToRecords) {
                String[] splitRecord = record.split("//");
                if (Integer.parseInt(splitRecord[3])==min){
                    cacheFileContent.replace(record+"::","");
                }
            }
            cacheFileContent+=searchLabel +"//"+ concatMatchLabels +"//"+concatMatchCoordinates+"//1//::";
        }else
            cacheFileContent+=searchLabel +"//"+ concatMatchLabels +"//"+concatMatchCoordinates+"//1//::";
        writeStorage("searchCache.txt",cacheFileContent);
    }

}
