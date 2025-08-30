package com.example.features.inspect.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.features.inspect.presentation.databinding.LayoutDayOfInspectTripRecyclerViewItemBinding
import com.example.features.inspect.presentation.models.DayOfTripInfoInspectTripPresentationModel

class AdapterDayOfInspectTripRecyclerView(private val days: List<DayOfTripInfoInspectTripPresentationModel>):
    RecyclerView.Adapter<AdapterDayOfInspectTripRecyclerView.ViewHolder>()
{

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener{

        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = LayoutDayOfInspectTripRecyclerViewItemBinding.inflate(
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

        val item = days[position]

        holder.binding.inspectedDayId.setText(item.label)

        holder.binding.inspectedDayId.setOnClickListener { _ ->

            this.onItemClickListener?.onItemClick(position)
        }

        holder.binding.inspectedDayId.isChecked = item.selected
    }

    override fun getItemCount(): Int {

        return days.size
    }

    fun addOnItemClickListener(listener: OnItemClickListener) {

        this.onItemClickListener = listener
    }

    class ViewHolder(val binding: LayoutDayOfInspectTripRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)
}