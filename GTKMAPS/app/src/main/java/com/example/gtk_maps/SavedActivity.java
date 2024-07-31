package com.example.gtk_maps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SavedActivity extends AppCompatActivity {

    private SaveManager saveManager;
    private ListView savedLV;
    private Button selectDD, deleteDD;
    private ArrayList<String> titles, labels, coordinates, categories, placeNames, cuisine, openingHours, charges;
    private Map<String,ArrayList<String>> savedMap;
    private static String[] returned = {"titles","labels","coordinates","categories","placeNames","cuisine","opening_hours","charge"};
    private ArrayAdapter<String> savedArrayAdapter;
    private Resources resources;
    private TextView placeDD, transportDD,distanceDD, categoriesDD, categoriesListDD, dateDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        saveManager = SaveManager.getInstance(SavedActivity.this);
        savedMap = saveManager.getSearches();

        titles = savedMap.get("titles");
        labels = savedMap.get("labels");
        coordinates = savedMap.get("coordinates");
        categories = savedMap.get("categories");
        placeNames = savedMap.get("placeNames");
        cuisine = savedMap.get("cuisines");
        openingHours = savedMap.get("openingHours");
        charges = savedMap.get("charges");



        resources = getResources();
        savedLV = findViewById(R.id.savedLV);


            savedArrayAdapter = new ArrayAdapter<>(SavedActivity.this, android.R.layout.simple_list_item_1, titles);
            savedLV.setAdapter(savedArrayAdapter);

            savedLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Dialog dialog = new Dialog(SavedActivity.this, R.style.CustomDialogTheme);
                    dialog.setContentView(R.layout.details_dialog);
                    Window window = dialog.getWindow();
                    if (window!=null) {
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        /*window.setBackgroundDrawableResource(R.drawable.fade);*/
                        window.setWindowAnimations(R.style.DialogAnimation);

                        WindowManager.LayoutParams layoutParams = window.getAttributes();
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Match parent width
                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content height
                        layoutParams.gravity = Gravity.TOP; // Position at the bottom of the screen
                        window.setAttributes(layoutParams);
                    }

                    dialog.setCancelable(true);
                    String[] details= labels.get(position).split(";;");
                    ArrayList<String> categoryDetails = new ArrayList<>(Arrays.asList(details[3].split(";")));

                    Map<String, Object> labelDetails = new HashMap<>();

                    labelDetails.put("place",details[0]);
                    labelDetails.put("transportMode",details[1]);
                    labelDetails.put("distance",details[2]);
                    labelDetails.put("categories",categoryDetails);

                    placeDD = dialog.findViewById(R.id.placeDD);
                    transportDD = dialog.findViewById(R.id.transportDD);
                    distanceDD = dialog.findViewById(R.id.distanceDD);
                    categoriesDD = dialog.findViewById(R.id.categoriesDD);
                    categoriesListDD= dialog.findViewById(R.id.categoriesListDD);
                    dateDD = dialog.findViewById(R.id.dateDD);

                    placeDD.setText(details[0]);
                    String transportMode = resources.getString(R.string.transporte_form)+ " " +details[1];
                    String distance = resources.getString(R.string.distance)+ " " +details[2];
                    transportDD.setText(transportMode);
                    distanceDD.setText(distance);
                    categoriesDD.setText(resources.getString(R.string.categories));
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String category : categoryDetails){
                        stringBuilder.append(category).append("\n");
                    }
                    categoriesListDD.setText(stringBuilder.toString());
                    dateDD.setText(details[4]);

                    selectDD = dialog.findViewById(R.id.selectDD);
                    deleteDD = dialog.findViewById(R.id.deleteDD);
                    selectDD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();

                            Map<String,ArrayList<String>> savedMap= new HashMap<>();
                            savedMap.put("coordinates", new ArrayList<>(Arrays.asList(coordinates.get(position).split(";;"))));
                            savedMap.put("categories", new ArrayList<>(Arrays.asList(categories.get(position).split(";;"))));
                            savedMap.put("placeNames",new ArrayList<>(Arrays.asList(placeNames.get(position).split(";;"))));
                            savedMap.put("cuisines",new ArrayList<>(Arrays.asList(cuisine.get(position).split(";;"))));
                            savedMap.put("openingHours",new ArrayList<>(Arrays.asList(openingHours.get(position).split(";;"))));
                            savedMap.put("charges",new ArrayList<>(Arrays.asList(charges.get(position).split(";;"))));

                            intent.putExtra("label", (Serializable) labelDetails);
                            intent.putExtra("savedMap", (Serializable) savedMap);
                            setResult(RESULT_OK, intent);
                            finish();
                            dialog.dismiss();
                        }
                    });
                    deleteDD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SavedActivity.this);
                            builder.setMessage(R.string.delete_message).setCancelable(true).setPositiveButton(
                                    R.string.positive,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    saveManager.removeSavedSearch(labels.get(position));

                                                    refreshDataSet();
                                                    savedArrayAdapter = new ArrayAdapter<>(SavedActivity.this, android.R.layout.simple_list_item_1, titles);
                                                    savedLV.setAdapter(savedArrayAdapter);

                                                }
                                            });/*
                                            saveManager.removeSavedSearch(labels.get(position));
                                            refreshDataSet();
                                            savedArrayAdapter.remove(labels.get(position));
                                            savedArrayAdapter.notifyDataSetChanged();*/
                                        }
                                    }).setNegativeButton(
                                    R.string.negative,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    }).show();
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }
            });
        }

    @Override
    protected void onPause() {
        super.onPause();
        clearAll();
    }

    private void clearAll(){
        titles.clear();
        labels.clear();
        coordinates.clear();
        categories.clear();
        placeNames.clear();
        cuisine.clear();
        openingHours.clear();
        charges.clear();
    }
        private void refreshDataSet(){

            savedMap = saveManager.getSearches();

            titles= savedMap.get("titles");
            labels= savedMap.get("labels");
            coordinates= savedMap.get("coordinates");
            categories= savedMap.get("categories");
            placeNames= savedMap.get("placeNames");
            cuisine = savedMap.get("cuisines");
            openingHours = savedMap.get("openingHours");
            charges = savedMap.get("charges");

        }
    private void animateListViewItemClick(View view) {
        ViewPropertyAnimator animator = view.animate()
                .alpha(0.5f)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100);
        animator.withEndAction(new Runnable() {
            @Override
            public void run() {
                view.animate()
                        .alpha(1.0f)
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(0);
            }
        });
    }
    }
