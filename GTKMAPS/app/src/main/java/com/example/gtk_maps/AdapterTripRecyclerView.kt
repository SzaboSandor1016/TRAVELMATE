package com.example.gtk_maps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gtk_maps.databinding.LayoutTripRecyclerViewItemBinding

class AdapterTripRecyclerView(private val trips: List<String>): RecyclerView.Adapter<AdapterTripRecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    interface OnClickListener{
        fun onClick(position: Int)
    }

    class ViewHolder(val binding: LayoutTripRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterTripRecyclerView.ViewHolder {

        val binding = LayoutTripRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterTripRecyclerView.ViewHolder, position: Int) {

        val item = trips[position]

        holder.binding.tripTitle.setText(item)

        holder.binding.tripTitle.setOnClickListener{ l ->

            onClickListener?.onClick(position)
        }
    }

    override fun getItemCount(): Int = trips.size

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }
}