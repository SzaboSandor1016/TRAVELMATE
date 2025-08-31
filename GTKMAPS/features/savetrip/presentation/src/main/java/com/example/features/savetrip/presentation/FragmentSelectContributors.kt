package com.example.features.savetrip.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.features.savetrip.presentation.databinding.FragmentSelectContributorsBinding
import com.example.features.savetrip.presentation.models.ContributorSaveTripPresentationModel
import com.example.features.savetrip.presentation.viewmodel.SaveTripViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/** [FragmentSelectContributors]
 * a [Fragment] to select contributors for a trip
 */
class FragmentSelectContributors : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentSelectContributors.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSelectContributors().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    private val viewModelSaveTrip: SaveTripViewModel by inject<SaveTripViewModel>()

    private lateinit var contributorsAdapter: AdapterContributorsRecyclerView

    private var recentContributors: ArrayList<ContributorSaveTripPresentationModel> = ArrayList()

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private var _binding : FragmentSelectContributorsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSelectContributorsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contributorsAdapter = AdapterContributorsRecyclerView(recentContributors)

        binding.contributorsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.contributorsRecyclerView.adapter = contributorsAdapter

        standardBottomSheetBehavior = BottomSheetBehavior.from(binding.tripDetailsLayout)

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //TODO REMINDER: THIS IS SOLVED BY A USE-CASE THAT RETURNS A FLOW WITH THE SELECTABLE CONTRIBUTORS
        //viewModelSaveTrip.getSelectableContributors()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSaveTrip.saveTripContributorsState.collect {

                    recentContributors.clear()

                    recentContributors.addAll(it)

                    contributorsAdapter.notifyDataSetChanged()
                }
            }
        }

        contributorsAdapter.setOnClickListener(object: AdapterContributorsRecyclerView.OnClickListener {
            override fun onClick(uid: String, setSelected: Boolean) {

                viewModelSaveTrip.selectUnselectContributor(
                    contributorUID = uid,
                    select = setSelected
                )
            }
        })

        contributorsAdapter.setOnItemLongClickListener(object : AdapterContributorsRecyclerView.OnItemLongClickListener {
            override fun onItemLongClick(uid: String) {

                showEditedContributorData(
                    uid = uid
                )
            }
        })

        binding.addContributorButton.setOnClickListener { _ ->

            //Todo notify user if username not found

            val username = binding.addContributor.getText().toString().trim()

            //TODO DO IT!!! (Completely forgot this)
           /* viewModelSaveTrip.getNewContributorData(
                username = username
            )*/
        }

        binding.done.setOnClickListener { _ ->

            findNavController().navigate(R.id.action_fragmentSelectContributors_to_fragmentSaveTrip)
        }

        binding.back.setOnClickListener { _ ->

            findNavController().navigate(R.id.action_fragmentSelectContributors_to_fragmentSaveTrip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun showEditedContributorData(uid: String) {

        val editedContributor = recentContributors.find { it.uid == uid }

        if (editedContributor != null) {

            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            binding.canUpdate.setOnCheckedChangeListener(null)

            binding.username.setText(editedContributor.username.toString())

            binding.canUpdate.setChecked(editedContributor.canUpdate)

            binding.canUpdate.setOnCheckedChangeListener { l, checked ->

                viewModelSaveTrip.setUserPermission(
                    userUID = editedContributor.uid.toString(),
                    permissionToUpdate = checked
                )
            }
        }
    }
}