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
import com.example.travel_mate.databinding.FragmentCustomPlaceBinding
import kotlinx.coroutines.launch
import kotlin.getValue
import androidx.core.view.isVisible

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

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

    private var customPlace: Place? = null

    private val viewModelMain: ViewModelMain by activityViewModels { Application.factory }
    private val viewModelUser: ViewModelUser by activityViewModels { Application.factory }

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

                viewModelMain.mainCustomPlaceState.collect {

                    customPlace = it.customPlace

                    showCustomPlaceData(
                        name = it.customPlace?.getName(),
                        address = it.customPlace?.getAddress()
                    )
                }
            }
        }

        binding.navigateTo.setOnClickListener { l ->

            showNavigateOptions()
        }

        binding.byCar.setOnClickListener { l ->

            viewModelMain.startNavigationToCustomPlace(
                coordinates = customPlace?.getCoordinates()!!,
                transportMode = "driving-car"
            )
        }

        binding.onFoot.setOnClickListener { l ->

            viewModelMain.startNavigationToCustomPlace(
                coordinates = customPlace?.getCoordinates()!!,
                transportMode = "foot-walking"
            )
        }

        binding.dismissCustomPlace.setOnClickListener { l ->

            viewModelMain.returnToPrevContent()

            viewModelMain.resetCustomPlace()
        }

        binding.setAsStart.setOnClickListener { l ->

            viewModelMain.returnToPrevContent()

            viewModelMain.initNewSearch(
                startPlace = this.customPlace!!
            )

            viewModelMain.resetCustomPlace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentLifecycle", "Parent/Child Fragment Destroyed")

        //dismissDialog()

        //binding.distanceGroup.clearOnButtonCheckedListeners()
        //binding.transportGroup.clearOnButtonCheckedListeners()

        _binding = null
    }

    private fun showCustomPlaceData(name: String?, address: Address?) {

        binding.customPlaceName.setText(name)

        binding.customPlaceAddress.setText(address?.getAddress())
    }

    fun showNavigateOptions() {

        if (binding.navigateOptionsBar.isVisible)
            binding.navigateOptionsBar.visibility = View.GONE
        else binding.navigateOptionsBar.visibility = View.VISIBLE
    }
}