package com.example.travel_mate

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_mate.databinding.FragmentRouteBinding
import com.example.travel_mate.databinding.FragmentSearchBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import kotlin.getValue

/*// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentRoute.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentRoute : Fragment() {
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
         * @return A new instance of fragment FragmentRoute.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentRoute().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }

    private var _binding: FragmentRouteBinding? = null
    private val binding get() = _binding!!

    private var startPlaces: ArrayList<Place> = ArrayList()
    private var routeStops: ArrayList<RouteNode> = ArrayList()
    private var routeMode: String = "foot-walking"

    private val viewModelMain: ViewModelMain by activityViewModels { Application.factory }
    private val viewModelUser: ViewModelUser by activityViewModels { Application.factory }

    private var categoryManager: ClassCategoryManager? = null
    private var resources: Resources? = null

    private lateinit var routeStopsAdapter: AdapterRouteStopsRecyclerView

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
        _binding = FragmentRouteBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        resources = getResources()

        /** [ViewModelMain.mainRouteNavigationState] observer
         *  observe the [viewModelMain]'s [ViewModelMain.mainRouteNavigationState]
         *  on state update
         *  - call [showNavigationData]
         *  - call [handleRouteStopsChange]
         *  - call [handleRouteNavigationChange]
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.mainRouteNavigationState.collect {

                    showNavigationData(
                        coordinates = it.currentLocation,
                        prevRouteStep = it.prevRouteStep,
                        currentRouteStep = it.currentRouteStep
                    )

                    handleRouteStopsChange(
                        mode = it.mode,
                        route = it.route
                    )

                    Log.d("routePolysFragment",  it.route.getRouteNodes().size.toString())

                    Log.d("refresh", "refresh")
                }

            }
        }

        routeStopsAdapter = AdapterRouteStopsRecyclerView(
            routeStops = this.routeStops,
            mode = this.routeMode
        )

        binding.routeStopsList.layoutManager = LinearLayoutManager(context)
        binding.routeStopsList.adapter = routeStopsAdapter

        val touchHelper: ItemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.Callback() {

            var drag = false

            var draggedIndex: Int = 0
            var targetIndex: Int = 0



            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {

                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN , ItemTouchHelper.END)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                draggedIndex = viewHolder.adapterPosition
                targetIndex = target.adapterPosition

                routeStopsAdapter.notifyItemMoved(draggedIndex, targetIndex);

                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                when(direction) {
                    ItemTouchHelper.END -> {
                        val removed = routeStops[viewHolder.adapterPosition]
                        viewModelMain.addRemovePlaceToRoute(removed.placeUUID.toString())
                    }
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    drag = true
                    Log.d("DragTest","DRAGGGING start");
                }
                if(actionState == ItemTouchHelper.ACTION_STATE_IDLE && drag) {
                    Log.d("DragTest", "DRAGGGING stop");

                    if (draggedIndex != targetIndex) {

                        val routeNode = routeStops[draggedIndex]

                        viewModelMain.reorderRoute(
                            newPosition = targetIndex,
                            nodeToMove = routeNode
                        )
                    }
                    drag = false
                }

            }

        })

        touchHelper.attachToRecyclerView(binding.routeStopsList)

        routeStopsAdapter.setOnClickListener(object : AdapterRouteStopsRecyclerView.OnClickListener {


            override fun onClick(
                uuid: String?,
                coordinates: Coordinates?
            ) {
                /**
                 * set the current place based on the uuid of the [Place]
                 * associated with the selected [RouteNode]
                 */
                viewModelMain.getCurrentPlaceByUUID(
                    uuid = uuid!!
                )

                /**
                 * call the [updateMapOnRouteStopSelected] method
                 */
                viewModelMain.setSelectedRouteNodePosition(
                    coordinates = coordinates!!
                )

            }
        })

        /**
         * check the walk mode as the default mode for route planning
         */
        binding.routeModeGroup.check(binding.routeSelectWalk.id)

        /**
         * add an [OnButtonCheckedListener] for the route transport mode selector
         * button group
         * on check change update the transport mode
         */
        binding.routeModeGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

            //get the currently selected index
            val index = binding.routeModeGroup.indexOfChild(binding.routeModeGroup.findViewById(binding.routeModeGroup.checkedButtonId))

            //set the route mode according to the index
            viewModelMain.setRouteTransportMode(
                index = index
            )
        }

        /**
         * add an [OnclickListener] for the [dismissRoutePlan] button
         * when clicked reset the route [ViewModelMain.resetRoute]
         */
        binding.dismissRoutePlan.setOnClickListener { l ->

            viewModelMain.resetRoute()

            viewModelMain.resetCurrentPlace()
        }

        binding.optimizeRoute.setOnClickListener { l ->
            viewModelMain.optimizeRoute()
        }

        binding.startNavigation.setOnClickListener { l ->
            viewModelMain.startNavigation()

            handleNavigationOnOff(true)
        }
        binding.cancelNavigation.setOnClickListener { l ->

            viewModelMain.stopNavigation()

            handleNavigationOnOff(false)

        }
//_________________________________________________________________________________________________________________________
// END OF ROUTE METHODS BLOCK
//_________________________________________________________________________________________________________________________
    }

    override fun onDestroyView() {
        super.onDestroyView()

        resources?.flushLayoutCache()

        _binding = null
    }

    /** Route methods block
     *  initializes the [routeStopsAdapter] with a default empty [routeStops]
     *   and the default [routeMode] that is walking
     *  sets a layout manager for the [binding.routeStopsList] recycler view
     *   and an adapter which is the initialized [routeStopsAdapter]
     *  sets an [onClickListener] for the same adapter too
     **/
//_________________________________________________________________________________________________________________________
// BEGINNING OF ROUTE METHODS BLOCK
//_________________________________________________________________________________________________________________________

    //Methods related to route
//_________________________________________________________________________________________________________________________
// BEGINNING OF METHODS FOR ROUTE
//_________________________________________________________________________________________________________________________
    /** [handleRouteStopsChange]
     * call [showRouteData] and [showHideSearchAndRouteElementsOnRouteStopsChange]
     */
    fun handleRouteStopsChange(mode: Boolean, route: Route) {

        showRouteData(
            route = route
        )

        /*showHideSearchAndRouteElementsOnRouteStopsChange(
            mode = mode,
            isRouteEmpty = route.getRouteNodes().size < 2
        )*/
    }

    /**
     * hide or show the search UI and the route plan UI if there is/isn't a selected place for route planning
     */
    /*fun showHideSearchAndRouteElementsOnRouteStopsChange(mode: Boolean, isRouteEmpty: Boolean) {

        when(mode) {

            true -> {

                binding.tripInfoBar.visibility = View.VISIBLE
                binding.searchContent.visibility = View.GONE
            }
            false -> {

                binding.tripInfoBar.visibility = View.GONE
                binding.searchContent.visibility = View.VISIBLE
            }
        }

        when(isRouteEmpty) {

            true -> {

                binding.routeInfoBar.visibility = View.GONE
            }
            false -> {

                binding.tripInfoBar.visibility = View.GONE
                binding.searchContent.visibility = View.GONE

                binding.routeInfoBar.visibility = View.VISIBLE
            }
        }

    }*/

    /** [showRouteData]
     * refresh the [routeStopsAdapter]'s list with the currently added places
     * set the calculated full duration of the route (by car/on foot)
     * as the text of the route mode selection buttons
     */
    private fun showRouteData(route: Route) {

        this.routeStops.clear()

        this.routeStops.addAll(route.getRouteNodes())

        this.routeMode = route.getTransportMode()

        this.routeStopsAdapter.mode = route.getTransportMode()

        routeStopsAdapter.notifyDataSetChanged()

        val fullWalkDuration = route.fullWalkDuration.toString() + " " + resources?.getString(R.string.duration_string)
        val fullCarDuration = route.fullCarDuration.toString() + " " + resources?.getString(R.string.duration_string)

        binding.routeSelectWalk.text =  fullWalkDuration
        binding.routeSelectCar.text =  fullCarDuration
    }

//_________________________________________________________________________________________________________________________
// END OF METHODS FOR MAP
//_________________________________________________________________________________________________________________________

    private fun handleNavigationOnOff(isActive: Boolean) {

        when(isActive) {

            true -> {
                binding.routeInfoLayout.visibility = View.GONE
                binding.navigationInfoLayout.visibility = View.VISIBLE
            }
            false -> {
                binding.routeInfoLayout.visibility = View.VISIBLE
                binding.navigationInfoLayout.visibility = View.GONE
            }
        }
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showNavigationData(coordinates: Coordinates?,prevRouteStep: RouteStep?, currentRouteStep: RouteStep?) {

        if (coordinates !=null && currentRouteStep != null) {

            showCurrentRouteStepData(
                currentRouteStep = currentRouteStep
            )
        }

        if ( prevRouteStep != null && prevRouteStep.name != null ) {

            handleSecondaryRouteStepVisibility( isSecondary = true)

            showPreviousRouteStepData(
                prevRouteStep = prevRouteStep
            )
        } else {
            handleSecondaryRouteStepVisibility(isSecondary = false)
        }
    }

    private fun showCurrentRouteStepData(currentRouteStep: RouteStep?) {

        if (currentRouteStep != null) {

            if (currentRouteStep.name != null && currentRouteStep.instruction != null) {

                binding.routeStopName.text = currentRouteStep.instruction

                binding.directionImage.setImageDrawable(categoryManager?.getInstructionImage(currentRouteStep.type!!))

            }
        }

    }
    private fun showPreviousRouteStepData(prevRouteStep: RouteStep) {

        if (prevRouteStep.instruction != null) {

            val secondaryContent = prevRouteStep.name ?: prevRouteStep.instruction

            binding.routeSecondaryStopName.text = secondaryContent

            binding.directionSecondaryImage.setImageDrawable(categoryManager?.getInstructionImage(prevRouteStep.type!!))

        }
    }
}