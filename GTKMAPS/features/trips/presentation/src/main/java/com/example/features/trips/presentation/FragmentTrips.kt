package com.example.features.trips.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.features.trips.presentation.databinding.FragmentTripsBinding
import com.example.features.trips.presentation.models.TripIdentifierTripsPresentationModel
import com.example.features.trips.presentation.models.TripTripsPresentationModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentTrips.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentTrips : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    private var trips: ArrayList<TripIdentifierTripsPresentationModel> = ArrayList()
    private var currentTrip: TripTripsPresentationModel? = null
    private var currentTripIdentifier: TripIdentifierTripsPresentationModel? = null

    private lateinit var tripsAdapter: AdapterTripRecyclerView

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!

    private val viewModelTrips: TripsViewModel by inject<TripsViewModel>()

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
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripsAdapter = AdapterTripRecyclerView(trips)
        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(this.context,
            LinearLayoutManager.VERTICAL, false)
        binding.tripsRecyclerView.adapter = tripsAdapter

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.tripDetailsLayout)

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        handleTabSelect(binding.tabLayout.getTabAt(0))

        viewLifecycleOwner.lifecycleScope.launch {

            repeatOnLifecycle(
                state = Lifecycle.State.STARTED
            ) {

                viewModelTrips.tripsState.collect {

                    updateTripList(it.trips)
                }
            }
        }

        /**
         * observe the [com.example.presentation.ViewModelUser]'s [com.example.presentation.ViewModelUser.currentTripUiState] [kotlinx.coroutines.flow.StateFlow]
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelTrips.selectedTripState.collect {

                    updateTripDetails(
                        trip = it.selectedTrip,
                        tripIdentifier = it.selectedIdentifier
                    )

                    /*if (it.currentTrip != null && it.tripIdentifier != null) {

                        currentTrip = it.currentTrip
                        currentTripIdentifier = it.tripIdentifier


                    }*/
                }
            }
        }

        /**
         * [com.example.travel_mate.ui.AdapterTripRecyclerView.OnClickListener]
         * for the [com.example.travel_mate.ui.AdapterTripRecyclerView] if there is an item clicked update
         * the [com.example.presentation.ViewModelUser.userUiState]
         */
        tripsAdapter.setOnClickListener(object : AdapterTripRecyclerView.OnClickListener{
            override fun onClick(position: Int) {
                //Log.d("FirebaseDatabaseNew", trips[position].uuid.toString())

                viewModelTrips.getSelectedTripData(trips[position])

                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        /** [TabLayout.OnTabSelectedListener]
         * fetch saved or shared places based on the selected index
         */
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab select
                handleTabSelect(
                    tab = tab
                )
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })

        binding.selectTrip.setOnClickListener { l ->

            viewModelTrips.setInspectedTrip()

            val request = NavDeepLinkRequest.Builder.fromUri(
                "android-app://app/main".toUri()
            ).build()

            findNavController().navigate(request)
        }
        binding.updateTrip.setOnClickListener { l ->

            //Log.d("FragmentSaveTripRepository", currentTrip?.uUID.toString())

            //TODO NOT A THING ANYMORE IT WILL RETURN TO USER EITHER WAY
            //viewModelUser.setUpdatedFrom("user")

            Log.d("updateTripTrips", "executed")

            viewModelTrips.updateSelectedTrip()

            val request = NavDeepLinkRequest.Builder.fromUri(
                "android-app://com.example.features/save_trip".toUri()
            ).build()

            findNavController().navigate(request)
        }

        binding.deleteTrip.setOnClickListener { l ->

            viewModelTrips.deleteSelectedTrip()

            handleTabSelect(binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentTrips.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentTrips().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }

    private fun updateTripList(trips: List<TripIdentifierTripsPresentationModel>) {

        this.trips.clear()

        this.trips.addAll(trips)

        Log.d("updateTrips", "updateTripList: Trips size is ${trips.size}")

        tripsAdapter.notifyDataSetChanged()

    }

    /** [handleTabSelect]
     * if the selected position is 0 fetch the locally saved [com.example.model.Trip]s
     * fetch the [com.example.model.Trip]s shared by the user if the index of the selected tab is 1
     * else fetch the [com.example.model.Trip]s that the current user has contributed to
     */
    private fun handleTabSelect(tab: TabLayout.Tab?) {

        val position = tab?.position

        viewModelTrips.getTripsBySelectedTabLayoutItemIndex(position!!)

        /*when(position) {

            0 ->  {

                Log.d("clicked", "myTripsSelected")
            }
            1 -> viewModelUser.fetchMyTripsFromDatabase()
            2 -> viewModelUser.fetchContributedTripsFromDatabase()
        }*/
    }

    private fun setContributorsList(contributorsList: List<String>) {


        when (contributorsList.size<3) {

            true -> {

                binding.contributorsList.setText(contributorsList.joinToString(separator = ",") { it })
            }
            else -> {

                val stringBuilder = StringBuilder(contributorsList[0])
                stringBuilder.append(" ," + contributorsList[1])
                stringBuilder.append(" + " + (contributorsList.size-1))

                binding.contributorsList.setText(stringBuilder.toString())
            }
        }

    }

    /** [updateTripDetails]
     * update the UI with the current [com.example.model.Trip]'s data
     */
    private fun updateTripDetails(trip: TripTripsPresentationModel, tripIdentifier: TripIdentifierTripsPresentationModel) {

        val title = when(trip) {
            TripTripsPresentationModel.Default -> ""
            is TripTripsPresentationModel.Local -> trip.title
            is TripTripsPresentationModel.Remote -> trip.title
        }

        val date = when(trip) {
            TripTripsPresentationModel.Default -> ""
            is TripTripsPresentationModel.Local -> trip.date
            is TripTripsPresentationModel.Remote -> trip.date
        }

        val note = when(trip) {
            TripTripsPresentationModel.Default -> ""
            is TripTripsPresentationModel.Local -> trip.note
            is TripTripsPresentationModel.Remote -> trip.note
        }

        binding.tripCreator.setText(tripIdentifier.creatorUsername())

        binding.tripTitle.setText(title)

        binding.tripDate.setText(date)

        binding.tripNote.setText(note)

        setContributorsList(tripIdentifier.getContributorsUsernames())

        checkContributorPermission(
            tripIdentifier = tripIdentifier
        )
    }

    private fun checkContributorPermission(tripIdentifier: TripIdentifierTripsPresentationModel) {

        binding.updateTrip.visibility = View.VISIBLE

        if (!tripIdentifier.hasPermissionToUpdate()) {

            binding.updateTrip.visibility = View.INVISIBLE

            /*val currentUser = tripIdentifier.contributors.values.find {
                it.uid == user?.uid
            }

            if (currentUser?.canUpdate != true ) {

            }*/
        }
    }

}