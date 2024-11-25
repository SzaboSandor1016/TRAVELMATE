package com.example.gtk_maps;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SaveManager extends Activity {

    private static SaveManager instance;
    private final Context context;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    //private ArrayList<String> categories;
    private static final String[] fileNames ={"preSave.txt","savedTitles.txt","savedLabels.txt","savedCoordinates.txt",
            "savedCategories.txt","savedPlaceNames.txt","savedCuisine.txt","savedOpeningHours.txt","savedCharges.txt"};
    //TODO ADD HERE

    private static final String[] toReturn = {"titles","labels","coordinates","categories","placeNames","cuisines","openingHours","charges"};
    //TODO ADD HERE

    public  interface ReadCallback{
        void onComplete(ArrayList<Save> result);
    }
    public interface WriteCallback{
        void onComplete();
    }
    public  interface GetSearches{
        void onComplete(ArrayList<Save> result);
    }
    public interface AddCallback{
        void onComplete();
    }
    public interface DeleteCallback{
        void onComplete();
    }



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

    private static void readStorage(Context context, ReadCallback callback) {
        executorService.execute(() -> {
            File dir = new File(context.getFilesDir(), "saved");
            File gpxfile = new File(dir, "saves.json");

            ArrayList<Save> result = new ArrayList<>();

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

                    String jsonContent = content.toString();

                    Gson gson = new Gson();
                    Type listType = TypeToken.getParameterized(ArrayList.class, Save.class).getType();
                    result = gson.fromJson(jsonContent,listType);

                    // Now 'content' contains the content read from the file
                    // You can use 'content.toString()' to get it as a String
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("readerError", e.toString());
                }
            } else {
                // File does not exist, handle accordingly
                // For example, you can throw an exception, log a message, etc.
            }

            ArrayList<Save> finalResult = result;
            new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(finalResult));
        });
    }

    private void writeStorage(ArrayList<Save> content, WriteCallback callback){
        executorService.execute(() -> {
            File dir = new File(context.getFilesDir(), "saved");
            if (!dir.exists()) {
                dir.mkdir();
            }


            try {
                File gpxfile = new File(dir, "saves.json");
                FileWriter writer = new FileWriter(gpxfile, false);

                writer.write("[");

                for (Save save: content) {
                    Log.d("writeStorage", "Save item data: " + save.toString());

                    String jsonContent = new GsonBuilder().setPrettyPrinting().create().toJson(save);
                    Log.d("writeStorage", "Generated JSON: " + jsonContent);

                    writer.write(jsonContent);
                    if (save!= content.get(content.size()-1))
                        writer.write(",");
                }
                writer.write("]");

                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(callback::onComplete);
        });
    }

    public void addSearch(Save save, AddCallback callback) {
        ArrayList<Save> add = new ArrayList<>();

        Log.d("saveTitle", save.getTitle());

        //private static String[] usefulTags= {"place","transportMode","distance","categories"};
        Log.d("writeStorage", "Content size: " + add.size());

        readStorage(context, new ReadCallback() {
            @Override
            public void onComplete(ArrayList<Save> result) {
                add.addAll(result);

                add.add(save);
                writeStorage(add, new WriteCallback() {
                    @Override
                    public void onComplete() {
                        add.clear();
                        callback.onComplete();
                    }
                });
            }
        });


    }

    public void getSearches(GetSearches getSearches){

        readStorage(context, new ReadCallback() {
            @Override
            public void onComplete(ArrayList<Save> result) {
                getSearches.onComplete(result);
            }
        });
    }

    public void deleteSavedSearch(Save toRemove, DeleteCallback callback){


        readStorage(context, new ReadCallback() {
            @Override
            public void onComplete(ArrayList<Save> result) {
                ArrayList<Save> saves = new ArrayList<>(result);
                int index = 0;

                for (Save save: saves){
                    if (save.getDate().equals(toRemove.getDate()) && save.getTitle().equals(toRemove.getTitle()))
                        index = saves.indexOf(save);
                }

                saves.remove(index);

                writeStorage(saves, new WriteCallback() {
                    @Override
                    public void onComplete() {
                        callback.onComplete();
                    }
                });
            }
        });


    }


}
