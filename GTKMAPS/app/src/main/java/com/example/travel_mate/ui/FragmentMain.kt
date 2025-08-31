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

    private var locationMarker: Marker? = null

    private var customPlace: Marker? = null
    private var unContainedMarkers: ArrayList<Marker> = ArrayList()
    private var containedMarkers: ArrayList<Marker> = ArrayList()
    private var routePolyLines: ArrayList<Polyline> = ArrayList()

    private val viewModelMain: ViewModelMain by inject<ViewModelMain>()

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

        binding.map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {

                checkBoundingBox(unContainedMarkers)
                return true
            }

            override fun onZoom(event: ZoomEvent): Boolean {

                checkBoundingBox(unContainedMarkers)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentLifecycle", "Parent/Child Fragment Destroyed")

        resources?.flushLayoutCache()

        _binding = null
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            // Do something for new state.
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {

                    viewModelMain.setSelectedPlaceContainerState("collapsed")

                }
                BottomSheetBehavior.STATE_EXPANDED -> {

                    viewModelMain.setSelectedPlaceContainerState("expanded")
                }

                BottomSheetBehavior.STATE_DRAGGING -> {
                }

                BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                }

                BottomSheetBehavior.STATE_HIDDEN -> {

                    val fragment = fragmentManager.findFragmentByTag("PLACE_DETAILS_FRAGMENT")
                    if (fragment != null) {
                        fragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss()
                    }
                }

                BottomSheetBehavior.STATE_SETTLING -> {
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Do something for slide offset.
        }
    }

    private fun updateMapContentWith(mapData: MapDataMapPresentationModel) {

        when (mapData) {

            is MapDataMapPresentationModel.Search -> {

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

    private fun handleStartPlaceChange(startPlace: PlaceDataMapPresentationModel?) {

        showStart(
            startPlace = startPlace
        )
    }

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

            val relatedPlace = m.relatedObject as String

            viewModelMain.setSelectedPlace(relatedPlace)

            viewModelMain.setSelectedPlaceContainerState("collapsed")

            initDetailsFragment()

            true
        }

        return marker
    }

    private fun showMapContent(
        places: List<PlaceDataMapPresentationModel>
    ) {

        unContainedMarkers.clear()

        unContainedMarkers.addAll(createMarkersOnMap(places = places))

        binding.map.invalidate()

        checkBoundingBox(unContainedMarkers)
    }

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

            binding.map.invalidate()
        }
    }

    private fun updateMapOnRouteStopSelected(coordinates: CoordinatesMapPresentationModel) {

        mapController.setZoom(15.0)
        val updatedCenter = GeoPoint(coordinates.latitude, coordinates.latitude)
        mapController.setCenter(updatedCenter)

    }

    private fun removeMapData(addMapEventsOverlay: Boolean){

        binding.map.overlays.removeAll(binding.map.overlays)

        if (addMapEventsOverlay)
            binding.map.overlays.add(mapEventsOverlay)

        binding.map.invalidate()
    }

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

    private fun initDetailsFragment() {
        val tag = "PLACE_DETAILS_FRAGMENT"

        val fragment = childFragmentManager.findFragmentByTag(tag) ?: FragmentPlaceDetails.Companion.newInstance()

        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.bottomSheetContainer.id, fragment, tag)
        }

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun handleRouteChange(
        route: List<Polyline>
    ) {

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
}