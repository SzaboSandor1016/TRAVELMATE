package com.example.travel_mate

//import com.example.gtk_maps.AppDatabase.SearchDao
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.navigation.OuterDestination
import com.example.navigation.OuterNavigator
import com.example.travel_mate.databinding.ActivityMainBinding


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

class ActivityMain : AppCompatActivity(), OuterNavigator/*, FragmentPlaceDetails.PlaceDetailsListener */{

    interface BottomSheetStateListener {
        fun onStateExpanded()
    }

    //private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters

    //private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /*@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val startUserActivityForResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode == ActivityUser.USER_ACTIVITY_RESULT_CODE) {
                activityResult.data?.getParcelableExtra(ActivityUser.USER_ACTIVITY_BUNDLE_ID, ClassTrip::class.java)?.let {
                    viewModelMain.setupNewTrip(it)
                }
            }
        }*/

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

        //TODO REMOVE THIS IF A
        // java.lang.NoSuchMethodError: No virtual method getInsetsController()Landroid/view/WindowInsetsController;
        // in class Landroid/view/Window; or its super classes (declaration of 'android.view.Window' appears in /system/framework/framework.jar!classes3.dex)
        // OCCURS
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        //TODO REMOVE FROM HERE
        window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }
        //TODO TO HERE IF THE ERROR MENTIONED ABOVE OCCURS

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
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        //fragmentsViewModel = ViewModelProvider(this).get(FragmentsViewModel::class.java)
        //navigation = Navigation(Volley.newRequestQueue(applicationContext), applicationContext)
        /*places = ArrayList()
        isUpdatedFromActivity = false*/
        //isNavi = false


        /*selectedPlaces = ArrayList()
        selectedCategories = ArrayList()
        allCategories = ArrayList()
        otherMarkers = ArrayList()
        routePolys = ArrayList()
        suggestedPlaces = ArrayList()
        allMarkers = ArrayList()
        routeMarkers = ArrayList()
        nameMarkers = ArrayList()*/




        /*mapAutoComplete = findViewById(R.id.map_autocomplete_text_field);
        mapAutoComplete.setThreshold(2);*///mAuth = FirebaseAuth.getInstance()
        /*appDatabase = databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "temporary_search"
        ).build()
        searchDao = appDatabase!!.searchDao()
        fragmentsViewModel!!.searchDao = searchDao*/

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            (data?.getParcelableExtra(ActivityUser.USER_ACTIVITY_BUNDLE_ID) as? ClassTrip)?.let {

                val stringBuilder = StringBuilder()

                stringBuilder.append(it.getStartPlace()?.getName() + " ")
                stringBuilder.append(it.getStartPlace()?.getAddress()?.getFullAddress())

                binding.placeSearch.setText(stringBuilder.toString())

                viewModelMain.setupNewTrip(it)
            }

        }
    }*/

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
        //binding.map.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onDestroy() {
        super.onDestroy()

        /*viewModelMain.places.removeObservers(this)
        viewModelMain.startPlace.removeObservers(this)
        viewModelMain.transportMode.removeObservers(this)
        viewModelMain.minute.removeObservers(this)*/
        /*viewModelTrip.tripPlaces.removeObservers(this)
        viewModelPhoton.autoCompleteResults.removeObservers(this)
        viewModelPhoton.reverseGeoCodeResults.removeObservers(this)
        viewModelOverpass.overpassResponse.removeObservers(this)
        uiController?.clearDialog()
        locationListener = null

        resources!!.flushLayoutCache()*/
    }

    override fun navigateTo(destination: OuterDestination) {

        when(destination) {
            OuterDestination.Save -> findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_FragmentMain_to_fragmentSaveTrip)
            OuterDestination.User -> findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_FragmentMain_to_FragmentUser)
        }
    }


}
