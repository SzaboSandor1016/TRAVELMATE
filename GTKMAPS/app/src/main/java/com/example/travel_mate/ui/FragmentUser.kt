package com.example.travel_mate.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_mate.Application
import com.example.travel_mate.R
import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl
import com.example.travel_mate.databinding.FragmentUserBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**[FragmentUser]
 * a [androidx.fragment.app.Fragment] to show the saved trips and the shared ones too if there is a user signed in
 */
class FragmentUser : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private var trips: ArrayList<TripRepositoryImpl.TripIdentifier> = ArrayList()
    private var currentTrip: Trip? = null
    private var currentTripIdentifier: TripRepositoryImpl.TripIdentifier? = null

    private var user: FirebaseUser? = null

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private lateinit var tripsAdapter: AdapterTripRecyclerView

    //private lateinit var uiControllerUser: UiControllerFragmentUser

    private val viewModelUser: ViewModelUser by inject<ViewModelUser>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //this may cause some problems in that case check this first
        //uiControllerUser = UiControllerFragmentUser(binding, requireContext(), this)

        tripsAdapter = AdapterTripRecyclerView(trips)
        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tripsRecyclerView.adapter = tripsAdapter

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.tripDetailsLayout)

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        handleTabSelect(binding.tabLayout.getTabAt(0))

        /**
         * observe the [ViewModelUser]'s [ViewModelUser.userUiState] state flow
         */
        viewLifecycleOwner.lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelUser.userUiState.collect{

                    user = it.user

                    updateUiOnSignedStateChange(it.username)

                    /*
                    clear the ArrayList containing the trips
                    then refresh it with a new data set read from the StateFlow
                     */
                    trips.clear()

                    trips.addAll(it.trips)

                    tripsAdapter.notifyDataSetChanged()

                    Log.d("trips3", tripsAdapter.itemCount.toString())

                }
            }
        }

        /**
         * observe the [ViewModelUser]'s [ViewModelUser.currentTripUiState] [kotlinx.coroutines.flow.StateFlow]
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelUser.currentTripUiState.collect {

                    if (it.currentTrip != null && it.tripIdentifier != null) {

                        currentTrip = it.currentTrip
                        currentTripIdentifier = it.tripIdentifier

                        updateTripDetails(
                            trip = it.currentTrip,
                            tripIdentifier = it.tripIdentifier
                        )
                    }
                }
            }
        }

        /**
         * [AdapterTripRecyclerView.OnClickListener]
         * for the [AdapterTripRecyclerView] if there is an item clicked update
         * the [ViewModelUser.userUiState]
         */
        tripsAdapter.setOnClickListener(object : AdapterTripRecyclerView.OnClickListener{
            override fun onClick(position: Int) {
                Log.d("FirebaseDatabaseNew", trips[position].uuid.toString())

                viewModelUser.setCurrentTripIdentifier(trips[position])

                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        /** [com.google.android.material.tabs.TabLayout.OnTabSelectedListener]
         * fetch saved or shared places based on the selected index
         */
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

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

            findNavController().navigate(R.id.action_FragmentUser_to_FragmentMain)
        }
        binding.updateTrip.setOnClickListener { l ->

            Log.d("FragmentSaveTripRepository", currentTrip?.uUID.toString())

            viewModelUser.setUpdatedFrom("user")

            findNavController().navigate(R.id.action_FragmentUser_to_fragmentSaveTrip)
        }

        binding.deleteTrip.setOnClickListener { l ->

            viewModelUser.deleteCurrentTrip(
                trip = currentTrip!!,
                tripIdentifier = currentTripIdentifier!!
            )

            handleTabSelect(binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition))
        }

        binding.back.setOnClickListener { l ->

            returnAndClear()
        }

        binding.signIn.setOnClickListener { l ->

            findNavController().navigate(R.id.action_FragmentUser_to_FragmentSignIn)
        }

        binding.signOut.setOnClickListener { l ->

            viewModelUser.signOut()
        }

        binding.settings.setOnClickListener { l ->
            findNavController().navigate(R.id.action_FragmentUser_to_fragmentSettings)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(inflater,container,false)
        return binding.root
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
         * @return A new instance of fragment FragmentUser.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FragmentUser().apply {
                arguments = Bundle().apply {
                }
            }
    }

    fun updateUserFragment() {
        //Todo update user fragment
    }

    /** [handleTabSelect]
     * if the selected position is 0 fetch the locally saved [Trip]s
     * fetch the [Trip]s shared by the user if the index of the selected tab is 1
     * else fetch the [Trip]s that the current user has contributed to
     */
    private fun handleTabSelect(tab: TabLayout.Tab?) {

        val position = tab?.position

        when(position) {

            0 ->  {
                viewModelUser.fetchSavedTrips()
                Log.d("clicked", "myTripsSelected")
            }
            1 -> viewModelUser.fetchMyTripsFromDatabase()
            2 -> viewModelUser.fetchContributedTripsFromDatabase()
        }
    }

    /** [returnAndClear]
     * clear the current [Trip]
     * then return to the [FragmentMain]
     */
    private fun returnAndClear() {

        viewModelUser.resetCurrentTrip()

        findNavController().navigate(R.id.action_FragmentUser_to_FragmentMain)
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
     * update the UI with the current [Trip]'s data
     */
    private fun updateTripDetails(trip: Trip, tripIdentifier: TripRepositoryImpl.TripIdentifier) {

        binding.tripCreator.setText(tripIdentifier.creatorUsername.toString())

        binding.tripTitle.setText(trip.title.toString())

        binding.tripDate.setText(trip.date.toString())

        binding.tripNote.setText(trip.note.toString())

        val usernames = tripIdentifier.contributors.values.toList().map { it.username.toString() }

        setContributorsList(usernames)

        checkContributorPermission(
            tripIdentifier = tripIdentifier
        )
    }

    /** [updateUiOnSignedStateChange]
     * update the ui if there is a user signed in
     */
    private fun updateUiOnSignedStateChange(username :String?) {

        if (username != null) {

            binding.signedIn.setText(username)

            binding.signIn.visibility = View.GONE
            binding.signOut.visibility = View.VISIBLE

            binding.settings.visibility = View.VISIBLE
        } else {

            binding.signedIn.setText(resources.getString(R.string.not_signed_in))

            binding.signIn.visibility = View.VISIBLE
            binding.signOut.visibility = View.GONE

            binding.settings.visibility = View.GONE
        }
    }
    private fun checkContributorPermission(tripIdentifier: TripRepositoryImpl.TripIdentifier) {

        binding.updateTrip.visibility = View.VISIBLE

        if (!tripIdentifier.permissionToUpdate && tripIdentifier.location != "local") {

            binding.updateTrip.visibility = View.INVISIBLE

            /*val currentUser = tripIdentifier.contributors.values.find {
                it.uid == user?.uid
            }

            if (currentUser?.canUpdate != true ) {

            }*/
        }
    }
}