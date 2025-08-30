package com.example.features.inspect.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.features.inspect.presentation.databinding.FragmentInspectTripBinding
import com.example.features.inspect.presentation.models.DayOfTripInfoInspectTripPresentationModel
import com.example.features.inspect.presentation.models.InspectTripInfoUIModel
import com.example.features.inspect.presentation.models.PlaceInfoInspectTripPresentationModel
import com.example.features.inspect.presentation.viewmodel.InspectTripViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentInspectTrip.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentInspectTrip : Fragment() {
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
         * @return A new instance of fragment FragmentInspectTrip.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentInspectTrip().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }

    private var _binding: FragmentInspectTripBinding? = null
    val binding get() = _binding!!

    private val days = ArrayList<DayOfTripInfoInspectTripPresentationModel>()

    private lateinit var adapterDays: AdapterDayOfInspectTripRecyclerView

    private val inspectTripViewModel: InspectTripViewModel by inject<InspectTripViewModel>()

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
        _binding = FragmentInspectTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterDays = AdapterDayOfInspectTripRecyclerView(this.days)

        binding.daysOfInspectTripRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.daysOfInspectTripRecyclerView.adapter = this.adapterDays

        adapterDays.addOnItemClickListener(object :
            AdapterDayOfInspectTripRecyclerView.OnItemClickListener {

            override fun onItemClick(position: Int) {
                inspectTripViewModel.setSelectedDayOfInspectedTrip(position)
            }

        })

        /** [com.example.travel_mate.ui.ViewModelMain.mainInspectTripState] observer
         *  observe the [inspectTripViewModel]'s [com.example.travel_mate.ui.ViewModelMain.mainInspectTripState]
         *  on state update
         *  - call [handleInspectedTripChange]
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                inspectTripViewModel.inspectTripInfoState.collect {

                    when(it.inspectedTrip) {
                        is InspectTripInfoUIModel.Default -> {
                            removeInspectedTripListeners()
                        }
                        is InspectTripInfoUIModel.Inspected -> {
                            handleInspectedTripChange(
                                title = it.inspectedTrip.title,
                                createdBy = it.inspectedTrip.creatorUsername,
                                startLabel = getTripStartLabel(it.inspectedTrip.startInfo)
                            )

                            updateDaysOfInspectTrip(
                                days = it.inspectedTrip.daysInfo
                            )

                            //TODO CREATE UI ELEMENTS FOR SHOWING TRIP DAYS
                            // done, HANDLE DAYS CLICK
                        }
                    }

                    /*val start = if(it.inspectedTrip != null) getTripStartLabel(it.inspectedTrip.startPlace)
                                else ""

                    if (it.inspectedTrip != null) {

                        inspectTripViewModel.setupNewTrip(
                            startPlace = it.inspectedTrip.startPlace,
                            places = it.inspectedTrip.places
                        )
                    } else {

                        inspectTripViewModel.resetDetails(
                            allDetails = true
                        )
                    }

                    handleInspectedTripChange(
                        editing = it.editing,
                        start = start,
                        inspectedTripIdentifier = it.inspectedTripIdentifier
                    )*/
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    //Methods for the buttons responsible for opening the fragment for saving or sharing a trip
//_________________________________________________________________________________________________________________________
// BEGINNING OF TRIP METHODS
//_________________________________________________________________________________________________________________________

    private fun getTripStartLabel(startPlace: PlaceInfoInspectTripPresentationModel): String {

        return startPlace.name.toString() +
                " ," +
                startPlace.addressInfo.toString()
    }

    private fun handleInspectedTripChange(
        //editing: Boolean,
        //start: String?,
        //inspectedTripIdentifier: InspectTripTripInfoInspectTripDomainModel.Inspected
        title: String?,
        createdBy: String?,
        startLabel: String
    ) {

        setupInspectedTripListeners()

        showInspectedTripData(
            createdBy = createdBy,
            title = title,
            startLabel = startLabel
        )

        //Log.d("editing", editing.toString())

        //if (inspectedTripIdentifier == null || editing) {

        /*} else {

            *//*showHideEditInspectedTripButton(
                permissionToUpdate = inspectedTripIdentifier.permissionToUpdate
            )*//*




            showInspectedTripData(
                startPlace = start!!,
                inspectedTripIdentifier = inspectedTripIdentifier
            )
        }*/
    }

    /*private fun showHideEditInspectedTripButton(permissionToUpdate: Boolean) {

        if (permissionToUpdate) {

            binding.editTripPlaces.visibility = View.VISIBLE
        } else {

            binding.editTripPlaces.visibility = View.GONE
        }
    }*/

    private fun updateDaysOfInspectTrip(days: List<DayOfTripInfoInspectTripPresentationModel>) {

        this.days.clear()

        this.days.addAll(days)

        this.adapterDays.notifyDataSetChanged()
    }

    private fun showInspectedTripData(
        createdBy: String?,
        title: String?,
        startLabel: String
    ) {

        binding.tripCreatorUsername.setText(createdBy)

        binding.tripTitle.setText(title)

        binding.tripStart.setText(startLabel)
    }

    private fun setupInspectedTripListeners() {

        /*binding.editTripPlaces.setOnClickListener { l ->

            inspectTripViewModel.editInspectedTrip()
        }*/

        binding.dismissInspectTrip.setOnClickListener { l ->

            inspectTripViewModel.endInspecting()

            //TODO REMINDER it was the search's resetDetails
            /*inspectTripViewModel.resetDetails(
                allDetails = true
            )*/

            //TODO NOTE: the main will handle the returning by itself
            //inspectTripViewModel.returnToPrevContent()
        }
    }
    private fun removeInspectedTripListeners() {

        //binding.editTripPlaces.setOnClickListener(null)

        binding.dismissInspectTrip.setOnClickListener(null)
    }

//_________________________________________________________________________________________________________________________
// END OF TRIP METHODS
//_________________________________________________________________________________________________________________________

}