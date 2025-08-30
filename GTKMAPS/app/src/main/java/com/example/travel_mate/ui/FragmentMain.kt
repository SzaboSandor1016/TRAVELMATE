package com.example.travel_mate.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.core.ui.R
import com.example.features.selectedplace.presentation.FragmentPlaceDetails
import com.example.travel_mate.databinding.FragmentMainBinding
import com.example.travel_mate.ui.models.CoordinatesMapPresentationModel
import com.example.travel_mate.ui.models.MapDataMapPresentationModel
import com.example.travel_mate.ui.models.PlaceDataMapPresentationModel
import com.example.travel_mate.ui.viewmodel.ViewModelMain
import com.example.core.utils.ClassCategoryManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.utils.BonusPackHelper
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMain.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class FragmentMain : Fragment(), MapEventsReceiver {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentMain.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FragmentMain().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //TODO MAAYBEEE INJECT IT
    private var categoryManager: ClassCategoryManager? = null
    private var resources: Resources? = null

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    //var startPlace: PlaceDataMapPresentationModel = PlaceDataMapPresentationModel()

    private var locationMarker: Marker? = null

    private var customPlace: Marker? = null
    private var unContainedMarkers: ArrayList<Marker> = ArrayList()
    private var containedMarkers: ArrayList<Marker> = ArrayList()
    private var routePolyLines: ArrayList<Polyline> = ArrayList()

    private val viewModelMain: ViewModelMain by inject<ViewModelMain>()
    //private val viewModelUser: ViewModelUser by inject<ViewModelUser>()

    private lateinit var mapController: IMapController
    private lateinit var startMarker: Marker
    private var markerClusterer: RadiusMarkerClusterer?= null

    private lateinit var containedOverlay: FolderOverlay
    private lateinit var mapEventsOverlay: MapEventsOverlay

    private var uiState: String = MapDataMapPresentationModel.Search().javaClass.name

    private lateinit var navController: NavController;

        private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }

        //viewModelUser = ViewModelProvider(this, MyApplication.factory)[ViewModelUser::class.java]
        //viewModelMain = ViewModelProvider(this, MyApplication.factory)[ViewModelMain::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO REMOVE THIS IF A
        // java.lang.NoSuchMethodError: No virtual method getInsetsController()Landroid/view/WindowInsetsController;
        // in class Landroid/view/Window; or its super classes (declaration of 'android.view.Window' appears in /system/framework/framework.jar!classes3.dex)
        // OCCURS
        // FROM HERE
        ViewCompat.setOnApplyWindowInsetsListener(binding.itemContainer) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(0,0,0,systemBars.bottom)
            insets
        }
        //TODO TO HERE

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

        val navHostFragment = childFragmentManager
            .findFragmentById(com.example.travel_mate.R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        mapController = binding.map.controller
        startMarker= Marker(binding.map)

        /*
        In theory this would make the map not to show constantly
        and limit the scrollable area
        if fully zoomed out, but actually it breaks the application
         */
        /*binding.map.isVerticalMapRepetitionEnabled = false
        binding.map.setScrollableAreaLimitLatitude(90.0,90.0,0)*/

        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        mapController.setZoom(15.0)
        val firstPoint = GeoPoint(47.09327, 17.91149)
        mapController.setCenter(firstPoint)
        binding.map.setMultiTouchControls(true)

        startMarker.position = firstPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        binding.map.overlays.add(startMarker)
        startMarker.icon = ResourcesCompat.getDrawable(/*fragmentMain.*/getResources(),
            R.drawable.ic_start_marker, context?.theme)

        categoryManager= ClassCategoryManager(requireContext())
        resources = getResources()

        containedOverlay = FolderOverlay()

        mapEventsOverlay = MapEventsOverlay(this)

        fragmentManager = childFragmentManager

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)

        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        /*viewModelUser.checkCurrentUser()

        viewModelMain.getInitialCurrentLocation()*/

        //replaceCurrentMainFragment(ViewModelMain.MainContent.SEARCH) //set the current main fragment to FragmentSearch

        /**Observe the [com.example.travel_mate.ui.ViewModelMain.chipsState], [com.example.travel_mate.ui.ViewModelMain.placeState], [com.example.travel_mate.ui.ViewModelMain.mainInspectTripState] and the [com.example.travel_mate.ui.ViewModelMain.mainSearchState] located in [viewModelMain]
         *
         **/
//_________________________________________________________________________________________________________________________
// BEGINNING OF MAIN VIEWMODEL OBSERVER
//_________________________________________________________________________________________________________________________

        //TODO When the selected place changes center the map to its position

        /** [com.example.travel_mate.ui.ViewModelMain.mainSearchState] observer
         *  observe the [viewModelMain]'s [com.example.travel_mate.ui.ViewModelMain.mainSearchState]
         *  on state update
         *  - the [handleStartPlaceChange] function is called
         *  - [handlePhotonObserve] function is called
         *   if its [startPlaces] is not empty
         *  - [showMapContent] function is called
         *  - [handleRouteStopsChange] function is called
         *  - [com.example.travel_mate.ui.ViewModelMain.getCurrentPlaceByUUID] function is called
         *   if there is a currentPlace selected
         *
         */
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.mapState.collect {

                    replaceCurrentMainFragment(it.mapData)

                    updateMapContentWith(it.mapData)

                    if (it.mapData is MapDataMapPresentationModel.Search) {

                        viewModelMain.initRouteWith(it.mapData.startPlace)

                        viewModelMain.initSaveWith()
                    }

                    if(it.mapData is MapDataMapPresentationModel.Inspect) {

                        viewModelMain.initRouteWith(it.mapData.startPlace)
                    }

                    /*Log.d("isStartPlacesEmpty", it.startPlaces.isEmpty().toString())

                    showMapContent(
                        places = it.places
                    )

                    if (it.currentPlaceUUID != null)
                        viewModelMain.getCurrentPlaceByUUID(
                            uuid = it.currentPlaceUUID
                        )

                    Log.d("tripEmpty", it.isTripEmpty.toString())*/
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.navigationLocationState.collect {

                    if (it != null) {

                        showNavigationData(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.selectedPlaceOptions.collect {

                    standardBottomSheetBehavior.setPeekHeight(it.containerHeight, true)
                }
            }
        }


//_________________________________________________________________________________________________________________________
// END OF MAIN VIEWMODEL OBSERVER
//_________________________________________________________________________________________________________________________

        /**Listener to handle navigating on map
         * when a scroll or a zoom event is detected on the map
         * the [checkBoundingBox] function is called
         */
//_________________________________________________________________________________________________________________________
// BEGINNING OF MAP LISTENERS
// _________________________________________________________________________________________________________________________
        binding.map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                //if (!isNavi!!) checkBoundingBox(allMarkers!!) else checkBoundingBox(routeMarkers!!)
                checkBoundingBox(unContainedMarkers)
                return true
            }

            override fun onZoom(event: ZoomEvent): Boolean {
                //if (!isNavi!!) checkBoundingBox(allMarkers!!) else checkBoundingBox(routeMarkers!!)
                checkBoundingBox(unContainedMarkers)
                return true
            }
        })

//_________________________________________________________________________________________________________________________
// END OF MAP LISTENER
// _________________________________________________________________________________________________________________________
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentLifecycle", "Parent/Child Fragment Destroyed")

        //dismissDialog()

        //binding.distanceGroup.clearOnButtonCheckedListeners()
        //binding.transportGroup.clearOnButtonCheckedListeners()

        resources?.flushLayoutCache()

        _binding = null
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    //Callback for bottom sheet state changes
//_________________________________________________________________________________________________________________________
// BEGINNING OF BOTTOM SHEET CALLBACK
//_________________________________________________________________________________________________________________________
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            // Do something for new state.
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    // Bezárt állapot
                    viewModelMain.setSelectedPlaceContainerState("collapsed")

                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    // Kinyitott állapot
                    viewModelMain.setSelectedPlaceContainerState("expanded")
                }
                // További állapotok kezelése...
                BottomSheetBehavior.STATE_DRAGGING -> {
                }

                BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                }

                BottomSheetBehavior.STATE_HIDDEN -> {
                    val fragment = fragmentManager.findFragmentByTag("PLACE_DETAILS_FRAGMENT")
                    if (fragment != null) {
                        fragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss()
                    }

                    //TODO A FLAG TO BE ABLE TO GET BACK HERE EASIER
                    // MAYBE RESET THE SELECTED PLACE HERE
                }

                BottomSheetBehavior.STATE_SETTLING -> {
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Do something for slide offset.
        }
    }
//_________________________________________________________________________________________________________________________
// END OF BOTTOM SHEET CALLBACK
//_________________________________________________________________________________________________________________________


    private fun updateMapContentWith(mapData: MapDataMapPresentationModel) {

        when (mapData) {

            is MapDataMapPresentationModel.Search -> {

                //binding.map.overlays.add(mapEventsOverlay)

                removeMapData(addMapEventsOverlay = true)

                handleStartPlaceChange(mapData.startPlace)

                showMapContent(mapData.places)
            }
            is MapDataMapPresentationModel.CustomPlace -> {

                removeMapData(addMapEventsOverlay = false)

                showCustomPlace(mapData.place.uuid, mapData.place.coordinates)
            }

            is MapDataMapPresentationModel.Inspect -> {

                removeMapData(addMapEventsOverlay = false)

                handleStartPlaceChange(
                    mapData.startPlace
                )

                showMapContent(mapData.days.flatMap { it.places })
            }
            is MapDataMapPresentationModel.Navigation -> {

                removeMapData(addMapEventsOverlay = false)

                handleNavigationChange(
                    mapData.route,
                    mapData.goal
                )
            }
            is MapDataMapPresentationModel.Route -> {

                removeMapData(addMapEventsOverlay = false)

                handleStartPlaceChange(mapData.startPlace)

                showMapContent(mapData.places)

                handleRouteChange(mapData.polylines)
            }

            MapDataMapPresentationModel.NavigationArrived -> {

                removeMapData(addMapEventsOverlay = false)
            }
        }
    }


//Methods related to the search text field
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR SEARCH TEXT FIELD
//_________________________________________________________________________________________________________________________

    /** [handleStartPlaceChange]
     *  on start place change in the [com.example.travel_mate.ui.ViewModelMain] caused by an update in [com.example.data.repositories.SearchRepositoryImpl]
     *  first remove all content from the map
     *  then call [showStart], [setStartTextFiled]
     *  and set the visibility of the category chips.
     */

    private fun handleStartPlaceChange(startPlace: PlaceDataMapPresentationModel?) {

        showStart(
            startPlace = startPlace
        )
    }


//_________________________________________________________________________________________________________________________
// END OF METHODS FOR SEARCH TEXT FIELD
//_________________________________________________________________________________________________________________________*/

    /*Methods of map related operations
    *
     */
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR MAP
//_________________________________________________________________________________________________________________________


    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {

        Log.d("mapSinglePress", "press")

        return true
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {

        viewModelMain.setCustomPlace(
            geoPoint = p!!
        )
        Log.d("mapLongPress", "press")

        return true
    }

    private fun showCustomPlace(uuid: String?, coordinates: CoordinatesMapPresentationModel?) {

        binding.map.overlays.remove(customPlace)

        if (uuid != null && coordinates != null) {
            customPlace = createMarker(
                uuid = uuid,
                category = null,
                coordinates = coordinates
            )

            binding.map.overlays.add(customPlace)
        }
    }

    /** [createMarkersOnMap]
     * [createMarkersOnMap] creates and returns a list of the markers
     *  *  based on the list of [com.example.travel_mate.ui.ViewModelMain.PlaceProcessed] classes passed as he functions parameter
     */
    private fun createMarkersOnMap(places: List<PlaceDataMapPresentationModel>): List<Marker> {

        val markers: ArrayList<Marker> = ArrayList()

        for (place in places){

            val marker = createMarker(
                uuid = place.uuid,
                category = place.category,
                coordinates = place.coordinates
            )
            val titleMarker = createTitleMarker(
                uuid = place.uuid,
                title = place.name.toString(),
                coordinates = place.coordinates
            )

            markers.add(
                setMarkerClickListener(
                    marker = marker
                )
            )
            markers.add(
                setMarkerClickListener(
                    marker = titleMarker
                )
            )
        }

        return markers
    }

    private fun createMarker(uuid: String, category: String?, coordinates: CoordinatesMapPresentationModel): Marker {

        val marker = Marker(binding.map)
        val position = GeoPoint(coordinates.latitude, coordinates.longitude)
        marker.position = position
        marker.icon = categoryManager?.getMarkerIcon(category)

        marker.relatedObject = uuid

        return marker
    }

    private fun createTitleMarker(uuid: String, title: String, coordinates: CoordinatesMapPresentationModel): Marker {

        val titleMarker = Marker(binding.map)
        val position = GeoPoint(coordinates.latitude, coordinates.longitude)
        titleMarker.position = position
        titleMarker.setTextIcon(title)

        titleMarker.relatedObject = uuid

        return titleMarker
    }

    private fun setMarkerClickListener(marker: Marker): Marker {

        marker.setOnMarkerClickListener{ m, mapView ->

            /** get the related object of the clicked marker
            that is the uuid of the place associated with it
            then calls an update on the current place in the ViewModel
            [com.example.travel_mate.ui.ViewModelMain.getCurrentPlaceByUUID]
            set the [com.google.android.material.bottomsheet.BottomSheetDialog]'s
            state as "collapsed"
             */

            val relatedPlace = m.relatedObject as String

            viewModelMain.setSelectedPlace(relatedPlace)

            viewModelMain.setSelectedPlaceContainerState("collapsed")

            initDetailsFragment()

            true
        }

        return marker
    }

    /** [showMapContent]
     *  handles state update events for all data related to the map
     *  - creates the marker list for [markerClusterer] with the places needed to be clustered
     *  which are the ones that are not part of the current [com.example.domain.models.Route]
     *  - create an other list for [Marker]'s that are part of the current [com.example.domain.models.Route]
     *  these are not clustered and always visible on the map
     *  - adds all the [Polyline]'s of the current [com.example.domain.models.Route]
     *  to the map too
     */
    private fun showMapContent(
        places: List<PlaceDataMapPresentationModel>
    ) {

        //binding.map.overlays.remove(containedOverlay)
        //binding.map.overlays.removeAll(containedMarkers)

        //containedOverlay = FolderOverlay()

        unContainedMarkers.clear()
        //containedMarkers.clear()

        unContainedMarkers.addAll(createMarkersOnMap(places = places/*.filter { !it.containedByRoute }*/))
        //containedMarkers.addAll(createMarkersOnMap(places = places.filter { it.containedByRoute }))

        /*containedMarkers.forEach {
            containedOverlay.add(it)
        }
        binding.map.overlays.add(containedOverlay)*/

        binding.map.invalidate()

        checkBoundingBox(unContainedMarkers)
    }

    /** [checkBoundingBox]
     *  check the bounding box of currently visible parts of the map
     *  it has a parameter of a list of markers that are needed to be checked if inside
     *  the current bounding box
     *  in case they are - add them to the [markerClusterer] and show them on map
     *  else - do not
     */
    private fun checkBoundingBox(markers: List<Marker>) {
        setupMarkerClusterer(requireContext(),binding.map)
        val boundingBox = binding.map.boundingBox
        for (marker in markers) {
            if (boundingBox.contains(marker.position)) {
                markerClusterer!!.add(marker)
            }
        }
        binding.map.overlays.add(markerClusterer)

        binding.map.invalidate()
    }

    /** [setupMarkerClusterer]
     * creates a new [markerClusterer] if it does not exists
     * else - first remove it from the map's overlays then create a new instance
     * with the icon, radius and zoom level at below which the clustering must happen
     * (overlays cannot be removed from the clusterer therefor it must be reinitialized every time
     * the data set changes)
     */
    private fun setupMarkerClusterer(context: Context, map: MapView) {
        if (markerClusterer != null) {
            map.overlays.remove(markerClusterer)

            binding.map.invalidate()
        }
        markerClusterer = RadiusMarkerClusterer(context)
        val clusterIcon = BonusPackHelper.getBitmapFromVectorDrawable(context, R.drawable.ic_other_marker)
        markerClusterer!!.setIcon(clusterIcon)
        markerClusterer!!.setRadius(300)
        markerClusterer!!.setMaxClusteringZoomLevel(14)
    }

    /** [showStart]
     * handles the change of the current [com.example.model.Search]'s start place change
     * when it changes the function creates a new marker for it
     * then adds it to the map's overlays
     */
    private fun showStart(startPlace: PlaceDataMapPresentationModel?) {

        if (startPlace != null) {

            val geo = GeoPoint(
                startPlace.coordinates.latitude,
                startPlace.coordinates.longitude
            )
            mapController.setCenter(geo)
            val start = Marker(binding.map)
            start.position = geo
            start.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

            binding.map.overlays.add(start)
            start.icon = ResourcesCompat.getDrawable(
                requireContext().resources,
                R.drawable.ic_start_marker,
                requireContext().theme
            )

            //binding.map.overlays.add(mapEventsOverlay)

            binding.map.invalidate()
        }
    }

    private fun updateMapOnRouteStopSelected(coordinates: CoordinatesMapPresentationModel) {

        mapController.setZoom(15.0)
        val updatedCenter = GeoPoint(coordinates.latitude, coordinates.latitude)
        mapController.setCenter(updatedCenter)

    }

    /** [removeMapData]
     * reset the UI elements related to searching when the start[com.example.model.Place] is changed
     */
    private fun removeMapData(addMapEventsOverlay: Boolean){

        binding.map.overlays.removeAll(binding.map.overlays)

        if (addMapEventsOverlay)
            binding.map.overlays.add(mapEventsOverlay)

        binding.map.invalidate()
    }
//_________________________________________________________________________________________________________________________
// END OF METHODS FOR MAP
//_________________________________________________________________________________________________________________________

//Methods related to internal fragments
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR INTERNAL FRAGMENTS
//_________________________________________________________________________________________________________________________

    private fun replaceCurrentMainFragment(fragment: MapDataMapPresentationModel) {

        val request: NavDeepLinkRequest

        val fragmentClassName: String

        when (fragment) {

            is MapDataMapPresentationModel.Search -> {

                fragmentClassName = fragment.javaClass.name

                request = buildNavDeepLinkRequest(
                    "android-app://com.example.features/search"
                )
            }

            is MapDataMapPresentationModel.Route -> {

                fragmentClassName = fragment.javaClass.name

                request = buildNavDeepLinkRequest(
                    "android-app://com.example.features/route"
                )
            }

            is MapDataMapPresentationModel.Inspect -> {

                fragmentClassName = fragment.javaClass.name

                request = buildNavDeepLinkRequest(
                    "android-app://com.example.features/inspect"
                )
            }

            is MapDataMapPresentationModel.CustomPlace -> {

                fragmentClassName = fragment.javaClass.name

                request = buildNavDeepLinkRequest(
                    "android-app://com.example.features/custom_place"
                )
            }

            is MapDataMapPresentationModel.Navigation -> {

                fragmentClassName = fragment.javaClass.name

                request = buildNavDeepLinkRequest(
                    "android-app://com.example.features/navigation"
                )
            }

            MapDataMapPresentationModel.NavigationArrived -> {

                fragmentClassName = fragment.javaClass.name

                request = buildNavDeepLinkRequest(
                    "android-app://com.example.features/navigation"
                )
            }
        }
        if (fragmentClassName != uiState) {

            uiState = fragmentClassName

            navController.navigate(request)
        }
    }

    private fun buildNavDeepLinkRequest(uriString: String): NavDeepLinkRequest {

        return NavDeepLinkRequest.Builder.fromUri(
            uriString.toUri()
        ).build()
    }

    /*private fun replaceWithSearchFragment() {
        val tag = "SEARCH_FRAGMENT"

        val fragment = childFragmentManager.findFragmentByTag(tag) ?: FragmentSearch.Companion.newInstance()

        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.mainFragmentContainer.id, fragment, tag)
        }
    }

    private fun replaceWithTripFragment() {
        val tag = "TRIP_FRAGMENT"

        val fragment = childFragmentManager.findFragmentByTag(tag) ?: FragmentInspectTrip.newInstance()

        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.mainFragmentContainer.id, fragment, tag)
        }
    }

    private fun replaceWithRouteFragment() {
        val tag = "ROUTE_FRAGMENT"

        val fragment = childFragmentManager.findFragmentByTag(tag) ?: FragmentRoute.Companion.newInstance()

        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.mainFragmentContainer.id, fragment, tag)
        }
    }

    private fun replaceWithNavigationFragment() {
        val tag = "NAVIGATION_FRAGMENT"

        val fragment = childFragmentManager.findFragmentByTag(tag) ?: FragmentNavigation.Companion.newInstance()

        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.mainFragmentContainer.id, fragment, tag)
        }
    }

    private fun replaceWithCustomPlaceFragment() {
        val tag = "CUSTOM_PLACE_FRAGMENT"

        val fragment =
            childFragmentManager.findFragmentByTag(tag) ?: FragmentCustomPlace.newInstance()


        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.mainFragmentContainer.id, fragment, tag)
        }
    }*/

    private fun initDetailsFragment() {
        val tag = "PLACE_DETAILS_FRAGMENT"

        val fragment = childFragmentManager.findFragmentByTag(tag) ?: FragmentPlaceDetails.Companion.newInstance()

        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.bottomSheetContainer.id, fragment, tag)
        }

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
//_________________________________________________________________________________________________________________________
// END OF METHODS FOR INTERNAL FRAGMENTS
//_________________________________________________________________________________________________________________________


    private fun handleRouteChange(
        route: List<Polyline>
    ) {

        //TODO REMINDER: I GET A LIST OF POLYLINES
        /*val routePolylines = when(route.getTransportMode()) {
            "driving-car" -> route.getRouteNodes().mapNotNull { it.carPolyLine }
            else -> route.getRouteNodes().mapNotNull { it.walkPolyLine }
        }*/

        showRouteDataOnMap(
            routePolylines = route
        )
    }

    private fun handleNavigationChange(
        routePolyline: Polyline,
        goal: CoordinatesMapPresentationModel
    ) {

        createLocationMarker(goal)

        showRouteDataOnMap(
            routePolylines = listOf(routePolyline)
        )
    }

    private fun createLocationMarker(location: CoordinatesMapPresentationModel) {

        val locationMarker = Marker(binding.map)

        locationMarker.position = GeoPoint(location.latitude, location.longitude)

        locationMarker.icon = ResourcesCompat.getDrawable(
            requireContext().resources,
            R.drawable.ic_other_marker,
            requireContext().theme
        )

        binding.map.overlays.add(locationMarker)
    }

    /*private fun showNavigationStart(navigationRouteNode: RouteNode, mode: String) {

        val geo = when (mode) {
            "foot-walking" -> GeoPoint(
                navigationRouteNode.walkRouteSteps[0].coordinates.getLatitude(),
                navigationRouteNode.walkRouteSteps[0].coordinates.getLongitude()
            )

            else -> GeoPoint(
                navigationRouteNode.carRouteSteps[0].coordinates.getLatitude(),
                navigationRouteNode.carRouteSteps[0].coordinates.getLongitude()
            )
        }
        mapController.setCenter(geo)
        locationMarker = Marker(binding.map)
        locationMarker?.position = geo
        locationMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        binding.map.overlays.add(locationMarker)
        locationMarker?.icon = ResourcesCompat.getDrawable(
            requireContext().resources,
            R.drawable.ic_instruction_depart,
            requireContext().theme
        )
        binding.map.invalidate()
    }*/

    private fun showRouteDataOnMap(
        routePolylines: List<Polyline>
    ) {

        binding.map.overlays.removeAll(routePolyLines)

        routePolyLines.clear()

        routePolyLines.addAll(routePolylines)

        binding.map.overlays.addAll(routePolyLines)

        binding.map.invalidate()
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showNavigationData(coordinates: CoordinatesMapPresentationModel?) {


        if (locationMarker!= null) {
            binding.map.overlays.remove(locationMarker)
        }

        if (coordinates !=null) {

            val geo = GeoPoint(
                coordinates.latitude,
                coordinates.longitude
            )
            mapController.setCenter(geo)
            locationMarker = Marker(binding.map)
            locationMarker?.position = geo
            locationMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

            binding.map.overlays.add(locationMarker)
            locationMarker?.icon = ResourcesCompat.getDrawable(
                requireContext().resources,
                R.drawable.ic_current_location,
                requireContext().theme
            )
            binding.map.invalidate()

        }
    }

    /*private fun showErrorMessage(errorType: ViewModelMain.ErrorGroup) {

        val message = when(errorType) {
            ViewModelMain.ErrorGroup.OTHER -> {R.string.error_other}
            ViewModelMain.ErrorGroup.CUSTOM_PLACE -> {R.string.error_custom_place}
            ViewModelMain.ErrorGroup.LOCATION -> {R.string.error_location}
            ViewModelMain.ErrorGroup.INIT_SEARCH -> {R.string.error_show_search}
            ViewModelMain.ErrorGroup.SEARCH_AUTO -> {R.string.error_autocomplete}
            ViewModelMain.ErrorGroup.REVERSE_GEO_CODE -> {R.string.error_location}
            ViewModelMain.ErrorGroup.SEARCH -> {R.string.error_search}
            ViewModelMain.ErrorGroup.NAVIGATION -> {R.string.error_navigation}
            else -> return
        }

        //Toast.makeText(this.context,message, Toast.LENGTH_LONG).show()
    }*/
}