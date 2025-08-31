package com.example.features.selectedplace.presentation

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.core.ui.R
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.features.selectedplace.presentation.databinding.FragmentPlaceDetailsBinding
import com.example.features.selectedplace.presentation.models.SelectedPlaceSelectedPlacePresentationModel
import com.example.features.selectedplace.presentation.viewmodel.SelectedPlaceViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/** [FragmentPlaceDetails]
 * a [Fragment] to show important information about a [com.example.model.Place]
 */
class FragmentPlaceDetails : Fragment(){

    private var _binding: FragmentPlaceDetailsBinding? = null
    private val binding get() = _binding!!

    private var isOpen: Boolean = false
    private var isContainedByTrip: Boolean = false
    private var isContainedByRoute: Boolean = false
    private var containerState: String = "collapsed"

    private val viewModelSelectedPlace: SelectedPlaceViewModel by inject<SelectedPlaceViewModel>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FragmentLifecycle", lifecycle.currentState.toString())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSelectedPlace.selectedPlaceState.collect {

                    Log.d("LifecycleTest", "repeatOnLifecycle started collecting")

                    try {

                        when(it) {

                            is SelectedPlaceSelectedPlacePresentationModel.Default -> {

                                Log.d("selectedPlaceType", "Default")
                            }
                            is SelectedPlaceSelectedPlacePresentationModel.Selected -> {

                                Log.d("selectedPlaceType", "Selected")

                                setPlaceDetails(it)

                                view.post {
                                    handleContainerHeightMeasurement()
                                }
                            }
                        }
                    } finally {
                        Log.d("LifecycleTest", "Collecting stopped")
                    }
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSelectedPlace.tripRouteState.collect {

                    Log.d("routeButtonStateFragment", it.containedByRoute.toString())

                    isContainedByTrip = it.containedByTrip
                    isContainedByRoute = it.containedByRoute

                    handleContainerState(
                        state = containerState,
                        isContainedByTrip = it.containedByTrip,
                        isContainedByRoute = it.containedByRoute
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSelectedPlace.selectedPlaceOptions.collect {


                    showHideTripButtonOnOrigin(
                        origin = it.origin
                    )
                    
                    handleContainerState(
                        state = it.containerState,
                        isContainedByTrip = isContainedByTrip,
                        isContainedByRoute = isContainedByRoute
                    )
                }
            }
        }

        binding.placeAddRemoveTrip.setOnClickListener{ l ->

            viewModelSelectedPlace.addPlaceToTrip()

            handleContainerState(
                state = containerState,
                isContainedByTrip = isContainedByTrip,
                isContainedByRoute = isContainedByRoute)
        }

        binding.placeAddRemoveRoute.setOnClickListener { l ->

            viewModelSelectedPlace.addPlaceToRoute()

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
        fun newInstance() =
            FragmentPlaceDetails().apply {
                arguments = Bundle().apply {
                }
            }
    }

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

    fun setPlaceDetails(place: SelectedPlaceSelectedPlacePresentationModel.Selected){

        binding.placeName.text = place.name

        binding.placeAddress.visibility = View.VISIBLE
        binding.placeAddress.text = place.address.getAddressString()

        if (place.cuisine != null && place.cuisine != "unknown") {
            binding.placeCuisine.visibility = View.VISIBLE
            binding.placeCuisineText.visibility = View.VISIBLE

            binding.placeCuisine.text = place.cuisine.replace(";", "\n")

        } else {
            binding.placeCuisine.visibility = View.GONE
            binding.placeCuisineText.visibility = View.GONE
        }
        if (place.openingHours != null && !place.openingHours.equals("unknown")) {
            binding.placeOpen.visibility = View.VISIBLE
            binding.placeOpenText.visibility = View.VISIBLE

            binding.placeOpen.text = place.openingHours.replace(";", "\n")

        } else {
            binding.placeOpen.visibility = View.GONE
            binding.placeOpenText.visibility = View.GONE
        }

        if (place.charge != null && !place.charge.equals("unknown")) {
            binding.placeCharge.visibility = View.VISIBLE
            binding.placeChargeText.visibility = View.VISIBLE

            binding.placeCharge.text = place.charge.replace(";", "\n")

        } else {
            binding.placeCharge.visibility = View.GONE
            binding.placeChargeText.visibility = View.GONE
        }

    }
    fun setClosedConstraints(){
        val routeParams = binding.placeAddRemoveRoute.layoutParams as ConstraintLayout.LayoutParams
        routeParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
        routeParams.topToTop = com.example.features.selectedplace.presentation.R.id.place_title_container
        binding.placeAddRemoveRoute.layoutParams = routeParams

        val nameParams = binding.placeName.layoutParams as ConstraintLayout.LayoutParams
        nameParams.rightToLeft = com.example.features.selectedplace.presentation.R.id.place_add_remove_trip
        nameParams.rightToRight = ConstraintLayout.LayoutParams.UNSET
        binding.placeName.layoutParams = nameParams
    }
    fun setOpenedConstraints(){
        val routeParams = binding.placeAddRemoveRoute.layoutParams as ConstraintLayout.LayoutParams
        routeParams.topToTop = ConstraintLayout.LayoutParams.UNSET
        routeParams.topToBottom = com.example.features.selectedplace.presentation.R.id.place_name
        binding.placeAddRemoveRoute.layoutParams = routeParams

        val nameParams = binding.placeName.layoutParams as ConstraintLayout.LayoutParams
        nameParams.rightToRight = com.example.features.selectedplace.presentation.R.id.place_title_container
        nameParams.rightToLeft = ConstraintLayout.LayoutParams.UNSET
        binding.placeName.layoutParams = nameParams

    }
    
    fun showHideTripButtonOnOrigin(origin: String) {

        binding.placeAddRemoveTrip.visibility = View.VISIBLE
        
        if (origin.equals("inspect")) {

            binding.placeAddRemoveTrip.visibility = View.GONE
        }
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

            viewModelSelectedPlace.setMainContainerHeight(totalHeight)
        }
    }

    private fun clearBindingListeners() {
        binding.placeAddRemoveTrip.setOnClickListener(null)
        binding.placeAddRemoveRoute.setOnClickListener(null)
    }

}