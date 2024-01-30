package com.example.gtk_maps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class SavedActivity extends AppCompatActivity {

    private boolean flag;
    private SaveManager saveManager;
    private ListView savedLV;
    private Button selectDD, deleteDD;
    private ArrayList<String> savedNames, savedLabels, savedCoordinates, savedCategories, savedPlaceNames;
    private ArrayAdapter<String> savedArrayAdapter;
    private Resources resources;
    TextView placeDD, transportDD,distanceDD, categoriesDD, categoriesListDD, dateDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        saveManager = new SaveManager(this);

        resources = getResources();
        savedLV = findViewById(R.id.savedLV);
        savedNames = saveManager.getSearchNames();
        savedLabels= saveManager.getSearchLabels();
        savedCoordinates= saveManager.getSearchCoordinates();
        savedCategories = saveManager.getSearchCategories();
        savedPlaceNames = saveManager.getSearchPlaceNames();

            savedArrayAdapter = new ArrayAdapter<>(SavedActivity.this, android.R.layout.simple_list_item_1, savedNames);
            savedLV.setAdapter(savedArrayAdapter);

            savedLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    animateListViewItemClick(view);

                    Dialog dialog = new Dialog(SavedActivity.this);
                    dialog.setContentView(R.layout.details_dialog);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(true);
                    String[] details= savedLabels.get(position).split(";;");
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
                    categoriesListDD.setText(details[3]);
                    dateDD.setText(details[4]);

                    selectDD = dialog.findViewById(R.id.selectDD);
                    deleteDD = dialog.findViewById(R.id.deleteDD);
                    selectDD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();

                            String[] savedCoordinatesArray = savedCoordinates.get(position).split(";;");
                            String[] savedCategoriesArray = savedCategories.get(position).split(";;");
                            String[] savedPlaceNamesArray = savedPlaceNames.get(position).split(";;");
                            ArrayList<String> savedCoordinatesArrayList = new ArrayList<>(Arrays.asList(savedCoordinatesArray));
                            ArrayList<String> savedCategoriesArrayList = new ArrayList<>(Arrays.asList(savedCategoriesArray));
                            ArrayList<String> savedPlaceNamesArrayList = new ArrayList<>(Arrays.asList(savedPlaceNamesArray));

                            intent.putStringArrayListExtra("savedSearch", savedCoordinatesArrayList);
                            intent.putStringArrayListExtra("savedSearchCategories", savedCategoriesArrayList);
                            intent.putStringArrayListExtra("savedSearchNames", savedPlaceNamesArrayList);
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
                                                    saveManager.removeSavedSearch(savedLabels.get(position));

                                                    refreshDataSet();
                                                    savedArrayAdapter = new ArrayAdapter<>(SavedActivity.this, android.R.layout.simple_list_item_1, savedNames);
                                                    savedLV.setAdapter(savedArrayAdapter);

                                                }
                                            });
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
        savedNames.clear();
        savedLabels.clear();
        savedCoordinates.clear();
        savedCategories.clear();
        savedPlaceNames.clear();
    }
        private void refreshDataSet(){
            savedNames = saveManager.getSearchNames();
            savedLabels= saveManager.getSearchLabels();
            savedCoordinates= saveManager.getSearchCoordinates();
            savedCategories = saveManager.getSearchCategories();
            savedPlaceNames = saveManager.getSearchPlaceNames();
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
