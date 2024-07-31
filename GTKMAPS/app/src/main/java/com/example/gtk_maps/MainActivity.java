package com.example.gtk_maps;

import static android.app.ProgressDialog.show;
import static java.lang.Math.abs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.Distance;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.nio.channels.SelectableChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// -------------------------------------------------------------------------------------------------------------
// | MainActivity                                                                                              |
// | Is for processing, and marking the coordinates coming from the MainActivity                               |
// | Contains:                                                                                                 |
// |                                                                                                           |
// | code necessary for embedding the OSM                                                                      |
// | menuBTN's OnClickListener                                                                                 |
// | markCoordinatesOnMap                                                                                      |
// -------------------------------------------------------------------------------------------------------------

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int REQUEST_CODE = 1;
    private MapView map = null;


    private SaveManager saveManager;
    private CategoryManager categoryManager;
    private FirebaseManager firebaseManager;
    private OpenRouteServiceAPI openRouteServiceAPI;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private int start=0;
    //private boolean enabledGPS, qrResponse= false;
    private Marker startMarker, newstartMarker;
    private ArrayList<Marker> selectedMarkers, selectedTextMarkers, allMarkers, textMarkers;
    private ArrayList<String> selectedMarkersArray,selectedTagsArray,selectedNamesArray,selectedCategoriesArray,
    selectedCuisineArray, selectedOpeningHoursArray, selectedChargesArray;
    private ArrayList<String> coordinates,categories,names,cuisine,openingHours,charge;
    private Map<String, Object> labelDetails;
    private GeoPoint currentLocation;
    private TextView markerNameMD, markerCategoryMD,markerCuisineMD,markerChargeMD,markerOpeningHoursMD,
            openTV, cuisineTV, chargesTV;
    private Button addMD, removeMD;
    private ImageButton menuBTN, addSaveBTN, routeBTN, shareSearchBTN;
    private Resources resources;
    private static String[] labelData= {"place","transportMode","distance","categories"};


    //Code lines necessary for the integration of the OSM
    //----------------------------------------------------------------------------------------------------------------
    //BEGIN
    //----------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //inflate and create the map
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        //String[] permissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        // Check for ACCESS_FINE_LOCATION permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


        //requestPermissionsIfNecessary(permissions
                // if you need to show the current location, uncomment the line below
                //
                // WRITE_EXTERNAL_STORAGE is required in order to show the map

        //);

        resources = MainActivity.this.getResources();

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        openRouteServiceAPI = new OpenRouteServiceAPI();

        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);

        saveManager = SaveManager.getInstance(MainActivity.this);
        categoryManager = new CategoryManager(MainActivity.this);
        firebaseManager = FirebaseManager.getInstance(MainActivity.this,mAuth,mDatabase,sharedPreferences);

        selectedMarkers = new ArrayList<>();
        selectedMarkersArray = new ArrayList<>();
        selectedTagsArray = new ArrayList<>();
        selectedNamesArray = new ArrayList<>();
        selectedCategoriesArray= new ArrayList<>();
        selectedCuisineArray = new ArrayList<>();
        selectedOpeningHoursArray = new ArrayList<>();
        selectedChargesArray = new ArrayList<>();
        selectedTextMarkers = new ArrayList<>();
        textMarkers = new ArrayList<>();
        allMarkers = new ArrayList<>();


        coordinates = new ArrayList<>();
        categories = new ArrayList<>();
        names = new ArrayList<>();
        cuisine= new ArrayList<>();
        openingHours= new ArrayList<>();
        charge= new ArrayList<>();

        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint firstPoint = new GeoPoint(47.09327, 17.91149);
        mapController.setCenter(firstPoint);
        map.setMultiTouchControls(true);
        startMarker = new Marker(map);
        startMarker.setPosition(firstPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(startMarker);
        startMarker.setIcon(resources.getDrawable(R.drawable.blue_marker));




        addSaveBTN= findViewById(R.id.addSaveBTN);
        addSaveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                Dialog dialog = new Dialog(MainActivity.this,R.style.CustomDialogTheme);
                dialog.setContentView(R.layout.add_search_dialog);

                Window window = dialog.getWindow();
                if(window!=null) {
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
                TextView titleASD = dialog.findViewById(R.id.titleASD);
                EditText nameASD = dialog.findViewById(R.id.nameASD);

                titleASD.setText(resources.getString(R.string.save_as));

                Button saveASD =dialog.findViewById(R.id.saveASD);
                saveASD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!nameASD.getText().toString().trim().equals("")) {
                            if (selectedMarkersArray.size() > 1) {
                                labelDetails.replace("categories", selectedCategoriesArray);
                                String label = generateLabel(labelDetails);
                                Map<String, ArrayList<String>> saveDetails = new HashMap<>();
                                saveDetails.put("coordinates", selectedMarkersArray);
                                saveDetails.put("categories", selectedTagsArray);
                                saveDetails.put("placeNames", selectedNamesArray);
                                saveDetails.put("cuisines", selectedCuisineArray);
                                saveDetails.put("openingHours", selectedOpeningHoursArray);
                                saveDetails.put("charges", selectedChargesArray);

                                saveManager.addSearch(nameASD.getText().toString().trim(), label, saveDetails);

                            } else {
                                String label = generateLabel(labelDetails);
                                Map<String, ArrayList<String>> saveDetails = new HashMap<>();
                                saveDetails.put("coordinates", coordinates);
                                saveDetails.put("categories", categories);
                                saveDetails.put("placeNames", names);
                                saveDetails.put("cuisines", cuisine);
                                saveDetails.put("openingHours", openingHours);
                                saveDetails.put("charges", charge);

                                saveManager.addSearch(nameASD.getText().toString().trim(), label, saveDetails);
                            }

                            Toast.makeText(MainActivity.this, R.string.add_toast, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }else {
                            Toast.makeText(MainActivity.this,R.string.save_title_empty,Toast.LENGTH_LONG).show();
                        }
                    }
                });
                    dialog.show();
            }
        });


        //OnClickListener for routeBTN
        //When the routeBTN is clicked the "Choose transportation mode" dialog is shown
        //----------------------------------------------------------------------------------------------------------------
        //BEGINNING OF routeBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------
        routeBTN = findViewById(R.id.routeBTN);
        routeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                showTransportationDialog(selectedMarkers);

            }
        });
        //----------------------------------------------------------------------------------------------------------------
        //END OF routeBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------

        menuBTN = findViewById(R.id.menuBTN);
        //Start the MenuActivity if the Menu button is clicked
        //----------------------------------------------------------------------------------------------------------------
        //BEGINNING OF menuBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------
        menuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        shareSearchBTN = findViewById(R.id.shareSearchBTN);

        if (mAuth.getCurrentUser()!=null || sharedPreferences.getBoolean("loggedIn", false)){
            shareSearchBTN.setVisibility(View.VISIBLE);
        }else {
            shareSearchBTN.setVisibility(View.INVISIBLE);
        }
        shareSearchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                Dialog dialog = new Dialog(MainActivity.this,R.style.CustomDialogTheme);
                dialog.setContentView(R.layout.share_search_dialog);

                Window window = dialog.getWindow();
                if(window!=null) {
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

                TextView titleSSD = dialog.findViewById(R.id.titleSSD);
                /*TextView addedEmailSSD = dialog.findViewById(R.id.addedEmailsSSD);*/
                EditText withSSD = dialog.findViewById(R.id.withSSD);
                EditText nameSSD = dialog.findViewById(R.id.nameSSD);

                ListView addedEmailsSSD = dialog.findViewById(R.id.addedEmailsSSD);
                ListView contactsListSSD = dialog.findViewById(R.id.contactListSSD);

                titleSSD.setText(resources.getString(R.string.share));

                //StringBuilder stringBuilder = new StringBuilder();
                ArrayList<String> usernames = new ArrayList<>();
                ArrayList<String> recentUsernames = new ArrayList<>();

                ArrayAdapter withEmailsArrayAdapter = new ArrayAdapter(dialog.getContext(),android.R.layout.simple_list_item_1,usernames);
                addedEmailsSSD.setAdapter(withEmailsArrayAdapter);

                ArrayAdapter contactsArrayAdapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_list_item_1, recentUsernames);
                contactsListSSD.setAdapter(contactsArrayAdapter);

                addedEmailsSSD.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        withEmailsArrayAdapter.remove(usernames.get(position));
                        withEmailsArrayAdapter.notifyDataSetChanged();
                        return true;
                    }
                });

                contactsListSSD.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!usernames.contains((String) recentUsernames.get(position))) {
                            usernames.add((String) recentUsernames.get(position));
                            withEmailsArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

                firebaseManager.getRecentSharedWithUsernames(new FirebaseManager.RecentUsernames() {
                    @Override
                    public void onSuccess(ArrayList<String> usernames) {
                        recentUsernames.addAll(usernames);

                        contactsArrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure() {

                    }
                });

                ImageButton addMoreSSD = dialog.findViewById(R.id.addMoreSSD);
                ImageButton contactsSSD = dialog.findViewById(R.id.contactsSSD);


                contactsSSD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        animateImageButton(v);
                        if (contactsListSSD.getVisibility()== View.GONE){
                            contactsListSSD.setVisibility(View.VISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                            contactsListSSD.startAnimation(animation);
                        }else {
                            contactsListSSD.setVisibility(View.GONE);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                            contactsListSSD.startAnimation(animation);
                        }
                    }
                });

                addMoreSSD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animateImageButton(v);
                        if (!usernames.contains(withSSD.getText().toString().trim())) {
                            if (!withSSD.getText().toString().trim().equals("")) {
                                usernames.add(withSSD.getText().toString().trim());

                                withEmailsArrayAdapter.notifyDataSetChanged();


                                withSSD.setText("");
                            }else {
                                Toast.makeText(MainActivity.this,R.string.empty_addressee,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });


                Button saveSSD =dialog.findViewById(R.id.saveSSD);
                saveSSD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (usernames.size()!=0) {
                            if (!nameSSD.getText().toString().trim().equals("")) {

                                for(String username: usernames){
                                    if (!recentUsernames.contains(username)){
                                        recentUsernames.add(username);
                                    }
                                }

                                firebaseManager.addRecentlySharedWithUsername(recentUsernames);

                                labelDetails.put("date", getCurrentDate());


                                if (selectedMarkersArray.size() > 1) {
                                    labelDetails.replace("categories", selectedCategoriesArray);

                                    Map<String, Object> shareDetails = new HashMap<>();
                                    shareDetails.put("coordinates", selectedMarkersArray);
                                    shareDetails.put("categories", selectedTagsArray);
                                    shareDetails.put("placeNames", selectedNamesArray);
                                    shareDetails.put("cuisines", selectedCuisineArray);
                                    shareDetails.put("openingHours", selectedOpeningHoursArray);
                                    shareDetails.put("charges", selectedChargesArray);

                                    firebaseManager.shareSearch(usernames, nameSSD.getText().toString().trim(),
                                            labelDetails, shareDetails);

                                } else {

                                    Map<String, Object> shareDetails = new HashMap<>();
                                    shareDetails.put("coordinates", coordinates);
                                    shareDetails.put("categories", categories);
                                    shareDetails.put("placeNames", names);
                                    shareDetails.put("cuisines", cuisine);
                                    shareDetails.put("openingHours", openingHours);
                                    shareDetails.put("charges", charge);

                                    firebaseManager.shareSearch(usernames, nameSSD.getText().toString().trim(),
                                            labelDetails, shareDetails);

                                }
                                dialog.dismiss();
                            }else{
                                Toast.makeText(MainActivity.this,R.string.share_title_empty,Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(MainActivity.this,R.string.empty_addressee_list,Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });

    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF menuBTN's OnClickListener
    //----------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== REQUEST_CODE){
            if (resultCode== RESULT_OK){
                //Handle the response of the MenuActivity,
                // calls the markCoordinatesOnMap function
                //----------------------------------------------------------------------------------------------------------------
                //BEGIN
                //----------------------------------------------------------------------------------------------------------------

                    if (data.getSerializableExtra("extractedMap")!=null){
                        addSaveBTN.setVisibility(View.VISIBLE);
                        shareSearchBTN.setVisibility(View.VISIBLE);
                        clearAll();
                        Map<String,ArrayList<String>> extractedMap= (Map<String, ArrayList<String>>) data.getSerializableExtra("extractedMap");
                        labelDetails = (Map<String, Object>) data.getSerializableExtra("label");
                        if (extractedMap!=null){

                            markCoordinatesOnMap(extractedMap);
                        }
                    }/*
                    if (data.getStringExtra("qrResponse")!=null){
                        addSaveBTN.setVisibility(View.VISIBLE);
                        splitResponse = data.getStringExtra("qrResponse").split(";");
                        markCoordinatesOnMap();
                    }*/if (data.getSerializableExtra("savedMap")!=null) {
                        addSaveBTN.setVisibility(View.VISIBLE);
                        shareSearchBTN.setVisibility(View.VISIBLE);
                        clearAll();
                        labelDetails = (Map<String, Object>) data.getSerializableExtra("label");
                        Map<String,ArrayList<String>> savedMap = (Map<String, ArrayList<String>>) data.getSerializableExtra("savedMap");
                        if (savedMap!=null){
                            markCoordinatesOnMap(savedMap);
                        }
                    }
                    if (data.getSerializableExtra("sharedMap")!=null) {
                        addSaveBTN.setVisibility(View.VISIBLE);
                        shareSearchBTN.setVisibility(View.VISIBLE);
                        clearAll();
                        labelDetails = (Map<String, Object>) data.getSerializableExtra("label");
                        Map<String,ArrayList<String>> sharedMap = (Map<String, ArrayList<String>>) data.getSerializableExtra("sharedMap");
                        if (sharedMap!=null){
                            markCoordinatesOnMap(sharedMap);
                        }
                    }

                }
                //----------------------------------------------------------------------------------------------------------------
                //END
                //----------------------------------------------------------------------------------------------------------------
            }
    }

    //Code lines necessary for the integration of the OSM
    //----------------------------------------------------------------------------------------------------------------
    //BEGIN
    //----------------------------------------------------------------------------------------------------------------
    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use

        if (mAuth.getCurrentUser()!=null || sharedPreferences.getBoolean("loggedIn", false)){
            shareSearchBTN.setVisibility(View.VISIBLE);
        }else {
            shareSearchBTN.setVisibility(View.INVISIBLE);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);

        //if you switch to the menu the selectedMarkers array is cleared
        // and the routeBTN is set invisible and unclickable
        routeBTN.setVisibility(View.INVISIBLE);
        routeBTN.setClickable(false);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAll();
    }
    private void clearAll(){
        selectedMarkers.clear();
        selectedMarkersArray.clear();
        selectedTagsArray.clear();
        selectedNamesArray.clear();
        selectedCategoriesArray.clear();
        selectedChargesArray.clear();
        selectedOpeningHoursArray.clear();
        selectedCuisineArray.clear();
        coordinates.clear();
        categories.clear();
        names.clear();
        cuisine.clear();
        openingHours.clear();
        charge.clear();
        allMarkers.clear();
        textMarkers.clear();
        selectedTextMarkers.clear();
        resources.flushLayoutCache();
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }*/

   /* private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }*/
    //----------------------------------------------------------------------------------------------------------------
    //END
    //----------------------------------------------------------------------------------------------------------------

    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_click);
        view.startAnimation(animation);
    }

    //Marks the coordinates provided by the MenuActivity
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF markCoordinatesOnMap
    //----------------------------------------------------------------------------------------------------------------
    public void markCoordinatesOnMap(Map<String, ArrayList<String>> mapResponse){

        map.getOverlays().clear();

        coordinates = mapResponse.get("coordinates");
        categories = mapResponse.get("categories");
        names = mapResponse.get("placeNames");
        cuisine= mapResponse.get("cuisines");
        openingHours= mapResponse.get("openingHours");
        charge= mapResponse.get("charges");

        Log.d("mainActivity", String.valueOf(coordinates.size()));
        Log.d("mainActivity", String.valueOf(categories.size()));
        Log.d("mainActivity", String.valueOf(charge.size()));

        String[] startPoint= coordinates.get(0).split(",");
        Double latitude = Double.parseDouble(startPoint[0]);
        Double longitude = Double.parseDouble(startPoint[1]);
        IMapController mapControllerOnResume = map.getController();
        mapControllerOnResume.setZoom(15.0);
        GeoPoint startPointOnCreate = new GeoPoint(latitude, longitude);

        newstartMarker = new Marker(map);
        newstartMarker.setPosition(startPointOnCreate);
        newstartMarker.setTitle(String.valueOf(R.string.start));
        newstartMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(newstartMarker);
        newstartMarker.setIcon(getResources().getDrawable(R.drawable.blue_marker));
        if (!selectedMarkers.contains(newstartMarker)) {
            selectedMarkers.add(newstartMarker);
            selectedMarkersArray.add(latitude + "," + longitude);
        }


        for (int i = 1; i < coordinates.size(); i++) {
            Marker marker = new Marker(map);
            marker.setIcon(categoryManager.getMarkerIcon(categories.get(i-1)));
            String[] markerPoint = coordinates.get(i).split(",");
            Double pointLat = Double.parseDouble(markerPoint[0]);
            Double pointLong = Double.parseDouble(markerPoint[1]);
            GeoPoint geoPoint = new GeoPoint(pointLat, pointLong);
            marker.setPosition(geoPoint);
            Marker markerTest = new Marker(map);
            markerTest.setTextIcon(names.get(i-1).toUpperCase());
            markerTest.setPosition(geoPoint);

            textMarkers.add(markerTest);
            allMarkers.add(marker);
            //OnClickListener for markers
            //if a marker is clicked, it is added to the selectedMarkers ArrayList and its image is replaced
            // if it is not already contained in the array
            // else it is removed, and its image is switched back to the original
            //----------------------------------------------------------------------------------------------------------------
            //BEGINNING OF OnClickListeners for markers
            //----------------------------------------------------------------------------------------------------------------
            marker.setOnMarkerClickListener((m, mapView) -> {
                //showTransportationDialog(m);
                GeoPoint position = m.getPosition();
                String positionString = String.valueOf(position.getLatitude())+"," + String.valueOf(position.getLongitude());
                int index = coordinates.indexOf(positionString);

                Dialog dialog = new Dialog(MainActivity.this,R.style.CustomDialogTheme);
                dialog.setContentView(R.layout.marker_dialog);

                Window window = dialog.getWindow();
                if(window!= null) {
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

                TextView markerNameMD = dialog.findViewById(R.id.markerNameMD);
                TextView markerCategoryMD = dialog.findViewById(R.id.markerCategoryMD);
                TextView markerCuisineMD = dialog.findViewById(R.id.markerCuisineMD);
                TextView markerOpeningHoursMD = dialog.findViewById(R.id.markerOpeningHoursMD);
                TextView markerChargeMD = dialog.findViewById(R.id.markerChargeMD);

                TextView openTV= dialog.findViewById(R.id.openTV);
                TextView cuisineTV= dialog.findViewById(R.id.cuisineTV);
                TextView chargesTV = dialog.findViewById(R.id.chargesTV);

                Button addMD= dialog.findViewById(R.id.addMD);
                Button removeMD= dialog.findViewById(R.id.removeMD);

                markerNameMD.setText(names.get(index-1));
                markerCategoryMD.setText(categoryManager.getMarkerFullCategory(categories.get(index - 1)));

                markerCuisineMD.setText(formatStringToShow(cuisine.get(index-1)));
                markerOpeningHoursMD.setText(formatStringToShow(openingHours.get(index-1)));
                markerChargeMD.setText(formatStringToShow(charge.get(index-1)));

                if (selectedMarkers.contains(m)){
                    addMD.setVisibility(View.INVISIBLE);
                    removeMD.setVisibility(View.VISIBLE);
                }else{
                    addMD.setVisibility(View.VISIBLE);
                    removeMD.setVisibility(View.INVISIBLE);
                }

                if (cuisine.get(index-1).equals("unknown")){
                    cuisineTV.setVisibility(View.GONE);
                    markerCuisineMD.setVisibility(View.GONE);
                }
                if (openingHours.get(index-1).equals("unknown")){
                    openTV.setVisibility(View.GONE);
                    markerOpeningHoursMD.setVisibility(View.GONE);
                }
                if (charge.get(index-1).equals("unknown")){
                    chargesTV.setVisibility(View.GONE);
                    markerChargeMD.setVisibility(View.GONE);
                }

                addMD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedMarkers.add(m);
                        selectedTextMarkers.add(textMarkers.get(allMarkers.indexOf(m)));
                        selectedMarkersArray.add(positionString);
                        selectedTagsArray.add(categories.get(index-1));
                        selectedNamesArray.add(names.get(index-1));
                        selectedCuisineArray.add(cuisine.get(index-1));
                        selectedOpeningHoursArray.add(openingHours.get(index-1));
                        selectedChargesArray.add(charge.get(index-1));

                        if (!selectedCategoriesArray.contains(categoryManager.getMarkerFullCategory(categories.get(index-1)))) {
                            selectedCategoriesArray.add(categoryManager.getMarkerFullCategory(categories.get(index - 1)));
                        }
                        m.setIcon(resources.getDrawable(R.drawable.green_route_marker));

                        if (selectedMarkers.size()>1 && selectedMarkers.size()<7){
                            routeBTN.setVisibility(View.VISIBLE);
                            routeBTN.setClickable(true);
                        }else{
                            routeBTN.setVisibility(View.INVISIBLE);
                            routeBTN.setClickable(false);
                        }

                        dialog.dismiss();
                    }
                });

                removeMD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectedMarkers.remove(m);
                        selectedMarkersArray.remove(positionString);
                        selectedTagsArray.remove(categories.get(index-1));
                        selectedNamesArray.remove(names.get(index-1));
                        selectedCuisineArray.remove(cuisine.get(index-1));
                        selectedOpeningHoursArray.remove(openingHours.get(index-1));
                        selectedChargesArray.remove(charge.get(index-1));

                        selectedCategoriesArray.remove(categoryManager.getMarkerFullCategory(categories.get(index-1)));
                        m.setIcon(categoryManager.getMarkerIcon(categories.get(index-1)));

                        if (selectedMarkers.size()>1){
                            routeBTN.setVisibility(View.VISIBLE);
                            routeBTN.setClickable(true);
                        }else{
                            routeBTN.setVisibility(View.INVISIBLE);
                            routeBTN.setClickable(false);
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();

                return true;
            });
            //----------------------------------------------------------------------------------------------------------------
            //END OF OnClickListeners for markers
            //----------------------------------------------------------------------------------------------------------------


            map.getOverlays().add(marker);
            map.getOverlays().add(markerTest);
            //marker.setIcon(getResources().getDrawable(R.drawable.green_marker));


        }

        mapControllerOnResume.setCenter(startPointOnCreate);
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF markCoordinatesOnMap
    //----------------------------------------------------------------------------------------------------------------

    private String formatStringToShow(String string){
        String[] stringArray = string.split(";");
        StringBuilder formattedString = new StringBuilder(stringArray[0]);

        for (int i=1; i<stringArray.length; i++){
            formattedString.append("\n");
            formattedString.append(stringArray[i]);
        }

        return formattedString.toString();
    }

    //ShowTransportationDialog
    //When the routeBTN is clicked, this dialog is shown
    //the user is able to choose a transportation mode, which are "on foot", "car", or "public transport"
    //When a transportation mode is chosen, the selectedMarkers array is sorted by the relative distance from each other
    // and stored in the sortedMarkers then the startNavigation function is called for each Marker pairs in the sortedMarkers
    //apart from these the other (not selected) markers are removed from the map
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF showTransportationDialog
    //----------------------------------------------------------------------------------------------------------------

    private void showTransportationDialog(ArrayList<Marker> markers) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(resources.getString(R.string.choose_transport));

        String[] transportationModes = {resources.getString(R.string.car), resources.getString(R.string.walk)};

        //ArrayList<Marker> sortedMarkers = sortMarkers(markers);

        builder.setItems(transportationModes, (dialog, which) -> {

            switch (which) {
                case 0:
                    // Autóval
                    removeOtherMarkers();
                    tspSolver("driving-car",markers);

                    /*startNavigation("driving-car", sortedMarkers.get(0), sortedMarkers.get(1));
                    for (int i= 1; i< (sortedMarkers.size())-1; i++ ) {

                        startNavigation("driving-car", sortedMarkers.get(i), sortedMarkers.get(i+1));
                    }
                    startNavigation("driving-car", sortedMarkers.get(sortedMarkers.size()-1),sortedMarkers.get(0));*/
                    break;

                case 1:
                    removeOtherMarkers();
                    tspSolver("foot-walking",markers);

                    /*startNavigation("foot-walking", sortedMarkers.get(0), sortedMarkers.get(1));
                    for (int i= 1; i< sortedMarkers.size()-1; i++ ) {

                        startNavigation("foot-walking", sortedMarkers.get(i), sortedMarkers.get(i+1));
                    }
                    startNavigation("foot-walking", sortedMarkers.get(sortedMarkers.size()-1),sortedMarkers.get(0));*/
                    break;

                case 2:
                    //selectedMarker = marker;
                    //removeOtherMarkers(selectedMarker);
                    //startNavigation("pt");
                    break;
            }
        });

        builder.create().show();
    }

    //----------------------------------------------------------------------------------------------------------------
    //END OF showTransportationDialog
    //----------------------------------------------------------------------------------------------------------------

    //removeOtherMarkers
    //removes not selected markers from map
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF removeOtherMarkers
    //----------------------------------------------------------------------------------------------------------------
    private void removeOtherMarkers() {
        List<Overlay> overlays = map.getOverlays();
        List<Overlay> overlaysToRemove = new ArrayList<>();

        for (Overlay overlay : overlays) {
                if (overlay instanceof Marker && !selectedMarkers.contains(overlay) && !selectedTextMarkers.contains(overlay)) {
                    if (overlay == newstartMarker) {

                    } else {
                        overlaysToRemove.add(overlay);
                    }
                }
        }

        overlays.removeAll(overlaysToRemove);
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF removeOtherMarkers
    //----------------------------------------------------------------------------------------------------------------

    //startNavigation
    //this function is for to call the API responsible for planning the route and draw the line between two markers
    //three parameters are given: transportationMode, and two Markers, start point and end point
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF startNavigation
    //----------------------------------------------------------------------------------------------------------------
    /*private void startNavigation(String transportationMode, Marker marker1, Marker marker2) {

        double startLat= marker1.getPosition().getLatitude();
        double startLng= marker1.getPosition().getLongitude();
        double destinationLat= marker2.getPosition().getLatitude();
        double destinationLng= marker2.getPosition().getLongitude();
        OpenRouteServiceAPI.getRoute(startLat, startLng, destinationLat, destinationLng, transportationMode, new OpenRouteServiceAPI.RouteCallback() {
            @Override
            public void onRouteReceived(String result) {

                try {




                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("OpenRouteServiceAPI", "Received null or empty response");
                }
            }

            @Override
            public void onRouteFailure() {

            }

        });
        }*/
    //----------------------------------------------------------------------------------------------------------------
    //END OF startNavigation
    //----------------------------------------------------------------------------------------------------------------

    private BoundingBox addPaddingToBoundingBox(BoundingBox boundingBox, double padding) {
        double latMin = boundingBox.getLatSouth() - padding;
        double latMax = boundingBox.getLatNorth() + padding;
        double lonMin = boundingBox.getLonWest() - padding;
        double lonMax = boundingBox.getLonEast() + padding;

        return new BoundingBox(latMax, lonMax, latMin, lonMin);
    }

    //sortMarkers
    //sorts the content of the ArrayList provided as the function's parameter
    //by the distance from each other
    //by the end of the sorting, the marker that follows an other marker is the one that is the closest from the preceding markers in the list
    //(in theory)
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF sortMarkers
    //----------------------------------------------------------------------------------------------------------------
    private ArrayList<Marker> sortMarkers(ArrayList<Marker> markers){
        //NOTE: It is actually a travelling agent problem, feel free to implement this feature
        for (int i=0; i<markers.size()-1; i++){
            double distance = haversine(markers.get(i).getPosition().getLatitude(),markers.get(i).getPosition().getLongitude(),markers.get(i+1).getPosition().getLatitude(),markers.get(i+1).getPosition().getLongitude());

            for (int j=i+1; j<markers.size(); j++){
                double compareDistance = haversine(markers.get(i).getPosition().getLatitude(),markers.get(i).getPosition().getLongitude(),markers.get(j).getPosition().getLatitude(),markers.get(j).getPosition().getLongitude());
                    if (compareDistance<distance){
                        distance= compareDistance;
                        Marker temp= markers.get(j);
                        markers.set(j, markers.get(i));
                        markers.set(i, temp);
                    }
            }
        }
        return markers;
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF sortMarkers
    //----------------------------------------------------------------------------------------------------------------

    //harvesine
    //Distance calculator
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF harvesine
    //----------------------------------------------------------------------------------------------------------------
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double earthRadius = 6371;
        double distance = earthRadius * c;

        return distance;
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF harvesine
    //----------------------------------------------------------------------------------------------------------------

    private String generateLabel(Map<String, Object> detailsMap){

        StringBuilder label = new StringBuilder();

        for(String labelElement: labelData){
            StringBuilder subEntry = new StringBuilder();
            Object element = detailsMap.get(labelElement);

            if (element.getClass()==ArrayList.class){
                for(int i=0; i<((ArrayList<?>) element).size(); i++){
                    subEntry.append(((ArrayList<?>) element).get(i)).append(";");
                }
                subEntry.delete(subEntry.length()-1,subEntry.length());
                label.append(subEntry).append(";;");
            }else
                label.append(element).append(";;");
        }
        label.append(getCurrentDate());


        return label.toString();
    }

    private String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    private interface RowsExecuted{
        void Success(ArrayList<ArrayList<Double>> distancesResult,
                     ArrayList<ArrayList<ArrayList<GeoPoint>>> geoPointsResult);
        void Failure();
    }
    private interface RowExecuted{
        void Success(ArrayList<Double> rowDistancesResult,
                     ArrayList<ArrayList<GeoPoint>> rowGeoPointsResult);
        void Failure();
    }

    private void tspSolver(String transportMode, ArrayList<Marker> selectedMarkers){


        ArrayList<ArrayList<Double>> distancesResult = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<GeoPoint>>> coordinatesResult = new ArrayList<>();

        executeRouteRequests(transportMode, selectedMarkers, distancesResult, coordinatesResult, 0, new RowsExecuted() {

            @Override
            public void Success(ArrayList<ArrayList<Double>> distancesResult, ArrayList<ArrayList<ArrayList<GeoPoint>>> geoPointsResult) {

                Log.d("routeRequest", distancesResult.toString());

                ArrayList<ArrayList<Double>> distances = new ArrayList<>(distancesResult);
                Log.d("indexesDistances", distances.toString());
                ArrayList<ArrayList<ArrayList<GeoPoint>>> geoPointsArray = new ArrayList<>(geoPointsResult);
                ArrayList<ArrayList<Double>> finalDistances = new ArrayList<>();
                ArrayList<ArrayList<GeoPoint>> finalGeoPoints = new ArrayList<>();
                ArrayList<Integer> indexes = new ArrayList<>();

                indexes.add(0);

                do {

                    int index = selectedMarkers.size()-1;
                    double minDistance = Double.MAX_VALUE;

                    for (int i= 0; i < distances.size(); i++){

                        for (int j = i+1; j< distances.get(i).size()-1; j++){

                            if ( distances.get(i).get(j)< minDistance && !indexes.contains(j)){
                                index = j;
                                minDistance = distances.get(i).get(j);
                            }

                        }
                    }
                    indexes.add(index);
                    Log.d("mittomen_indexes", indexes.toString());
                    finalGeoPoints.add(geoPointsArray.get(indexes.get(indexes.size()-2)).get(index));

                }while (finalGeoPoints.size()<selectedMarkers.size()-1);

                finalGeoPoints.add(geoPointsArray.get(0).get(indexes.get(indexes.size()-1)));

                Log.d("indexes", indexes.toString());

                for (int i = 0; i< finalGeoPoints.size(); i++) {
                    Polyline polyline = new Polyline();
                    polyline.setPoints(finalGeoPoints.get(i));
                    polyline.setColor(Color.argb(200, 42, 63, 117));
                    polyline.getOutlinePaint().setStrokeWidth(15);
                    map.getOverlayManager().add(polyline);

                    BoundingBox existingBoundingBox = BoundingBox.fromGeoPoints(finalGeoPoints.get(i));

                    BoundingBox newBoundingBox = addPaddingToBoundingBox(existingBoundingBox, 0.001);
                    map.zoomToBoundingBox(newBoundingBox, true);
                }
            }

            @Override
            public void Failure() {
                Toast.makeText(MainActivity.this,R.string.route_planning_error,Toast.LENGTH_LONG).show();
                Log.e("nem_megyen", "nem megyen");
            }
        });

    }

    private void executeRouteRequests(String transportMode,ArrayList<Marker> selectedMarkers,ArrayList<ArrayList<Double>> distancesResult,
                                      ArrayList<ArrayList<ArrayList<GeoPoint>>> geoPointsResult,int index, RowsExecuted rowsExecuted){

        ArrayList<Double> rowCoordinates = new ArrayList<>();

        for(int i=0; i<index; i++){
            rowCoordinates.add(selectedMarkers.get(i).getPosition().getLatitude());
            rowCoordinates.add(selectedMarkers.get(i).getPosition().getLongitude());
        }

        for (int i = index; i< selectedMarkers.size(); i++){
            rowCoordinates.add(selectedMarkers.get(i).getPosition().getLatitude());
            rowCoordinates.add(selectedMarkers.get(i).getPosition().getLongitude());
        }
        ArrayList<Double> rowDistances = new ArrayList<>();
        ArrayList<ArrayList<GeoPoint>> rowGeoPoints = new ArrayList<>();

        Log.d("rowCoordinates", rowCoordinates.toString());

        executeRow(transportMode, rowCoordinates,rowDistances,rowGeoPoints,index*2, 0,  new RowExecuted() {
            @Override
            public void Success(ArrayList<Double> rowDistancesResult, ArrayList<ArrayList<GeoPoint>> rowGeoPointsResult) {

                for (int i=0; i< selectedMarkers.size()-rowDistancesResult.size(); i++){
                    rowDistances.add(Double.MAX_VALUE);
                    rowGeoPoints.add(new ArrayList<>());
                }
                ArrayList<Double> rowDistances = new ArrayList<>(rowDistancesResult);
                ArrayList<ArrayList<GeoPoint>> rowGeoPoints = new ArrayList<>(rowGeoPointsResult);

                distancesResult.add(rowDistances);
                geoPointsResult.add(rowGeoPoints);
                if (index== selectedMarkers.size()-1){
                    rowsExecuted.Success(distancesResult,geoPointsResult);
                    Log.d("rowGEO", geoPointsResult.toString());
                }else {
                    Log.d("rowGEO", geoPointsResult.toString());
                    executeRouteRequests(transportMode, selectedMarkers,distancesResult,geoPointsResult, index+1, rowsExecuted);
                }
            }

            @Override
            public void Failure() {
                rowsExecuted.Failure();
            }
        });

    }

    private void executeRow(String transportMode, ArrayList<Double> coordinatesOrig,
                            ArrayList<Double> rowDistances,ArrayList<ArrayList<GeoPoint>> rowGeoPoints,int startIndex,int index,  RowExecuted rowExecuted){

        OpenRouteServiceAPI.getRoute(coordinatesOrig.get(startIndex), coordinatesOrig.get(startIndex+1), coordinatesOrig.get(index), coordinatesOrig.get(index+1), transportMode, new OpenRouteServiceAPI.RouteCallback() {
            @Override
            public void onRouteReceived(String result) {
                //rowDistances.add()

                try {
                    JSONObject jsonResponse = new JSONObject(result);

                    Log.d("routeResponse", jsonResponse.toString());

                    JSONArray features = jsonResponse.getJSONArray("features");

                    JSONObject firstFeature = features.getJSONObject(0);

                    JSONObject geometry = firstFeature.getJSONObject("geometry");

                    JSONObject properties = firstFeature.getJSONObject("properties");

                    JSONObject summary = properties.getJSONObject("summary");

                    double distance = Double.MAX_VALUE;

                    if (summary.has("distance")) {

                        distance = summary.getDouble("distance");

                    }

                    JSONArray coordinates = null;

                    if ( geometry.has("coordinates")){

                        coordinates = geometry.getJSONArray("coordinates");

                    }

                    ArrayList<GeoPoint> routePoints = new ArrayList<>();

                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray point = coordinates.getJSONArray(i);
                        double lat = point.getDouble(1);
                        double lon = point.getDouble(0);

                        routePoints.add(new GeoPoint(lat, lon));
                    }
                    rowGeoPoints.add(routePoints);
                    rowDistances.add(distance);

                    if (index == coordinatesOrig.size() - 2) {
                        rowExecuted.Success(rowDistances,rowGeoPoints);
                    }else {
                        executeRow(transportMode,coordinatesOrig,rowDistances,rowGeoPoints,startIndex, index+2,rowExecuted);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onRouteFailure() {
                rowExecuted.Failure();
            }
        });

    }
}



























