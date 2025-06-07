package com.example.travel_mate.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.travel_mate.Application
import com.example.travel_mate.R
import com.example.travel_mate.data.Place
import com.example.travel_mate.databinding.FragmentPlaceDetailsBinding
import kotlinx.coroutines.launch

/** [FragmentPlaceDetails]
 * a [androidx.fragment.app.Fragment] to show important information about a [com.example.travel_mate.data.Place]
 */
class FragmentPlaceDetails : Fragment(){
    // TODO: Rename and change types of parameters
    interface PlaceDetailsListener {
        fun onTitleContainerMeasured(height: Int)
    }


    private var _binding: FragmentPlaceDetailsBinding? = null
    private val binding get() = _binding!!

    private var isOpen: Boolean = false
    private var isContainedByTrip: Boolean = false
    private var isContainedByRoute: Boolean = false
    private var containerState: String = "collapsed"

    private lateinit var place: Place/*
    private val viewModelDetails: ViewModelFragmentPlaceDetails by activityViewModels()*/
    private val viewModelMain: ViewModelMain by activityViewModels { Application.Companion.factory }

    private var listener: PlaceDetailsListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? PlaceDetailsListener
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                /*place = it.getParcelable(PLACE)!!
                isContainedByTrip = it.getBoolean(CONTAINED)*/
            //}
        }
        //viewModelMain = ViewModelProvider(this, MyApplication.factory)[ViewModelMain::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FragmentLifecycle", lifecycle.currentState.toString())

        /**
         * observe the [ViewModelMain.placeState] [kotlinx.coroutines.flow.StateFlow]
         * if there is a [Place] selected update its TextViews
         * and Buttons based on the [Place]'s data
         */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.placeState.collect {

                    Log.d("LifecycleTest", "repeatOnLifecycle started collecting")

                    try {

                        if (it.currentPlace != null) {

                            place = it.currentPlace

                            containerState = it.containerState

                            isContainedByTrip = it.currentPlace.isContainedByTrip()
                            isContainedByRoute = it.currentPlace.isContainedByRoute()

                            setPlaceDetails(it.currentPlace)

                            handleContainerState(
                                state = it.containerState,
                                isContainedByTrip = it.currentPlace.isContainedByTrip(),
                                isContainedByRoute = it.currentPlace.isContainedByRoute()
                            )

                            view.post {
                                handleContainerHeightMeasurement()
                            }
                        }
                    } finally {
                        Log.d("LifecycleTest", "Collecting stopped")
                    }
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.mainContentState.collect {

                    binding.placeAddRemoveTrip.visibility = View.VISIBLE

                    if (it.prevContents.isNotEmpty()) {

                        if (it.currentContentId == ViewModelMain.MainContent.INSPECT
                            || it.prevContents.peek() == ViewModelMain.MainContent.INSPECT
                        ) {
                            binding.placeAddRemoveTrip.visibility = View.GONE
                        }
                    }
                }
            }

        }

        /**
         * calls the [ViewModelMain.addRemovePlaceToTrip] function
         * updates the ui by calling the [handleContainerState] function
         */
        binding.placeAddRemoveTrip.setOnClickListener{ l ->

            viewModelMain.addRemovePlaceToTrip(place.uUID)

            handleContainerState(
                state = containerState,
                isContainedByTrip = isContainedByTrip,
                isContainedByRoute = isContainedByRoute)
        }

        /**
         * calls the [ViewModelMain.addRemovePlaceToRoute] function
         * updates the ui by calling the [handleContainerState] function
         */
        binding.placeAddRemoveRoute.setOnClickListener { l ->

            viewModelMain.addRemovePlaceToRoute(place.uUID)

            handleContainerState(
                state = containerState,
                isContainedByTrip = isContainedByTrip,
                isContainedByRoute = isContainedByRoute)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearBindingListeners()

        Log.d("FragmentLifecycle", "Parent/Child Fragment Destroyed")

        _binding = null  // Elkerüli a memória szivárgást
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentPlaceDetails.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*place: ClassPlace, isPlaceContainedByTrip: Boolean*/) =
            FragmentPlaceDetails().apply {
                arguments = Bundle().apply {
                    /*putParcelable(PLACE, place)
                    putBoolean(CONTAINED, isPlaceContainedByTrip)*/
                }
            }
    }

    /** [handleContainerState]
     * updates the UI based on the parameters passed
     * [state] is the [com.google.android.material.bottomsheet.BottomSheetDialog]'s
     * state that contains this fragment
     * [isContainedByTrip] tells if the specific place is contained by the [com.example.travel_mate.data.Trip] currently being planned
     * [isContainedByRoute] tells if the specific place is contained by the [com.example.travel_mate.data.Route] currently being planned
     */
    fun handleContainerState(state: String, isContainedByTrip: Boolean, isContainedByRoute: Boolean) {

        when (state) {
            "collapsed" -> {
                setClosedConstraints()
                this.isOpen = false

            }
            "expanded" -> {
                setOpenedConstraints()
                this.isOpen = true
            }

        }

        updateTripButtons(
            isContainedByTrip = isContainedByTrip,
            isOpen = isOpen
        )

        updateRouteButtons(
            isContainedByRoute = isContainedByRoute,
            isOpen = isOpen
        )

    }

    /** [setPlaceDetails]
     * update the UI elements containing the place' important information
     */
    fun setPlaceDetails(place: Place){

        binding.placeName.text = place.getName()

        if (place.getAddress()?.getAddress() != null) {
            binding.placeAddress.visibility = View.VISIBLE
            binding.placeAddress.text = place.getAddress()?.getAddress()
        } else {
            binding.placeAddress.visibility = View.GONE

        }
        if (place.getCuisine() != null && !place.getCuisine().equals("unknown")) {
            binding.placeCuisine.visibility = View.VISIBLE
            binding.placeCuisineText.visibility = View.VISIBLE

            binding.placeCuisine.text = place.getCuisine()!!.replace(";", "\n")

        } else {
            binding.placeCuisine.visibility = View.GONE
            binding.placeCuisineText.visibility = View.GONE
        }
        if (place.getOpeningHours() != null && !place.getOpeningHours().equals("unknown")) {
            binding.placeOpen.visibility = View.VISIBLE
            binding.placeOpenText.visibility = View.VISIBLE

            binding.placeOpen.text = place.getOpeningHours()!!.replace(";", "\n")

        } else {
            binding.placeOpen.visibility = View.GONE
            binding.placeOpenText.visibility = View.GONE
        }

        if (place.getCharge() != null && !place.getCharge().equals("unknown")) {
            binding.placeCharge.visibility = View.VISIBLE
            binding.placeChargeText.visibility = View.VISIBLE

            binding.placeCharge.text = place.getCharge()!!.replace(";", "\n")

        } else {
            binding.placeCharge.visibility = View.GONE
            binding.placeChargeText.visibility = View.GONE
        }

    }
    fun setClosedConstraints(){
        val routeParams = binding.placeAddRemoveRoute.layoutParams as ConstraintLayout.LayoutParams
        routeParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
        routeParams.topToTop = R.id.place_title_container
        binding.placeAddRemoveRoute.layoutParams = routeParams

        val nameParams = binding.placeName.layoutParams as ConstraintLayout.LayoutParams
        nameParams.rightToLeft = R.id.place_add_remove_trip
        nameParams.rightToRight = ConstraintLayout.LayoutParams.UNSET
        binding.placeName.layoutParams = nameParams
    }
    fun setOpenedConstraints(){
        val routeParams = binding.placeAddRemoveRoute.layoutParams as ConstraintLayout.LayoutParams
        routeParams.topToTop = ConstraintLayout.LayoutParams.UNSET
        routeParams.topToBottom = R.id.place_name
        binding.placeAddRemoveRoute.layoutParams = routeParams

        val nameParams = binding.placeName.layoutParams as ConstraintLayout.LayoutParams
        nameParams.rightToRight = R.id.place_title_container
        nameParams.rightToLeft = ConstraintLayout.LayoutParams.UNSET
        binding.placeName.layoutParams = nameParams

    }

    fun updateTripButtons(isContainedByTrip: Boolean, isOpen: Boolean) {

        when(isOpen){
            true -> {
                when(isContainedByTrip){
                    true -> {
                        binding.placeAddRemoveTrip.text = resources.getString(R.string.remove_from_trip)
                        binding.placeAddRemoveTrip.icon = ContextCompat.getDrawable(requireContext(),
                            R.drawable.ic_remove)
                    }
                    else -> {
                        binding.placeAddRemoveTrip.text = resources.getString(R.string.add_to_trip)
                        binding.placeAddRemoveTrip.icon = ContextCompat.getDrawable(requireContext(),
                            R.drawable.ic_add)
                    }
                }
            }
            else -> {

                binding.placeAddRemoveTrip.text = ""

                when(isContainedByTrip){
                    true -> {
                        binding.placeAddRemoveTrip.icon = ContextCompat.getDrawable(requireContext(),
                            R.drawable.ic_remove)
                    }
                    else -> {
                        binding.placeAddRemoveTrip.icon = ContextCompat.getDrawable(requireContext(),
                            R.drawable.ic_add)
                    }
                }
            }
        }


    }

    fun updateRouteButtons(isContainedByRoute: Boolean, isOpen: Boolean) {

        when(isOpen){
            true -> {
                when(isContainedByRoute){
                    true -> {
                        binding.placeAddRemoveRoute.text = resources.getString(R.string.remove_from_tour)
                        binding.placeAddRemoveRoute.icon = ContextCompat.getDrawable(requireContext(),
                            R.drawable.ic_remove)
                    }
                    else -> {
                        binding.placeAddRemoveRoute.text = resources.getString(R.string.add_to_tour)
                        binding.placeAddRemoveRoute.icon = ContextCompat.getDrawable(requireContext(),
                            R.drawable.ic_route_plan)
                    }
                }
            }
            else -> {

                binding.placeAddRemoveRoute.text = ""

                when(isContainedByRoute){
                    true -> {
                        binding.placeAddRemoveRoute.icon = ContextCompat.getDrawable(requireContext(),
                            R.drawable.ic_remove)
                    }
                    else -> {
                        binding.placeAddRemoveRoute.icon = ContextCompat.getDrawable(requireContext(),
                            R.drawable.ic_route_plan)
                    }
                }
            }
        }


    }

    private fun handleContainerHeightMeasurement(){
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {

            val totalHeight =
                binding.dragHandle.height + binding.placeTitleContainer.height

            viewModelMain.setFragmentContainerHeight(totalHeight)
        }
    }

    private fun clearBindingListeners() {
        binding.placeAddRemoveTrip.setOnClickListener(null)
        binding.placeAddRemoveRoute.setOnClickListener(null)
    }

}