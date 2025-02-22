package com.example.gtk_maps

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.example.gtk_maps.databinding.FragmentPlaceDetailsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private const val PLACE = "place"
private const val CONTAINED = "contained"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPlaceDetails.newInstance] factory method to
 * create an instance of this fragment.
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

    private lateinit var place: ClassPlace
    private val viewModelDetails: ViewModelFragmentPlaceDetails by activityViewModels()
    private val viewModelTrip: ViewModelTrip by activityViewModels()

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
                place = it.getParcelable(PLACE)!!
                isContainedByTrip = it.getBoolean(CONTAINED)
            //}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPlaceDetails(place)

        updateTripButtons(isContainedByTrip,isOpen)

        view.post {

            handleContainerHeightMeasurement()

        }

        viewModelDetails.containerState.observe(viewLifecycleOwner) { state ->

            when (state) {
                "collapsed" -> {
                    setClosedConstraints()
                    this.isOpen = false
                    updateTripButtons(isContainedByTrip,isOpen)
                }
                "expanded" -> {
                    setOpenedConstraints()
                    this.isOpen = true
                    updateTripButtons(isContainedByTrip,isOpen)
                }
            }

        }
        viewModelTrip.tripPlaces.observe(viewLifecycleOwner) { places ->

            isContainedByTrip = viewModelTrip.isPlaceContainedByTrip(place)
            updateTripButtons(isContainedByTrip,isOpen)
        }


        binding.placeAddRemoveTrip.setOnClickListener{ l ->

            when (isContainedByTrip){
                true -> {
                    viewModelTrip.removePlaceFromTrip(place)
                }
                else -> {
                    viewModelTrip.addPlaceToTrip(place)
                }
            }

        }
        binding.placeAddRemoveRoute.setOnClickListener { l ->
            //var place = uiController?.findMostRecentPlace()
            //TODO( "Implement route planning")
        }

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
        fun newInstance(place: ClassPlace, isPlaceContainedByTrip: Boolean) =
            FragmentPlaceDetails().apply {
                arguments = Bundle().apply {
                    putParcelable(PLACE, place)
                    putBoolean(CONTAINED, isPlaceContainedByTrip)
                }
            }
    }

    fun updatePlaceDetails(place: ClassPlace, isPlaceContainedByTrip: Boolean){
        if (this.place == place && this.isContainedByTrip == isPlaceContainedByTrip) {
            return // Nincs változás, nincs szükség frissítésre
        }

        this.place = place

        setPlaceDetails(this.place)

        isContainedByTrip = isPlaceContainedByTrip

        updateTripButtons(isPlaceContainedByTrip,false)

        setClosedConstraints()

        view?.post {
            handleContainerHeightMeasurement()
        }
    }


    fun setPlaceDetails(place: ClassPlace){

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
                        binding.placeAddRemoveTrip.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_remove)
                    }
                    else -> {
                        binding.placeAddRemoveTrip.text = resources.getString(R.string.add_to_trip)
                        binding.placeAddRemoveTrip.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_add)
                    }
                }
            }
            else -> {

                binding.placeAddRemoveTrip.text = ""

                when(isContainedByTrip){
                    true -> {
                        binding.placeAddRemoveTrip.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_remove)
                    }
                    else -> {
                        binding.placeAddRemoveTrip.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_add)
                    }
                }
            }
        }


    }

    private fun handleContainerHeightMeasurement(){
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val totalHeight = binding.dragHandle.height + binding.placeTitleContainer.height
            listener?.onTitleContainerMeasured(totalHeight)
        }
    }

    private fun clearBindingListeners() {
        binding.placeAddRemoveTrip.setOnClickListener(null)
        binding.placeAddRemoveRoute.setOnClickListener(null)
    }

}