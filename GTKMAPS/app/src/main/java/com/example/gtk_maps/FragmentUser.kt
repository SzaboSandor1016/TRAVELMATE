package com.example.gtk_maps

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gtk_maps.databinding.FragmentUserBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseUser

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentUser : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private var trips: ArrayList<ClassTrip> = ArrayList()

    private var user: FirebaseUser? = null

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private lateinit var tripsAdapter: AdapterTripRecyclerView

    //private lateinit var uiControllerUser: UiControllerFragmentUser

    var selected: Selected? = null

    private var viewModelFirebase: ViewModelFirebase? = null
    private var viewModelSave: ViewModelSave? = null
    private var viewModelTrip: ViewModelTrip? = null

    interface Selected{
        fun onSelect()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        selected = context as? Selected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        viewModelFirebase = ViewModelProvider(viewModelStore,MyApplication.factory)[ViewModelFirebase::class.java]
        viewModelSave =  ViewModelProvider(viewModelStore,MyApplication.factory)[ViewModelSave::class.java]
        viewModelTrip = ViewModelProvider(viewModelStore,MyApplication.factory)[ViewModelTrip::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = viewModelFirebase?.getCurrentUser()

        //this may cause some problems in that case check this first
        //uiControllerUser = UiControllerFragmentUser(binding, requireContext(), this)

        viewModelSave?.readSavedTrips()

        tripsAdapter = AdapterTripRecyclerView(ArrayList())
        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tripsRecyclerView.adapter = tripsAdapter

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.tripDetailsLayout)

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        viewModelSave?.trips?.observe(viewLifecycleOwner) { trips ->

            this.trips.clear()
            this.trips.addAll(trips)

            tripsAdapter = AdapterTripRecyclerView(trips.map { it.getTitle()!! }.toList())
            binding.tripsRecyclerView.adapter = tripsAdapter

            tripsAdapter.setOnClickListener(object : AdapterTripRecyclerView.OnClickListener{
                override fun onClick(position: Int) {
                    Log.d("onClick", "clicked")

                    updateTripDetails(trips[position])

                    binding.selectTrip.setOnClickListener { l ->

                        viewModelTrip?.setCurrentTrip(trips[position])
                        selected?.onSelect()
                    }

                    standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            })
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

    private fun updateTripDetails(trip:ClassTrip) {

        binding.tripTitle.setText(trip.getTitle())
        binding.tripDate.setText(trip.getDate())
        binding.tripNote.setText(trip.getNote())
    }
}