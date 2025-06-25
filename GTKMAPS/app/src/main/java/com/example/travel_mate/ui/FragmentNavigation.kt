package com.example.travel_mate.ui

import android.annotation.SuppressLint
import android.content.res.Resources
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
import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.data.RouteStep
import com.example.travel_mate.databinding.FragmentNavigationBinding
import com.example.travel_mate.ui.ViewModelMain.MainContent
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [FragmentNavigation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentNavigation : Fragment() {
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
         * @return A new instance of fragment FragmentNavigation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentNavigation().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                */
            }
    }

    private var _binding: FragmentNavigationBinding? = null
    val binding get() = _binding!!

    private val viewModelMain: ViewModelMain by inject<ViewModelMain>()

    private var categoryManager: ClassCategoryManager? = null
    private var resources: Resources? = null

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
        _binding = FragmentNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryManager = ClassCategoryManager(requireContext())
        resources = getResources()

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.mainNavigationInfoState.collect {

                    showNavigationData(
                        startedFrom = it.startedFrom,
                        showToNextDestination = it.showToNextDestination,
                        prevRouteStep = it.prevRouteStep,
                        currentRouteStep = it.currentRouteStep
                    )

                    Log.d("refresh", "refresh")
                }
            }
        }


        binding.cancelNavigation.setOnClickListener { l ->

            viewModelMain.stopNavigation()

            viewModelMain.returnToPrevContent()
        }

        binding.nextGoal.setOnClickListener { l ->

            viewModelMain.navigateToNextPlaceInRoute()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentLifecycle", "Parent/Child Fragment Destroyed")

        //dismissDialog()

        //binding.distanceGroup.clearOnButtonCheckedListeners()
        //binding.transportGroup.clearOnButtonCheckedListeners()

        resources?.flushLayoutCache()

        _binding = null
    }

    private fun handleSecondaryRouteStepVisibility(isSecondary: Boolean) {

        when(isSecondary) {

            true -> {
                binding.navigationSecondaryInfoLayout.visibility = View.VISIBLE
            }
            false -> {
                binding.navigationSecondaryInfoLayout.visibility = View.GONE
            }
        }
    }

    private fun handleNextGoalVisibility(endOfRoute: Boolean, startedFrom: Int) {

        when(endOfRoute && startedFrom==0) {

            true -> {
                binding.nextGoal.visibility = View.VISIBLE
            }
            false -> {
                binding.nextGoal.visibility = View.GONE
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showNavigationData(
        startedFrom: Int,
        showToNextDestination: Boolean,
        prevRouteStep: RouteStep?,
        currentRouteStep: RouteStep?
    ) {

        if (currentRouteStep != null) {

            showCurrentRouteStepData(
                currentRouteStep = currentRouteStep
            )
        }

        handleSecondaryRouteStepVisibility(
            isSecondary = false
        )

        handleNextGoalVisibility(
            endOfRoute = showToNextDestination,
            startedFrom = startedFrom
        )

        if (prevRouteStep != null && !showToNextDestination) {

            handleSecondaryRouteStepVisibility(
                isSecondary = true
            )

            showPreviousRouteStepData(
                prevRouteStep = prevRouteStep
            )
        }
    }

    private fun showCurrentRouteStepData(currentRouteStep: RouteStep?) {

        if (currentRouteStep != null) {

            if (currentRouteStep.name != null && currentRouteStep.instruction != null) {

                binding.routeStopName.setText(currentRouteStep.instruction)

                binding.directionImage.setImageDrawable(categoryManager?.getInstructionImage(currentRouteStep.type!!))

            }
        }

    }
    private fun showPreviousRouteStepData(prevRouteStep: RouteStep) {

        val secondaryContent = prevRouteStep.instruction ?: prevRouteStep.name

        binding.routeSecondaryStopName.setText(secondaryContent)

        binding.directionSecondaryImage.setImageDrawable(categoryManager?.getInstructionImage(prevRouteStep.type!!))
    }
}