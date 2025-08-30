package com.example.features.route.presentation

import android.content.res.Resources
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.core.ui.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.features.route.presentation.databinding.FragmentRouteBinding
import com.example.features.route.presentation.models.CoordinatesRoutePresentationModel
import com.example.features.route.presentation.models.RouteInfoNodeRoutePresentationModel
import com.example.features.route.presentation.models.RouteInfoRoutePresentationModel
import com.example.features.route.presentation.models.RouteInfoStateRoutePresentationModel
import com.example.core.utils.ClassCategoryManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
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

    //private var startPlaces: ArrayList<Place> = ArrayList()
    private var routeStops: ArrayList<RouteInfoNodeRoutePresentationModel> = ArrayList()

    private val viewModelRoute: RouteViewModel by inject<RouteViewModel>()

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

        categoryManager= ClassCategoryManager(requireContext())
        resources = getResources()

        /** [com.example.travel_mate.ui.ViewModelMain.mainRouteState] observer
         *  observe the [viewModelRoute]'s [com.example.travel_mate.ui.ViewModelMain.mainRouteState]
         *  on state update
         *  - call [handleRouteStopsChange]
         *  - call [handleRouteNavigationChange]
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelRoute.routeInfoState.collect {

                    when(it) {

                        is RouteInfoStateRoutePresentationModel.Empty -> {}

                        is RouteInfoStateRoutePresentationModel.Route -> {
                            handleRouteStopsChange(
                                route = it.routeInfo
                            )

                            Log.d("routePolysFragment",  it.routeInfo.infoNodes.size.toString())

                            Log.d("refresh", "refresh")
                        }
                    }
                }
            }
        }

        routeStopsAdapter = AdapterRouteStopsRecyclerView(
            routeStops = this.routeStops
        )

        binding.routeStopsList.layoutManager = LinearLayoutManager(context)
        binding.routeStopsList.adapter = routeStopsAdapter

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            var drag = false
            var selected = false

            var draggedIndex: Int = 0
            var targetIndex: Int = 0

            var originalIndex: Int = 0

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {

                return makeMovementFlags(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    ItemTouchHelper.END
                )
            }

            override fun canDropOver(
                recyclerView: RecyclerView,
                current: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return target.bindingAdapterPosition != 0
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val isDraggingUpward = dY < 0

                val isDraggingIntoUndraggableArea =
                    (isDraggingUpward && recyclerView.findViewHolderForAdapterPosition(0)
                        ?.let { !canDropOver(recyclerView, viewHolder, it) } == true)

                val newDy = if (isDraggingIntoUndraggableArea) {
                    0f  // Clamp
                } else {
                    dY
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    newDy,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                if (!selected) {
                    originalIndex = viewHolder.bindingAdapterPosition

                    selected = true
                }

                draggedIndex = viewHolder.bindingAdapterPosition
                targetIndex = target.bindingAdapterPosition

                routeStopsAdapter.notifyItemMoved(draggedIndex, targetIndex);

                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                when (direction) {
                    ItemTouchHelper.END -> {
                        val removed = routeStops[viewHolder.bindingAdapterPosition]
                        viewModelRoute.removePlaceFromRoute(removed.placeUUID)
                    }
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    drag = true
                    Log.d("DragTest", "DRAGGGING start");
                }
                if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && drag) {
                    Log.d("DragTest", "DRAGGGING stop");

                    if (draggedIndex != targetIndex) {

                        val routeNode = routeStops[originalIndex]

                        Log.d("targetIndex", targetIndex.toString())

                        //Log.d("draggedIndex", routeNode. .toString())

                        viewModelRoute.reorderRoute(
                            newPosition = targetIndex,
                            placeUUID = routeNode.placeUUID
                        )
                    }
                    drag = false
                    selected = false
                }

            }

        })

        touchHelper.attachToRecyclerView(binding.routeStopsList)

        routeStopsAdapter.setOnClickListener(object : AdapterRouteStopsRecyclerView.OnClickListener {


            override fun onClick(
                uuid: String?,
                coordinates: CoordinatesRoutePresentationModel?
            ) {
                /**
                 * set the current place based on the uuid of the [com.example.model.Place]
                 * associated with the selected [com.example.domain.models.RouteNode]
                 */
                viewModelRoute.setSelectedPlace(
                    uuid = uuid!!
                )

                //TODO REMINDER THE MAINFRAGMENT UPDATES THE MAP ON SELECTED PLACE CHANGE
                /**
                 * call the [updateMapOnRouteStopSelected] method
                 */
                /*viewModelRoute.setSelectedRouteNodePosition(
                    coordinates = coordinates!!
                )*/

            }
        })

        /**
         * check the walk mode as the default mode for route planning
         */
        handleRouteModeSelect(index = 0)

        /**
         * add an [OnButtonCheckedListener] for the route transport mode selector
         * button group
         * on check change update the transport mode
         */
        binding.routeSelectWalk.setOnClickListener { _ ->

            handleRouteModeSelect(
                index = 0
            )
        }

        binding.routeSelectCar.setOnClickListener { _ ->

            handleRouteModeSelect(
                index = 1
            )
        }

        binding.startNavigation.setOnClickListener { l ->

            viewModelRoute.startNavigation()
        }

        /**
         * add an [OnclickListener] for the [dismissRoutePlan] button
         * when clicked reset the route [com.example.travel_mate.ui.ViewModelMain.resetRoute]
         */
        binding.dismissRoutePlan.setOnClickListener { l ->

            viewModelRoute.resetRoute()

            //viewModelRoute.returnToPrevContent()

            viewModelRoute.resetSelectedPlace()
        }

        binding.optimizeRoute.setOnClickListener { l ->
            viewModelRoute.optimizeRoute()
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
    fun handleRouteStopsChange(route: RouteInfoRoutePresentationModel) {

        showRouteData(
            routeInfo = route
        )
    }

    private fun handleRouteModeSelect(index: Int) {

        if (index == 0) {

            viewModelRoute.setRouteTransportMode(
                transportModeIndex = index
            )

            binding.routeSelectWalk.isChecked = true
            binding.routeSelectCar.isChecked = false
        }
        if (index == 1) {

            viewModelRoute.setRouteTransportMode(
                transportModeIndex = index
            )

            binding.routeSelectCar.isChecked = true
            binding.routeSelectWalk.isChecked = false
        }
    }


    /** [showRouteData]
     * refresh the [routeStopsAdapter]'s list with the currently added places
     * set the calculated full duration of the route (by car/on foot)
     * as the text of the route mode selection buttons
     */
    private fun showRouteData(routeInfo: RouteInfoRoutePresentationModel) {

        this.routeStops.clear()

        this.routeStops.addAll(routeInfo.infoNodes)

        routeStopsAdapter.notifyDataSetChanged()

        val fullWalkDuration = routeInfo.fullWalkDuration.toString() + " " + resources?.getString(R.string.duration_string)
        val fullCarDuration = routeInfo.fullCarDuration.toString() + " " + resources?.getString(R.string.duration_string)

        binding.routeSelectWalk.text =  fullWalkDuration
        binding.routeSelectCar.text =  fullCarDuration
    }

//_________________________________________________________________________________________________________________________
// END OF METHODS FOR ROUTE
//_________________________________________________________________________________________________________________________


}