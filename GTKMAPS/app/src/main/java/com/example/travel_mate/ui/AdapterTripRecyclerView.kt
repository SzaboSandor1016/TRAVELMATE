package com.example.travel_mate.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_mate.data.TripRepositoryImpl
import com.example.travel_mate.databinding.LayoutTripRecyclerViewItemBinding

/** [com.example.travel_mate.AdapterTripRecyclerView]
 * Defines an adapter for the [androidx.recyclerview.widget.RecyclerView] responsible for listing
 * saved and shared [com.example.travel_mate.data.Trip]s
 *
 * Defines an [OnClickListener] for the list items that returns the position of the specific trip clicked
 *
 * Accepts the [List] of the [TripRepositoryImpl.TripIdentifier]s of the trips that need to be shown as parameter
 */
class AdapterTripRecyclerView(private val trips: List<TripRepositoryImpl.TripIdentifier>): RecyclerView.Adapter<AdapterTripRecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    interface OnClickListener{
        fun onClick(position: Int)
    }

    class ViewHolder(val binding: LayoutTripRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = LayoutTripRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = trips.elementAt(position)

        holder.binding.tripTitle.setText(item.title)

        holder.binding.tripTitle.setOnClickListener{ l ->

            onClickListener?.onClick(position)
        }
    }

    override fun getItemCount(): Int = trips.size

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }
}