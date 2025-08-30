package com.example.features.savetrip.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.features.savetrip.presentation.databinding.LayoutAssignablePlaceRecyclerViewBinding
import com.example.features.savetrip.presentation.models.PlaceSaveTripPresentationModel

class AdapterAssignablePlaceRecyclerView(private val places: List<PlaceSaveTripPresentationModel>):
    RecyclerView.Adapter<AdapterAssignablePlaceRecyclerView.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null;

    interface OnItemClickListener {

        fun onItemClick(placeUUID: String)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding: LayoutAssignablePlaceRecyclerViewBinding =
            LayoutAssignablePlaceRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = places[position]

        holder.binding.assignablePlaceId.setText(item.name)

        holder.binding.assignablePlaceId.setOnClickListener { _ ->

            onItemClickListener?.onItemClick(item.uUID)
        }
    }

    override fun getItemCount(): Int {
        return places.size
    }

    class ViewHolder(val binding: LayoutAssignablePlaceRecyclerViewBinding):
        RecyclerView.ViewHolder(binding.root){}

    fun setOnItemClickListener(listener: OnItemClickListener) {

        this.onItemClickListener = listener;
    }
}