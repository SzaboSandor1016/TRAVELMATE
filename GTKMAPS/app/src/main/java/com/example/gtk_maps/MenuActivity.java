package com.example.gtk_maps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

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
    private int flag=0;
    private final int avgWalkSpeed = 3500;
    private final int avgCarSpeed = 40000;
    //private boolean savedSearch;
    private double dist;
    private String categories="", nearbyUrl, nameUrl;
    private String[] splitResult;
    private GeoPoint currentLocation=null;
    private LinearLayout detailedLL, matchLL;
    private EditText cityET, streetET, houseNumberET, placeET;
    private Button categoriesBTN, detailedBTN;
    private ImageButton searchByPlaceBTN, savedBTN;
    //ImageButton optionsBTN;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public static Switch searchByCurrentPositionSW;
    private Spinner timeSpinner;
    private ListView matchLV;
    private GifImageView loadingGif;
    private RadioGroup formOfTransport;
    private MyLocationListener locationListener;
    private RequestQueue volleyQueue;
    private VolleyRequests volleyRequests;
    private Resources resources;

    private static final int CATEGORIES_REQUEST_CODE = 1;
    private static final int SAVED_REQUEST_CODE = 2;
    private static final int QR_READER_CODE = 3;
    private ArrayList<String> categoryNames,matchCoordinatesArrayList, matchLabelsArrayList;
    private ArrayAdapter<String> matchArrayAdapter;

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
        //optionsBTN= findViewById(R.id.optionsBTN);
        loadingGif = findViewById(R.id.loadingGif);
        formOfTransport = findViewById(R.id.formOfTravel);
        placeET= findViewById(R.id.placeET);
        detailedBTN= findViewById(R.id.detailedBTN);
        savedBTN = findViewById(R.id.savedBTN);

        detailedLL= findViewById(R.id.detailedLL);
        matchLL = findViewById(R.id.matchLL);

        matchLV= findViewById(R.id.matchLV);

        loadingGif.setVisibility(View.INVISIBLE);

        saveManager= new SaveManager(this);
        volleyQueue = Volley.newRequestQueue(MenuActivity.this);
        volleyRequests = new VolleyRequests(volleyQueue, MenuActivity.this);

        matchCoordinatesArrayList= new ArrayList<>();
        matchLabelsArrayList= new ArrayList<>();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.distances_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        /*optionsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });*/

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
                LinearLayout.LayoutParams layoutParams;
                if (flag==0) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    detailedLL.startAnimation(animation);

                    detailedLL.setVisibility(View.VISIBLE);

                    cityET.setEnabled(true);
                    streetET.setEnabled(true);
                    houseNumberET.setEnabled(true);
                    flag=1;
                }else {

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                    detailedLL.startAnimation(animation);
                    detailedLL.setVisibility(View.GONE);

                    cityET.setEnabled(false);
                    streetET.setEnabled(false);
                    houseNumberET.setEnabled(false);
                    flag=0;
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

                animateImageButton(v);
                /*else{
                    //public transport
                    dist = avgCarSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                }*/

                String nameOfPlace = placeET.getText().toString().trim();
                String city = cityET.getText().toString().trim();
                String street = streetET.getText().toString().trim();
                String houseNumber = houseNumberET.getText().toString().trim();

                /*if (savedSearch){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //editor.putString("nameOfPlace", nameOfPlace);
                    editor.putInt("selected",timeSpinner.getSelectedItemPosition());
                    editor.putFloat("dist", (float) dist);
                    editor.putString("categories", categories);
                    editor.apply();
                }*/

                // if the searchByCurrentPositionSW is not switched a search is made with the name of the starting point
                // provided through the placeET EditText field
                //----------------------------------------------------------------------------------------------------------------
                //BEGINNING OF searching by the name of the starting point
                //----------------------------------------------------------------------------------------------------------------

                if (currentLocation == null) {

                    ManipulateUrl manipulateCenterUrl = new ManipulateUrl(nameOfPlace, city,street,houseNumber);
                    nameUrl=manipulateCenterUrl.getCenterUrl();
                    Log.d("nameUrl", nameUrl);

                    volleyRequests.makeCenterRequest(nameUrl, new VolleyRequests.CenterVolleyCallback() {
                        @Override
                        public void onSuccess(ArrayList<String> matchCoordinatesResult, ArrayList<String> matchLabelsResult) {
                            loadingGif.setVisibility(View.INVISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                            matchLL.startAnimation(animation);
                            matchLL.setVisibility(View.VISIBLE);

                            Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                            detailedLL.startAnimation(animation2);
                            detailedLL.setVisibility(View.GONE);

                            matchLabelsArrayList=matchLabelsResult;
                            matchCoordinatesArrayList= matchCoordinatesResult;

                            matchArrayAdapter = new ArrayAdapter<>(MenuActivity.this, android.R.layout.simple_list_item_1, matchLabelsArrayList);
                            matchLV.setAdapter(matchArrayAdapter);

                            matchLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    animateListViewItemClick(view);

                                    int selectedDistanceIndex = timeSpinner.getSelectedItemPosition();
                                    if (selectedDistanceIndex!=0){
                                        saveManager.setDistance((String) timeSpinner.getSelectedItem());
                                    }
                                    if (selectedDistanceIndex == 0) {
                                        Toast.makeText(MenuActivity.this, resources.getString(R.string.please_choose_distance), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if(formOfTransport.getCheckedRadioButtonId() == -1)
                                    {
                                        Toast.makeText(MenuActivity.this, resources.getString(R.string.please_choose_transport_mode), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if(formOfTransport.getCheckedRadioButtonId() == R.id.walk)
                                    {
                                        dist = avgWalkSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                                        saveManager.setTransportMode(resources.getString(R.string.walk));

                                    }else if(formOfTransport.getCheckedRadioButtonId() == R.id.car)
                                    {
                                        dist = avgCarSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                                        saveManager.setTransportMode(resources.getString(R.string.car));
                                    }

                                    saveManager.setPlace(matchLabelsArrayList.get(position));
                                    saveManager.preAddSearch();

                                    loadingGif.setVisibility(View.VISIBLE);
                                    splitResult = matchCoordinatesArrayList.get(position).split(",");
                                    ManipulateUrl manipulateNearbyUrl = new ManipulateUrl(categories, splitResult[0], splitResult[1], dist);
                                    nearbyUrl = manipulateNearbyUrl.getNearbyUrl();
                                    Log.d("nearbyUrl", nearbyUrl);
                                    volleyRequests.findNearbyPlacesRequest(matchCoordinatesArrayList.get(position),nearbyUrl, new VolleyRequests.NearbyVolleyCallback() {
                                        @Override
                                        public void onSuccess(ArrayList<String> result, ArrayList<String> namesResult,ArrayList<String>  tagsResults) {
                                            Intent intent = new Intent();
                                            intent.putExtra("coordsResponse", result);
                                            intent.putExtra("coordsNamesResponse",namesResult);
                                            intent.putExtra("coordsTagsResponse",tagsResults);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                            volleyRequests.clearAllVolleyrequest();
                                        }
                                        @Override
                                        public void onError(String error) {
                                            Toast.makeText(MenuActivity.this, resources.getString(R.string.something_happened), Toast.LENGTH_LONG).show();
                                            loadingGif.setVisibility(View.INVISIBLE);
                                            volleyRequests.clearAllVolleyrequest();
                                        }
                                    });
                                }
                            });

                        }
                        @Override
                        public void onError(String error) {
                            Toast.makeText(MenuActivity.this, resources.getString(R.string.there_is_no_such_place), Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 0);
                            matchLL.setLayoutParams(layoutParams);
                            volleyRequests.clearAllVolleyrequest();
                        }
                    });
                //----------------------------------------------------------------------------------------------------------------
                //END OF searching by the name of the starting point
                //----------------------------------------------------------------------------------------------------------------
                }else{

                //----------------------------------------------------------------------------------------------------------------
                //BEGINNING OF searching by the current location
                //----------------------------------------------------------------------------------------------------------------

                    int selectedDistanceIndex = timeSpinner.getSelectedItemPosition();
                    if (selectedDistanceIndex!=0){
                        saveManager.setDistance((String) timeSpinner.getSelectedItem());
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
                        saveManager.setTransportMode(resources.getString(R.string.walk));

                    }else if(formOfTransport.getCheckedRadioButtonId() == R.id.car)
                    {
                        dist = avgCarSpeed * (Double.parseDouble(timeSpinner.getSelectedItem().toString()) / 60);
                        saveManager.setTransportMode(resources.getString(R.string.car));
                    }

                    ManipulateUrl manipulateUrl = new ManipulateUrl(categories, String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), dist);
                    nearbyUrl = manipulateUrl.getNearbyUrl();
                    Log.d("nearbyUrl", nearbyUrl);
                    String start = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                    volleyRequests.findNearbyPlacesRequest(start, nearbyUrl, new VolleyRequests.NearbyVolleyCallback() {
                        @Override
                        public void onSuccess(ArrayList<String>  result, ArrayList<String>  namesResult, ArrayList<String>  tagsResults) {
                            saveManager.preAddSearch();
                            Intent intent = new Intent();
                            intent.putStringArrayListExtra("coordsResponse",result);
                            intent.putStringArrayListExtra("coordsNamesResponse",namesResult);
                            intent.putStringArrayListExtra("coordsTagsResponse",tagsResults);
                            setResult(RESULT_OK, intent);
                            finish();
                            volleyRequests.clearAllVolleyrequest();
                        }
                        @Override
                        public void onError(String error) {
                            Toast.makeText(MenuActivity.this, resources.getString(R.string.something_happened), Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                            volleyRequests.clearAllVolleyrequest();
                        }
                    });

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
                    saveManager.setPlace(resources.getString(R.string.current));
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
                        saveManager.setCategories(categoryNames);
                        Log.d("categories", categories);
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
                if (data.getStringArrayListExtra("savedSearch")!=null){
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("savedSearch", data.getStringArrayListExtra("savedSearch"));
                    intent.putStringArrayListExtra("savedSearchCategories", data.getStringArrayListExtra("savedSearchCategories"));
                    intent.putStringArrayListExtra("savedSearchNames", data.getStringArrayListExtra("savedSearchNames"));
                    setResult(RESULT_OK,intent);
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

    }
}
