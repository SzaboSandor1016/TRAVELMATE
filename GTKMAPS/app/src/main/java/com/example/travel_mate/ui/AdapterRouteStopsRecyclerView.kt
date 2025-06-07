package com.example.travel_mate.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_mate.data.Coordinates
import com.example.travel_mate.R
import com.example.travel_mate.data.RouteNode
import com.example.travel_mate.databinding.LayoutRouteStopItemBinding

/** [com.example.travel_mate.AdapterRouteStopsRecyclerView]
 * An adapter for a [androidx.recyclerview.widget.RecyclerView] containing the [RouteNode]s (the stops)
 * of a [com.example.travel_mate.data.Route]
 * defines an [OnClickListener] that returns the uuid and the [Coordinates] of the [com.example.travel_mate.data.Place]
 * that the clicked item is associated with.
 *
 * Accepts a [List] of [RouteNode]s and a route transport mode [String] as parameters,
 * and fills the list accordingly
 * (for example if the [mode] is "driving-car", the [MaterialTextView]s containing the
 * partial duration and distance will be filled with the distance and duration values
 * which tells how much time does it take to reach that stop, and what will be the distance travelled)
 */
class AdapterRouteStopsRecyclerView(private val routeStops: List<RouteNode>, var mode: String):
    RecyclerView.Adapter<AdapterRouteStopsRecyclerView.ViewHolder>() {

        private lateinit var context: Context

    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(uuid: String?, coordinates: Coordinates?)
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

        var durationString: String = ""
        var distanceString: String = ""

        when(mode) {
            "foot-walking" -> {
                durationString =
                    item.walkDuration.toString() + " " + context.getString(R.string.duration_string)
                distanceString =
                    item.walkDistance.toString() + " " + context.getString(R.string.distance_string)
            }
            "driving-car" -> {
                durationString =
                    item.carDuration.toString() + " " + context.getString(R.string.duration_string)
                distanceString =
                    item.carDistance.toString() + " " + context.getString(R.string.distance_string)
            }
        }

        holder.binding.routeStopName.setText(item.name)

        holder.binding.routeStopDuration.setText(durationString)

        holder.binding.routeStopDistance.setText(distanceString)

        holder.binding.itemRoot.setOnClickListener { l ->

            onClickListener?.onClick(
                uuid = item.placeUUID,
                coordinates = item.coordinate
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