package com.example.travel_mate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_mate.databinding.LayoutContributorsRecyclerViewItemBinding

/**[com.example.travel_mate.AdapterContributorsRecyclerView]
 * An adapter for the recycler view that allows listing and selecting
 * contributors of the specific [Trip]
 * defines an [OnClickListener] that returns the index of the selected item
 * a contributor list item contains just the username
 *
 * Accepts a [List] of multiple [ViewModelUser.Contributor]
 */
class AdapterContributorsRecyclerView(private val contributors: List<ViewModelUser.Contributor>):
    RecyclerView.Adapter<AdapterContributorsRecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    interface OnClickListener{
        fun onClick(position: Int)
    }

    class ViewHolder(val binding: LayoutContributorsRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterContributorsRecyclerView.ViewHolder {

        val binding = LayoutContributorsRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterContributorsRecyclerView.ViewHolder, position: Int) {

        val item = contributors[position]

        holder.binding.contributorUsername.isChecked = item.selected

        holder.binding.contributorUsername.setText(item.data.second)

        holder.binding.contributorUsername.setOnClickListener{ l ->

            onClickListener?.onClick(
                position = position
            )
        }
    }

    override fun getItemCount(): Int = contributors.size

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }
}