package com.example.gtk_maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// -------------------------------------------------------------------------------------------------------------
// | ListActivity                                                                                              |
// | Is for selection of categories                                                                            |
// | Contains:                                                                                                 |
// |                                                                                                           |
// | a lot of onCheckedChangeListeners                                                                         |
// | SharedPreferences (saving the state of the checkboxes)                                                    |
// -------------------------------------------------------------------------------------------------------------

public class ListActivity extends AppCompatActivity {

    private CheckBox selectAll,monumentchurch,museumexhibition,park,nationalpark,themepark,
            castlefort,accomodation,shopping,farm,beach,hiking,cycling,boat,watersport,
            food,entertainment,spa,music,concerts,theatre,sports, lookout;
    private ArrayList<String> categoryNames, firebaseCategories;
    private String url="";
    private Button returnBTN;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        categoryNames = new ArrayList<>();
        firebaseCategories= new ArrayList<>();

        selectAll = findViewById(R.id.selectAll);
        monumentchurch = findViewById(R.id.monumentchurch);
        museumexhibition = findViewById(R.id.museumexhibition);
        park = findViewById(R.id.park);
        nationalpark = findViewById(R.id.nationalpark);
        themepark = findViewById(R.id.themepark);
        castlefort = findViewById(R.id.castlefort);
        accomodation = findViewById(R.id.accomodation);
        shopping = findViewById(R.id.shopping);
        farm = findViewById(R.id.farm);
        beach = findViewById(R.id.beach);
        hiking = findViewById(R.id.hiking);
        cycling = findViewById(R.id.cycling);
        boat = findViewById(R.id.boat);
        watersport = findViewById(R.id.watersport);
        food = findViewById(R.id.food);
        entertainment = findViewById(R.id.entertainment);
        spa = findViewById(R.id.spa);
        music = findViewById(R.id.music);
        concerts = findViewById(R.id.concerts);
        theatre = findViewById(R.id.theatre);
        sports = findViewById(R.id.sports);
        lookout = findViewById(R.id.lookout);

        //data query of the saved states of each checkbox, and the url, then update with the received data
        //----------------------------------------------------------------------------------------------------------------
        //BEGIN
        //----------------------------------------------------------------------------------------------------------------
        SharedPreferences sharedPreferences = getSharedPreferences("ListVals", Context.MODE_PRIVATE);

        selectAll.setChecked(sharedPreferences.getBoolean("isCheckedAll",false));
        monumentchurch.setChecked(sharedPreferences.getBoolean("isCheckedMC",false));
        museumexhibition.setChecked(sharedPreferences.getBoolean("isCheckedME",false));
        park.setChecked(sharedPreferences.getBoolean("isCheckedP",false));
        nationalpark.setChecked(sharedPreferences.getBoolean("isCheckedNP",false));
        themepark.setChecked(sharedPreferences.getBoolean("isCheckedTP",false));
        castlefort.setChecked(sharedPreferences.getBoolean("isCheckedCF",false));
        accomodation.setChecked(sharedPreferences.getBoolean("isCheckedACC",false));
        shopping.setChecked(sharedPreferences.getBoolean("isCheckedS",false));
        farm.setChecked(sharedPreferences.getBoolean("isCheckedF",false));
        beach.setChecked(sharedPreferences.getBoolean("isCheckedB",false));
        hiking.setChecked(sharedPreferences.getBoolean("isCheckedH",false));
        cycling.setChecked(sharedPreferences.getBoolean("isCheckedC",false));
        boat.setChecked(sharedPreferences.getBoolean("isCheckedBT",false));
        watersport.setChecked(sharedPreferences.getBoolean("isCheckedW",false));
        food.setChecked(sharedPreferences.getBoolean("isCheckedFD",false));
        entertainment.setChecked(sharedPreferences.getBoolean("isCheckedENT",false));
        spa.setChecked(sharedPreferences.getBoolean("isCheckedSA",false));
        music.setChecked(sharedPreferences.getBoolean("isCheckedMS",false));
        concerts.setChecked(sharedPreferences.getBoolean("isCheckedCS",false));
        theatre.setChecked(sharedPreferences.getBoolean("isCheckedTH",false));
        sports.setChecked(sharedPreferences.getBoolean("isCheckedSP",false));
        lookout.setChecked(sharedPreferences.getBoolean("isCheckedL",false));
        
        String savedURL = sharedPreferences.getString("savedURL", "");
        Set<String> savedCategoryNames = sharedPreferences.getStringSet("savedCategoryNames", null);
        Set<String> savedFirebaseCategories = sharedPreferences.getStringSet("savedFirebaseCategories", null);
        if (savedCategoryNames!=null) {
            categoryNames.addAll(savedCategoryNames);
        }
        if (savedFirebaseCategories!=null) {
            firebaseCategories.addAll(savedFirebaseCategories);
        }
        Log.d("categoryNames", String.valueOf(categoryNames));
        url= url + savedURL;

        returnBTN= findViewById(R.id.returnBTN);

        returnBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("savedURL", url);
                Set<String> savedCategoryNames = new HashSet<>(categoryNames);
                editor.putStringSet("savedCategoryNames",savedCategoryNames);
                Set<String> savedFirebaseCategories = new HashSet<>(firebaseCategories);
                editor.putStringSet("savedFirebaseCategories",savedFirebaseCategories);
                editor.apply();
                Intent intent = new Intent();
                intent.putExtra("categories", url);
                intent.putStringArrayListExtra("categoryNames", categoryNames);
                intent.putStringArrayListExtra("firebaseCategories", firebaseCategories);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //A LOT of setOnCheckedChangeListeners, and SharedPreferences for each checkbox
        //IMPORTANT: In case of better idea feel free to replace this solution
        //----------------------------------------------------------------------------------------------------------------
        //BEGIN
        //----------------------------------------------------------------------------------------------------------------

        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    setCheck(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedAll", true);
                    editor.apply();
                }
                else{
                    setCheck(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedAll");
                    editor.apply();
                }
            }
        });

        lookout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"tourism\"=\"viewpoint\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedL", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("lookout");
                }else{
                    url= url.replace("nwr[\"tourism\"=\"viewpoint\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedL");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("lookout");
                }
            }
        });

        monumentchurch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"historic\"=\"monument\"](around:dist,startLat,startLong);" +
                            "nwr[\"building\"=\"church\"](around:dist,startLat,startLong);" +
                            "nwr[\"building\"=\"temple\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedMC", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("monumentchurch");
                }else{
                    url= url.replace("nwr[\"historic\"=\"monument\"](around:dist,startLat,startLong);" +
                            "nwr[\"building\"=\"church\"](around:dist,startLat,startLong);" +
                            "nwr[\"building\"=\"temple\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedMC");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("monumentchurch");
                }
            }
        });
        museumexhibition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"tourism\"=\"museum\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"gallery\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"arts_centre\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"exhibition_centre\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedME", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("museumexhibition");
                }else{
                    url= url.replace("nwr[\"tourism\"=\"museum\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"gallery\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"arts_centre\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"exhibition_centre\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedME");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("museumexhibition");
                }
            }
        });
        park.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"leisure\"=\"park\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedP", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("park");
                }else{
                    url= url.replace("nwr[\"leisure\"=\"park\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedP");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("park");
                }
            }
        });
        nationalpark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"boundary\"=\"national_park\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedNP", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("nationalpark");
                }else{
                    url= url.replace("nwr[\"boundary\"=\"national_park\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedNP");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("nationalpark");
                }
            }
        });
        themepark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"tourism\"=\"theme_park\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedTP", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("themepark");
                }else{
                    url= url.replace("nwr[\"tourism\"=\"theme_park\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedTP");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("themepark");
                }
            }
        });
        castlefort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"historic\"=\"fort\"](around:dist,startLat,startLong);" +
                                "nwr[\"historic\"=\"castle\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedCF", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("castlefort");
                }else{
                    url= url.replace("nwr[\"historic\"=\"fort\"](around:dist,startLat,startLong);" +
                            "nwr[\"historic\"=\"castle\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedCF");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("castlefort");
                }
            }
        });
        accomodation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"building\"=\"hotel\"](around:dist,startLat,startLong);" +
                            "nwr[\"leisure\"=\"summer_camp\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"caravan_site\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"hostel\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"motel\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"guest_house\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"camp_site\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedACC", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("accomodation");
                }else{
                    url= url.replace("nwr[\"building\"=\"hotel\"](around:dist,startLat,startLong);" +
                            "nwr[\"leisure\"=\"summer_camp\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"caravan_site\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"hostel\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"motel\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"guest_house\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"camp_site\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedACC");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("accomodation");
                }
            }
        });
        shopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"shop\"=\"department_store\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"mall\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"boutique\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"clothes\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"shoes\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedS", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("shopping");
                }else{
                    url= url.replace("nwr[\"shop\"=\"department_store\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"mall\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"boutique\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"clothes\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"shoes\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedS");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("shopping");
                }
            }
        });
        farm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"shop\"=\"farm\"](around:dist,startLat,startLong);" +
                                "nwr[\"shop\"=\"marketplace\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedF", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("farm");
                }else{
                    url= url.replace("nwr[\"shop\"=\"farm\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"marketplace\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedF");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("farm");
                }
            }
        });
        beach.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"leisure\"=\"beach_resort\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedB", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("beach");
                }else{
                    url= url.replace("nwr[\"leisure\"=\"beach_resort\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedB");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("beach");
                }
            }
        });
        hiking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"route\"=\"hiking\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedH", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("hiking");
                }else{
                    url= url.replace("nwr[\"route\"=\"hiking\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedH");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("hiking");
                }
            }
        });
        cycling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"route\"=\"cycling\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedC", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("cycling");
                }else{
                    url= url.replace("nwr[\"route\"=\"cycling\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedC");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("cycling");
                }
            }
        });
        boat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"boat\"=\"yes\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"sailing\"](around:dist,startLat,startLong);" +
                            "nwr[\"leisure\"=\"marina\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedBT", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("boat");
                }else{
                    url= url.replace("nwr[\"boat\"=\"yes\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"sailing\"](around:dist,startLat,startLong);" +
                            "nwr[\"leisure\"=\"marina\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedBT");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("boat");
                }
            }
        });
        watersport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"sport\"=\"surfing\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"swimming\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"wakeboarding\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"water_polo\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"water_ski\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedW", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("watersport");
                }else{
                    url= url.replace("nwr[\"sport\"=\"surfing\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"swimming\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"wakeboarding\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"water_polo\"](around:dist,startLat,startLong);" +
                            "nwr[\"sport\"=\"water_ski\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedW");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("watersport");
                }
            }
        });
        food.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"amenity\"=\"restaurant\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"biergarten\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"cafe\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"food_court\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"fast_food\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"pub\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"bar\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedFD", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("food");
                }else{
                    url= url.replace("nwr[\"amenity\"=\"restaurant\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"biergarten\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"cafe\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"food_court\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"fast_food\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"pub\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"bar\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedFD");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("food");

                }
            }
        });
        entertainment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"amenity\"=\"casino\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"cinema\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"nightclub\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedENT", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("entertainment");
                }else{
                    url= url.replace("nwr[\"amenity\"=\"casino\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"cinema\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"nightclub\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedENT");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("entertainment");
                }
            }
        });
        spa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"shop\"=\"massage\"](around:dist,startLat,startLong);"
                            + "nwr[\"leisure\"=\"water_park\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedSA", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("spa");
                }else{
                    url= url.replace("nwr[\"shop\"=\"massage\"](around:dist,startLat,startLong);" +
                            "nwr[\"leisure\"=\"water_park\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedSA");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("spa");
                }
            }
        });
        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"amenity\"=\"music_venue\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"community_centre\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedMS", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("music");
                }else{
                    url= url.replace("nwr[\"amenity\"=\"music_venue\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"community_centre\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedMS");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("music");
                }
            }
        });
        concerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"amenity\"=\"music_venue\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedCS", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("concerts");
                }else{
                    url= url.replace("nwr[\"amenity\"=\"music_venue\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedCS");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("concerts");
                }
            }
        });
        theatre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedTH", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("theatre");
                }else{
                    url= url.replace("nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedTH");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("theatre");
                }
            }
        });
        sports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    url = url + "nwr[\"amenity\"=\"events_venue\"](around:dist,startLat,startLong);" +
                            "nwr[\"building\"=\"stadium\"](around:dist,startLat,startLong);" +
                            "nwr[\"building\"=\"sports_hall\"](around:dist,startLat,startLong);";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCheckedSP", true);
                    editor.apply();
                    categoryNames.add((String) buttonView.getText());
                    firebaseCategories.add("sports");
                }else{
                    url= url.replace("nwr[\"amenity\"=\"events_venue\"](around:dist,startLat,startLong);" +
                            "nwr[\"building\"=\"stadium\"](around:dist,startLat,startLong);" +
                            "nwr[\"building\"=\"sports_hall\"](around:dist,startLat,startLong);","");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isCheckedSP");
                    editor.apply();
                    categoryNames.remove((String) buttonView.getText());
                    firebaseCategories.remove("sports");
                }
            }
        });
        //----------------------------------------------------------------------------------------------------------------
        //END
        //----------------------------------------------------------------------------------------------------------------
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        categoryNames.clear();
        firebaseCategories.clear();
        url="";
    }

    //----------------------------------------------------------------------------------------------------------------
    //BEGIN
    //----------------------------------------------------------------------------------------------------------------
    public void setCheck(boolean value)
    {
        selectAll.setChecked(value);
        monumentchurch.setChecked(value);
        museumexhibition.setChecked(value);
        park.setChecked(value);
        nationalpark.setChecked(value);
        themepark.setChecked(value);
        castlefort.setChecked(value);
        accomodation.setChecked(value);
        shopping.setChecked(value);
        farm.setChecked(value);
        beach.setChecked(value);
        hiking.setChecked(value);
        cycling.setChecked(value);
        boat.setChecked(value);
        watersport.setChecked(value);
        food.setChecked(value);
        entertainment.setChecked(value);
        spa.setChecked(value);
        music.setChecked(value);
        concerts.setChecked(value);
        theatre.setChecked(value);
        sports.setChecked(value);
        lookout.setChecked(value);
    }
    //----------------------------------------------------------------------------------------------------------------
    //END
    //----------------------------------------------------------------------------------------------------------------
}