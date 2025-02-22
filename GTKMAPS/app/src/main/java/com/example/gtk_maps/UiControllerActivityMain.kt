package com.example.gtk_maps

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.example.gtk_maps.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.utils.BonusPackHelper
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class ActivityMainUIController(
    private val context: Context,
    private val activity: ActivityMain,
    private val viewModelOverpass: ViewModelOverpass,
    private val viewModelMain: ViewModelMain,
    private val binding: ActivityMainBinding
) {

    private val chipGroupCategories: Map<Int, Array<ChipCategory>> = mapOf(
        0 to arrayOf(
            ChipCategory(1,"\"amenity\"=\"restaurant\"",R.drawable.ic_restaurant,R.string.restaurant, false,"restaurant"),
            ChipCategory(2,"\"amenity\"=\"cafe\"", R.drawable.ic_cafe,R.string.cafe,false,"cafe"),
            ChipCategory(3,"\"amenity\"=\"fast_food\"",R.drawable.ic_fast_food,R.string.fast_food, false,"fast_food"),
            ChipCategory(4,"\"amenity\"=\"pub\";\"amenity\"=\"bar\"",R.drawable.ic_bar,R.string.pub_bar,false,"pub_bar")
        ),
        3 to arrayOf(
            ChipCategory(1,"\"amenity\"=\"cinema\"", R.drawable.ic_cinema,R.string.cinema, false,"cinema"),
            ChipCategory(2,"\"amenity\"=\"theatre\"", R.drawable.ic_theatre,R.string.theatre, false,"theatre"),
            ChipCategory(3,"\"amenity\"=\"nightclub\"", R.drawable.ic_nightclub, R.string.nightclub, false,"nightclub"),
            ChipCategory(4,"\"amenity\"=\"casino\"",R.drawable.ic_casino, R.string.casino, false,"casino")
        ), 5 to arrayOf(
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

    private var dialog: Dialog? = null
    private var prevChip: Chip? = null

    private val mapController: IMapController = binding.map.controller
    private val startMarker: Marker = Marker(binding.map)
    private var markerClusterer: RadiusMarkerClusterer?= null
    private val categoryManager: ClassCategoryManager = ClassCategoryManager(context)
    private var allMarkers: ArrayList<Marker> = ArrayList()

    init {
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        mapController.setZoom(15.0)
        val firstPoint = GeoPoint(47.09327, 17.91149)
        mapController.setCenter(firstPoint)
        binding.map.setMultiTouchControls(true)

        startMarker.position = firstPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        binding.map.overlays.add(startMarker)
        startMarker.icon = ResourcesCompat.getDrawable(activity.resources,R.drawable.ic_start_marker, context.theme)

        binding.map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                //if (!isNavi!!) checkBoundingBox(allMarkers!!) else checkBoundingBox(routeMarkers!!)
                checkBoundingBox(allMarkers)
                return true
            }

            override fun onZoom(event: ZoomEvent): Boolean {
                //if (!isNavi!!) checkBoundingBox(allMarkers!!) else checkBoundingBox(routeMarkers!!)
                checkBoundingBox(allMarkers)
                return true
            }
        })

        binding.chipGroups.setOnScrollChangeListener{ _, _, _, _, _ ->

            dialog?.dismiss()
            dialog = null

            prevChip?.isChecked = false

        }


        for (groupChip in binding.chipGroupChips.children){

            if (groupChip is Chip) {

                groupChip.setOnCheckedChangeListener{ l, isChecked ->

                    val groupIndex = binding.chipGroupChips.indexOfChild(binding.chipGroupChips.findViewById(l.id))

                    if (groupIndex in chipGroupCategories.keys){

                        if (!isChecked){
                            if (dialog!=null) {
                                dialog!!.dismiss()
                                dialog = null
                            }
                        }else{

                            prevChip = groupChip

                            val chipGroupContent = chipGroupCategories[groupIndex]

                            createChipGroupDialog(groupChip,chipGroupContent)

                        }


                    }else if (groupIndex == 1){

                        handleCategoryFiltererChipCheckChange(accommodationChip.content,accommodationChip.category, isChecked)

                    }else if (groupIndex == 2){

                        handleCategoryFiltererChipCheckChange(exhibitionChip.content,exhibitionChip.category, isChecked)

                    }else {

                        handleCategoryFiltererChipCheckChange(landmarkChip.content,landmarkChip.category, isChecked)

                    }


                }
            }
        }

    }

    fun searchChipCategory(content: String, category: String){

        val url = when(viewModelMain.getCalculatedDistance()){

            null -> getNearbyUrl(
                content,
                viewModelMain.getStartPlace()!!.getAddress()!!.getCity()!!
            )

            else -> getNearbyUrl(
                content,
                viewModelMain.getStartPlace()?.getCoordinates()?.getLatitude()
                    .toString(),
                viewModelMain.getStartPlace()?.getCoordinates()?.getLongitude()
                    .toString(),
                viewModelMain.getCalculatedDistance()!!
            )
        }

        viewModelOverpass.searchOverpass(url,category)
    }



    fun prepareForNewSearch(startPlace: ClassPlace){

        showStart(startPlace)

        resetSearchParameters()
    }

    fun showMarkersOnMap(places: ArrayList<ClassPlace>){

        val mapControllerOnResume = binding.map.controller
        mapControllerOnResume.setZoom(15.0)

        val startPlace = viewModelMain.getStartPlace()

        showStart(startPlace)

        for (place in places){
            val marker: Marker = Marker(binding.map)
            val position = GeoPoint(place.getCoordinates()?.getLatitude()!!, place.getCoordinates()?.getLongitude()!!)
            marker.position = position
            marker.icon= categoryManager.getMarkerIcon(place.getCategory()!!)

            //operations related to the visibility check of the place's details
            //val markerState = MarkerState(place, false)
            //marker.relatedObject = markerState

            marker.relatedObject = place

            marker.setOnMarkerClickListener{ m, mapView ->

                //related to the data class down below
                //val state = m.relatedObject as? MarkerState
                val relatedPlace = m.relatedObject as ClassPlace

                activity.initDetailsFragment(relatedPlace)

                /*if (state?.isDetailsVisible!!){

                    //activity.setPlaceDetails(state.isDetailsVisible,place)

                    state.isDetailsVisible = false
                }else {

                    var placeOfMarker = state.place

                    *//*activity.setPlaceDetails(state.isDetailsVisible,placeOfMarker)

                    activity.setClosedConstraints()

                    activity.updateTripButtons(false, placeOfMarker)*//*

                    state.isDetailsVisible = true
                }*/

                true
            }

            Log.d("test3",place.getName()!!)

            allMarkers.add(marker)

        }


        checkBoundingBox(allMarkers)
    }

    private fun checkBoundingBox(markers: ArrayList<Marker>) {
        setupMarkerClusterer(context,binding.map)
        val boundingBox = binding.map.boundingBox
        for (marker in markers) {
            if (boundingBox.contains(marker.position)) {
                markerClusterer!!.add(marker)
            }
        }
        binding.map.overlays.add(markerClusterer)
    }

    private fun setupMarkerClusterer(context: Context, map: MapView) {
        if (markerClusterer != null) map.overlays.remove(markerClusterer)
        markerClusterer = RadiusMarkerClusterer(context)
        val clusterIcon = BonusPackHelper.getBitmapFromVectorDrawable(context, R.drawable.ic_other_marker)
        markerClusterer!!.setIcon(clusterIcon)
        markerClusterer!!.setRadius(300)
        markerClusterer!!.setMaxClusteringZoomLevel(14)
    }

    private fun showStart(startPlace: ClassPlace?) {

        binding.map.overlays.removeAll(binding.map.overlays)
        allMarkers.clear()

        val geo = GeoPoint(startPlace!!.getCoordinates()?.getLatitude()!!, startPlace.getCoordinates()?.getLongitude()!!)
        mapController.setCenter(geo)
        val start = Marker(binding.map)
        start.position = geo
        start.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        binding.map.overlays.add(start)
        start.icon = ResourcesCompat.getDrawable(context.resources,R.drawable.ic_start_marker, context.theme)
    }

    fun handleTripUiChanges(isEmpty: Boolean){

        when(isEmpty) {
            true -> binding.tripChips.visibility = View.GONE
            else -> binding.tripChips.visibility = View.VISIBLE
        }

    }

    fun handleUiChangesForSave(){
        activity.initSaveFragment()
    }

    //Completely useless functions at the moment but it could be useful in the future / related to the visibility of
    //the container of the place's details
    /*fun updateTripButtons(isOpen: Boolean){

        val place = findMostRecentPlace()

        activity.updateTripButtons(isOpen, place!!)
    }*/

    /*fun findMostRecentPlace(): ClassPlace? {
        var place: ClassPlace? = null
        for (marker in allMarkers) {
            val markerState = marker.relatedObject as? MarkerState
            if (markerState?.isDetailsVisible!!) place = markerState.place
        }
        return place
    }*/

    private fun createChipGroupDialog(groupChip: Chip, chipGroupContent :Array<ChipCategory>?){
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.layout_popup_menu,null)

        dialog = Dialog(context)
        dialog?.setContentView(popupView)
        dialog?.setCancelable(false)

        groupChip.post {
            // Get chip position on screen
            val location = IntArray(2)
            groupChip.getLocationOnScreen(location)
            val chipHeight = groupChip.height * 0.5

            // Position the dialog under the chip
            dialog?.window?.apply {
                val params = this.attributes
                params.gravity = Gravity.TOP or Gravity.START
                params.x = location[0] // Set X position to chip's left edge
                params.y = (location[1]+chipHeight).toInt() // Set Y position just below the chip
                this.attributes = params
                setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                setBackgroundDrawable(ResourcesCompat.getDrawable(activity.resources,R.color.transparent, context.theme))
                setDimAmount(0f)
            }

            val layout = popupView.findViewById<LinearLayout>(R.id.popup_menu_content)

            for (content in chipGroupContent!!){

                val chip = createSearchChip(content)

                layout.addView(chip)

            }
        }


        dialog?.show()
    }

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
            this.chipBackgroundColor = resources.getColorStateList(R.color.color_filter_button_active, context.theme)
            isChipIconVisible = !chipCategory.checked!!
        }

        chip.setOnCheckedChangeListener{ _, isChecked ->

            chip.isChipIconVisible = !isChecked

            if(!isChecked){
                chipCategory.checked = false

                handleCategoryFiltererChipCheckChange(chipCategory.content,chipCategory.category,
                    chipCategory.checked!!
                )
            }else{
                chipCategory.checked = true

                handleCategoryFiltererChipCheckChange(chipCategory.content,chipCategory.category,
                    chipCategory.checked!!)
            }
        }
        return chip
    }

    private fun handleCategoryFiltererChipCheckChange(content: String, category: String, checked: Boolean){

        if (checked){
            handleCategoryFilterChipChecked(content,category)
        }else{
            handleCategoryFilterChipUnchecked(category)
        }

    }

    private fun handleCategoryFilterChipChecked(content: String, category: String){

        searchChipCategory(content,category)
    }

    private fun handleCategoryFilterChipUnchecked(category: String){

        viewModelMain.removePlacesByCategory(category)
    }

    fun resetUiOnLocationClicked(){

        binding.placeSearch.setText("")

        resetSearchParameters()
    }

    fun handleUiChangesForExtendedSearch(isChecked: Boolean) {

        if (isChecked) {
            handleExtendedSearchChecked()
        }else {
            handleExtendedSearchUnchecked()
        }

        binding.distanceGroup.isEnabled = false

        clearDialog()
    }

    private fun handleExtendedSearchChecked(){
        binding.transportGroup.visibility = View.VISIBLE
        binding.distanceGroup.visibility = View.VISIBLE

        binding.chipGroups.visibility = View.GONE
    }

    private fun  handleExtendedSearchUnchecked(){
        binding.transportGroup.visibility = View.GONE
        binding.distanceGroup.visibility = View.GONE

        binding.transportGroup.clearChecked()
        binding.distanceGroup.clearChecked()

        binding.chipGroups.visibility = View.VISIBLE
    }

    fun updateUiOnTransportModeSelected(hasChecked: Boolean){

        if (hasChecked){
            binding.distanceGroup.isEnabled = true
        } else {
            binding.distanceGroup.clearChecked()
            binding.distanceGroup.isEnabled = false

        }
    }

    fun handleMinuteChanged(){

        binding.transportGroup.visibility = View.GONE
        binding.distanceGroup.visibility = View.GONE

        binding.chipGroups.visibility = View.VISIBLE
    }

    fun resetSearchParameters() {

        showMarkersOnMap(ArrayList())

        resetSearchedCategories()

    }
    private fun resetSearchedCategories(){

        chipGroupCategories.values.flatMap { it.asList() }.forEach { it.checked = false }

        listOf(accommodationChip, landmarkChip, exhibitionChip).forEach { it.checked = false }

    }


    private fun getNearbyUrl(content: String, lat: String, lon: String, dist: Double): String{

        val splitContent: List<String> = content.split(";")
        var fullString = ""

        for (string in splitContent) {

            var baseString = "nwr[;;](around:dist,startLat,startLong);"

            baseString = baseString.replace(";;", string, true)
            fullString += baseString
        }

        var baseurl = "[out:json];($fullString);out center;";

        baseurl = baseurl.replace("dist", dist.toString());
        baseurl = baseurl.replace("startLat", lat);
        baseurl = baseurl.replace("startLong", lon);

        return  baseurl;
    }
    private fun getNearbyUrl(content: String, city: String): String{

        val splitContent: List<String> = content.split(";")
        var fullString = ""
        for (string in splitContent) {

            var baseString = "nwr[;;][\"addr:city\"=\"$city\"];"

            baseString = baseString.replace(";;", string, true)

            fullString += baseString
        }

        val baseurl = "[out:json];($fullString);out center;";

        return  baseurl;
    }

    fun clearDialog(){
        if (dialog!=null) {
            dialog?.dismiss()
            dialog = null
            binding.chipGroupChips.clearCheck()
        }
    }



    //Currently unnecessary data class since the visibility is not used anywhere
    //TODO if the visibility would be necessary anywhere in the future,
    // uncomment the related lines in the findMostRecentPlace
    // and the showMarkersOnMap functions
//data class MarkerState(val place: ClassPlace, var isDetailsVisible: Boolean)

    data class ChipCategory(val id: Int?,val content: String,val icon: Int?,val title: Int?,var checked: Boolean?,var category: String)
}