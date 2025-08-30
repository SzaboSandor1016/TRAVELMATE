package com.example.features.savetrip.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.features.savetrip.presentation.databinding.LayoutAddPlaceToDayRecyclerViewBinding
import com.example.features.savetrip.presentation.databinding.LayoutPlacesOfDayRecyclerViewBinding
import com.example.features.savetrip.presentation.models.PlaceSaveTripPresentationModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView


class AdapterPlacesOfDayRecyclerView(private val places: List<PlaceSaveTripPresentationModel>):
RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var addPlaceOnClickListener: AddPlaceOnClickListener? = null

    private var removePlaceOnclickListener: RemovePlaceFromDayOnClickListener? = null

    interface AddPlaceOnClickListener {

        fun onClick()
    }

    interface RemovePlaceFromDayOnClickListener {

        fun onClick(placeUUID: String)
    }

    /*sealed interface ListItem {

        fun getItemType(): Int

        data class PlaceItem(val type: Int, val text: String): ListItem {
            override fun getItemType(): Int {
                return this.type;
            }
        }

        data class AddItem(val type: Int): ListItem {
            override fun getItemType(): Int {
                return this.type;
            }
        }
    }*/

    /*abstract class BaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: ListItem)
    }*/

    class PlaceViewHolder(val binding: LayoutPlacesOfDayRecyclerViewBinding): RecyclerView.ViewHolder(binding.root) {
    }

    class AddViewHolder(val binding: LayoutAddPlaceToDayRecyclerViewBinding): RecyclerView.ViewHolder(binding.root) {

    }

    private val VIEW_TYPE_DEFAULT = 0;
    private val VIEW_TYPE_ADD = 1;

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        if(viewType == 1) {
            val binding = LayoutAddPlaceToDayRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)

            return AddViewHolder(binding)
        } else {
            val binding = LayoutPlacesOfDayRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
            return PlaceViewHolder(binding);
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (position != places.size) {

            val item = places[position]

            (holder as PlaceViewHolder).binding.placeId.setText(item.name.toString())

            holder.binding.removePlaceFromDay.setOnClickListener {

                removePlaceOnclickListener?.onClick(item.uUID)
            }

        } else {

            (holder as AddViewHolder).binding.addPlace.setOnClickListener { l ->

                addPlaceOnClickListener?.onClick()
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if(position == places.size) VIEW_TYPE_ADD else VIEW_TYPE_DEFAULT
    }

    override fun getItemCount(): Int {
        return places.size + 1
    }

    class ViewHolder(binding: ViewBinding): RecyclerView.ViewHolder(when(binding) {
        is LayoutPlacesOfDayRecyclerViewBinding -> binding.root
        else -> (binding as LayoutAddPlaceToDayRecyclerViewBinding).root
    }) {
    }


    fun setAddPlaceOnClickListener(listener: AddPlaceOnClickListener) {

        this.addPlaceOnClickListener = listener;
    }

    fun setRemovePlaceFromDayOnClickListener(listener: RemovePlaceFromDayOnClickListener) {

        this.removePlaceOnclickListener = listener;
    }
}
