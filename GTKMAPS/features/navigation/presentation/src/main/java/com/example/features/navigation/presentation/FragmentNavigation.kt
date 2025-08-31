package com.example.features.navigation.presentation

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.features.navigation.presentation.databinding.FragmentNavigationBinding
import com.example.features.navigation.presentation.models.NavigationInfoNavigationPresentationModel
import com.example.core.utils.ClassCategoryManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
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

    private val viewModelNavigation: NavigationViewModel by inject<NavigationViewModel>()

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

                viewModelNavigation.navigationInfo.collect {

                    when(it.navigationInfo) {
                        is NavigationInfoNavigationPresentationModel.Default -> {}
                        is NavigationInfoNavigationPresentationModel.Navigation -> {
                            showNavigationData(
                                prevRouteStepName = it.navigationInfo.prevStepName,
                                prevRouteStepInstruction = it.navigationInfo.prevStepInstruction,
                                prevRouteStepInstructionType = it.navigationInfo.prevStepInstructionType,
                                currentRouteStepName = it.navigationInfo.currentStepName,
                                currentRouteStepInstruction = it.navigationInfo.currentStepInstruction,
                                currentRouteStepInstructionType = it.navigationInfo.currentStepInstructionType
                            )

                            handleNextGoalVisibility(false)
                        }

                        is NavigationInfoNavigationPresentationModel.Arrived -> {

                            showNavigationData(
                                prevRouteStepName = null,
                                prevRouteStepInstruction = null,
                                prevRouteStepInstructionType = null,
                                currentRouteStepName = it.navigationInfo.finalStepName,
                                currentRouteStepInstruction = it.navigationInfo.finalStepInstruction,
                                currentRouteStepInstructionType = it.navigationInfo.finalStepInstructionType
                            )

                            handleSecondaryRouteStepVisibility(false)
                            handleNextGoalVisibility(it.navigationInfo.hasNextDestination)
                        }//TODO CREATE ARRIVED UI ELEMENTS
                    }



                    Log.d("refresh", "refresh")
                }
            }
        }


        binding.cancelNavigation.setOnClickListener { l ->

            viewModelNavigation.endNavigation()
        }

        binding.nextGoal.setOnClickListener { l ->

            viewModelNavigation.navigateToNextPlace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FragmentLifecycle", "Parent/Child Fragment Destroyed")

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

    private fun handleNextGoalVisibility(hasNextDestination: Boolean) {

        when(hasNextDestination) {

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
        prevRouteStepName: String?,
        prevRouteStepInstruction: String?,
        prevRouteStepInstructionType: Int?,
        currentRouteStepName: String?,
        currentRouteStepInstruction: String?,
        currentRouteStepInstructionType: Int?,
    ) {

        if (currentRouteStepName != null && currentRouteStepInstruction != null) {

            showCurrentRouteStepData(
                currentRouteStepName = currentRouteStepName,
                currentRouteStepInstruction = currentRouteStepInstruction,
                currentRouteStepInstructionType = currentRouteStepInstructionType
            )
        }

        handleSecondaryRouteStepVisibility(
            isSecondary = false
        )

        if (prevRouteStepName != null && prevRouteStepInstruction != null) {

            handleSecondaryRouteStepVisibility(
                isSecondary = true
            )

            showPreviousRouteStepData(
                prevRouteStepName = prevRouteStepName,
                prevRouteStepInstruction = prevRouteStepInstruction,
                prevRouteStepInstructionType = prevRouteStepInstructionType
            )
        }
    }

    private fun showCurrentRouteStepData(
        currentRouteStepName: String?,
        currentRouteStepInstruction: String?,
        currentRouteStepInstructionType: Int?,
    ) {

        val currentContent = currentRouteStepInstruction ?: currentRouteStepName?: ""

        binding.routeStopName.setText(currentContent)

        if (currentRouteStepInstructionType != null)
            binding.directionImage.setImageDrawable(
                categoryManager?.getInstructionImage(
                    currentRouteStepInstructionType
                )
            )

    }
    private fun showPreviousRouteStepData(
        prevRouteStepName: String?,
        prevRouteStepInstruction: String?,
        prevRouteStepInstructionType: Int?,
    ) {

        val secondaryContent = prevRouteStepInstruction ?: prevRouteStepName?: ""

        binding.routeSecondaryStopName.setText(secondaryContent)

        if (prevRouteStepInstructionType != null)
            binding.directionSecondaryImage.setImageDrawable(
                categoryManager?.getInstructionImage(
                    prevRouteStepInstructionType
                )
            )
    }
}