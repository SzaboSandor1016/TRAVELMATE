package com.example.travel_mate

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_mate.databinding.FragmentMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.utils.BonusPackHelper
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMain.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class FragmentMain : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    data class ChipCategory(val id: Int?,val content: String,val icon: Int?,val title: Int?,var checked: Boolean?,var category: String)

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

    private lateinit var suggestionsAdapter: AdapterSuggestion
    private lateinit var routeStopsAdapter: AdapterRouteStopsRecyclerView

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    var dist: Double = 0.0
    var startPlace: Place = Place()

    private var startPlaces: ArrayList<Place> = ArrayList()
    private var suggestions: ArrayList<String> = ArrayList()
    private var routeStops: ArrayList<RouteNode> = ArrayList()
    private var routeMode: String = "foot-walking"
    private var unContainedMarkers: ArrayList<Marker> = ArrayList()
    private var containedMarkers: ArrayList<Marker> = ArrayList()
    private var routePolyLines: ArrayList<Polyline> = ArrayList()

    private var locationListener: ClassLocationListener? = null

    private var categoryManager: ClassCategoryManager? = null
    private var resources: Resources? = null

    private val viewModelMain: ViewModelMain by activityViewModels { MyApplication.factory }
    private val viewModelUser: ViewModelUser by activityViewModels { MyApplication.factory }

    private lateinit var fragmentManager: FragmentManager

    /*private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase*/

    private val chipGroupCategories: Map<Int, List<ChipCategory>> = mapOf(
        0 to listOf(
            ChipCategory(1,"\"amenity\"=\"restaurant\"",R.drawable.ic_restaurant,R.string.restaurant, false,"restaurant"),
            ChipCategory(2,"\"amenity\"=\"cafe\"", R.drawable.ic_cafe,R.string.cafe,false,"cafe"),
            ChipCategory(3,"\"amenity\"=\"fast_food\"",R.drawable.ic_fast_food,R.string.fast_food, false,"fast_food"),
            ChipCategory(4,"\"amenity\"=\"pub\";\"amenity\"=\"bar\"",R.drawable.ic_bar,R.string.pub_bar,false,"pub_bar")
        ),
        3 to listOf(
            ChipCategory(1,"\"amenity\"=\"cinema\"", R.drawable.ic_cinema,R.string.cinema, false,"cinema"),
            ChipCategory(2,"\"amenity\"=\"theatre\"", R.drawable.ic_theatre,R.string.theatre, false,"theatre"),
            ChipCategory(3,"\"amenity\"=\"nightclub\"", R.drawable.ic_nightclub, R.string.nightclub, false,"nightclub"),
            ChipCategory(4,"\"amenity\"=\"casino\"",R.drawable.ic_casino, R.string.casino, false,"casino")
        ), 5 to listOf(
            ChipCategory(1,"\"tourism\"=\"theme_park\"",R.drawable.ic_theme_park,R.string.adventure_park,false,"theme_park"),
            ChipCategory(2,"\"leisure\"=\"water_park\"", R.drawable.ic_beach_resort,R.string.spa,false,"water_park"),
            ChipCategory(3,"\"leisure\"=\"beach_resort\"", R.drawable.ic_beach,R.string.beach_resort,false,"beach_resort"),
            ChipCategory(4,"\"tourism\"=\"zoo\"",R.drawable.ic_zoo,R.string.zoo,false,"zoo")
        ))

    private val landmarkChip: ChipCategory = ChipCategory(null,"\"historic\"=\"memorial\";" +
            "\"building\"=\"cathedral\";" +
            "\"amenity\"=\"place_of_worship\";" +
            "\"amenity\"=\"monastery\";" +
            "\"historic\"=\"building\";" +
            "\"building\"=\"basilica\";" +
            "\"historic\"=\"monument\";" +
            "\"building\"=\"church\";" +
            "\"building\"=\"temple\";" +
            "\"castle_type\"=\"palace\";" +
            "\"historic\"=\"fort\";" +
            "\"historic\"=\"castle\"" //;+ add
        ,null,null,null,"landmark")

    private val accommodationChip: ChipCategory = ChipCategory(null,"\"building\"=\"hotel\";" +
            "\"leisure\"=\"summer_camp\";" +
            "\"tourism\"=\"caravan_site\";" +
            "\"tourism\"=\"hostel\";" +
            "\"tourism\"=\"motel\";" +
            "\"tourism\"=\"guest_house\";" +
            "\"tourism\"=\"camp_site\"" //; + add
        ,null,null,null,"accommodation")
    private val exhibitionChip: ChipCategory = ChipCategory(null,"\"tourism\"=\"museum\";" +
            "\"tourism\"=\"gallery\";" +
            "\"amenity\"=\"arts_centre\";" +
            "\"amenity\"=\"exhibition_centre\"" //; + add
        ,null,null,null,"exhibition")

    private var chipGroupCategory: List<ChipCategory> = emptyList()

    private lateinit var dialog: Dialog

    private lateinit var mapController: IMapController
    private lateinit var startMarker: Marker
    private var markerClusterer: RadiusMarkerClusterer?= null

    private lateinit var popupView: View

    /**
     * search for potential starting places based on the input text
     * if the length is longer or equal to 4
     * */
    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            if (s.length >= 4) {

                viewModelMain.searchAutocomplete(s.toString())
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

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

        mapController = binding.map.controller
        startMarker= Marker(binding.map)

        /*
        In theory this would make the map not to show constantly
        and limit the scrollable area
        if fully zoomed out but actually it breaks the application
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
        startMarker.icon = ResourcesCompat.getDrawable(/*fragmentMain.*/getResources(),R.drawable.ic_start_marker, context?.theme)
        
        val inflater = LayoutInflater.from(context)
        popupView = inflater.inflate(R.layout.layout_popup_menu,null)

        dialog = Dialog(popupView.context)
        dialog.setContentView(popupView)
        dialog.setCancelable(false)

        categoryManager= ClassCategoryManager(requireContext())
        resources = getResources()

        /*firebaseAuth = Firebase.auth
        database = */

        fragmentManager = childFragmentManager

        viewModelUser.checkCurrentUser()

/**Observe the [ViewModelMain.chipsState], [ViewModelMain.placeState] and the [ViewModelMain.mainSearchState] located in [viewModelMain]
 *
 **/
//_________________________________________________________________________________________________________________________
// BEGINNING OF MAIN VIEWMODEL OBSERVER
//_________________________________________________________________________________________________________________________

        /** [ViewModelMain.chipsState] observer
         *  observe the [viewModelMain]'s [ViewModelMain.chipsState]
         *  on state update
         *  - the [createChipGroupDialog] function is called
         *   if the currentChipGroup and the currentChipGroupContent != null
         *   else dismiss the [dialog]
         *  - [showHideExtendedSearch] function is called
         *  - [enableDisableMinuteSelect] function is called
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.chipsState.collect {

                    /*if (it.currentPlace != null)
                        updateCurrentPlace(it.currentPlace)*/
                    if (it.currentChipGroup != null && it.currentChipGroupContent != null) {

                        //chipGroupCategory = it.currentChipGroupContent

                        createChipGroupDialog(
                            id = it.currentChipGroup,
                            chipGroupContent = it.currentChipGroupContent
                        )

                    } else {

                        dismissDialog()
                    }

                    showHideExtendedSearch(it.showExtendedSearch)

                    enableDisableMinuteSelect(it.transportMode)

                    dist = it.distance

                    Log.d("refresh", "refresh")
                }

            }
        }

        /** [ViewModelMain.mainSearchState] observer
         *  observe the [viewModelMain]'s [ViewModelMain.mainSearchState]
         *  on state update
         *  - the [handleStartPlaceChange] function is called
         *  - [handlePhotonObserve] function is called
         *   if its [startPlaces] is not empty
         *  - [showMapContent] function is called
         *  - [handleRouteStopsChange] function is called
         *  - [ViewModelMain.getCurrentPlaceByUUID] function is called
         *   if there is a currentPlace selected
         *  - [handleTripButtons] function is called
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMain.mainSearchState.collect {

                    Log.d("isStartPlacesEmpty", it.startPlaces.isEmpty().toString())

                    if (it.startPlaces.isNotEmpty())
                        handlePhotonObserve(
                            places = it.startPlaces
                        )

                    showMapContent(
                        places = it.places,
                        routeNodes = it.route.getRouteNodes(),
                        mode = it.route.getTransportMode()
                    )

                    handleRouteStopsChange(
                        route = it.route
                    )

                    Log.d("routePolysFragment",  it.route.getRouteNodes().size.toString())

                    if (it.currentPlaceUUID != null)
                        viewModelMain.getCurrentPlaceByUUID(
                            uuid = it.currentPlaceUUID
                        )

                    handleTripButtons(
                        isTripPlacesEmpty = it.isTripEmpty
                    )

                    Log.d("tripEmpty", it.isTripEmpty.toString())
                }
            }
        }

        /** [ViewModelMain.placeState] observer
         *  observe the [viewModelMain]'s [ViewModelMain.placeState]
         *  on state update
         *  - sets the [standardBottomSheetBehavior]'s peekHeight
         *      on the measured height read from the state
         */

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.placeState.collect {

                    /*if (it.currentPlace != null)
                        updateCurrentPlace(it.currentPlace)*/
                    standardBottomSheetBehavior.peekHeight = it.containerHeight

                    Log.d("refresh", "refresh")
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.mainStartPlaceState.collect {

                    handleStartPlaceChange(
                        startPlace = it.startPlace
                    )

                    Log.d("refresh", "refresh")
                }

            }
        }



//_________________________________________________________________________________________________________________________
// END OF MAIN VIEWMODEL OBSERVER
//_________________________________________________________________________________________________________________________

    /** Route methods block
     *  initializes the [routeStopsAdapter] with a default empty [routeStops]
     *   and the default [routeMode] that is walking
     *  sets a layout manager for the [binding.routeStopsList] recycler view
     *   and an adapter which is the initialized [routeStopsAdapter]
     *  sets an [onClickListener] for the same adapter too
     **/
//_________________________________________________________________________________________________________________________
// BEGINNING OF ROUTE METHODS BLOCK
//_________________________________________________________________________________________________________________________

        routeStopsAdapter = AdapterRouteStopsRecyclerView(
            routeStops = this.routeStops,
            mode = this.routeMode
        )

        binding.routeStopsList.layoutManager = LinearLayoutManager(context)
        binding.routeStopsList.adapter = routeStopsAdapter

        val touchHelper: ItemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.Callback() {

            var drag = false

            var draggedIndex: Int = 0
            var targetIndex: Int = 0



            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {

                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN , ItemTouchHelper.END)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                draggedIndex = viewHolder.adapterPosition
                targetIndex = target.adapterPosition

                routeStopsAdapter.notifyItemMoved(draggedIndex, targetIndex);

                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                when(direction) {
                    ItemTouchHelper.END -> {
                        val removed = routeStops[viewHolder.adapterPosition]
                        viewModelMain.addRemovePlaceToRoute(removed.placeUUID.toString())
                    }
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    drag = true
                    Log.d("DragTest","DRAGGGING start");
                }
                if(actionState == ItemTouchHelper.ACTION_STATE_IDLE && drag) {
                    Log.d("DragTest", "DRAGGGING stop");

                    if (draggedIndex != targetIndex) {

                        val routeNode = routeStops[draggedIndex]

                        viewModelMain.reorderRoute(
                            newPosition = targetIndex,
                            nodeToMove = routeNode
                        )
                    }
                    drag = false
                }

            }

        })

        touchHelper.attachToRecyclerView(binding.routeStopsList)

        routeStopsAdapter.setOnClickListener(object : AdapterRouteStopsRecyclerView.OnClickListener {


            override fun onClick(
                uuid: String?,
                coordinates: Coordinates?
            ) {
                /**
                 * set the current place based on the uuid of the [Place]
                 * associated with the selected [RouteNode]
                 */
                viewModelMain.getCurrentPlaceByUUID(
                    uuid = uuid!!
                )

                /**
                 * call the [updateMapOnRouteStopSelected] method
                 */
                updateMapOnRouteStopSelected(
                    coordinates = coordinates!!
                )

            }
        })

        /**
         * check the walk mode as the default mode for route planning
         */
        binding.routeModeGroup.check(binding.routeSelectWalk.id)

        /**
         * add an [OnButtonCheckedListener] for the route transport mode selector
         * button group
         * on check change update the transport mode
         */
        binding.routeModeGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

            //get the currently selected index
            val index = binding.routeModeGroup.indexOfChild(binding.routeModeGroup.findViewById(binding.routeModeGroup.checkedButtonId))

            //set the route mode according to the index
            viewModelMain.setRouteTransportMode(
                index = index
            )
        }

        /**
         * add an [OnclickListener] for the [dismissRoutePlan] button
         * when clicked reset the route [ViewModelMain.resetRoute]
         */
        binding.dismissRoutePlan.setOnClickListener { l ->

            viewModelMain.resetRoute()

            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.optimizeRoute.setOnClickListener { l ->
            viewModelMain.optimizeRoute()
        }
//_________________________________________________________________________________________________________________________
// END OF ROUTE METHODS BLOCK
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


        /**Listener to handle CLICKING THE USER BUTTON
         * navigate to the [FragmentUser]
         * */
//_________________________________________________________________________________________________________________________
// BEGINNING OF USER BUTTON LISTENER
// _________________________________________________________________________________________________________________________
        binding.userBTN2.setOnClickListener{ v ->
            animateImageButton(v)

            findNavController().navigate(R.id.action_FragmentMain_to_FragmentUser)
        }
//_________________________________________________________________________________________________________________________
// END OF USER BUTTON LISTENER
// _________________________________________________________________________________________________________________________


        /**Listeners to handle searching start place
         * initialize the [suggestionsAdapter] adapter
         * set this adapter for the [placeSearch] [MaterialAutoCompleteTextView]
         * sets the background of the same text view
         * create an [OnItemClickListener] too
         */
//_________________________________________________________________________________________________________________________
// BEGINNING OF PLACE SEARCH LISTENERS
// _________________________________________________________________________________________________________________________

        suggestionsAdapter = AdapterSuggestion(
            context =  requireContext(),
            resource = R.layout.layout_autocomplete_item ,
            objects = suggestions
        )

        binding.placeSearch.setAdapter(suggestionsAdapter)

        binding.placeSearch.setDropDownBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.shape_autocomplete_dropdown))

        /*
        * initialize a new search
         * hide the soft input keyboard
         */
        binding.placeSearch.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                viewModelMain.initNewSearch(startPlaces[position])

                viewModelMain.resetStartPlaces()

                clearChips()

                dismissDialog()

                Log.d("viewModelStartPlaceTest1", startPlaces[position].getName()!!)

                /*binding.placeSearch.setText(suggestions[position])*/

                val inputMethodManager: InputMethodManager =
                    getSystemService(requireContext(),InputMethodManager::class.java) as InputMethodManager
                val currentFocusView = activity?.currentFocus
                currentFocusView?.let {
                    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                }
                binding.placeSearch.clearFocus()

            }
        // set the threshold as 4 for the search text view
        binding.placeSearch.threshold = 4

        binding.placeSearch.addTextChangedListener(textWatcher)

//_________________________________________________________________________________________________________________________
// END OF PLACE SEARCH LISTENERS
//_________________________________________________________________________________________________________________________

//Listeners to handle extended search
//_________________________________________________________________________________________________________________________
// BEGINNING OF EXTENDED SEARCH LISTENERS
//_________________________________________________________________________________________________________________________

        /**
         * if the "extended search" button is checked set the extendedSearchVisible state attribute as true
         * in [ViewModelMain]
         * else reset the extended search attributes which are the selected transport mode and the distance in minutes
         */
        binding.openExtended.addOnCheckedChangeListener { button, isChecked ->

            if (isChecked) {

                viewModelMain.setExtendedSearchVisible(true)

            } else {

                if (dist != 0.0) {

                    viewModelMain.resetDetails()

                }
                uncheckExtendedSearch()

                clearChips()

                dismissDialog()

                viewModelMain.resetExtendedSearch()
            }
        }
        /**
         * transport mode selection listener
         * set the transport mode as selected
         */
        binding.transportGroup.addOnButtonCheckedListener{ group, checkedId, isChecked ->

            val index = binding.transportGroup.indexOfChild(binding.transportGroup.findViewById(binding.transportGroup.checkedButtonId))

            viewModelMain.setTransportMode(index)
        }
        /**
         * distance selection listener
         * set the distance that is in minutes as selected
         */
        binding.distanceGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

            val index = binding.distanceGroup.indexOfChild(binding.distanceGroup.findViewById(binding.distanceGroup.checkedButtonId))

            viewModelMain.setMinute(index)

            viewModelMain.setExtendedSearchSelected(true)
        }
//_________________________________________________________________________________________________________________________
// END OF EXTENDED SEARCH LISTENERS
//_________________________________________________________________________________________________________________________

//Listener to handle using the current location for start place
//_________________________________________________________________________________________________________________________
// BEGINNING OF CURRENT LOCATION LISTENERS
//_________________________________________________________________________________________________________________________

        /**
         * location button listener
         * if it is checked, ask for current location updates
         * then with the received coordinates, make a reverseGeoCode request to the [PhotonRemoteDataSource]
         * if it is unchecked reset the ui, the search
         * preparing it for an other location selection or an input to the search field
         */
        binding.useLocation.addOnCheckedChangeListener{ l, isChecked ->

            viewModelMain.resetFullDetails()

            if (isChecked) {

                val currentCoordinates = getCurrentLocation()

                if (currentCoordinates.getLatitude() != 0.0 && currentCoordinates.getLongitude() != 0.0) {

                    viewModelMain.searchReverseGeoCode(currentCoordinates)
                }

                binding.placeSearch.isEnabled = false
            } else {

                binding.placeSearch.isEnabled = true
            }
        }
//_________________________________________________________________________________________________________________________
// END OF CURRENT LOCATION LISTENERS
//_________________________________________________________________________________________________________________________

//Listeners for chips used to search nearby relevant places by specific categories
//_________________________________________________________________________________________________________________________
// BEGINNING OF CHIP LISTENERS
//_________________________________________________________________________________________________________________________
        /**
         * if the scrollView containing the category chips
         * are scrolled call the [ViewModelMain.resetCurrentChipGroup] function
         */
        binding.chipGroups.setOnScrollChangeListener{ _, _, _, _, _ ->

            viewModelMain.resetCurrentChipGroup()
        }

        /**
         * add a listener for each chip in the category chips [android.widget.HorizontalScrollView]
         * in [fragment_main.xml]
         *-  on select if the selected chip is not a group chip
         * make a search for the places with the category that specific chip represents
         * - if it is a group chip update the dialog that contains the chips that represent
         * the categories defined in that group
         * - else, on uncheck remove the markers based on the category associated with them
         */
        for (groupChip in binding.chipGroupChips.children){

            if (groupChip is Chip) {

                groupChip.setOnCheckedChangeListener{ l, isChecked ->

                    val groupIndex = binding.chipGroupChips.indexOfChild(binding.chipGroupChips.findViewById(l.id))

                    if (groupIndex in chipGroupCategories.keys){

                        if (!isChecked){

                            viewModelMain.resetCurrentChipGroup()

                        }else{
                            Log.d("check", "check")

                            val chipGroupContent = chipGroupCategories[groupIndex]

                            if (chipGroupContent != null) {
                                viewModelMain.setCurrentChipGroup(l.id,chipGroupContent)
                            }
                        }


                    }else if (groupIndex == 1){

                        handleCategoryFiltererChipCheckChange(accommodationChip.content,accommodationChip.category, isChecked/*, l.id*/)
                    }else if (groupIndex == 2){

                        handleCategoryFiltererChipCheckChange(exhibitionChip.content,exhibitionChip.category, isChecked/*, l.id*/)

                    }else {

                        handleCategoryFiltererChipCheckChange(landmarkChip.content,landmarkChip.category, isChecked/*, l.id*/)

                    }


                }
            }
        }
//_________________________________________________________________________________________________________________________
// END OF CHIP LISTENERS
//_________________________________________________________________________________________________________________________
        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)

        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentLifecycle", "Parent/Child Fragment Destroyed")

        dismissDialog()
        locationListener = null

        binding.distanceGroup.clearOnButtonCheckedListeners()
        binding.transportGroup.clearOnButtonCheckedListeners()

        resources!!.flushLayoutCache()

        locationListener?.stopListener()

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
                    viewModelMain.setContainerState("collapsed")

                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    // Kinyitott állapot
                    viewModelMain.setContainerState("expanded")
                }
                // További állapotok kezelése...
                BottomSheetBehavior.STATE_DRAGGING -> {
                }

                BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                }

                BottomSheetBehavior.STATE_HIDDEN -> {
                    val fragment = fragmentManager.fragments[0]
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
//_________________________________________________________________________________________________________________________
// END OF BOTTOM SHEET CALLBACK
//_________________________________________________________________________________________________________________________

//Methods related to the search text field
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR SEARCH TEXT FIELD
//_________________________________________________________________________________________________________________________

    /** [handleStartPlaceChange]
     *  on start place change in the [ViewModelMain] caused by an update in [SearchRepository]
     *  first remove all content from the map
     *  then call [showStart], [setStartTextFiled]
     *  and set the visibility of the category chips.
     */

    private fun handleStartPlaceChange(startPlace: Place?) {

        resetUiOnStartPlaceChange()

        showStart(
            startPlace = startPlace
        )

        setStartTextFiled(
            startPlace = startPlace
        )

        if (startPlace != null) {

            binding.chipGroupChips.visibility = View.VISIBLE
        } else {

            binding.chipGroupChips.visibility = View.GONE
        }
    }

    /** [setStartTextFiled]
     * set the search field's hint as the current start place's name + address
     * on search place change from [ViewModelMain] and [SearchRepository]
     */
    private fun setStartTextFiled(startPlace: Place?) {

        if (startPlace != null) {

            this.startPlace = startPlace

            resetSearchField()

            val stringBuilder = StringBuilder()

            stringBuilder.append(startPlace.getName() + " ")
            stringBuilder.append(startPlace.getAddress()?.getFullAddress())

            binding.placeSearch.setText("")
            binding.placeSearch.hint = stringBuilder.toString()
        } else {

            resetSearchField()
        }
    }

    /** [resetSearchField]
     * set the default hint for the search field
     */
    private fun resetSearchField() {

        binding.placeSearch.hint = resources?.getString(R.string.search_place)
        binding.placeSearch.setText("")
    }

    /** [getCurrentLocation]
     *  ask for a location update, generate a [Coordinates] from it and return
     */
    private fun getCurrentLocation(): Coordinates{

        locationListener = ClassLocationListener(requireContext())

        return Coordinates(locationListener!!.getLatitude(),locationListener!!.getLongitude())
    }

    /** [handlePhotonObserve]
     *  handle the potential start places change received from the autocomplete service
     *  refresh the text view's adapter with the name + address string received
     */
    private fun handlePhotonObserve(places: List<Place>){

        startPlaces.clear()
        suggestions.clear()

        startPlaces.addAll(places)

        suggestions.addAll( startPlaces.map {

            val suggestion = StringBuilder()
            suggestion.append(it.getName()).append(" ")
            suggestion.append(it.getAddress()?.getFullAddress() ?: "")

            suggestion.toString()
        })

        suggestionsAdapter = AdapterSuggestion(
            context =  requireContext(),
            resource = R.layout.layout_autocomplete_item ,
            objects = suggestions
        )

        binding.placeSearch.setAdapter(suggestionsAdapter)

        suggestionsAdapter.notifyDataSetChanged()
    }
//_________________________________________________________________________________________________________________________
// END OF METHODS FOR SEARCH TEXT FIELD
//_________________________________________________________________________________________________________________________


//Methods related to the extended search
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR EXTENDED SEARCH
//_________________________________________________________________________________________________________________________
    /** [showHideExtendedSearch]
     *  show or hide the extended search buttons based on the current value of the
     *  [viewModelMain]'s state attributes
     */
    private fun showHideExtendedSearch(showExtendedSearch: Boolean) {

        if (showExtendedSearch) {

            binding.transportGroup.visibility = View.VISIBLE
            binding.distanceGroup.visibility = View.VISIBLE

            binding.chipGroups.visibility = View.GONE

        }else {

            binding.transportGroup.visibility = View.GONE
            binding.distanceGroup.visibility = View.GONE

            binding.chipGroups.visibility = View.VISIBLE
        }

        dismissDialog()
    }

    private fun enableDisableMinuteSelect(transportMode: String?) {

        val hasChecked = transportMode != null

        if (hasChecked){
            binding.distanceGroup.isEnabled = true
        } else {
            binding.distanceGroup.clearChecked()
            binding.distanceGroup.isEnabled = false
        }
    }

    private fun uncheckExtendedSearch() {

        binding.transportGroup.clearChecked()
        binding.distanceGroup.clearChecked()
        binding.distanceGroup.isEnabled = false
    }
//_________________________________________________________________________________________________________________________
// END OF METHODS FOR EXTENDED SEARCH
//_________________________________________________________________________________________________________________________


//Methods related to the category chips
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR CATEGORY CHIPS
//_________________________________________________________________________________________________________________________
    /** [createChipGroupDialog]
     *  create the customised [Dialog] that contains the search categories of that
     *  specific group
     */
    private fun createChipGroupDialog(id: Int, chipGroupContent :List<ChipCategory>){

        val groupChip: Chip = binding.chipGroupChips.findViewById(id)

        groupChip.post {
            // Get chip position on screen

            val location = IntArray(2)
            groupChip.getLocationOnScreen(location)
            val chipHeight = groupChip.height * 0.5

            // Position the dialog under the chip
            dialog.window?.apply {
                val params = this.attributes
                params.gravity = Gravity.TOP or Gravity.START
                params.x = location[0] // Set X position to chip's left edge
                params.y = (location[1]+chipHeight).toInt() // Set Y position just below the chip
                this.attributes = params
                setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                setBackgroundDrawable(ResourcesCompat.getDrawable(/*fragmentMain.*/resources!!,R.color.transparent, context.theme))
                setDimAmount(0f)
            }

            val layout = popupView.findViewById<LinearLayout>(R.id.popup_menu_content)

            layout.removeAllViews()

            for (content in chipGroupContent){

                val chip = createSearchChip(content)

                layout.addView(chip)

            }

            dialog.show()
        }
    }

    private fun dismissDialog() {
        dialog.dismiss()
    }

    /** [createSearchChip]
     * create a chip that will be added to the dialog's layout
     * these chips work the same way as the ones not part of a group
     */
    private fun createSearchChip(chipCategory: ChipCategory): Chip{
        val chip = Chip(context,null,R.style.material3_Chip).apply {

            this.id = chipCategory.id!!
            this.chipIcon = ResourcesCompat.getDrawable(resources,chipCategory.icon!!, context.theme)
            this.text = resources.getString(chipCategory.title!!)
            this.isClickable = true
            this.isCheckable = true
            this.isChecked = chipCategory.checked!!
            val cornerRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f, // 8dp
                resources.displayMetrics
            )
            this.chipCornerRadius = cornerRadius
            isChipIconVisible = !chipCategory.checked!!
        }

        chip.setOnCheckedChangeListener{ _, isChecked ->

            chip.isChipIconVisible = !isChecked/*

            val index = chipGroupCategory.indexOf(chipCategory)*/

            if(!isChecked){

                //chipGroupCategory[index].checked = false
                chipCategory.checked = false

                handleCategoryFiltererChipCheckChange(chipCategory.content,chipCategory.category,
                    chipCategory.checked!!
                )
            }else{
                //chipGroupCategory[index].checked = true
                chipCategory.checked = true

                handleCategoryFiltererChipCheckChange(chipCategory.content,chipCategory.category,
                    chipCategory.checked!!)
            }/*
            Log.d("ischipchecked", chipGroupCategory[index].checked.toString())
            viewModelMain.setCurrentChipGroupContent(chipGroupCategory)*/

        }
        return chip
    }

    private fun handleCategoryFiltererChipCheckChange(content: String, category: String, checked: Boolean, id: Int? = null){

        if (checked){
            handleCategoryFilterChipChecked(content,category)
            if (id != null)
                viewModelMain.addSelectedChip(id)
        }else{
            handleCategoryFilterChipUnchecked(category)
            if (id != null)
                viewModelMain.removeSelectedChip(id)
        }

    }

    private fun handleCategoryFilterChipChecked(content: String, category: String){

        searchChipCategory(content,category)
    }

    /** [handleCategoryFilterChipUnchecked]
     * remove the places with the category given in parameter
     */
    private fun handleCategoryFilterChipUnchecked(category: String){

        viewModelMain.removePlacesByCategory(category)
    }

    /** [searchChipCategory]
     *  make a search by the selected category and the current start place's coordinates
     */
    private fun searchChipCategory(content: String, category: String){

        viewModelMain.searchOverpass(
            content,
            startPlace.getCoordinates().getLatitude().toString(),
            startPlace.getCoordinates().getLongitude().toString(),
            startPlace.getAddress()?.getCity()!!,
            category
        )
    }

    /** [clearChips]
     *  reset the selection of the selected category chips
     */
    private fun clearChips() {

        chipGroupCategories.values.flatten().forEach { it.checked = false }

        listOf(accommodationChip, landmarkChip, exhibitionChip).forEach { it.checked = false }

        binding.chipGroupChips.clearCheck()
    }
//_________________________________________________________________________________________________________________________
// END OF METHODS FOR CATEGORY CHIPS
//_________________________________________________________________________________________________________________________


/**Methods of map related operations
*
 */
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR MAP
//_________________________________________________________________________________________________________________________

    /** [showMarkersOnMap]
     * [showMarkersOnMap] creates and returns a list of the markers
     *  *  based on the list of [ViewModelMain.PlaceProcessed] classes passed as he functions parameter
     */
    private fun showMarkersOnMap(places: List<ViewModelMain.PlaceProcessed>): List<Marker> {

        val markers: ArrayList<Marker> = ArrayList()

        for (place in places){
            val marker: Marker = Marker(binding.map)
            val titleMarker = Marker(binding.map)
            val position = GeoPoint(place.coordinates.getLatitude(), place.coordinates.getLongitude())
            marker.position = position
            titleMarker.position = position
            marker.icon = categoryManager?.getMarkerIcon(place.category)
            titleMarker.setTextIcon(place.title)

            marker.relatedObject = place.uuid

            marker.setOnMarkerClickListener{ m, mapView ->

                /** get the related object of the clicked marker
                    that is the uuid of the place associated with it
                    then calls an update on the current place in the ViewModel
                    [ViewModelMain.getCurrentPlaceByUUID]
                    set the [com.google.android.material.bottomsheet.BottomSheetDialog]'s
                    state as "collapsed"
                 */

                val relatedPlace = m.relatedObject as String

                viewModelMain.getCurrentPlaceByUUID(relatedPlace)

                viewModelMain.setContainerState("collapsed")

                initDetailsFragment()

                true
            }

            markers.add(marker)
            markers.add(titleMarker)
        }

        return markers
    }

    /** [showMapContent]
     *  handles state update events for all data related to the map
     *  - creates the marker list for [markerClusterer] with the places needed to be clustered
     *  which are the ones that are not part of the current [Route]
     *  - create an other list for [Marker]'s that are part of the current [Route]
     *  these are not clustered and always visible on the map
     *  - adds all the [org.osmdroid.views.overlay.Polyline]'s of the current [Route]
     *  to the map too
     */
    private fun showMapContent(
        places: List<ViewModelMain.PlaceProcessed>,
        routeNodes: List<RouteNode>?,
        mode: String?
    ) {

        binding.map.overlays.removeAll(unContainedMarkers)
        binding.map.overlays.removeAll(containedMarkers)
        binding.map.overlays.removeAll(routePolyLines)

        unContainedMarkers.clear()
        containedMarkers.clear()
        routePolyLines.clear()

        unContainedMarkers.addAll(showMarkersOnMap(places = places.filter { !it.containedByRoute }))
        containedMarkers.addAll(showMarkersOnMap(places = places.filter { it.containedByRoute }))

        if (routeNodes != null)
            when(mode) {
                "foot-walking" -> for (polyline in routeNodes.mapNotNull { it.walkPolyLine }) {
                    routePolyLines.add(polyline)
                }
                "driving-car" -> for (polyline in routeNodes.mapNotNull { it.carPolyLine }) {
                    routePolyLines.add(polyline)
                }
            }


        binding.map.overlays.addAll(routePolyLines)
        binding.map.overlays.addAll(containedMarkers)

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
     * handles the change of the current [Search]'s start place change
     * when it changes the function creates a new marker for it
     * then adds it to the map's overlays
     */
    private fun showStart(startPlace: Place?) {

        if (startPlace != null) {

            val geo = GeoPoint(
                startPlace.getCoordinates().getLatitude(),
                startPlace.getCoordinates().getLongitude()
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
//_________________________________________________________________________________________________________________________
// END OF METHODS FOR MAP
//_________________________________________________________________________________________________________________________

//Methods related to route
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR ROUTE
//_________________________________________________________________________________________________________________________
    /** [handleRouteStopsChange]
     * call [showRouteData] and [showHideSearchAndRouteElementsOnRouteStopsChange]
     */
    fun handleRouteStopsChange(route: Route) {

        showRouteData(
            route = route
        )

        showHideSearchAndRouteElementsOnRouteStopsChange(
            isRouteEmpty = route.getRouteNodes().size < 2
        )
    }

    /**
     * hide or show the search UI and the route plan UI if there is/isn't a selected place for route planning
     */
    fun showHideSearchAndRouteElementsOnRouteStopsChange(isRouteEmpty: Boolean) {

        when(isRouteEmpty) {

            true -> {
                binding.searchContent.visibility = View.VISIBLE
                binding.routeInfoBar.visibility = View.GONE
            }
            false -> {
                binding.searchContent.visibility = View.GONE
                binding.routeInfoBar.visibility = View.VISIBLE
            }
        }
    }

    /** [showRouteData]
     * refresh the [routeStopsAdapter]'s list with the currently added places
     * set the calculated full duration of the route (by car/on foot)
     * as the text of the route mode selection buttons
     */
    private fun showRouteData(route: Route) {

        this.routeStops.clear()

        this.routeStops.addAll(route.getRouteNodes())

        this.routeMode = route.getTransportMode()

        this.routeStopsAdapter.mode = route.getTransportMode()

        routeStopsAdapter.notifyDataSetChanged()

        val fullWalkDuration = route.fullWalkDuration.toString() + " " + resources?.getString(R.string.duration_string)
        val fullCarDuration = route.fullCarDuration.toString() + " " + resources?.getString(R.string.duration_string)

        binding.routeSelectWalk.text =  fullWalkDuration
        binding.routeSelectCar.text =  fullCarDuration
    }

    private fun updateMapOnRouteStopSelected(coordinates: Coordinates) {

        mapController.setZoom(15.0)
        val updatedCenter = GeoPoint(coordinates.getLatitude(), coordinates.getLongitude())
        mapController.setCenter(updatedCenter)

    }

//_________________________________________________________________________________________________________________________
// END OF METHODS FOR MAP
//_________________________________________________________________________________________________________________________

//Methods related to internal fragments
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR INTERNAL FRAGMENTS
//_________________________________________________________________________________________________________________________
    private fun initDetailsFragment() {
        val tag = "PLACE_DETAILS_FRAGMENT"

        val existingFragment = childFragmentManager.findFragmentByTag(tag)

        if (existingFragment == null) {

            val fragment = FragmentPlaceDetails.newInstance()

            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace(binding.bottomSheetContainer.id, fragment, tag)
            }

        }

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
//_________________________________________________________________________________________________________________________
// END OF METHODS FOR INTERNAL FRAGMENTS
//_________________________________________________________________________________________________________________________
    private fun animateImageButton(view: View) {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.image_button_click)
        view.startAnimation(animation)
    }

    private fun handleTripButtons(isTripPlacesEmpty: Boolean){

        when (isTripPlacesEmpty){
            true -> {
                removeTripButtonListeners()
            }
            else -> {
                setupTripButtonListeners()
            }
        }
        handleTripUiChanges(isTripPlacesEmpty)

    }

//Methods for the buttons responsible for opening the fragment for saving or sharing a trip
//_________________________________________________________________________________________________________________________
// BEGINNING OF TRIP METHODS
//_________________________________________________________________________________________________________________________

    /**
     *
     */
    private fun setupTripButtonListeners() {
        binding.saveTrip.setOnClickListener { l ->

            viewModelUser.setUpdatedFrom("main")

            viewModelUser.initAddUpdateTrip(
                startPlace = viewModelMain.getStartPlace()!!,
                places = viewModelMain.getPlacesContainedByTrip()
            )
            findNavController().navigate(R.id.action_FragmentMain_to_fragmentSaveTrip)
        }
        binding.dismissTrip.setOnClickListener { l ->

            viewModelMain.clearTrip()
        }
    }

    private fun removeTripButtonListeners() {

        binding.saveTrip.setOnClickListener(null)
        binding.dismissTrip.setOnClickListener(null)
    }

    private fun handleTripUiChanges(isEmpty: Boolean){

        when(isEmpty) {
            false -> {
                binding.saveTrip.setVisibility(View.VISIBLE)
                binding.dismissTrip.setVisibility(View.VISIBLE)
            }
            true -> {
                binding.saveTrip.setVisibility(View.GONE)
                binding.dismissTrip.setVisibility(View.GONE)
            }
        }

    }

//_________________________________________________________________________________________________________________________
// END OF TRIP METHODS
//_________________________________________________________________________________________________________________________

    private fun resetUiOnStartPlaceChange(){

        binding.map.overlays.removeAll(binding.map.overlays)

        binding.map.invalidate()

        clearChips()

        dismissDialog()
    }

}