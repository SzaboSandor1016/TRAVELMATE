package com.example.features.route.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.features.route.presentation.databinding.LayoutRouteStopItemBinding
import com.example.features.route.presentation.models.CoordinatesRoutePresentationModel
import com.example.features.route.presentation.models.RouteInfoNodeRoutePresentationModel
import com.example.core.ui.R

/** [com.example.travel_mate.AdapterRouteStopsRecyclerView]
 * An adapter for a [RecyclerView] containing the [com.example.domain.models.RouteNode]s (the stops)
 * of a [com.example.domain.models.Route]
 * defines an [OnClickListener] that returns the uuid and the [com.example.model.Coordinates] of the [com.example.model.Place]
 * that the clicked item is associated with.
 *
 * Accepts a [List] of [com.example.domain.models.RouteNode]s and a route transport mode [String] as parameters,
 * and fills the list accordingly
 * (for example if the [mode] is "driving-car", the [MaterialTextView]s containing the
 * partial duration and distance will be filled with the distance and duration values
 * which tells how much time does it take to reach that stop, and what will be the distance travelled)
 */
class AdapterRouteStopsRecyclerView(private val routeStops: List<RouteInfoNodeRoutePresentationModel>):
    RecyclerView.Adapter<AdapterRouteStopsRecyclerView.ViewHolder>() {

        private lateinit var context: Context

    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(uuid: String?, coordinates: CoordinatesRoutePresentationModel?)
    }

    class ViewHolder(val binding: LayoutRouteStopItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {


        val binding =
            LayoutRouteStopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        this.context = parent.context

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = routeStops[position]

        val durationString: String = item.duration.toString() + " " + context.getString(R.string.duration_string)
        val distanceString: String = item.distance.toString() + " " + context.getString(R.string.distance_string)

        holder.binding.routeStopName.setText(item.name)

        holder.binding.routeStopDuration.setText(durationString)

        holder.binding.routeStopDistance.setText(distanceString)

        holder.binding.itemRoot.setOnClickListener { l ->

            onClickListener?.onClick(
                uuid = item.placeUUID,
                coordinates = item.coordinates
            )
        }
    }

    override fun getItemCount(): Int {
        return routeStops.size
    }

    fun setOnClickListener(listener: OnClickListener?) {

        this.onClickListener = listener
    }
}