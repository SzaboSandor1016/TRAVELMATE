package com.example.features.search.presentation

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.LinearLayout
import com.example.core.ui.R
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.features.search.presentation.databinding.FragmentSearchBinding
import com.example.features.search.presentation.models.PlaceSearchPresentationModel
import com.example.features.search.presentation.models.SearchStartSearchPresentationModel
import com.example.features.search.presentation.models.SearchStartStatePresentationModel
import com.example.navigation.OuterNavigator
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.collections.map

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSearch.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSearch : Fragment() {
    /*// TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentSearch.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSearch().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }
    data class ChipCategory(val id: Int?,val content: String,val icon: Int?,val title: Int?,var checked: Boolean?,var category: String)

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    /*private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase*/

    private val chipGroupCategories: Map<Int, List<ChipCategory>> = mapOf(
        0 to listOf(
            ChipCategory(1,"\"amenity\"=\"restaurant\"",
                null,
                R.string.restaurant, false,"restaurant"),
            ChipCategory(2,"\"amenity\"=\"cafe\"", null, R.string.cafe,false,"cafe"),
            ChipCategory(3,"\"amenity\"=\"fast_food\"",
                null,
                R.string.fast_food, false,"fast_food"),
            ChipCategory(4,"\"amenity\"=\"pub\";\"amenity\"=\"bar\"",
                null,
                R.string.pub_bar,false,"pub_bar")
        ),
        3 to listOf(
            ChipCategory(1,"\"amenity\"=\"cinema\"", null, R.string.cinema, false,"cinema"),
            ChipCategory(2,"\"amenity\"=\"theatre\"", null,
                R.string.theatre, false,"theatre"),
            ChipCategory(3,"\"amenity\"=\"nightclub\"", null, R.string.nightclub, false,"nightclub"),
            ChipCategory(4,"\"amenity\"=\"casino\"",null, R.string.casino, false,"casino")
        ), 5 to listOf(
            ChipCategory(1,"\"tourism\"=\"theme_park\"",
                null,
                R.string.adventure_park,false,"theme_park"),
            ChipCategory(2,"\"leisure\"=\"water_park\"",null,
                R.string.spa,false,"water_park"),
            ChipCategory(3,"\"leisure\"=\"beach_resort\"",null,
                R.string.beach_resort,false,"beach_resort"),
            ChipCategory(4,"\"tourism\"=\"zoo\"",null, R.string.zoo,false,"zoo")
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

    private lateinit var suggestionsAdapter: AdapterSuggestion

    private var resources: Resources? = null

    private val viewModelSearch: SearchViewModel by viewModel { parametersOf(activity as OuterNavigator) }

    //private val viewModelUser: ViewModelUser by inject<ViewModelUser>()

    private lateinit var popupView: View

    private var editingInspectTrip: Boolean = false

    //var startPlace: PlaceSearchPresentationModel = PlaceSearchPresentationModel()
    private var startPlaces: ArrayList<PlaceSearchPresentationModel> = ArrayList()
    private var suggestions: ArrayList<String> = ArrayList()

    private lateinit var dialog: Dialog

    var parametersSelected: Boolean = false

    /**
     * search for potential starting places based on the input text
     * if the length is longer or equal to 4
     * */
    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            if (s.length >= 4) {
                viewModelSearch.searchAutocomplete(s.toString())
            }
        }
        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inflater = LayoutInflater.from(context)
        popupView = inflater.inflate(com.example.features.search.presentation.R.layout.layout_popup_menu,null)

        resources = getResources()

        dialog = Dialog(popupView.context)
        dialog.setContentView(popupView)
        dialog.setCancelable(false)

        /** [com.example.travel_mate.ui.ViewModelMain.chipsState] observer
         *  observe the [viewModelSearch]'s [com.example.travel_mate.ui.ViewModelMain.chipsState]
         *  on state update
         *  - the [createChipGroupDialog] function is called
         *   if the currentChipGroup and the currentChipGroupContent != null
         *   else dismiss the [dialog]
         *  - [showHideExtendedSearch] function is called
         *  - [enableDisableMinuteSelect] function is called
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSearch.chipsState.collect {

                    if (it.currentChipGroup != null && it.currentChipGroupContent != null) {

                        createChipGroupDialog(
                            id = it.currentChipGroup,
                            chipGroupContent = it.currentChipGroupContent
                        )

                    } else {

                        dismissDialog()
                    }

                    showHideExtendedSearch(it.showExtendedSearch)

                    enableDisableMinuteSelect(it.transportModeSelected)

                    parametersSelected = it.extendedSearchSelected

                    Log.d("refresh", "refresh")
                }

            }
        }

        /** [com.example.travel_mate.ui.ViewModelMain.mainSearchState] observer
         *  observe the [viewModelSearch]'s [com.example.travel_mate.ui.ViewModelMain.mainSearchState]
         *  on state update
         *  - the [handleStartPlaceSelected] function is called
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
                viewModelSearch.searchAutocompleteState.collect {

                    Log.d("isStartPlacesEmpty", it.isEmpty().toString())

                    if (it.isNotEmpty())
                        handlePhotonObserve(
                            places = it
                        )

                    //TODO REMINDER: IT is separated now
                    /*if (it.currentPlaceUUID != null)
                        viewModelSearch.getCurrentPlaceByUUID(
                            uuid = it.currentPlaceUUID
                        )*/
                }
            }
        }

        /** [com.example.travel_mate.ui.ViewModelMain.mainStartPlaceState] observer
         *  observe the [viewModelSearch]'s [com.example.travel_mate.ui.ViewModelMain.mainStartPlaceState]
         *  on state update
         *  - call [handleStartPlaceSelected]
         */

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSearch.searchStartState.collect {

                    when(it) {

                        is SearchStartStatePresentationModel.Empty -> {/*TODO*/}
                        is SearchStartStatePresentationModel.StartSelected -> {
                            handleStartPlaceSelected(
                                startPlace = it.searchInfo
                            )
                        }
                    }

                    Log.d("refresh", "refresh")
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSearch.tripButtonState.collect {

                    Log.d("trip_buttons_visibility", "Save Trip buttons visible? $it")

                    handleTripButtons(
                        isTripPlacesEmpty = it
                    )
                }
            }
        }

        //TODO REMINDER: It is separated now do not need to observe such thing
        /** [com.example.travel_mate.ui.ViewModelMain.mainInspectTripState] observer
         *  observe the [viewModelSearch]'s [com.example.travel_mate.ui.ViewModelMain.mainInspectTripState]
         *  on state update
         *  - call [enableDisableNavigateToUserFragment]
         */
        /*viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSearch.mainInspectTripState.collect {

                    editingInspectTrip = it.editing

                    enableDisableNavigateToUserFragment(
                        editing = it.editing
                    )

                    handleInspectTripButtons(
                        editing = it.editing
                    )

                    Log.d("editing", it.editing.toString())
                }
            }
        }*/

        /**Listener to handle CLICKING THE USER BUTTON
         * navigate to the [com.example.travel_mate.ui.FragmentUser]
         * */
//_________________________________________________________________________________________________________________________
// BEGINNING OF USER BUTTON LISTENER
// _________________________________________________________________________________________________________________________
        binding.navigateToUser.setOnClickListener{ v ->

            /*val request = NavDeepLinkRequest.Builder.fromUri(
                "android-app://com.example.features/user".toUri()
            ).build()

            findNavController().navigate(request)*/

            viewModelSearch.navigateToUser()
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
            context = requireContext(),
            resource = com.example.features.search.presentation.R.layout.layout_autocomplete_item,
            objects = suggestions
        )

        binding.placeSearch.setAdapter(suggestionsAdapter)

        binding.placeSearch.setDropDownBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(),
                R.drawable.shape_autocomplete_dropdown))

        /*
        * initialize a new search
         * hide the soft input keyboard
         */
        binding.placeSearch.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                viewModelSearch.initSearchWith(startPlaces[position])

                viewModelSearch.resetAutocomplete()

                clearChips()

                dismissDialog()

                //Log.d("viewModelStartPlaceTest1", startPlaces[position].getName()!!)

                val inputMethodManager: InputMethodManager =
                    ContextCompat.getSystemService(
                        requireContext(),
                        InputMethodManager::class.java
                    ) as InputMethodManager
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
         * in [com.example.travel_mate.ui.ViewModelMain]
         * else reset the extended search attributes which are the selected transport mode and the distance in minutes
         */
        binding.openExtended.addOnCheckedChangeListener { button, isChecked ->

            clearChips()

            dismissDialog()

            if (isChecked) {

                viewModelSearch.setExtendedSearchVisible(true)

            } else {

                if (parametersSelected) {
//todo FIX if the location is turned off first then this will cause the app to crash
                    //TODO also implement function to do so
                    viewModelSearch.resetDetails(
                        allDetails = false
                    )

                }

                uncheckExtendedSearch()

                viewModelSearch.resetExtendedSearch()
            }
        }
        /**
         * transport mode selection listener
         * set the transport mode as selected
         */


        binding.selectWalk.addOnCheckedChangeListener { _ , isChecked ->

            handleTransportModeSelect(
                index = 0,
                isChecked = isChecked
            )
        }
        binding.selectCar.addOnCheckedChangeListener { _ , isChecked ->

            handleTransportModeSelect(
                index = 1,
                isChecked = isChecked
                )
        }
        /**
         * distance selection listener
         * set the distance that is in minutes as selected
        */
        binding.select15.addOnCheckedChangeListener { _ , isChecked ->

            handleMinuteSelect(
                index = 0,
                isChecked = isChecked
            )
        }

        binding.select30.addOnCheckedChangeListener { _ , isChecked ->

            handleMinuteSelect(
                index = 1,
                isChecked = isChecked
            )
        }

        binding.select45.addOnCheckedChangeListener { _ , isChecked ->

            handleMinuteSelect(
                index = 2,
                isChecked = isChecked
            )
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
         * then with the received coordinates, make a reverseGeoCode request to the [com.example.domain.datasources.PhotonRemoteDataSource]
         * if it is unchecked reset the ui, the search
         * preparing it for an other location selection or an input to the search field
         */
        binding.useLocation.addOnCheckedChangeListener{ l, isChecked ->


            viewModelSearch.resetDetails(
                allDetails = true
            )

            if (isChecked) {

                viewModelSearch.initSearchWithLocation()

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
         * are scrolled call the [com.example.travel_mate.ui.ViewModelMain.resetCurrentChipGroup] function
         */
        binding.chipGroups.setOnScrollChangeListener{ _, _, _, _, _ ->

            viewModelSearch.resetCurrentChipGroup()
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

                            viewModelSearch.resetCurrentChipGroup()

                        }else{
                            Log.d("check", "check")

                            val chipGroupContent = chipGroupCategories[groupIndex]

                            if (chipGroupContent != null) {
                                viewModelSearch.setCurrentChipGroup(l.id,chipGroupContent)
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

    }

    override fun onDestroyView() {
        super.onDestroyView()

        dismissDialog()

        binding.selectWalk.clearOnCheckedChangeListeners()
        binding.selectCar.clearOnCheckedChangeListeners()
        binding.select15.clearOnCheckedChangeListeners()
        binding.select30.clearOnCheckedChangeListeners()
        binding.select45.clearOnCheckedChangeListeners()

        resources?.flushLayoutCache()

        _binding = null
    }

    //Methods related to the search text field
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR SEARCH TEXT FIELD
//_________________________________________________________________________________________________________________________

    /** [handleStartPlaceSelected]
     *  on start place change in the [com.example.travel_mate.ui.ViewModelMain] caused by an update in [com.example.data.repositories.SearchRepositoryImpl]
     *  first remove all content from the map
     *  then call [showStart], [setStartTextField]
     *  and set the visibility of the category chips.
     */

    private fun handleStartPlaceSelected(startPlace: SearchStartSearchPresentationModel) {

        resetUiOnStartPlaceChange()

        setStartTextField(
            startPlace = startPlace
        )

        binding.chipGroupChips.visibility = View.VISIBLE

        /*if (startPlace != null) {


        } else {

            binding.chipGroupChips.visibility = View.GONE
        }*/
    }

    private fun handleStartPlaceUnSelected() {

        resetUiOnStartPlaceChange()

        binding.chipGroupChips.visibility = View.GONE

        resetSearchField()
        /*if (startPlace != null) {


        } else {

            binding.chipGroupChips.visibility = View.GONE
        }*/
    }

    /** [setStartTextField]
     * set the search field's hint as the current start place's name + address
     * on search place change from [com.example.travel_mate.ui.ViewModelMain] and [com.example.data.repositories.SearchRepositoryImpl]
     */
    private fun setStartTextField(startPlace: SearchStartSearchPresentationModel) {

        resetSearchField()

        val stringBuilder = StringBuilder()

        stringBuilder.append(startPlace.name.toString() + " ")
        stringBuilder.append(startPlace.getAddressString())

        binding.placeSearch.setText("")
        binding.placeSearch.hint = stringBuilder.toString()

        /*if (startPlace != null) {

            //this.startPlace = startPlace



        } else {

            resetSearchField()
        }*/
    }

    /** [resetSearchField]
     * set the default hint for the search field
     */
    private fun resetSearchField() {

        binding.placeSearch.hint = resources?.getString(R.string.search_place)
        binding.placeSearch.setText("")
    }

    /** [handlePhotonObserve]
     *  handle the potential start places change received from the autocomplete service
     *  refresh the text view's adapter with the name + address string received
     */
    private fun handlePhotonObserve(places: List<PlaceSearchPresentationModel>){

        startPlaces.clear()
        suggestions.clear()

        startPlaces.addAll(places)

        suggestions.addAll( startPlaces.map {

            val suggestion = StringBuilder()
            suggestion.append(it.name).append(" ")
            suggestion.append(it.address.getAddressString())

            suggestion.toString()
        })

        suggestionsAdapter = AdapterSuggestion(
            context = requireContext(),
            resource = com.example.features.search.presentation.R.layout.layout_autocomplete_item,
            objects = suggestions
        )

        binding.placeSearch.setAdapter(suggestionsAdapter)

        suggestionsAdapter.notifyDataSetChanged()
    }

    /** [resetUiOnStartPlaceChange]
     * reset the UI elements related to searching when the start[com.example.model.Place] is changed
     */
    private fun resetUiOnStartPlaceChange(){

        clearChips()

        dismissDialog()
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
     *  [viewModelSearch]'s state attributes
     */
    private fun showHideExtendedSearch(showExtendedSearch: Boolean) {

        if (showExtendedSearch) {

            binding.transportGroupLayout.visibility = View.VISIBLE
            binding.distanceGroup.visibility = View.VISIBLE

            binding.chipGroups.visibility = View.GONE

        }else {

            binding.transportGroupLayout.visibility = View.GONE
            binding.distanceGroup.visibility = View.GONE

            binding.chipGroups.visibility = View.VISIBLE
        }

        dismissDialog()
    }

    private fun enableDisableMinuteSelect(transportModeSelected: Boolean) {

        if (transportModeSelected){

            binding.select15.isEnabled = true
            binding.select30.isEnabled = true
            binding.select45.isEnabled = true
        } else {

            binding.select15.isChecked = false
            binding.select30.isChecked = false
            binding.select45.isChecked = false

            binding.select15.isEnabled = false
            binding.select30.isEnabled = false
            binding.select45.isEnabled = false
        }
    }

    private fun uncheckExtendedSearch() {

        binding.selectWalk.isChecked = false
        binding.selectCar.isChecked = false

        binding.select15.isChecked = false
        binding.select30.isChecked = false
        binding.select45.isChecked = false

        binding.select15.isEnabled = false
        binding.select30.isEnabled = false
        binding.select45.isEnabled = false
    }

    //TODO update these to use default walk if not selected and default (15) minutes if not selected
    private fun handleTransportModeSelect(index: Int, isChecked: Boolean) {

        if (isChecked) {

            viewModelSearch.setSearchTransportMode(index)

            when (index) {

                0 -> {
                    binding.selectCar.isChecked = false
                }

                1 -> {
                    binding.selectWalk.isChecked = false
                }
            }
        } else {

            viewModelSearch.setSearchTransportMode(-1);

            binding.selectWalk.isChecked = false
            binding.selectCar.isChecked = false
        }
    }

    private fun handleMinuteSelect(index: Int, isChecked: Boolean) {

        if (isChecked) {

            viewModelSearch.setSearchMinute(index)

            //viewModelSearch.setExtendedSearchSelected(true)

            when (index) {

                0 -> {

                    binding.select30.isChecked = false
                    binding.select45.isChecked = false
                }

                1 -> {

                    binding.select15.isChecked = false
                    binding.select45.isChecked = false
                }

                2 -> {

                    binding.select15.isChecked = false
                    binding.select30.isChecked = false
                }
            }
        } else {

            binding.select15.isChecked = false
            binding.select30.isChecked = false
            binding.select45.isChecked = false
        }
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
                setBackgroundDrawable(
                    ResourcesCompat.getDrawable(/*fragmentMain.*/resources!!,
                        R.color.transparent, context.theme))
                setDimAmount(0f)
            }

            val layout = popupView.findViewById<LinearLayout>(com.example.features.search.presentation.R.id.popup_menu_content)

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
    private fun createSearchChip(chipCategory: ChipCategory): Chip {

        val chip = this.layoutInflater.inflate(com.example.features.search.presentation.R.layout.item_chip_category,null,false) as Chip

        chip.id = chipCategory.id!!
        chip.text = resources?.getString(chipCategory.title!!)
        chip.isClickable = true
        chip.isCheckable = true
        chip.isChecked = chipCategory.checked!!
        chip.isChipIconVisible = !chipCategory.checked!!

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

    private fun handleCategoryFiltererChipCheckChange(content: String, category: String, checked: Boolean, id: Int? = null){

        if (checked){
            handleCategoryFilterChipChecked(content,category)
            if (id != null)
                viewModelSearch.addSelectedChip(id)
        }else{
            handleCategoryFilterChipUnchecked(category)
            if (id != null)
                viewModelSearch.removeSelectedChip(id)
        }

    }

    private fun handleCategoryFilterChipChecked(content: String, category: String){

        searchChipCategory(content,category)
    }

    /** [handleCategoryFilterChipUnchecked]
     * remove the places with the category given in parameter
     */
    private fun handleCategoryFilterChipUnchecked(category: String){

        viewModelSearch.removePlacesByCategory(category)
    }

    /** [searchChipCategory]
     *  make a search by the selected category and the current start place's coordinates
     */
    private fun searchChipCategory(content: String, category: String){

        viewModelSearch.searchNearbyPlacesBy(
            content = content,
            /*startPlace.getCoordinates().getLatitude().toString(),
            startPlace.getCoordinates().getLongitude().toString(),
            startPlace.getAddress()?.getCity()!!,*/
            category = category
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

    //TODO REMINDER: INSPECTING IS NOW INDEPENDENT WILL NOT BE NECESSARY
    // BUT LEFT HERE BY THE SAME CONSIDERATION AS OTHER INSPECT TRIP METHODS
    /*private fun handleInspectTripButtons(editing: Boolean){

        when (editing){
            false -> {
                removeInspectTripButtonListeners()
            }
            true -> {
                setupInspectTripButtonListeners()
            }
        }
        handleInspectTripUiChanges(editing)

    }*/

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

    /*private fun enableDisableNavigateToUserFragment(editing: Boolean) {

        binding.navigateToUser.isEnabled = !editing
    }*/

    //TODO NOTE SEPARATED BUT LEFT HERE FOR FUTURE REFERENCE OR IN CASE OF CONFUSION
    /*private fun setupInspectTripButtonListeners() {

        binding.saveInspectTrip.setOnClickListener { l ->

            viewModelUser.saveTripWithUpdatedPlaces(
                startPlace = viewModelSearch.getStartPlace()!!,
                places = viewModelSearch.getPlacesContainedByTrip()
            )
        }

        binding.dismissInspectTrip.setOnClickListener { l ->

            viewModelSearch.cancelEditInspected()
        }
    }*/
    //TODO NOTE Like the one above
/*
    private fun removeInspectTripButtonListeners() {

        binding.saveInspectTrip.setOnClickListener(null)
        binding.dismissInspectTrip.setOnClickListener(null)
    }*/

    /**
     *
     */
    private fun setupTripButtonListeners() {

        binding.saveTrip.setOnClickListener { l ->

            //TODO REMINDER: I think this will not be necessary
            //viewModelUser.setUpdatedFrom("main")
            //TODO this one neither
            /*viewModelUser.initAddUpdateTrip(
                startPlace = viewModelSearch.getStartPlace()!!,
                places = viewModelSearch.getPlacesContainedByTrip()
            )*/

            viewModelSearch.navigateToSave()
        }

        binding.updateTrip.setOnClickListener { _ ->

            viewModelSearch.navigateToUser()
        }

        binding.dismissTrip.setOnClickListener { l ->

            viewModelSearch.clearPlacesAddedToTrip()
        }
    }

    private fun removeTripButtonListeners() {

        binding.saveTrip.setOnClickListener(null)
        binding.updateTrip.setOnClickListener(null)
        binding.dismissTrip.setOnClickListener(null)
    }

    /*private fun handleInspectTripUiChanges(editing: Boolean){

        when(editing) {
            true -> {
                binding.saveInspectTrip.setVisibility(View.VISIBLE)
                binding.dismissInspectTrip.setVisibility(View.VISIBLE)
            }
            false -> {
                binding.saveInspectTrip.setVisibility(View.GONE)
                binding.dismissInspectTrip.setVisibility(View.GONE)
            }
        }

    }*/

    private fun handleTripUiChanges(isEmpty: Boolean){

        binding.tripButtons.setVisibility(View.GONE)
        //binding.dismissTrip.setVisibility(View.GONE)

        if (!isEmpty) {

            binding.tripButtons.setVisibility(View.VISIBLE)
            //binding.dismissTrip.setVisibility(View.VISIBLE)
        }
    }

//_________________________________________________________________________________________________________________________
// END OF TRIP METHODS
//_________________________________________________________________________________________________________________________
}