package com.example.gtk_maps;

import static com.android.volley.toolbox.Volley.newRequestQueue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RESOURCES = "resources";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private final int avgWalkSpeed = 3500;
    private final int avgCarSpeed = 40000;

    private AutoCompleteTextView searchAutoCompleteTextField;

    private Button select15,select30,select45,
            selectHotel,selectAccommodation,selectMuseum,
            selectNightclub;
    private ImageButton selectWalk,selectCar,searchByPlaceBTN;
    private LinearLayout categoriesContainer,categoriesHandle,selectHotelLayout, selectAccommodationLayout, selectMuseumLayout, selectNightclubLayout;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public Switch searchByCurrentPositionSW;
    private View categoriesHandleHandle;
    private RecyclerView otherCategoriesList;

    private Place startPlace;
    private MyLocationListener locationListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String url="";
    private int selectedSpeed;
    private int selectedDistance;
    private String selectedTransport;
    private ArrayList<Place> places;
    private ArrayList<String> displayableSuggestions;
    private ArrayList<String> displayableCoordinates;
    private ArrayList<Address> displayableAddresses;
    private ArrayList<String> allCategories;
    private ArrayList<String> firebaseCategories;
    private ArrayList<Category> initCategories;

    private CategoryRecyclerViewAdapter categoryAdapter;
    private FirebaseManager firebaseManager;
    private Requests requests;
    private RequestQueue volleyQueue;
    private Context context;
    private Resources resources;
    private SharedPreferences sharedPreferences;
    private FragmentsViewModel fragmentsViewModel;
    //private String mParam2;



    public class CategoryData {
        String label;
        String firebaseCategory;
        String urlPart;
        int[] drawables;

        CategoryData(String label,String firebaseCategory, String urlPart, int... drawables) {
            this.label = label;
            this.firebaseCategory = firebaseCategory;
            this.urlPart = urlPart;
            this.drawables = drawables;
        }
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
*/
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        // args.put(RESOURCES, resources);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/

        fragmentsViewModel = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.resources = getResources();
        this.context = requireContext();

        sharedPreferences = context.getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseManager = FirebaseManager.getInstance(context, mAuth, mDatabase, sharedPreferences);

        volleyQueue = newRequestQueue(context);
        requests = new Requests(volleyQueue, context);
        otherCategoriesList.setLayoutManager(new LinearLayoutManager(context));

        places = new ArrayList<>();

        displayableSuggestions = new ArrayList<>();
        displayableCoordinates = new ArrayList<>();
        displayableAddresses = new ArrayList<>();
        firebaseCategories = new ArrayList<>();
        allCategories = new ArrayList<>();


        initCategories = initCategories();

        categoryAdapter = new CategoryRecyclerViewAdapter(context,resources, initCategories, new CategoryRecyclerViewAdapter.AddRemoveCategory() {
            @Override
            public void addCategory(String category) {
                allCategories.add(category);
            }

            @Override
            public void removeCategory(String category) {
                allCategories.remove(category);
            }
        }, new CategoryRecyclerViewAdapter.UpdateUrl() {
            @Override
            public void addToURL(String urlPart) {
                url = url + urlPart;
            }

            @Override
            public void removeFromURL(String urlPart) {
                url = url.replace(urlPart, "");
            }
        }, new CategoryRecyclerViewAdapter.AddRemoveFirebaseCategory() {
            @Override
            public void addCategory(String category) {
                firebaseCategories.add(category);
            }

            @Override
            public void removeCategory(String category) {
                firebaseCategories.remove(category);
            }
        });
        otherCategoriesList.setAdapter(categoryAdapter);


        searchAutoCompleteTextField.setThreshold(2);

        searchAutoCompleteTextField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchAutoCompleteTextField.setText(displayableSuggestions.get(position));
                String startCoordinates = displayableCoordinates.get(position);
                String[] splitStartCoordinates = startCoordinates.split(" ");

                startPlace = new Place();
                startPlace.setAddress(displayableAddresses.get(position));
                startPlace.setCoordinatesWithLatLon(Double.parseDouble(splitStartCoordinates[0]), Double.parseDouble(splitStartCoordinates[1]));

            }
        });


        searchAutoCompleteTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    requests.getAutocompleteRequest(s.toString(), new Requests.GetAutocompleteCallback() {
                        @Override
                        public void onResponse(Response<PhotonResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<String> suggestions = new ArrayList<>();
                                List<String> dSuggestions = new ArrayList<>();
                                List<String> coordinates = new ArrayList<>();
                                List<Address> addresses = new ArrayList<>();
                                for (PhotonResponse.Feature feature : response.body().getFeatures()) {
                                    StringBuilder nameSuggestion = new StringBuilder();
                                    StringBuilder elseSuggestion = new StringBuilder();
                                    StringBuilder fullSuggestion = new StringBuilder();
                                    StringBuilder coordinate = new StringBuilder();
                                    Address address = new Address();
                                    PhotonResponse.Geometry geometry = feature.getGeometry();
                                    PhotonResponse.Properties properties = feature.getProperties();

                                    if (properties.getName() != null) {
                                        nameSuggestion.append(properties.getName());
                                    }
                                    if (properties.getCity() != null) {
                                        elseSuggestion.append(properties.getCity()).append(", ");
                                        address.setCity(properties.getCity());
                                    }
                                    if (properties.getStreet() != null) {
                                        elseSuggestion.append(properties.getStreet()).append(" ");
                                        address.setStreet(properties.getStreet());
                                    }
                                    if (properties.getHouseNumber() != null) {
                                        elseSuggestion.append(properties.getHouseNumber()).append(", ");
                                        address.setHouseNumber(properties.getHouseNumber());
                                    }
                                    if (properties.getCountry() != null) {
                                        elseSuggestion.append(properties.getCountry());
                                    }
                                    if (geometry.getCoordinates() != null) {
                                        coordinate.append(geometry.getCoordinates()[1]).append(" ").append(geometry.getCoordinates()[0]);
                                    }
                                    //modified.add(toBeDisplayed.toString());
                                    fullSuggestion.append(nameSuggestion).append("; ").append(elseSuggestion);
                                    suggestions.add(fullSuggestion.toString());
                                    coordinates.add(coordinate.toString());
                                    addresses.add(address);
                                    if (nameSuggestion.length() == 0) {
                                        dSuggestions.add(elseSuggestion.toString());
                                    } else {
                                        dSuggestions.add(nameSuggestion.toString());
                                    }
                                }
                                Log.d("result", suggestions.toString());
                                displayableSuggestions.clear();
                                displayableSuggestions.addAll(dSuggestions);

                                displayableCoordinates.clear();
                                displayableCoordinates.addAll(coordinates);

                                displayableAddresses.clear();
                                displayableAddresses.addAll(addresses);

                                //ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                                //        android.R.layout.simple_dropdown_item_1line, suggestions);
                                ArrayAdapter<String> suggestionsAdapter = new SuggestionLayout(context, R.layout.suggestion_layout, suggestions);
                                searchAutoCompleteTextField.setAdapter(suggestionsAdapter);
                                suggestionsAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchByCurrentPositionSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchAutoCompleteTextField.setEnabled(!isChecked);

                if (isChecked) {

                    locationListener = new MyLocationListener(context);
                    double longitude = locationListener.getLongitude();
                    double latitude = locationListener.getLatitude();

                    Log.d("gps", longitude + " " + latitude);

                    startPlace = new Place();
                    startPlace.setCoordinatesWithLatLon(latitude, longitude);


                }else {
                    locationListener.stopListener();
                    startPlace = null;
                }
            }
        });
        //----------------------------------------------------------------------------------------------------------------
        //END OF searchByCurrentPositionSW's OnCheckedChangeListener
        //----------------------------------------------------------------------------------------------------------------

        searchByPlaceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                places.clear();

                if(startPlace!=null) {

                    if (startPlace.getCoordinates().getLat() != 0.0 && startPlace.getCoordinates().getLon() != 0.0) {

                        places.add(startPlace);

                        //TODO fix the position when the fragment layout is opened
                        /*map.setScrollableAreaLimitLatitude(places.get(0).getCoordinates().getLat(), places.get(0).getCoordinates().getLat(), 0);
                        map.setScrollableAreaLimitLongitude(places.get(0).getCoordinates().getLon(), places.get(0).getCoordinates().getLon(), 0);*/

                        double dist = selectedSpeed * selectedDistance / 60.0;
                        ManipulateUrl manipulateUrl = new ManipulateUrl();
                        String nearbyUrl = manipulateUrl.getNearbyUrl(url, String.valueOf(places.get(0).getCoordinates().getLat()), String.valueOf(places.get(0).getCoordinates().getLon()), dist);
                        Log.d("nearbyUrl", nearbyUrl);
                        requests.findNearbyPlacesRequest(nearbyUrl, new Requests.NearbyVolleyCallback() {
                            @Override
                            public void onResponse(ArrayList<Place> results) {
                                places.addAll(results);
                                // TODO return the places to the MainCtivity
                                /*//fragmentsViewModel.setCategories(allCategories);
                                fragmentsViewModel.getCategories().postValue(allCategories);
                                //fragmentsViewModel.setPlaces(places);
                                fragmentsViewModel.getPlaces().postValue(places);
                                //fragmentsViewModel.setDistance(selectedDistance);
                                fragmentsViewModel.getDistance().postValue(selectedDistance);
                                //fragmentsViewModel.setTransport(selectedTransport);
                                fragmentsViewModel.getTransport().postValue(selectedTransport);
                                //markCoordinatesOnMap(places);*/

                                fragmentsViewModel.uploadToDatabase(selectedDistance,selectedTransport,places,allCategories);

                                fragmentsViewModel.getUpdated().postValue(fragmentsViewModel.getUpdated().getValue());
                            }

                            @Override
                            public void onFailure(String error) {

                            }
                        });
                        firebaseManager.incrementDatabaseStat(firebaseCategories, selectedTransport, String.valueOf(selectedDistance));

                    } else {
                        Toast.makeText(context, R.string.current_location_off, Toast.LENGTH_LONG).show();
                        searchByCurrentPositionSW.setChecked(false);
                    }
                }else {
                    Toast.makeText(context, R.string.something_happened, Toast.LENGTH_LONG).show();
                }
            }
        });

        selectWalk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectWalk.setActivated(!selectWalk.isActivated());

                if (selectWalk.isActivated()) {
                    selectedSpeed = avgWalkSpeed;
                    selectedTransport = "walk";
                    selectCar.setActivated(false);
                    selectWalk.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    selectCar.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                } else
                    selectWalk.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
            }
        });
        selectCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCar.setActivated(!selectCar.isActivated());

                if (selectCar.isActivated()) {
                    selectedSpeed = avgCarSpeed;
                    selectedTransport = "car";
                    selectWalk.setActivated(false);
                    selectCar.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    selectWalk.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                } else
                    selectCar.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
            }
        });
        select15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select15.setActivated(!select15.isActivated());

                if (select15.isActivated()) {
                    selectedDistance = 15;
                    select30.setActivated(false);
                    select45.setActivated(false);
                    select15.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    select30.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                    select45.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                } else {
                    select15.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));

                }
            }
        });
        select30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select30.setActivated(!select30.isActivated());

                if (select30.isActivated()) {
                    selectedDistance = 30;
                    select15.setActivated(false);
                    select45.setActivated(false);
                    select30.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    select15.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                    select45.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                } else {
                    select30.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                }
            }
        });
        select45.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select45.setActivated(!select45.isActivated());

                if (select45.isActivated()) {
                    selectedDistance = 45;
                    select15.setActivated(false);
                    select30.setActivated(false);
                    select45.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    select15.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                    select30.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                } else
                    select45.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
            }
        });
        selectHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectHotelLayout.setActivated(!selectHotelLayout.isActivated());

                if (selectHotelLayout.isActivated()) {
                    selectHotelLayout.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    allCategories.add(resources.getString(R.string.hotel));
                    firebaseCategories.add("restaurant");
                    url = url + "nwr[\"amenity\"=\"restaurant\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"biergarten\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"cafe\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"food_court\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"fast_food\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"pub\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"bar\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"confectionery\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"pastry\"](around:dist,startLat,startLong);";
                } else {
                    allCategories.remove(resources.getString(R.string.hotel));
                    firebaseCategories.remove("restaurant");
                    selectHotelLayout.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                    url = url.replace("nwr[\"amenity\"=\"restaurant\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"biergarten\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"cafe\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"food_court\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"fast_food\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"pub\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"bar\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"confectionery\"](around:dist,startLat,startLong);" +
                            "nwr[\"shop\"=\"pastry\"](around:dist,startLat,startLong);", "");
                }
            }
        });
        selectAccommodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAccommodationLayout.setActivated(!selectAccommodationLayout.isActivated());

                if (selectAccommodationLayout.isActivated()) {
                    allCategories.add(resources.getString(R.string.accommodation));
                    firebaseCategories.add("accommodation");
                    selectAccommodationLayout.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    url = url + "nwr[\"building\"=\"hotel\"](around:dist,startLat,startLong);" +
                            "nwr[\"leisure\"=\"summer_camp\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"caravan_site\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"hostel\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"motel\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"guest_house\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"camp_site\"](around:dist,startLat,startLong);";
                } else {
                    allCategories.remove(resources.getString(R.string.accommodation));
                    firebaseCategories.remove("accommodation");
                    selectAccommodationLayout.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                    url = url.replace("nwr[\"building\"=\"hotel\"](around:dist,startLat,startLong);" +
                            "nwr[\"leisure\"=\"summer_camp\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"caravan_site\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"hostel\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"motel\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"guest_house\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"camp_site\"](around:dist,startLat,startLong);", "");
                }
            }
        });
        selectMuseum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMuseumLayout.setActivated(!selectMuseumLayout.isActivated());
                allCategories.add(resources.getString(R.string.museum));
                firebaseCategories.add("museum_exhibition");
                if (selectMuseum.isActivated()) {
                    selectMuseumLayout.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    url = url + "nwr[\"tourism\"=\"museum\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"gallery\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"arts_centre\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"exhibition_centre\"](around:dist,startLat,startLong);";

                } else {
                    allCategories.remove(resources.getString(R.string.museum));
                    firebaseCategories.remove("museum_exhibition");
                    selectMuseumLayout.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                    url = url.replace("nwr[\"tourism\"=\"museum\"](around:dist,startLat,startLong);" +
                            "nwr[\"tourism\"=\"gallery\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"arts_centre\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"exhibition_centre\"](around:dist,startLat,startLong);", "");
                }
            }
        });
        selectNightclub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNightclubLayout.setActivated(!selectNightclubLayout.isActivated());
                allCategories.add(resources.getString(R.string.nightclub));
                firebaseCategories.add("entertainment");
                if (selectNightclub.isActivated()) {
                    selectNightclubLayout.setBackground(resources.getDrawable(R.drawable.toggle_button_style_on));
                    url = url + "nwr[\"amenity\"=\"casino\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"cinema\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"nightclub\"](around:dist,startLat,startLong);";
                } else {
                    allCategories.remove(resources.getString(R.string.nightclub));
                    firebaseCategories.remove("entertainment");
                    selectNightclubLayout.setBackground(resources.getDrawable(R.drawable.toggle_button_style_off));
                    url = url.replace("nwr[\"amenity\"=\"casino\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"cinema\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);" +
                            "nwr[\"amenity\"=\"nightclub\"](around:dist,startLat,startLong);", "");
                }
            }
        });

        GestureDetector categoriesGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Lefelé pöccintés
                if (velocityY > 0) {
                    categoriesHandleHandle.animate().rotation(0).setDuration(300).start();
                    categoriesContainer.animate().translationY(categoriesContainer.getHeight() - categoriesHandle.getHeight())
                            .setDuration(300).start();
                }
                // Felfelé pöccintés
                else if (velocityY < 0) {
                    categoriesHandleHandle.animate().rotation(180).setDuration(300).start();
                    categoriesContainer.animate().translationY(0.0F).setDuration(300).start();
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true; // Szükséges ahhoz, hogy a fling esemény működjön
            }
        });

        categoriesHandle.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return categoriesGestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);



        searchAutoCompleteTextField = view.findViewById(R.id.search_autocomplete_text_field);
        selectWalk = view.findViewById(R.id.select_walk);
        selectCar = view.findViewById(R.id.select_car);
        select15 = view.findViewById(R.id.select_15);
        select30 = view.findViewById(R.id.select_30);
        select45 = view.findViewById(R.id.select_45);
        selectHotel = view.findViewById(R.id.select_hotel);
        selectHotelLayout = view.findViewById(R.id.select_hotel_layout);
        selectAccommodation = view.findViewById(R.id.select_accomodation);
        selectAccommodationLayout = view.findViewById(R.id.select_accomodation_layout);
        selectMuseum = view.findViewById(R.id.select_museum);
        selectMuseumLayout = view.findViewById(R.id.select_museum_layout);
        selectNightclub = view.findViewById(R.id.select_nightclub);
        selectNightclubLayout = view.findViewById(R.id.select_nightclub_layout);
        searchByPlaceBTN = view.findViewById(R.id.searchByPlaceBTN);
        searchByCurrentPositionSW = view.findViewById(R.id.searchByCurrentPositionSW);
        otherCategoriesList = view.findViewById(R.id.other_categories_list);


        categoriesContainer = view.findViewById(R.id.categories_container);
        categoriesHandle = view.findViewById(R.id.categories_handle);
        categoriesHandleHandle = view.findViewById(R.id.categories_handle_arrow);

        return view;
    }

    private ArrayList<CategoryData> getCategoryData() {
        return new ArrayList<>(Arrays.asList(
                new CategoryData(resources.getString(R.string.theatre),"theatre", "nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);", R.drawable.theatre),
                new CategoryData(resources.getString(R.string.shopping),"shopping", "nwr[\"shop\"=\"department_store\"](around:dist,startLat,startLong);nwr[\"shop\"=\"mall\"](around:dist,startLat,startLong);nwr[\"shop\"=\"boutique\"](around:dist,startLat,startLong);nwr[\"shop\"=\"clothes\"](around:dist,startLat,startLong);nwr[\"shop\"=\"shoes\"](around:dist,startLat,startLong);", R.drawable.mall, R.drawable.store, R.drawable.shop),
                new CategoryData(resources.getString(R.string.church),"monument_church","nwr[\"historic\"=\"memorial\"](around:dist,startLat,startLong);nwr[\"building\"=\"cathedral\"](around:dist,startLat,startLong);nwr[\"amenity\"=\"place_of_worship\"](around:dist,startLat,startLong);nwr[\"amenity\"=\"monastery\"](around:dist,startLat,startLong);nwr[\"historic\"=\"building\"](around:dist,startLat,startLong);nwr[\"building\"=\"basilica\"](around:dist,startLat,startLong);nwr[\"historic\"=\"monument\"](around:dist,startLat,startLong);nwr[\"building\"=\"church\"](around:dist,startLat,startLong);nwr[\"building\"=\"temple\"](around:dist,startLat,startLong);", R.drawable.church),
                new CategoryData(resources.getString(R.string.castle),"castle_fort", "nwr[\"castle_type\"=\"palace\"](around:dist,startLat,startLong);nwr[\"historic\"=\"fort\"](around:dist,startLat,startLong);nwr[\"historic\"=\"castle\"](around:dist,startLat,startLong);", R.drawable.castle),
                new CategoryData(resources.getString(R.string.beach),"beach", "nwr[\"leisure\"=\"beach_resort\"](around:dist,startLat,startLong);", R.drawable.beach, R.drawable.beach_resort),
                // További elemek hozzáadása itt
                new CategoryData(resources.getString(R.string.festival),"music_festival", "nwr[\"amenity\"=\"music_venue\"](around:dist,startLat,startLong);nwr[\"amenity\"=\"community_centre\"](around:dist,startLat,startLong);", R.drawable.music),
                new CategoryData(resources.getString(R.string.spa), "spa", "nwr[\"shop\"=\"massage\"](around:dist,startLat,startLong);nwr[\"leisure\"=\"water_park\"](around:dist,startLat,startLong);", R.drawable.massage),
                new CategoryData(resources.getString(R.string.sport_event),"sport","nwr[\"amenity\"=\"events_venue\"](around:dist,startLat,startLong);nwr[\"leisure\"=\"stadium\"](around:dist,startLat,startLong);nwr[\"building\"=\"stadium\"](around:dist,startLat,startLong);nwr[\"building\"=\"sports_hall\"](around:dist,startLat,startLong);", R.drawable.community,R.drawable.sports),
                new CategoryData(resources.getString(R.string.aquatics),"watersport","nwr[\"sport\"=\"surfing\"](around:dist,startLat,startLong);nwr[\"sport\"=\"swimming\"](around:dist,startLat,startLong);nwr[\"sport\"=\"wakeboarding\"](around:dist,startLat,startLong);nwr[\"sport\"=\"water_polo\"](around:dist,startLat,startLong);nwr[\"sport\"=\"water_ski\"](around:dist,startLat,startLong);", R.drawable.surfing,R.drawable.beach_resort),
                new CategoryData(resources.getString(R.string.concert),"concert","nwr[\"amenity\"=\"music_venue\"](around:dist,startLat,startLong);nwr[\"amenity\"=\"theatre\"](around:dist,startLat,startLong);", R.drawable.community, R.drawable.music),
                new CategoryData(resources.getString(R.string.park),"park","nwr[\"tourism\"=\"zoo\"](around:dist,startLat,startLong);nwr[\"landuse\"=\"recreation_ground\"](around:dist,startLat,startLong);nwr[\"amenity\"=\"fountain\"](around:dist,startLat,startLong);nwr[\"leisure\"=\"park\"](around:dist,startLat,startLong);", R.drawable.park),
                new CategoryData(resources.getString(R.string.adventure_park),"theme_park", "nwr[\"tourism\"=\"theme_park\"](around:dist,startLat,startLong);", R.drawable.theme_park),
                new CategoryData(resources.getString(R.string.farmers_market),"farmers_market", "nwr[\"shop\"=\"farm\"](around:dist,startLat,startLong);nwr[\"amenity\"=\"marketplace\"](around:dist,startLat,startLong);", R.drawable.farm),
                new CategoryData(resources.getString(R.string.viewpoint),"viewpoint", "nwr[\"tourism\"=\"viewpoint\"](around:dist,startLat,startLong);", R.drawable.viewpoint),
                new CategoryData(resources.getString(R.string.hiking),"hiking", "nwr[\"route\"=\"hiking\"](around:dist,startLat,startLong);", R.drawable.hiking),
                new CategoryData(resources.getString(R.string.bicycle),"cycling", "nwr[\"route\"=\"cycling\"](around:dist,startLat,startLong);", R.drawable.cycling),
                new CategoryData(resources.getString(R.string.sailing),"sailing", "nwr[\"boat\"=\"yes\"](around:dist,startLat,startLong);nwr[\"sport\"=\"sailing\"](around:dist,startLat,startLong);nwr[\"leisure\"=\"marina\"](around:dist,startLat,startLong);", R.drawable.boat,R.drawable.sailing),
                new CategoryData(resources.getString(R.string.national_park),"national_park", "nwr[\"boundary\"=\"national_park\"](around:dist,startLat,startLong);", R.drawable.national_park)
        ));
    }

    private ArrayList<Category> initCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        for (CategoryData data : getCategoryData()) {
            Drawable first = null;
            Drawable second = null;
            Drawable third = null;

            if (data.drawables.length > 0) first = resources.getDrawable(data.drawables[0]);
            if (data.drawables.length > 1) second = resources.getDrawable(data.drawables[1]);
            if (data.drawables.length > 2) third = resources.getDrawable(data.drawables[2]);
            if (data.label.equals(resources.getString(R.string.aquatics))){
                second.setTint(resources.getColor(R.color.beach_resort_alt,context.getTheme()));
            }
            if (data.label.equals(resources.getString(R.string.concert))){
                second.setTint(resources.getColor(R.color.music_alt, context.getTheme()));
            }
            if (data.label.equals(resources.getString(R.string.theatre))){
                first.setTint(resources.getColor(R.color.theatre_alt, context.getTheme()));
            }

            categories.add(new Category(first, second, third, data.urlPart, data.label, data.firebaseCategory));
        }
        return categories;
    }
    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.image_button_click);
        view.startAnimation(animation);
    }
}