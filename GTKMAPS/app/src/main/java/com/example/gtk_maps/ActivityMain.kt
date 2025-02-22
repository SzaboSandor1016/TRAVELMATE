package com.example.gtk_maps

//import com.example.gtk_maps.AppDatabase.SearchDao
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.gtk_maps.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker


private operator fun <E> Set<E>.get(i: Int): Any {
    return this[i]
}

// -------------------------------------------------------------------------------------------------------------
// | MainActivity                                                                                              |
// | Is for processing, and marking the coordinates coming from the MainActivity                               |
// | Contains:                                                                                                 |
// |                                                                                                           |
// | code necessary for embedding the OSM                                                                      |
// | menuBTN's OnClickListener                                                                                 |
// | markCoordinatesOnMap                                                                                      |
// -------------------------------------------------------------------------------------------------------------

class ActivityMain : AppCompatActivity(), FragmentPlaceDetails.PlaceDetailsListener {

    interface BottomSheetStateListener {
        fun onStateExpanded()
    }

    //private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private val avgWalkSpeed = 3500
    private val avgCarSpeed = 40000

    private lateinit var binding: ActivityMainBinding

    private var suggestionsAdapter: AdapterSuggestion? = null

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private var search: ClassSearch? = null
    private var trip: ClassTrip? = null
    private var route: ClassRoute? = null

    private var selectedMode:Boolean? = null
    private var dist: Double = 0.0

    private var startPlaces: ArrayList<ClassPlace> = ArrayList()
    private var suggestions: ArrayList<String> = ArrayList()


    private var checkedGroup = -1
    private var isItemContainerOpen = false
    private var prevMarker: Marker? = null

    private  var isPopupVisible = false

    private var locationListener: ClassLocationListener? = null
    private var uiController: ActivityMainUIController? = null

    /*private var selectedPlaces: ArrayList<ClassPlace>? = null
    private var selectedCategories: ArrayList<String>? = null
    private var allCategories: ArrayList<String>? = null
    private var suggestedPlaces: ArrayList<String>? = null
    private var otherMarkers: ArrayList<Marker>? = null
    private var allMarkers: ArrayList<Marker>? = null
    private var routePolys: ArrayList<Polyline>? = null*/

    private var places: ArrayList<ClassPlace>? = null
    private var isUpdatedFromActivity = false
    private var isNavi: Boolean? = null

    //private AutoCompleteTextView mapAutoComplete;
    var categoryManager: ClassCategoryManager? = null
    private var resources: Resources? = null
    /*private var navigation: Navigation? = null*/

    private lateinit var repository: DataRepository

    private lateinit var viewModelMain: ViewModelMain
    private lateinit var viewModelOverpass: ViewModelOverpass
    private lateinit var viewModelPhoton: ViewModelPhoton
    private lateinit var viewModelPlaceDetails: ViewModelFragmentPlaceDetails
    private lateinit var viewModelSave: ViewModelSave
    private lateinit var viewModelTrip: ViewModelTrip

    private lateinit var fragmentManager: FragmentManager

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    /*private var appDatabase: AppDatabase? = null
    private var searchDao: SearchDao? = null*/
    private var routeMarkers: ArrayList<Marker>? = null
    private var nameMarkers: ArrayList<Marker>? = null

    companion object {
        private const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2
        private const val SHARED_REQUEST_CODE = 3
        private const val REQUEST_CODE = 1
    }

    //Code lines necessary for the integration of the OSM
    //----------------------------------------------------------------------------------------------------------------
    //BEGIN
    //----------------------------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            //val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(
                0,0,0,0
            )
            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.itemContainer) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }


        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        repository = DataRepository.getInstance()
        firebaseAuth = Firebase.auth
        database = Firebase.database

        viewModelOverpass = ViewModelProvider(this, MyApplication.factory)[ViewModelOverpass::class.java]
        viewModelPhoton = ViewModelProvider(this, MyApplication.factory)[ViewModelPhoton::class.java]
        viewModelPlaceDetails = ViewModelProvider(this, MyApplication.factory)[ViewModelFragmentPlaceDetails::class.java]
        viewModelSave = ViewModelProvider(this, MyApplication.factory)[ViewModelSave::class.java]
        viewModelMain = ViewModelProvider(this, MyApplication.factory)[ViewModelMain::class.java]
        viewModelTrip = ViewModelProvider(this, MyApplication.factory)[ViewModelTrip::class.java]


        fragmentManager = supportFragmentManager

        uiController = ActivityMainUIController(this, this, viewModelOverpass, viewModelMain, binding)

        //String[] permissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }

        // Check for ACCESS_FINE_LOCATION permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        //fragmentsViewModel = ViewModelProvider(this).get(FragmentsViewModel::class.java)
        //navigation = Navigation(Volley.newRequestQueue(applicationContext), applicationContext)
        places = ArrayList()
        isUpdatedFromActivity = false
        isNavi = false


        /*selectedPlaces = ArrayList()
        selectedCategories = ArrayList()
        allCategories = ArrayList()
        otherMarkers = ArrayList()
        routePolys = ArrayList()
        suggestedPlaces = ArrayList()
        allMarkers = ArrayList()
        routeMarkers = ArrayList()
        nameMarkers = ArrayList()*/

        categoryManager= ClassCategoryManager(this@ActivityMain)
        resources = getResources()


        /*mapAutoComplete = findViewById(R.id.map_autocomplete_text_field);
        mapAutoComplete.setThreshold(2);*///mAuth = FirebaseAuth.getInstance()
        /*appDatabase = databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "temporary_search"
        ).build()
        searchDao = appDatabase!!.searchDao()
        fragmentsViewModel!!.searchDao = searchDao*/


        binding.placeSearch.setDropDownBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.shape_autocomplete_dropdown))

        binding.placeSearch.onItemClickListener = OnItemClickListener { parent, view, position, id ->

            binding.placeSearch.setText(suggestions[position])

            viewModelTrip.clearTrip()

            viewModelTrip.setUUID()

            viewModelMain.setStartPlace(startPlaces[position])

            val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val currentFocusView = currentFocus
            currentFocusView?.let {
                inputMethodManager.hideSoftInputFromWindow(it.windowToken,0)
            }

            //todo if further changes become necessary move this to a separate func.
            binding.chipGroups.visibility = View.VISIBLE

        }

        binding.placeSearch.threshold = 4

        binding.placeSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 4) {

                    viewModelPhoton.searchAutoComplete(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        binding.userBTN2.setOnClickListener(View.OnClickListener { v ->
            animateImageButton(v)
            startActivity(Intent(this@ActivityMain, ActivityUser::class.java))

        })

        binding.openExtended.addOnCheckedChangeListener { button, isChecked ->

            uiController?.handleUiChangesForExtendedSearch(isChecked)

            if (isChecked) {
                setExtendedSearchListeners()
            } else {

                if (viewModelMain.getCalculatedDistance()!=null) {

                    removeExtendedSearchListeners()

                    clearSearchAndUi()
                }
            }
        }



        binding.useLocation.addOnCheckedChangeListener{ l, isChecked ->

            if (isChecked) {

                val currentCoordinates = getCurrentLocation()

                if (currentCoordinates.getLatitude() != 0.0 && currentCoordinates.getLongitude() != 0.0) {

                    viewModelPhoton.searchReverseGeoCode(currentCoordinates)
                }
            } else {

                uiController?.resetUiOnLocationClicked()

                viewModelMain.resetSearchDetails()
            }
        }

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)

        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)



        viewModelMain.places.observe(this@ActivityMain) { places ->

            uiController?.showMarkersOnMap(places)

            Log.d("test4", places.size.toString())
        }

        viewModelMain.startPlace.observe(this@ActivityMain) { startPlace ->

            uiController?.prepareForNewSearch(startPlace!!)

            viewModelTrip.clearTrip()

            viewModelTrip.setUUID()

            viewModelTrip.setTripStartPlace(startPlace!!)

            Log.d("test5", startPlace.getName().toString())
        }

        viewModelMain.transportMode.observe(this@ActivityMain) { transportMode ->

            val hasChecked = transportMode != null

            uiController?.updateUiOnTransportModeSelected(hasChecked)
        }

        viewModelMain.minute.observe(this@ActivityMain) { minute ->

            Log.d("minute", minute.toString())

            uiController?.handleMinuteChanged()

            if (minute != null){

                calculateSearchDistance()
            }
        }

        viewModelTrip.tripPlaces.observe(this@ActivityMain) { places ->

            handleTripChips(places.isEmpty())
        }

        viewModelPhoton.autoCompleteResults.observe(this@ActivityMain) { results ->

            handlePhotonObserve(results, false)
        }

        viewModelPhoton.reverseGeoCodeResults.observe(this@ActivityMain) { results ->

            handlePhotonObserve(results, true)

        }
        viewModelOverpass.overpassResponse.observe(this@ActivityMain) { results ->

            handleOverpassObserve(results)

        }


    }

    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use

        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume();*/
        //needed for compass, my location overlays, v6.0.0 and up
    }

    public override fun onPause() {
        super.onPause()
        binding.map.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModelMain.places.removeObservers(this)
        viewModelMain.startPlace.removeObservers(this)
        viewModelMain.transportMode.removeObservers(this)
        viewModelMain.minute.removeObservers(this)
        viewModelTrip.tripPlaces.removeObservers(this)
        viewModelPhoton.autoCompleteResults.removeObservers(this)
        viewModelPhoton.reverseGeoCodeResults.removeObservers(this)
        viewModelOverpass.overpassResponse.removeObservers(this)
        uiController?.clearDialog()
        locationListener = null
        suggestionsAdapter = null

        resources!!.flushLayoutCache()
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            // Do something for new state.
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    // Bezárt állapot
                    viewModelPlaceDetails.setContainerState("collapsed")

                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    // Kinyitott állapot
                    viewModelPlaceDetails.setContainerState("expanded")
                }
                // További állapotok kezelése...
                BottomSheetBehavior.STATE_DRAGGING -> {
                    //TODO()
                }

                BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    //TODO()
                }

                BottomSheetBehavior.STATE_HIDDEN -> {
                    //TODO()
                    val fragment = supportFragmentManager.fragments[0]
                    if (fragment != null) {
                        supportFragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss()
                    }
                }

                BottomSheetBehavior.STATE_SETTLING -> {
                    //TODO()
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Do something for slide offset.
        }
    }

    //----------------------------------------------------------------------------------------------------------------
    //END
    //----------------------------------------------------------------------------------------------------------------
    private fun animateImageButton(view: View) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.image_button_click)
        view.startAnimation(animation)
    }



    override fun onTitleContainerMeasured(height: Int) {

        val standardBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)
        standardBottomSheetBehavior.peekHeight = height
    }

    private fun handleTripChips(isTripPlacesEmpty: Boolean){

        when (isTripPlacesEmpty){
            true -> {
                removeTripChips()
                uiController?.handleTripUiChanges(true)
            }
            else -> {
                setupTripChips()
                uiController?.handleTripUiChanges(false)
            }
        }

    }


    //Currently unnecessary function related to the route planning
    //In the future similar method would be implemented in the UIControllerActivityMain
    //but this may be useful at some point so it is left here for now
    /*private fun removeOtherMarkers() {
        routeMarkers!!.clear()
        setupMarkerClusterer(this)
        for (place in selectedPlaces!!) {
            for (marker in allMarkers!!) {
                if (marker.position.longitude == place.getCoordinates()!!.getLongitude() && marker.position.latitude == place.getCoordinates()!!.getLatitude()) {
                    radiusMarkerClusterer!!.add(marker)
                    routeMarkers!!.add(marker)
                } else otherMarkers!!.add(marker)
            }
        }
        Log.d("routeSize", routeMarkers!!.size.toString())
        map.overlays.add(radiusMarkerClusterer)
    }*/


    private fun getCurrentLocation(): ClassCoordinates{

        locationListener = ClassLocationListener(this@ActivityMain)

        return ClassCoordinates(locationListener!!.getLatitude(),locationListener!!.getLongitude())
    }


    private fun setExtendedSearchListeners(){

        binding.transportGroup.addOnButtonCheckedListener{ group, checkedId, isChecked ->

            val index = binding.transportGroup.indexOfChild(binding.transportGroup.findViewById(binding.transportGroup.checkedButtonId))

            viewModelMain.setTransportMode(index)
        }

        binding.distanceGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

            val index = binding.distanceGroup.indexOfChild(binding.distanceGroup.findViewById(binding.distanceGroup.checkedButtonId))

            viewModelMain.setMinute(index)
        }
    }

    private fun removeExtendedSearchListeners(){
        binding.distanceGroup.clearOnButtonCheckedListeners()
        binding.transportGroup.clearOnButtonCheckedListeners()
    }

    private fun calculateSearchDistance(){

        viewModelMain.calculateDistance()
    }

    private fun clearSearchAndUi(){
        viewModelMain.resetSearchDetails()

        uiController?.resetSearchParameters()
    }

    private fun handleOverpassObserve(places: ArrayList<ClassPlace>){

        viewModelMain.addPlaces(places)
        //Log.d("test2", places[0].getName()!!)
    }

    private fun handlePhotonObserve(places: ArrayList<ClassPlace>, isFromLocation: Boolean){

        startPlaces.clear()
        suggestions.clear()

        startPlaces.addAll(places)

        for (place in places) {
            val suggestion = StringBuilder()
            suggestion.append(place.getName()).append(" ")
            suggestion.append(place.getAddress()?.getFullAddress() ?: "")

            suggestions.add(suggestion.toString())
        }

        if (startPlaces.size < 2 && isFromLocation) {

            viewModelMain.setStartPlace(startPlaces[0])

            binding.placeSearch.setText(suggestions[0])


            //todo if further ui changes become necessary create a separate func.
            binding.chipGroups.visibility = View.VISIBLE

            clearSearchAndUi()

        }else {

            suggestionsAdapter = AdapterSuggestion(
                this@ActivityMain,
                R.layout.layout_autocomplete_item,
                suggestions
            )
            binding.placeSearch.setAdapter(
                suggestionsAdapter
            )
        }

        suggestionsAdapter?.notifyDataSetChanged()
    }


    private fun setupTripChips() {

        binding.saveTrip.setOnClickListener { l ->

            uiController?.handleUiChangesForSave()
        }
        binding.dismissTrip.setOnClickListener { l ->

            viewModelTrip.clearTrip()
        }
    }

    private fun removeTripChips() {
        binding.tripChips.visibility = View.GONE

        binding.saveTrip.setOnClickListener(null)
        binding.dismissTrip.setOnClickListener(null)
    }

    fun initDetailsFragment(place: ClassPlace) {
        val tag = "PLACE_DETAILS_FRAGMENT"

        val isPlaceContainedByTrip = viewModelTrip.isPlaceContainedByTrip(place)

        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        if (existingFragment == null) {

            val fragment = FragmentPlaceDetails.newInstance(place, isPlaceContainedByTrip)

            supportFragmentManager.beginTransaction()
                .replace(R.id.bottom_sheet_container, fragment, tag)
                .commit()
        } else {
            (existingFragment as FragmentPlaceDetails).updatePlaceDetails(place, isPlaceContainedByTrip)
        }

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun initSaveFragment() {
        val tag = "SAVE_TRIP_FRAGMENT"

        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        if (existingFragment == null) {

            // Ha nem létezik még a fragment, létrehozzuk
            val fragment = FragmentSaveTrip.newInstance()

            supportFragmentManager.beginTransaction()
                .replace(R.id.bottom_sheet_container, fragment, tag) // Itt ugyanazt a konténert használjuk
                .commit()
        } else {
            (existingFragment as FragmentSaveTrip).updateSaveTrip()
        }

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}
