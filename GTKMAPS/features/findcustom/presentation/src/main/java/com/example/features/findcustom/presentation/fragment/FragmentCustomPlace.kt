package com.example.features.findcustom.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.features.findcustom.presentation.databinding.FragmentCustomPlaceBinding
import com.example.features.findcustom.presentation.models.AddressCustomPlacePresentationModel
import com.example.features.findcustom.presentation.models.CustomPlaceStatePresentationModel
import com.example.features.findcustom.presentation.viewmodels.CustomPlaceViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentCustomPlace.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentCustomPlace : Fragment() {
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
         * @return A new instance of fragment FragmentCustomPlace.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentCustomPlace().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }

    private var _binding: FragmentCustomPlaceBinding? = null
    val binding: FragmentCustomPlaceBinding get() = _binding!!

    private val customPlaceViewModel: CustomPlaceViewModel by inject<CustomPlaceViewModel>()

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
        _binding = FragmentCustomPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                customPlaceViewModel.infoState.collect {

                    when(it){
                        is CustomPlaceStatePresentationModel.Empty -> {}
                        is CustomPlaceStatePresentationModel.Custom -> showCustomPlaceData(
                            name = it.place.name,
                            address = it.place.address
                        )
                    }
                }
            }
        }

        binding.navigateTo.setOnClickListener { l ->

            showNavigateOptions()
        }

        binding.byCar.setOnClickListener { l ->

            customPlaceViewModel.navigateToCustomPlace(
                transportMode = "driving-car"
            )
        }

        binding.onFoot.setOnClickListener { l ->

            customPlaceViewModel.navigateToCustomPlace(
                transportMode = "foot-walking"
            )
        }

        binding.dismissCustomPlace.setOnClickListener { l ->

            customPlaceViewModel.resetCustomPlace()
        }

        binding.setAsStart.setOnClickListener { l ->

            customPlaceViewModel.setAsStartPlaceOfSearch()

            customPlaceViewModel.resetCustomPlace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentLifecycle", "Parent/Child Fragment Destroyed")

        _binding = null
    }

    private fun showCustomPlaceData(name: String?, address: AddressCustomPlacePresentationModel) {

        binding.customPlaceName.setText(name)

        binding.customPlaceAddress.setText(address.toAddressString())
    }

    fun showNavigateOptions() {

        if (binding.navigateOptionsBar.isVisible)
            binding.navigateOptionsBar.visibility = View.GONE
        else binding.navigateOptionsBar.visibility = View.VISIBLE
    }
}