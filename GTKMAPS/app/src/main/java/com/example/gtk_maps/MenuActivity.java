package com.example.gtk_maps;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

// -------------------------------------------------------------------------------------------------------------
// | MenuActivity                                                                                              |
// | is a main menu                                                                                            |
// | Mostly for calling and handling the data returned by other activities that are important to make a search |
// | and returns processed set of coordinates almost ready to be shown on the map                              |
// | Contains:                                                                                                 |
// |                                                                                                           |
// | searchByPlaceBTN's OnClickListener                                                                        |
// | searchByCurrentPositionSW's OnCheckedChangeListener                                                       |
// | categoriesBTN's OnClickListener                                                                           |
// | scanQRCode                                                                                                |
// | findNearbyPlacesRequest                                                                                   |
// -------------------------------------------------------------------------------------------------------------



public class MenuActivity extends AppCompatActivity {


    private SaveManager saveManager;
    private VolleyRequests volleyRequests;
    private CacheManager cacheManager;
    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final int avgWalkSpeed = 3500;
    private final int avgCarSpeed = 40000;
    //private boolean savedSearch;
    private double dist;
    private String categories="", nearbyUrl, nameUrl, transportMode, distance, tMode,selectedDist, place;
    private String[] splitResult;
    private GeoPoint currentLocation=null;
    private LinearLayout detailedLL, matchLL;
    private EditText cityET, streetET, houseNumberET, placeET;
    private Button categoriesBTN, detailedBTN;
    private ImageButton searchByPlaceBTN, savedBTN, openBTN, userBTN;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public static Switch searchByCurrentPositionSW;
    private Spinner timeSpinner;
    private ListView matchLV;
    private GifImageView loadingGif;
    private RadioGroup formOfTransport;
    private MyLocationListener locationListener;
    private RequestQueue volleyQueue;
    private Resources resources;

    private static final int CATEGORIES_REQUEST_CODE = 1;
    private static final int SAVED_REQUEST_CODE = 2;
    private static final int SHARED_REQUEST_CODE =3;
    //private static final int QR_READER_CODE = 3;
    private ArrayList<String> categoryNames,matchCoordinatesArrayList, matchLabelsArrayList, firebaseCategories;
    private ArrayAdapter<String> matchArrayAdapter;
    private SharedPreferences sharedPreferences, warning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        resources = getResources();

        cityET = findViewById(R.id.cityET);
        streetET = findViewById(R.id.streetET);
        houseNumberET = findViewById(R.id.houseNumberET);
        timeSpinner = findViewById(R.id.timeSpinner);
        searchByPlaceBTN = findViewById(R.id.searchByPlaceBTN);
        categoriesBTN = findViewById(R.id.categoriesBTN);
        searchByCurrentPositionSW = findViewById(R.id.searchByCurrentPositionSW);
        userBTN= findViewById(R.id.userBTN);
        loadingGif = findViewById(R.id.loadingGif);
        formOfTransport = findViewById(R.id.formOfTravel);
        placeET= findViewById(R.id.placeET);
        detailedBTN= findViewById(R.id.detailedBTN);
        savedBTN = findViewById(R.id.savedBTN);

        detailedLL= findViewById(R.id.detailedLL);
        matchLL = findViewById(R.id.matchLL);

        matchLV= findViewById(R.id.matchLV);

        loadingGif.setVisibility(View.INVISIBLE);

        saveManager= SaveManager.getInstance(MenuActivity.this);
        volleyQueue = Volley.newRequestQueue(MenuActivity.this);
        volleyRequests = new VolleyRequests(volleyQueue, MenuActivity.this);
        cacheManager = new CacheManager(MenuActivity.this);

        matchCoordinatesArrayList= new ArrayList<>();
        matchLabelsArrayList= new ArrayList<>();
        firebaseCategories= new ArrayList<>();

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);
        warning= getSharedPreferences("warning", Context.MODE_PRIVATE);

        firebaseManager= FirebaseManager.getInstance(MenuActivity.this,mAuth,mDatabase, sharedPreferences);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.distances_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        userBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                Intent intent = new Intent(MenuActivity.this, UserActivity.class);
                startActivityForResult(intent, SHARED_REQUEST_CODE);
            }
        });

        savedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                Intent intent = new Intent(MenuActivity.this, SavedActivity.class);
                startActivityForResult(intent,SAVED_REQUEST_CODE);
            }
        });

        detailedBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (detailedLL.getVisibility()==View.GONE) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    detailedLL.startAnimation(animation);

                    detailedLL.setVisibility(View.VISIBLE);

                    if (matchLV.getVisibility()==View.VISIBLE){
                        matchLV.setVisibility(View.GONE);
                        Animation closeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                        matchLV.startAnimation(closeAnimation);
                    }

                    cityET.setEnabled(true);
                    streetET.setEnabled(true);
                    houseNumberET.setEnabled(true);
                }else {

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                    detailedLL.startAnimation(animation);
                    detailedLL.setVisibility(View.GONE);

                    cityET.setEnabled(false);
                    streetET.setEnabled(false);
                    houseNumberET.setEnabled(false);
                }

            }
        });

        openBTN= findViewById(R.id.openBTN);
        openBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                if (matchLV.getVisibility()==View.VISIBLE){
                    matchLV.setVisibility(View.GONE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                    matchLV.startAnimation(animation);

                }else {
                    matchLV.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    matchLV.startAnimation(animation);
                    if (detailedLL.getVisibility()==View.VISIBLE){
                        detailedLL.setVisibility(View.GONE);
                        Animation closeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                        detailedLL.startAnimation(closeAnimation);
                    }
                }
            }
        });


        // Set an onClickListener for the searcByPlaceBTN,
        // and send a request to the Overpass API,
        // that returns the coordinates of the place with te provided name
        // or simply uses the currentLocation
        // The findNearbyPlacesRequest method is called either way, with the url parameter
        //----------------------------------------------------------------------------------------------------------------
        //BEGINNING OF searchByPlaceBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------
        
        searchByPlaceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingGif.setVisibility(View.VISIBLE);
                openBTN.setVisibility(View.INVISIBLE);
                animateImageButton(v);
                /*else{
                    //public transport
                    dist = avgCarSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                }*/

                String nameOfPlace = placeET.getText().toString().trim();
                String city = cityET.getText().toString().trim();
                String street = streetET.getText().toString().trim();
                String houseNumber = houseNumberET.getText().toString().trim();

                boolean notEmpty = !(nameOfPlace.equals("") && city.equals("") && street.equals("") && houseNumber.equals(""));
                if (notEmpty)
                    cacheManager.setSearchLabelDetails(nameOfPlace,city,street,houseNumber);
                // if the searchByCurrentPositionSW is not switched a search is made with the name of the starting point
                // provided through the placeET EditText field
                //----------------------------------------------------------------------------------------------------------------
                //BEGINNING OF searching by the name of the starting point
                //----------------------------------------------------------------------------------------------------------------

                if (currentLocation == null) {
                    if (notEmpty){

                        boolean isFirstWarning = warning.getBoolean("isFirstWarning",true);

                        if(isFirstWarning) {
                            Dialog dialog = new Dialog(MenuActivity.this);
                            dialog.setContentView(R.layout.longer_wait_alert);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);
                            dialog.show();
                            SharedPreferences.Editor editor = warning.edit();
                            editor.putBoolean("isFirstWarning", false);
                            editor.apply();
                        }

                        if (cacheManager.checkCacheFileContentIfContains()){
                            ArrayList<String> cacheFileMatchLabels= cacheManager.getCacheFileMatchLabels();
                            ArrayList<String> cacheFileMatchCoordinates= cacheManager.getCacheFileMatchCoordinates();
                            nearbyRequestHandler(cacheFileMatchLabels, cacheFileMatchCoordinates);

                        }else {

                            ManipulateUrl manipulateCenterUrl = new ManipulateUrl(nameOfPlace, city, street, houseNumber);
                            nameUrl = manipulateCenterUrl.getCenterUrl();
                            Log.d("nameUrl", nameUrl);

                            volleyRequests.makeCenterRequest(nameUrl, new VolleyRequests.CenterVolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<String> matchCoordinatesResult, ArrayList<String> matchLabelsResult) {

                                    cacheManager.writeToCacheFile(matchCoordinatesResult,matchLabelsResult);

                                    nearbyRequestHandler(matchLabelsResult, matchCoordinatesResult);

                            }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(MenuActivity.this, resources.getString(R.string.there_is_no_such_place), Toast.LENGTH_LONG).show();
                                    loadingGif.setVisibility(View.INVISIBLE);

                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, 0);
                                    matchLL.setLayoutParams(layoutParams);
                                    volleyRequests.clearAllVolleyRequest();
                                }
                            });
                        }
                    }else {
                        Toast.makeText(MenuActivity.this,R.string.provide_start, Toast.LENGTH_LONG).show();
                        loadingGif.setVisibility(View.INVISIBLE);
                    }

                //----------------------------------------------------------------------------------------------------------------
                //END OF searching by the name of the starting point
                //----------------------------------------------------------------------------------------------------------------
                }else{

                //----------------------------------------------------------------------------------------------------------------
                //BEGINNING OF searching by the current location
                //----------------------------------------------------------------------------------------------------------------

                    int selectedDistanceIndex = timeSpinner.getSelectedItemPosition();
                    if (selectedDistanceIndex!=0){
                        selectedDist = (String) timeSpinner.getSelectedItem();
                        distance=(String) timeSpinner.getSelectedItem();
                    }
                    if (selectedDistanceIndex == 0) {
                        Toast.makeText(MenuActivity.this, resources.getString(R.string.please_choose_distance), Toast.LENGTH_SHORT).show();
                        loadingGif.setVisibility(View.INVISIBLE);
                        return;
                    }

                    if(formOfTransport.getCheckedRadioButtonId() == -1)
                    {
                        Toast.makeText(MenuActivity.this, resources.getString(R.string.please_choose_transport_mode), Toast.LENGTH_SHORT).show();
                        loadingGif.setVisibility(View.INVISIBLE);
                        return;
                    }
                    if(formOfTransport.getCheckedRadioButtonId() == R.id.walk)
                    {
                        dist = avgWalkSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                        tMode = resources.getString(R.string.walk);
                        transportMode="walk";

                    }else if(formOfTransport.getCheckedRadioButtonId() == R.id.car)
                    {
                        dist = avgCarSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                        tMode = resources.getString(R.string.car);
                        transportMode="car";
                    }

                    if (!categories.equals("")) {

                        ManipulateUrl manipulateUrl = new ManipulateUrl(categories, String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), dist);
                        nearbyUrl = manipulateUrl.getNearbyUrl();
                        Log.d("nearbyUrl", nearbyUrl);
                        String start = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                        volleyRequests.findNearbyPlacesRequest(start, nearbyUrl, new VolleyRequests.NearbyVolleyCallback() {
                            @Override
                            public void onSuccess(Map<String, ArrayList<String>> extractedMap) {

                                Map<String, Object> labelDetails = new HashMap<>();
                                labelDetails.put("categories", categoryNames);
                                labelDetails.put("place", resources.getString(R.string.current));
                                labelDetails.put("transportMode", tMode);
                                labelDetails.put("distance", selectedDist);

                                Intent intent = new Intent();
                                intent.putExtra("label", (Serializable) labelDetails);
                                intent.putExtra("extractedMap", (Serializable) extractedMap);
                                setResult(RESULT_OK, intent);
                                if (firebaseCategories.size() != 0) {
                                    firebaseManager.incrementDatabaseStat(firebaseCategories, transportMode, timeSpinner.getSelectedItem().toString());
                                }
                                finish();
                                volleyRequests.clearAllVolleyRequest();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(MenuActivity.this, resources.getString(R.string.something_happened), Toast.LENGTH_LONG).show();
                                loadingGif.setVisibility(View.INVISIBLE);
                                volleyRequests.clearAllVolleyRequest();
                            }
                        });
                    }else {
                        Toast.makeText(MenuActivity.this,R.string.empty_category_list,Toast.LENGTH_LONG).show();
                    }

                //----------------------------------------------------------------------------------------------------------------
                //END OF searching by the current location
                //----------------------------------------------------------------------------------------------------------------
                }
            }
        });
        //----------------------------------------------------------------------------------------------------------------
        //END OF searchByPlaceBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------



        //A setOnCheckedChangeListener for the searchByCurrentPositionSW switch that sets the value of the currentLocation
        // variable, to the latest known location of the device
        // Used by the OnClick function of the searchByPlaceBTN
        //----------------------------------------------------------------------------------------------------------------
        //BEGINNING OF searchByCurrentPositionSW's OnCheckedChangeListener
        //----------------------------------------------------------------------------------------------------------------
        searchByCurrentPositionSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                placeET.setEnabled(!isChecked);

                LinearLayout.LayoutParams detailedLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 0);
                detailedLL.setLayoutParams(detailedLayoutParams);

                if (isChecked) {

                    locationListener = new MyLocationListener(MenuActivity.this);
                    double longitude = locationListener.getLongitude();
                    double latitude = locationListener.getLatitude();

                    currentLocation = new GeoPoint(latitude, longitude);
                    Log.d("gps", longitude + " " + latitude);
                    locationListener.stopListener();
                }
                else currentLocation= null;
            }
        });
        //----------------------------------------------------------------------------------------------------------------
        //END OF searchByCurrentPositionSW's OnCheckedChangeListener
        //----------------------------------------------------------------------------------------------------------------


        //set an onClickListener for categoriesBTN,
        //that opens the categories in an other activity
        //----------------------------------------------------------------------------------------------------------------
        //BEGINNING OF categoriesBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------

        categoriesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MenuActivity.this, ListActivity.class);
                startActivityForResult(intent, CATEGORIES_REQUEST_CODE);
            }
        });

        //----------------------------------------------------------------------------------------------------------------
        //END OF categoriesBTN's OnClickListener
        //----------------------------------------------------------------------------------------------------------------
    }
    
    // Starts the QR code reader of the mobile device
    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF scanQRCode
    //----------------------------------------------------------------------------------------------------------------
    /*public void scanQRCode(View v) {
        // we need to create the object
        // of IntentIntegrator class
        // which is the class of QR library
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);

        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setRequestCode(QR_READER_CODE);
        intentIntegrator.initiateScan();
    }*/
    //----------------------------------------------------------------------------------------------------------------
    //END OF scanQRCode
    //----------------------------------------------------------------------------------------------------------------

    private void nearbyRequestHandler(ArrayList<String> labels,ArrayList<String> coordinates) {
        openBTN.setVisibility(View.VISIBLE);

        loadingGif.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.delayed_slide_down);
        matchLV.startAnimation(animation);
        matchLV.setVisibility(View.VISIBLE);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
        detailedLL.startAnimation(animation2);
        detailedLL.setVisibility(View.GONE);

        matchArrayAdapter = new ArrayAdapter<>(MenuActivity.this, android.R.layout.simple_list_item_1, matchLabelsArrayList);
        matchLV.setAdapter(matchArrayAdapter);

        if (!matchArrayAdapter.isEmpty()) {
            matchArrayAdapter.clear();
            // Clear the data sources
            matchLabelsArrayList.clear();
            matchCoordinatesArrayList.clear();
        }
        matchLabelsArrayList.addAll(labels);
        matchCoordinatesArrayList.addAll(coordinates);

        matchArrayAdapter.notifyDataSetChanged();

        matchLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                animateListViewItemClick(view);

                int selectedDistanceIndex = timeSpinner.getSelectedItemPosition();
                if (selectedDistanceIndex != 0) {
                    selectedDist = (String) timeSpinner.getSelectedItem();
                    distance = (String) timeSpinner.getSelectedItem();
                }
                if (selectedDistanceIndex == 0) {
                    Toast.makeText(MenuActivity.this, resources.getString(R.string.please_choose_distance), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (formOfTransport.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(MenuActivity.this, resources.getString(R.string.please_choose_transport_mode), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (formOfTransport.getCheckedRadioButtonId() == R.id.walk) {
                    dist = avgWalkSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                    tMode = resources.getString(R.string.walk);
                    transportMode= "walk";

                } else if (formOfTransport.getCheckedRadioButtonId() == R.id.car) {
                    dist = avgCarSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                    tMode = resources.getString(R.string.car);
                    transportMode="car";
                }

                place = matchLabelsArrayList.get(position);

                loadingGif.setVisibility(View.VISIBLE);
                splitResult = matchCoordinatesArrayList.get(position).split(",");

                if (!categories.equals("")) {

                    ManipulateUrl manipulateNearbyUrl = new ManipulateUrl(categories, splitResult[0], splitResult[1], dist);
                    nearbyUrl = manipulateNearbyUrl.getNearbyUrl();
                    Log.d("nearbyUrl", nearbyUrl);
                    volleyRequests.findNearbyPlacesRequest(matchCoordinatesArrayList.get(position), nearbyUrl, new VolleyRequests.NearbyVolleyCallback() {
                        @Override
                        public void onSuccess(Map<String, ArrayList<String>> returnTagsMap) {

                            Map<String, Object> labelDetails = new HashMap<>();
                            labelDetails.put("categories", categoryNames);
                            labelDetails.put("place", place);
                            labelDetails.put("transportMode", tMode);
                            labelDetails.put("distance", selectedDist);

                            Intent intent = new Intent();
                            intent.putExtra("label", (Serializable) labelDetails);
                            intent.putExtra("extractedMap", (Serializable) returnTagsMap);
                            setResult(RESULT_OK, intent);
                            if (firebaseCategories.size() != 0) {
                                firebaseManager.incrementDatabaseStat(firebaseCategories, transportMode, timeSpinner.getSelectedItem().toString());
                            }
                            finish();
                            volleyRequests.clearAllVolleyRequest();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(MenuActivity.this, resources.getString(R.string.something_happened), Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                            volleyRequests.clearAllVolleyRequest();
                        }
                    });
                }else {
                    Toast.makeText(MenuActivity.this,R.string.empty_category_list,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------
    //BEGINNING OF handling the responses of QR code reader and category selection
    //----------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CATEGORIES_REQUEST_CODE){
            if (resultCode== RESULT_OK){
                //Handle the string returned by the ListActivity
                // containing the categories in proper format
                //----------------------------------------------------------------------------------------------------------------
                //BEGIN
                //----------------------------------------------------------------------------------------------------------------
                if (data.getStringExtra("categories")!=null){
                        categories = data.getStringExtra("categories");
                        categoryNames = data.getStringArrayListExtra("categoryNames");
                        if (data.getStringArrayListExtra("firebaseCategories")!=null) {
                            firebaseCategories.addAll(data.getStringArrayListExtra("firebaseCategories"));
                        }
                        Log.d("categories", categories);
                        Log.d("firebaseCategories", String.valueOf(firebaseCategories));
                    }else {
                        Toast.makeText(MenuActivity.this, resources.getString(R.string.category_selection_error), Toast.LENGTH_LONG).show();
                    }

                //----------------------------------------------------------------------------------------------------------------
                //END
                //----------------------------------------------------------------------------------------------------------------

            }
        }
        if (requestCode==SAVED_REQUEST_CODE){
            if (resultCode== RESULT_OK){
                //Handle the string returned by the ListActivity
                // containing the categories in proper format
                //----------------------------------------------------------------------------------------------------------------
                //BEGIN
                //----------------------------------------------------------------------------------------------------------------
                if (data.getSerializableExtra("savedMap")!=null){
                    Intent intent = new Intent();
                    intent.putExtra("label", data.getSerializableExtra("label"));
                    intent.putExtra("savedMap", data.getSerializableExtra("savedMap"));
                    setResult(RESULT_OK,intent);
                    finish();
                }

                //----------------------------------------------------------------------------------------------------------------
                //END
                //----------------------------------------------------------------------------------------------------------------

            }
        }
        if (requestCode==SHARED_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Handle the string returned by the ListActivity
                // containing the categories in proper format
                //----------------------------------------------------------------------------------------------------------------
                //BEGIN
                //----------------------------------------------------------------------------------------------------------------
                if (data.getSerializableExtra("sharedMap") != null) {
                    Intent intent = new Intent();
                    intent.putExtra("label", data.getSerializableExtra("label"));
                    intent.putExtra("sharedMap", data.getSerializableExtra("sharedMap"));
                    setResult(RESULT_OK, intent);
                    finish();
                }

                //----------------------------------------------------------------------------------------------------------------
                //END
                //----------------------------------------------------------------------------------------------------------------

            }
        }
        /*if (requestCode== QR_READER_CODE){
            if (resultCode==RESULT_OK) {
                //Handle the response of the QR code reader
                // containing the coordinates in proper format
                //----------------------------------------------------------------------------------------------------------------
                //BEGIN
                //----------------------------------------------------------------------------------------------------------------
                IntentResult intentResult = IntentIntegrator.parseActivityResult(resultCode,data);
                saveManager.setPlace(String.valueOf(R.string.QR));
                saveManager.setTransportMode(" ");
                saveManager.setDistance(" ");
                saveManager.preAddSearch();
                Intent i = new Intent();

                i.putExtra("qrResponse", intentResult.getContents());

                setResult(RESULT_OK, i);

                finish();
                //----------------------------------------------------------------------------------------------------------------
                //END
                //----------------------------------------------------------------------------------------------------------------
            }
        }*/

    }
    //----------------------------------------------------------------------------------------------------------------
    //END OF handling the responses of QR code reader and category selection
    //----------------------------------------------------------------------------------------------------------------

    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_click);
        view.startAnimation(animation);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseCategories.clear();
        resources.flushLayoutCache();
    }
}
