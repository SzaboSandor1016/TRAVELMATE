package com.example.features.trips.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.features.trips.presentation.databinding.LayoutTripRecyclerViewItemBinding
import com.example.features.trips.presentation.models.TripIdentifierTripsPresentationModel

/** [com.example.travel_mate.AdapterTripRecyclerView]
 * Defines an adapter for the [RecyclerView] responsible for listing
 * saved and shared [com.example.model.Trip]s
 *
 * Defines an [OnClickListener] for the list items that returns the position of the specific trip clicked
 *
 * Accepts the [List] of the [com.example.data.repositories.TripRepositoryImpl.TripIdentifier]s of the trips that need to be shown as parameter
 */
class AdapterTripRecyclerView(private val trips: List<TripIdentifierTripsPresentationModel>): RecyclerView.Adapter<AdapterTripRecyclerView.ViewHolder>() {

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

        val item = trips[position]

        val title = when(item) {

            is TripIdentifierTripsPresentationModel.Local -> item.title

            is TripIdentifierTripsPresentationModel.Remote -> item.title

            is TripIdentifierTripsPresentationModel.Default -> ""
        }

        holder.binding.tripTitle.setText(title)

        holder.binding.tripTitle.setOnClickListener{ l ->

            onClickListener?.onClick(position)
        }
    }

    override fun getItemCount(): Int = trips.size

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }
}