package com.example.gtk_maps;

import static android.app.ProgressDialog.show;
import static com.android.volley.toolbox.Volley.newRequestQueue;
import static java.lang.Math.abs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private static final int SHARED_REQUEST_CODE =3;
    private static final int REQUEST_CODE = 1;

    private ImageButton userBTN;

    private BottomNavigationView menuLayout;
    private FrameLayout menuItemLayout;
    private View handleArrow;
    private LinearLayout itemContainer;
    private LinearLayout handle;
    private FloatingActionButton routePlanActionButton;

    private ArrayAdapter<String> placesAdapter;

    private int selectedDistance;
    private String selectedTransport;
    private ArrayList<Place> selectedPlaces;
    private ArrayList<String> selectedCategories;
    private ArrayList<String> allCategories;
    private ArrayList<String> suggestedPlaces;
    private ArrayList<Marker> otherMarkers;
    private ArrayList<Marker> allMarkers;
    private ArrayList<Polyline> routePolys;
    private ArrayList<Place> places;
    private boolean isUpdatedFromActivity;
    private Boolean isNavi;

    private MapView map = null;



    private Marker startMarker;

    private LinearLayout categoriesChipLayout, searchDetailsLayout;
    private ImageView transportImage;
    private TextView withinText;
    private AutoCompleteTextView mapAutoComplete;


    private CategoryManager categoryManager;
    private Resources resources;

    private Navigation navigation;

    private FragmentsViewModel fragmentsViewModel;
    private FirebaseAuth mAuth;

    private AppDatabase appDatabase;
    private AppDatabase.SearchDao searchDao;


    private RadiusMarkerClusterer radiusMarkerClusterer;
    private ArrayList<Marker> routeMarkers;
    private ArrayList<Marker> nameMarkers;


    //Code lines necessary for the integration of the OSM
    //----------------------------------------------------------------------------------------------------------------
    //BEGIN
    //----------------------------------------------------------------------------------------------------------------


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //inflate and create the map

        map = findViewById(R.id.map);
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
        resources = MainActivity.this.getResources();

        fragmentsViewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);



        navigation = new Navigation(newRequestQueue(getApplicationContext()),getApplicationContext());

        places = new ArrayList<>();

        isUpdatedFromActivity = false;
        isNavi = false;


        selectedPlaces = new ArrayList<>();
        selectedCategories = new ArrayList<>();
        allCategories = new ArrayList<>();
        otherMarkers = new ArrayList<>();
        routePolys = new ArrayList<>();
        suggestedPlaces = new ArrayList<>();
        allMarkers = new ArrayList<>();
        routeMarkers = new ArrayList<>();
        nameMarkers = new ArrayList<>();


        categoryManager = new CategoryManager(MainActivity.this);

        menuLayout = findViewById(R.id.menu_layout);
        menuItemLayout = findViewById(R.id.menu_item_layout);

        handleArrow = findViewById(R.id.handle_arrow);
        itemContainer = findViewById(R.id.item_container);
        handle = findViewById(R.id.handle);

        categoriesChipLayout = findViewById(R.id.categories_chip_layout);
        searchDetailsLayout = findViewById(R.id.search_details_layout);
        transportImage = findViewById(R.id.transport_image);
        withinText = findViewById(R.id.within_text);

        mapAutoComplete = findViewById(R.id.map_autocomplete_text_field);
        mapAutoComplete.setThreshold(2);

        mAuth = FirebaseAuth.getInstance();

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "temporary_search").build();

         searchDao = appDatabase.searchDao();

        fragmentsViewModel.setSearchDao(searchDao);


        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint firstPoint = new GeoPoint(47.09327, 17.91149);
        mapController.setCenter(firstPoint);
        map.setMultiTouchControls(true);

        startMarker = new Marker(map);
        startMarker.setPosition(firstPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        allMarkers.add(startMarker);
        map.getOverlays().add(startMarker);
        startMarker.setIcon(resources.getDrawable(R.drawable.start_marker));


        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Lefelé pöccintés
                if (velocityY > 0) {
                    //if (isItemContainerOut) {
                    handleArrow.animate().rotation(0).setDuration(300).start();
                    itemContainer.animate().translationY(itemContainer.getHeight()-handle.getHeight()).setDuration(300).start();
                    map.setMultiTouchControls(true);
                    map.setBuiltInZoomControls(true);
                    map.resetScrollableAreaLimitLatitude();
                    map.resetScrollableAreaLimitLongitude();


                    if (places.size()>1){
                        searchDetailsLayout.setVisibility(View.VISIBLE);
                    }
                    if (selectedPlaces.size()>1){
                        routePlanActionButton.setVisibility(View.VISIBLE);
                    }

                    /*} else {
                        menuContainer.animate().translationY(+menuContainer.getHeight()).setDuration(300).start();
                        searchContainer.animate().translationY(searchContainer.getHeight() - handle.getHeight()).setDuration(300).start();
                        isMenuOut = false;
                    }*/
                }
                // Felfelé pöccintés
                else if (velocityY < 0) {
                    //if (!isItemContainerOut) {
                    map.setMultiTouchControls(false);
                    map.setBuiltInZoomControls(false);
                    handleArrow.animate().rotation(180).setDuration(300).start();
                    itemContainer.animate().translationY(0).setDuration(300).start();
                    //searchContainer.animate().translationY(searchContainer.getHeight() - menuContainer.getHeight() - handle.getHeight()).setDuration(300).start();
                    //isItemContainerOut = true;

                    searchDetailsLayout.setVisibility(View.GONE);

                    if (selectedPlaces.size()>1) {
                        isUpdatedFromActivity = true;

                        /*fragmentsViewModel.getCategories().postValue(selectedCategories);
                        //fragmentsViewModel.setCategories(selectedCategories);
                        fragmentsViewModel.getPlaces().postValue(selectedPlaces);
                        //fragmentsViewModel.setPlaces(selectedPlaces);*/

                        fragmentsViewModel.uploadToDatabase(selectedDistance,selectedTransport,
                                selectedPlaces,selectedCategories);


                    }
                    if (routePlanActionButton.getVisibility() == View.VISIBLE){
                        routePlanActionButton.setVisibility(View.GONE);
                    }

                    if (places.size()==0) {
                        map.setScrollableAreaLimitLatitude(47.09327, 47.09327, 0);
                        map.setScrollableAreaLimitLongitude(17.91149, 17.91149, 0);
                    }else{
                        map.setScrollableAreaLimitLatitude(places.get(0).getCoordinates().getLat(), places.get(0).getCoordinates().getLat(), 0);
                        map.setScrollableAreaLimitLongitude(places.get(0).getCoordinates().getLon(), places.get(0).getCoordinates().getLon(), 0);
                    }
                    /*} else {

                        searchContainer.animate().translationY(-menuContainer.getHeight()).setDuration(300).start();
                        categoriesContainer.animate().translationY(categoriesContainer.getHeight() - menuContainer.getHeight() - handle.getHeight()).setDuration(300).start();
                        map.setMultiTouchControls(false);
                        map.setBuiltInZoomControls(false);

                        isSecondOut = true;
                    }*/
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true; // Szükséges ahhoz, hogy a fling esemény működjön
            }
        });


        handle.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Átadjuk az érintési eseményeket a GestureDetector-nak

                return gestureDetector.onTouchEvent(event);
            }
        });

        map.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {

                if (!isNavi)
                    checkBoundingBox(allMarkers);
                else checkBoundingBox(routeMarkers);

                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {

                if(!isNavi)
                    checkBoundingBox(allMarkers);
                else checkBoundingBox(routeMarkers);

                return true;
            }
        });


        SaveFragment saveFragment = new SaveFragment();
        SearchFragment searchFragment = new SearchFragment();
        ShareFragment shareFragment = new ShareFragment();

        menuLayout.setSelectedItemId(R.id.search_menu_item);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_item_layout, searchFragment)
                .commit();

        menuLayout.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                if (item.getItemId()==R.id.save_menu_item){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.menu_item_layout, saveFragment)
                            .commit();
                    return true;
                }
                if (item.getItemId()==R.id.search_menu_item){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.menu_item_layout, searchFragment)
                            .commit();
                    return true;
                }
                if (item.getItemId()==R.id.share_menu_item) {
                    if (mAuth.getCurrentUser()!=null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.menu_item_layout, shareFragment)
                                .commit();
                        return true;
                    }else{
                        Toast.makeText(MainActivity.this,R.string.please_login,Toast.LENGTH_LONG).show();
                    }
                }
                    /*switch (item.getItemId()) {
                    case R.id.save_menu_item:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.menu_item_layout, saveFragment)
                                .commit();
                        return true;

                    case R.id.search_menu_item:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.menu_item_layout, searchFragment)
                                .commit();
                        return true;

                    case R.id.share_menu_item:
                        if (mAuth.getCurrentUser()!=null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.menu_item_layout, shareFragment)
                                    .commit();
                            return true;
                        }else{
                            Toast.makeText(MainActivity.this,R.string.please_login,Toast.LENGTH_LONG).show();
                        }

                }*/
                return false;
            }
        });

        /*fragmentsViewModel.getPlaces().observe(MainActivity.this, new Observer<ArrayList<Place>>() {
            @Override
            public void onChanged(ArrayList<Place> p) {
                if (!isUpdatedFromActivity) {
                    suggestedPlaces.clear();

                    selectedPlaces.clear();
                    places.clear();
                    places.addAll(p);

                    map.setScrollableAreaLimitLatitude(places.get(0).getCoordinates().getLat(), places.get(0).getCoordinates().getLat(), 0);
                    map.setScrollableAreaLimitLongitude(places.get(0).getCoordinates().getLon(), places.get(0).getCoordinates().getLon(), 0);
                    handleArrow.animate().rotation(0).setDuration(300).start();
                    itemContainer.animate().translationY(itemContainer.getHeight()-handle.getHeight()).setDuration(300).start();
                    map.setMultiTouchControls(true);
                    map.setBuiltInZoomControls(true);
                    map.resetScrollableAreaLimitLatitude();
                    map.resetScrollableAreaLimitLongitude();

                    markCoordinatesOnMap(places);
                }else {
                    isUpdatedFromActivity = false;
                }
            }
        });

        fragmentsViewModel.getTransport().observe(this, new Observer<String>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onChanged(String s) {
                selectedTransport = s;

                searchDetailsLayout.setVisibility(View.VISIBLE);

                if (s.equals("walk")){
                    transportImage.setImageDrawable(resources.getDrawable(R.drawable.walk, getTheme()));
                }else {
                    transportImage.setImageDrawable(resources.getDrawable(R.drawable.car, getTheme()));
                }

            }
        });
        fragmentsViewModel.getDistance().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                selectedDistance = integer;

                switch (integer){
                    case 15: {
                        withinText.setText(R.string.within_15);
                        break;
                    }
                    case 30: {
                        withinText.setText(R.string.within_30);
                        break;
                    }
                    default: {
                        withinText.setText(R.string.within_45);
                        break;
                    }
                }

            }
        });
        fragmentsViewModel.getCategories().observe(this, new Observer<ArrayList<String>>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onChanged(ArrayList<String> arrayList) {
                allCategories.clear();
                allCategories.addAll(arrayList);

                categoriesChipLayout.removeAllViews();

                for (String category: arrayList){
                    Chip chip = new Chip(MainActivity.this);
                    chip.setText(category);
                    chip.setBackgroundDrawable(resources.getDrawable(R.drawable.toggle_button_style_off,getTheme()));
                    categoriesChipLayout.addView(chip);
                }
            }
        });*/

        fragmentsViewModel.getUpdated().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (!isUpdatedFromActivity) {
                    suggestedPlaces.clear();

                    selectedPlaces.clear();
                    places.clear();

                    allCategories.clear();

                    ExecutorService executorService = Executors.newSingleThreadExecutor();

                    executorService.execute(() -> {

                        try {
                            List<Search> search = fragmentsViewModel.getSearch();
                            /*
                            List<SearchWithPlaces> searchWithPlaces = fragmentsViewModel.getSearchWithPlaces();*/
                            new Handler(Looper.getMainLooper()).post(() -> {
                                executorService.execute(() -> {

                                    try {

                                        List<Place> tempPlace = fragmentsViewModel.getPlaces(search.get(0).getId());

                                        new Handler(Looper.getMainLooper()).post(() -> {

                                            executorService.execute(() -> {
                                                try {

                                                    List<String> categoryEntities = fragmentsViewModel.getCategories(search.get(0).getId());

                                                    new Handler(Looper.getMainLooper()).post(() -> {
                                                        allCategories.addAll(categoryEntities);
                                                        places.addAll(tempPlace);

                                                        if (search.get(0).getTransport().equals("walk")){
                                                            transportImage.setImageDrawable(resources.getDrawable(R.drawable.walk, getTheme()));
                                                        }else {
                                                            transportImage.setImageDrawable(resources.getDrawable(R.drawable.car, getTheme()));
                                                        }

                                                        selectedDistance = ((int) search.get(0).getDistance());
                                                        selectedTransport = search.get(0).getTransport();

                                                        switch (selectedDistance){
                                                            case 15: {
                                                                withinText.setText(R.string.within_15);
                                                                break;
                                                            }
                                                            case 30: {
                                                                withinText.setText(R.string.within_30);
                                                                break;
                                                            }
                                                            default: {
                                                                withinText.setText(R.string.within_45);
                                                                break;
                                                            }
                                                        }

                                                        categoriesChipLayout.removeAllViews();

                                                        for (String category: allCategories){
                                                            Chip chip = new Chip(MainActivity.this);
                                                            chip.setText(category);
                                                            chip.setBackgroundDrawable(resources.getDrawable(R.drawable.toggle_button_style_off,getTheme()));
                                                            categoriesChipLayout.addView(chip);
                                                        }

                                                        searchDetailsLayout.setVisibility(View.VISIBLE);

                                                        map.setScrollableAreaLimitLatitude(places.get(0).getCoordinates().getLat(), places.get(0).getCoordinates().getLat(), 0);
                                                        map.setScrollableAreaLimitLongitude(places.get(0).getCoordinates().getLon(), places.get(0).getCoordinates().getLon(), 0);
                                                        handleArrow.animate().rotation(0).setDuration(300).start();
                                                        itemContainer.animate().translationY(itemContainer.getHeight()-handle.getHeight()).setDuration(300).start();
                                                        map.setMultiTouchControls(true);
                                                        map.setBuiltInZoomControls(true);
                                                        map.resetScrollableAreaLimitLatitude();
                                                        map.resetScrollableAreaLimitLongitude();

                                                        markCoordinatesOnMap(places);
                                                    });
                                                }catch (Exception exception){}

                                            });

                                        });

                                    }catch (Exception ignored){}

                                });


                            });
                        } catch (Exception e) {
                        }

                    });

                }else {
                    isUpdatedFromActivity = false;
                }

            }
        });



        //OnClickListener for routeBTN
        //When the routeBTN is clicked the "Choose transportation mode" dialog is shown
        //----------------------------------------------------------------------------------------------------------------
        //BEGINNING OF routeBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------
        routePlanActionButton = findViewById(R.id.route_plan_action_button);
        routePlanActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                if (!isNavi) {
                    if (selectedPlaces.size()!=0) {

                        routePlanActionButton.setImageDrawable(resources.getDrawable(R.drawable.route_plan_done, getTheme()));

                        removeOtherMarkers();


                        String mode;

                        if (selectedTransport.equals("car"))
                            mode = "driving-car";
                        else mode = "foot-walking";

                        navigation.tspSolver(mode, selectedPlaces, new Navigation.TspCallback() {
                            @Override
                            public void onComplete(ArrayList<Polyline> polylines,ArrayList<Integer>indexes) {
                                map.getOverlays().addAll(polylines);
                                routePolys.addAll(polylines);
                                for (int i=1; i<selectedPlaces.size();i++){
                                    Place place = selectedPlaces.get(i);
                                    for (Marker marker: allMarkers){
                                        if (marker.getPosition().getLatitude()==place.getCoordinates().getLat() && marker.getPosition().getLongitude()==place.getCoordinates().getLon()){
                                            if (nameMarkers.contains(marker)){
                                                marker.setTextIcon(indexes.get(selectedPlaces.indexOf(place)) + " " + place.getName());
                                            }
                                        }
                                    }
                                }
                                map.invalidate();
                                isNavi = true;
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(MainActivity.this, R.string.route_planning_error, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this,R.string.route_empty_place_list,Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    routePlanActionButton.setImageDrawable(resources.getDrawable(R.drawable.route_plan, getTheme()));

                    List<Marker> markers = radiusMarkerClusterer.getItems();
                    setupMarkerClusterer(MainActivity.this);
                    markers.addAll(otherMarkers);

                    for (int i=1; i<selectedPlaces.size();i++){
                        Place place = selectedPlaces.get(i);
                        for (Marker marker: markers){
                            if (marker.getPosition().getLatitude()==place.getCoordinates().getLat() && marker.getPosition().getLongitude()==place.getCoordinates().getLon()){
                                if (nameMarkers.contains(marker)){
                                    marker.setTextIcon(place.getName());
                                }
                            }
                        }
                    }

                    for (Marker marker: markers){
                        radiusMarkerClusterer.add(marker);
                    }

                    map.getOverlays().add(radiusMarkerClusterer);
                    map.getOverlays().removeAll(routePolys);
                    map.invalidate();
                    isNavi = false;
                }

            }
        });
        //----------------------------------------------------------------------------------------------------------------
        //END OF routeBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------


        userBTN= findViewById(R.id.userBTN2);
        userBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivityForResult(intent, SHARED_REQUEST_CODE);
            }
        });


        mapAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = placesAdapter.getItem(position);

                for (Place place : places){
                    if (place.getName()!=null) {
                        if (place.getName().equals(name)) {
                            GeoPoint geoPoint = new GeoPoint(place.getCoordinates().getLat(), place.getCoordinates().getLon());
                            mapController.setCenter(geoPoint);
                            mapController.setZoom(20.0);
                        }
                    }
                }
            }
        });


    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF menuBTN's OnClickListener
    //----------------------------------------------------------------------------------------------------------------


    //Code lines necessary for the integration of the OSM
    //----------------------------------------------------------------------------------------------------------------
    //BEGIN
    //----------------------------------------------------------------------------------------------------------------
    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use

        /*if (mAuth.getCurrentUser()!=null || sharedPreferences.getBoolean("loggedIn", false)){
            shareSearchBTN.setVisibility(View.VISIBLE);
        }else {
            shareSearchBTN.setVisibility(View.INVISIBLE);
        }*/

        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume();*/ //needed for compass, my location overlays, v6.0.0 and up
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
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clearAll();
    }

/*    @Override
    protected void onStop() {
        super.onStop();

        appDatabase.clearAllTables();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHARED_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getSerializableExtra("result") != null) {

                        Share share = (Share) data.getSerializableExtra("result");
                        places.clear();
                        selectedPlaces.clear();
                        places.addAll(share.getSharedPlaces());
                        selectedTransport = share.getShareDetails().getTransportMode();
                        allCategories.addAll(share.getShareDetails().getCategories());

                        if (share.getShareDetails().getTransportMode().equals("walk")){
                            transportImage.setImageDrawable(resources.getDrawable(R.drawable.walk, getTheme()));
                        }else {
                            transportImage.setImageDrawable(resources.getDrawable(R.drawable.car, getTheme()));
                        }

                        selectedDistance = (share.getShareDetails().getDistance());

                        switch (selectedDistance){
                            case 15: {
                                withinText.setText(R.string.within_15);
                                break;
                            }
                            case 30: {
                                withinText.setText(R.string.within_30);
                                break;
                            }
                            default: {
                                withinText.setText(R.string.within_45);
                                break;
                            }
                        }

                        categoriesChipLayout.removeAllViews();

                        for (String category: allCategories){
                            Chip chip = new Chip(MainActivity.this);
                            chip.setText(category);
                            chip.setBackgroundDrawable(resources.getDrawable(R.drawable.toggle_button_style_off,getTheme()));
                            categoriesChipLayout.addView(chip);
                        }

                        searchDetailsLayout.setVisibility(View.VISIBLE);


                        isUpdatedFromActivity = true;
                        /*fragmentsViewModel.getTransport().postValue(share.getShareDetails().getTransportMode());
                        fragmentsViewModel.getCategories().postValue(share.getShareDetails().getCategories());
                        fragmentsViewModel.getPlaces().postValue(share.getSharedPlaces());
                        fragmentsViewModel.getCategories().postValue(share.getShareDetails().getCategories());
                        fragmentsViewModel.getDistance().postValue(share.getShareDetails().getDistance());*/
                        fragmentsViewModel.uploadToDatabase(share.getShareDetails().getDistance(),share.getShareDetails().getTransportMode(),
                                share.getSharedPlaces(),share.getShareDetails().getCategories());


                        markCoordinatesOnMap(places);

                    }
                }
            }
        }
    }

    private void clearAll(){
        resources.flushLayoutCache();
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
    public void markCoordinatesOnMap(ArrayList<Place> places){

        map.getOverlays().clear();

        IMapController mapControllerOnResume = map.getController();
        mapControllerOnResume.setZoom(15.0);

        allMarkers = new ArrayList<>();
        nameMarkers.clear();

        Marker start = new Marker(map);
        GeoPoint startGeoPoint = new GeoPoint(places.get(0).getCoordinates().getLat(),places.get(0).getCoordinates().getLon());
        start.setPosition(startGeoPoint);
        start.setIcon(resources.getDrawable(R.drawable.start_marker));

        allMarkers.add(start);
        start.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);

        if (!selectedPlaces.contains(places.get(0))) {
            selectedPlaces.add(places.get(0));
        }

        for(int i = 1; i< places.size(); i++){
            suggestedPlaces.add(places.get(i).getName());
        }

        placesAdapter = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,suggestedPlaces);
        mapAutoComplete.setAdapter(placesAdapter);


        for (int i = 1; i < places.size(); i++) {

            Marker marker = new Marker(map);
            Drawable icon = categoryManager.getMarkerIcon(places.get(i).getCategory());

            if (categoryManager.getMarkerFullCategory(places.get(i).getCategory()).equals(resources.getString(R.string.aquatics))){
                icon.setTint(resources.getColor(R.color.beach_resort_alt,getTheme()));
            }
            if (categoryManager.getMarkerFullCategory(places.get(i).getCategory()).equals(resources.getString(R.string.concert))){
                icon.setTint(resources.getColor(R.color.music_alt, getTheme()));
            }
            if (categoryManager.getMarkerFullCategory(places.get(i).getCategory()).equals(resources.getString(R.string.theatre))){
                icon.setTint(resources.getColor(R.color.theatre_alt, getTheme()));
            }

            marker.setIcon(icon);
            marker.setTitle(places.get(i).getName());
            GeoPoint geoPoint = new GeoPoint(places.get(i).getCoordinates().getLat(),places.get(i).getCoordinates().getLon());
            marker.setPosition(geoPoint);

            Marker nameMarker = new Marker(map);
            nameMarker.setTextIcon(places.get(i).getName());
            nameMarker.setPosition(geoPoint);

            allMarkers.add(marker);
            allMarkers.add(nameMarker);
            nameMarkers.add(nameMarker);

            /*textMarkers.add(nameMarker);
            allMarkers.add(marker);*/
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
                int index = 0;

                for(Place place: places){
                    if (place.getCoordinates().getLat()==position.getLatitude() && place.getCoordinates().getLon()==position.getLongitude())
                        index = places.indexOf(place);
                }


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
                TextView markerAddressMD = dialog.findViewById(R.id.markerAddressMD);

                TextView openTV= dialog.findViewById(R.id.openTV);
                TextView cuisineTV= dialog.findViewById(R.id.cuisineTV);
                TextView chargesTV = dialog.findViewById(R.id.chargesTV);

                Button addMD= dialog.findViewById(R.id.addMD);
                Button removeMD= dialog.findViewById(R.id.removeMD);

                markerNameMD.setText(places.get(index).getName());
                markerCategoryMD.setText(categoryManager.getMarkerFullCategory(places.get(index).getCategory()));

                markerCuisineMD.setText(places.get(index).getCuisine().replaceAll(";","\n"));
                markerOpeningHoursMD.setText(places.get(index).getOpeningHours().replaceAll(";","\n"));
                markerChargeMD.setText(places.get(index).getCharge().replaceAll(";","\n"));

                if (selectedPlaces.contains(places.get(index))){
                    addMD.setVisibility(View.INVISIBLE);
                    removeMD.setVisibility(View.VISIBLE);
                }else{
                    addMD.setVisibility(View.VISIBLE);
                    removeMD.setVisibility(View.INVISIBLE);
                }

                if (places.get(index).getAddress()!=null)
                    if (places.get(index).getAddress().AddressAsString().equals("unknown")){
                        markerAddressMD.setVisibility(View.GONE);
                    }else markerAddressMD.setText(places.get(index).getAddress().AddressAsString());
                else markerAddressMD.setVisibility(View.GONE);

                if (places.get(index).getCuisine().equals("unknown")){
                    cuisineTV.setVisibility(View.GONE);
                    markerCuisineMD.setVisibility(View.GONE);
                }
                if (places.get(index).getOpeningHours().equals("unknown")){
                    openTV.setVisibility(View.GONE);
                    markerOpeningHoursMD.setVisibility(View.GONE);
                }
                if (places.get(index).getCharge().equals("unknown")){
                    chargesTV.setVisibility(View.GONE);
                    markerChargeMD.setVisibility(View.GONE);
                }

                int finalIndex = index;
                addMD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPlaces.add(places.get(finalIndex));

                        if (selectedPlaces.size()>2){
                            routePlanActionButton.setVisibility(View.VISIBLE);
                        }

                        if (!selectedCategories.contains(categoryManager.getMarkerFullCategory(places.get(finalIndex).getCategory()))) {
                            selectedCategories.add(categoryManager.getMarkerFullCategory(places.get(finalIndex).getCategory()));
                        }
                        m.setIcon(resources.getDrawable(R.drawable.selected_marker));
                        map.invalidate();
                        dialog.dismiss();
                    }
                });

                removeMD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectedPlaces.remove(places.get(finalIndex));

                        if (selectedPlaces.size()<2){
                            routePlanActionButton.setVisibility(View.GONE);
                        }

                        selectedCategories.remove(categoryManager.getMarkerFullCategory(places.get(finalIndex).getCategory()));
                        m.setIcon(categoryManager.getMarkerIcon(places.get(finalIndex).getCategory()));
                        map.invalidate();
                        dialog.dismiss();
                    }
                });

                dialog.show();

                return true;
            });
            //----------------------------------------------------------------------------------------------------------------
            //END OF OnClickListeners for markers
            //----------------------------------------------------------------------------------------------------------------


            /*map.getOverlays().add(marker);
            map.getOverlays().add(nameMarker);*/
            //marker.setIcon(getResources().getDrawable(R.drawable.other_marker));


        }
        checkBoundingBox(allMarkers);
        mapControllerOnResume.setCenter(start.getPosition());
    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF markCoordinatesOnMap
    //----------------------------------------------------------------------------------------------------------------

    //removeOtherMarkers
    //removes not selected markers from map
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF removeOtherMarkers
    //----------------------------------------------------------------------------------------------------------------
    private void removeOtherMarkers() {
        routeMarkers.clear();

        setupMarkerClusterer(this);
        for (Place place : selectedPlaces) {
            for (Marker marker: allMarkers) {
                if (marker.getPosition().getLongitude() == place.getCoordinates().getLon() && marker.getPosition().getLatitude() == place.getCoordinates().getLat()){
                    radiusMarkerClusterer.add(marker);
                    routeMarkers.add(marker);
                }else otherMarkers.add(marker);
            }
        }
        Log.d("routeSize", String.valueOf(routeMarkers.size()));
        map.getOverlays().add(radiusMarkerClusterer);
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

    //----------------------------------------------------------------------------------------------------------------
    //END OF startNavigation
    //----------------------------------------------------------------------------------------------------------------

    private void setupMarkerClusterer(Context context){

        if (radiusMarkerClusterer!=null)
            map.getOverlays().remove(radiusMarkerClusterer);


        radiusMarkerClusterer = new RadiusMarkerClusterer(context);

        Bitmap clusterIcon = BonusPackHelper.getBitmapFromVectorDrawable(this,R.drawable.other_marker);

        radiusMarkerClusterer.setIcon(clusterIcon);

        radiusMarkerClusterer.setRadius(300);

        radiusMarkerClusterer.setMaxClusteringZoomLevel(16);

    }

    private void checkBoundingBox(ArrayList<Marker> markers){

        if (map!=null) {
            setupMarkerClusterer(this);

            BoundingBox boundingBox = map.getBoundingBox();

            for (Marker marker : markers) {
                if (boundingBox.contains(marker.getPosition())) {
                    radiusMarkerClusterer.add(marker);
                }
            }

            map.getOverlays().add(radiusMarkerClusterer);
        }
    }

}