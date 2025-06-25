package com.example.travel_mate.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.travel_mate.Application
import com.example.travel_mate.data.Place
import com.example.travel_mate.data.TripRepositoryImpl
import com.example.travel_mate.databinding.FragmentInspectTripBinding
import com.example.travel_mate.ui.ViewModelMain.MainContent
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
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

    private val viewModelMain: ViewModelMain by inject<ViewModelMain>()

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

        /** [ViewModelMain.mainInspectTripState] observer
         *  observe the [viewModelMain]'s [ViewModelMain.mainInspectTripState]
         *  on state update
         *  - call [handleInspectedTripChange]
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.mainInspectTripState.collect {

                    val start = if(it.inspectedTrip != null) getTripStartLabel(it.inspectedTrip.startPlace)
                                else ""

                    if (it.inspectedTrip != null) {

                        viewModelMain.setupNewTrip(
                            startPlace = it.inspectedTrip.startPlace,
                            places = it.inspectedTrip.places
                        )
                    } else {

                        viewModelMain.resetDetails(
                            allDetails = true
                        )
                    }

                    handleInspectedTripChange(
                        editing = it.editing,
                        start = start,
                        inspectedTripIdentifier = it.inspectedTripIdentifier
                    )
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

    private fun getTripStartLabel(startPlace: Place?): String {

        return startPlace?.getName().toString() +
                " ," +
                startPlace?.getAddress()?.getFullAddress()
                    .toString()
    }

    private fun handleInspectedTripChange(
        editing: Boolean,
        start: String?,
        inspectedTripIdentifier: TripRepositoryImpl.TripIdentifier?
    ) {

        Log.d("editing", editing.toString())

        if (inspectedTripIdentifier == null || editing) {

            removeInspectedTripListeners()

        } else {

            showHideEditInspectedTripButton(
                permissionToUpdate = inspectedTripIdentifier.permissionToUpdate
            )

            setupInspectedTripListeners()


            showInspectedTripData(
                startPlace = start!!,
                inspectedTripIdentifier = inspectedTripIdentifier
            )
        }
    }

    private fun showHideEditInspectedTripButton(permissionToUpdate: Boolean) {

        if (permissionToUpdate) {

            binding.editTripPlaces.visibility = View.VISIBLE
        } else {

            binding.editTripPlaces.visibility = View.GONE
        }
    }

    private fun showInspectedTripData(startPlace: String, inspectedTripIdentifier: TripRepositoryImpl.TripIdentifier) {

        binding.tripCreatorUsername.setText(inspectedTripIdentifier.creatorUsername.toString())

        binding.tripTitle.setText(inspectedTripIdentifier.title.toString())

        binding.tripStart.setText(startPlace)
    }

    private fun setupInspectedTripListeners() {

        binding.editTripPlaces.setOnClickListener { l ->

            viewModelMain.editInspectedTrip()
        }

        binding.dismissInspectTrip.setOnClickListener { l ->

            viewModelMain.resetCurrentTripInRepository()

            viewModelMain.resetDetails(
                allDetails = true
            )

            viewModelMain.returnToPrevContent()
        }
    }
    private fun removeInspectedTripListeners() {

        binding.editTripPlaces.setOnClickListener(null)

        binding.dismissInspectTrip.setOnClickListener(null)
    }

//_________________________________________________________________________________________________________________________
// END OF TRIP METHODS
//_________________________________________________________________________________________________________________________

}