package com.example.travel_mate

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel_mate.databinding.FragmentSelectContributorsBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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

    private val viewModelUser: ViewModelUser by activityViewModels { MyApplication.factory }

    private lateinit var contributorsAdapter: AdapterContributorsRecyclerView

    private var recentContributors: ArrayList<ViewModelUser.Contributor> = ArrayList()

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

        viewModelUser.getRecentContributors()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelUser.currentTripUiState.collect {

                    recentContributors.clear()

                    recentContributors.addAll(it.recentContributors)

                    contributorsAdapter.notifyDataSetChanged()
                }
            }
        }

        contributorsAdapter.setOnClickListener(object: AdapterContributorsRecyclerView.OnClickListener {
            override fun onClick(position: Int) {
                viewModelUser.selectContributor(
                    position = position
                )

                Log.d("selectedContributor", recentContributors[position].selected.toString())
            }
        })

        binding.addContributorButton.setOnClickListener { _ ->

            val username = binding.addContributor.getText().toString().trim()

            viewModelUser.getNewContributorData(
                username = username
            )
        }

        binding.done.setOnClickListener { _ ->

            val selectedContributors = recentContributors.filter{ it.selected == true }.associate { Pair(it.data.first, it.selected) }
            val usernames = recentContributors.associate { Pair(it.data.first,true) }

            viewModelUser.setCurrentTripContributors(
                contributors = selectedContributors
            )

            viewModelUser.setRecentContributors(
                contributors = usernames
            )

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

}