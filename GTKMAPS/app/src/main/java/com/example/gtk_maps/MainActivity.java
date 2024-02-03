package com.example.gtk_maps;

import static android.app.ProgressDialog.show;
import static java.lang.Math.abs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final int REQUEST_CODE = 1;
    private int start=0;
    private boolean enabledGPS, qrResponse= false;
    private MapView map = null;
    private Marker startMarker, newstartMarker;
    private String[] startPoint;
    private ArrayList<Marker> selectedMarkers;
    private ArrayList<String> splitResponseArray, selectedMarkersArray,selectedTagsArray,selectedNamesArray, tagsResponseArray,namesResponseArray,
    selectedCategoriesArray;

    private GeoPoint currentLocation;
    private EditText nameASD;
    private TextView titleASD, markerNameMD, markerCategoryMD;
    private Button saveASD, addMD, removeMD;
    private ImageButton menuBTN, addSaveBTN, routeBTN;
    //private MyLocationListener locationListener;
    Resources resources;
    private SaveManager saveManager;
    private CategoryManager categoryManager;


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

        resources = MainActivity.this.getResources();

        saveManager = new SaveManager(this);
        categoryManager = new CategoryManager(MainActivity.this);

        selectedMarkers = new ArrayList<>();
        splitResponseArray = new ArrayList<>();
        selectedMarkersArray = new ArrayList<>();
        selectedTagsArray = new ArrayList<>();
        selectedNamesArray = new ArrayList<>();
        tagsResponseArray = new ArrayList<>();
        namesResponseArray = new ArrayList<>();
        selectedCategoriesArray= new ArrayList<>();
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        String[] permissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        requestPermissionsIfNecessary(permissions
                // if you need to show the current location, uncomment the line below
                //
                // WRITE_EXTERNAL_STORAGE is required in order to show the map

        );
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint firstPoint = new GeoPoint(47.09327, 17.91149);
        mapController.setCenter(firstPoint);
        map.setMultiTouchControls(true);

            //if (enabledGPS){
                //----------------------------------------------------------------------------------------------------------------
                // Get current location
                //----------------------------------------------------------------------------------------------------------------
/*                    locationListener = new MyLocationListener(MainActivity.this);
                    double longitude = locationListener.getLongitude();
                    double latitude = locationListener.getLatitude();

                    currentLocation = new GeoPoint(latitude, longitude);

                    startMarker = new Marker(map);
                    startMarker.setPosition(currentLocation);
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    map.getOverlays().add(startMarker);
                    mapController.setCenter(currentLocation);
                    startMarker.setIcon(getResources().getDrawable(R.drawable.blue_marker));
                    startMarker.setTitle("Current location");*/

            //----------------------------------------------------------------------------------------------------------------
            // END
            //----------------------------------------------------------------------------------------------------------------
            //}else{
                startMarker = new Marker(map);
                startMarker.setPosition(firstPoint);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                map.getOverlays().add(startMarker);
                startMarker.setIcon(resources.getDrawable(R.drawable.blue_marker));
                startMarker.setTitle(resources.getString(R.string.start));
            //}




        addSaveBTN= findViewById(R.id.addSaveBTN);
        addSaveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_search_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                titleASD = dialog.findViewById(R.id.titleASD);
                nameASD = dialog.findViewById(R.id.nameASD);

                titleASD.setText(resources.getString(R.string.save_as));

                saveASD =dialog.findViewById(R.id.saveASD);
                saveASD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSaveBTN.setVisibility(View.INVISIBLE);
                        if(selectedMarkersArray.size()>1) {
                            saveManager.addSelectedSearch(nameASD.getText().toString().trim(), selectedMarkersArray, selectedTagsArray, selectedNamesArray,selectedCategoriesArray);
                        }
                        else {
                            saveManager.addSearch(nameASD.getText().toString().trim(), splitResponseArray, tagsResponseArray, namesResponseArray);
                        }

                        Toast.makeText(MainActivity.this,R.string.add_toast,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
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

                    if (data.getStringArrayListExtra("coordsResponse")!=null){
                        addSaveBTN.setVisibility(View.VISIBLE);
                        clearAll();
                        splitResponseArray = data.getStringArrayListExtra("coordsResponse");
                        namesResponseArray = data.getStringArrayListExtra("coordsNamesResponse");
                        tagsResponseArray = data.getStringArrayListExtra("coordsTagsResponse");
                        markCoordinatesOnMap();
                    }/*
                    if (data.getStringExtra("qrResponse")!=null){
                        addSaveBTN.setVisibility(View.VISIBLE);
                        splitResponse = data.getStringExtra("qrResponse").split(";");
                        markCoordinatesOnMap();
                    }*/if (data.getStringArrayListExtra("savedSearch")!=null) {
                        addSaveBTN.setVisibility(View.VISIBLE);
                        clearAll();
                        splitResponseArray = data.getStringArrayListExtra("savedSearch");
                        tagsResponseArray = data.getStringArrayListExtra("savedSearchCategories");
                        namesResponseArray = data.getStringArrayListExtra("savedSearchNames");
                        /*Log.d("responseArray", String.valueOf(splitResponseArray.size()));
                        Log.d("responseArray", String.valueOf(namesResponseArray.size()));
                        Log.d("responseArray", String.valueOf(tagsResponseArray.size()));
                    Log.d("responseArray", String.valueOf(splitResponseArray));
                    Log.d("responseArray", String.valueOf(namesResponseArray));
                    Log.d("responseArray", String.valueOf(tagsResponseArray));*/
                        markCoordinatesOnMap();
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
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
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
    }
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
    public void markCoordinatesOnMap(){
        map.getOverlays().clear();
        startPoint= splitResponseArray.get(0).split(",");
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
        if (!selectedMarkers.contains(newstartMarker))
            selectedMarkers.add(newstartMarker);
        if (!selectedMarkersArray.contains(latitude+"," + longitude))
            selectedMarkersArray.add(latitude+"," + longitude);

        for (int i = 1; i < splitResponseArray.size(); i++) {
            Marker marker = new Marker(map);
            marker.setIcon(categoryManager.getMarkerIcon(tagsResponseArray.get(i-1)));
            String[] markerPoint = splitResponseArray.get(i).split(",");
            Double pointLat = Double.parseDouble(markerPoint[0]);
            Double pointLong = Double.parseDouble(markerPoint[1]);
            GeoPoint geoPoint = new GeoPoint(pointLat, pointLong);
            marker.setPosition(geoPoint);
            Marker markerTest = new Marker(map);
            markerTest.setTextIcon(namesResponseArray.get(i-1).toUpperCase());
            markerTest.setPosition(geoPoint);
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
                int index = splitResponseArray.indexOf(positionString);

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.marker_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);

                markerNameMD = dialog.findViewById(R.id.markerNameMD);
                markerCategoryMD = dialog.findViewById(R.id.markerCategoryMD);

                addMD= dialog.findViewById(R.id.addMD);
                removeMD= dialog.findViewById(R.id.removeMD);

                markerNameMD.setText(namesResponseArray.get(index-1));
                markerCategoryMD.setText(categoryManager.getMarkerFullCategory(tagsResponseArray.get(index - 1)));

                if (selectedMarkers.contains(m)){
                    addMD.setVisibility(View.INVISIBLE);
                    removeMD.setVisibility(View.VISIBLE);
                }else{
                    addMD.setVisibility(View.VISIBLE);
                    removeMD.setVisibility(View.INVISIBLE);
                }

                addMD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedMarkers.add(m);
                        selectedMarkersArray.add(positionString);
                        selectedTagsArray.add(tagsResponseArray.get(index-1));
                        selectedNamesArray.add(namesResponseArray.get(index-1));
                        if (!selectedCategoriesArray.contains(categoryManager.getMarkerFullCategory(tagsResponseArray.get(index-1)))) {
                            selectedCategoriesArray.add(categoryManager.getMarkerFullCategory(tagsResponseArray.get(index - 1)));
                        }
                        m.setIcon(resources.getDrawable(R.drawable.green_route_marker));

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

                removeMD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectedMarkers.remove(m);
                        selectedMarkersArray.remove(positionString);
                        selectedTagsArray.remove(tagsResponseArray.get(index-1));
                        selectedNamesArray.remove(namesResponseArray.get(index-1));
                        selectedCategoriesArray.remove(categoryManager.getMarkerFullCategory(tagsResponseArray.get(index-1)));
                        m.setIcon(categoryManager.getMarkerIcon(tagsResponseArray.get(index-1)));

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

    private void clearAll(){
        selectedMarkers.clear();
        splitResponseArray.clear();
        selectedMarkersArray.clear();
        selectedTagsArray.clear();
        selectedNamesArray.clear();
        tagsResponseArray.clear();
        namesResponseArray.clear();
        selectedCategoriesArray.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAll();
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
        builder.setTitle(getResources().getString(R.string.choose_transport));

        String[] transportationModes = {getResources().getString(R.string.car), getResources().getString(R.string.walk)};

        ArrayList<Marker> sortedMarkers = sortMarkers(markers);

        builder.setItems(transportationModes, (dialog, which) -> {

            switch (which) {
                case 0:
                    // Autóval
                    removeOtherMarkers();
                    startNavigation("driving-car", sortedMarkers.get(0), sortedMarkers.get(1));
                    for (int i= 1; i< (sortedMarkers.size())-1; i++ ) {

                        startNavigation("driving-car", sortedMarkers.get(i), sortedMarkers.get(i+1));
                    }
                    startNavigation("driving-car", sortedMarkers.get(sortedMarkers.size()-1),sortedMarkers.get(0));
                    break;

                case 1:
                    removeOtherMarkers();
                    startNavigation("foot-walking", sortedMarkers.get(0), sortedMarkers.get(1));
                    for (int i= 1; i< sortedMarkers.size()-1; i++ ) {

                        startNavigation("foot-walking", sortedMarkers.get(i), sortedMarkers.get(i+1));
                    }
                    startNavigation("foot-walking", sortedMarkers.get(sortedMarkers.size()-1),sortedMarkers.get(0));
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
                if (overlay instanceof Marker && !selectedMarkers.contains(overlay)) {
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
    private void startNavigation(String transportationMode, Marker marker1, Marker marker2) {

        double startLat= marker1.getPosition().getLatitude();
        double startLng= marker1.getPosition().getLongitude();
        double destinationLat= marker2.getPosition().getLatitude();
        double destinationLng= marker2.getPosition().getLongitude();
        OpenRouteServiceAPI.getRoute(startLat, startLng, destinationLat, destinationLng, transportationMode, new OpenRouteServiceAPI.RouteCallback() {
            @Override
            public void onRouteReceived(String result) {

                if (result != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);

                        JSONArray features = jsonResponse.getJSONArray("features");

                        JSONObject firstFeature = features.getJSONObject(0);

                        JSONObject geometry = firstFeature.getJSONObject("geometry");

                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        List<GeoPoint> routePoints = new ArrayList<>();

                        for (int i = 0; i < coordinates.length(); i++) {
                            JSONArray point = coordinates.getJSONArray(i);
                            double lat = point.getDouble(1);
                            double lon = point.getDouble(0);

                            routePoints.add(new GeoPoint(lat, lon));
                        }

                        Polyline polyline = new Polyline();
                        polyline.setPoints(routePoints);
                        map.getOverlayManager().add(polyline);

                        BoundingBox existingBoundingBox = BoundingBox.fromGeoPoints(routePoints);

                        BoundingBox newBoundingBox = addPaddingToBoundingBox(existingBoundingBox, 0.001);
                        map.zoomToBoundingBox(newBoundingBox, true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("OpenRouteServiceAPI", "Received null or empty response");
                    }
                }
            }

        });
        }
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
}



























