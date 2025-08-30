package com.example.features.savetrip.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.features.savetrip.presentation.databinding.LayoutDaysRecyclerViewBinding
import com.example.features.savetrip.presentation.models.DayOfTripSaveTripPresentationModel

class AdapterDaysRecyclerView(private val days: List<DayOfTripSaveTripPresentationModel>):
RecyclerView.Adapter<AdapterDaysRecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    interface OnClickListener {

        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = LayoutDaysRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        );
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = days[position]

        holder.binding.dayLabel.setText(item.label)

        holder.binding.dayLabel.isChecked = item.selected

        holder.binding.dayLabel.setOnClickListener { l ->

            onClickListener?.onClick(position)
        }
    }

    override fun getItemCount(): Int = days.size

    class ViewHolder(val binding: LayoutDaysRecyclerViewBinding): RecyclerView.ViewHolder(binding.root)

    fun setOnClickListener(listener: OnClickListener) {

        this.onClickListener = listener;
    }
}