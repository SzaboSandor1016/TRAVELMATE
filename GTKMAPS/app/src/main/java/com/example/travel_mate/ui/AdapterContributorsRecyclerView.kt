package com.example.travel_mate.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travel_mate.data.Contributor
import com.example.travel_mate.databinding.LayoutContributorsRecyclerViewItemBinding

/**[com.example.travel_mate.AdapterContributorsRecyclerView]
 * An adapter for the recycler view that allows listing and selecting
 * contributors of the specific [com.example.travel_mate.data.Trip]
 * defines an [OnClickListener] that returns the index of the selected item
 * a contributor list item contains just the username
 *
 * Accepts a [List] of multiple [ViewModelUser.Contributor]
 */
class AdapterContributorsRecyclerView(private val contributors: List<Contributor>):
    RecyclerView.Adapter<AdapterContributorsRecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnClickListener{
        fun onClick(uid: String)
    }
    interface OnItemLongClickListener{
        fun onItemLongClick(uid: String)
    }

    class ViewHolder(val binding: LayoutContributorsRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = LayoutContributorsRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = contributors[position]

        holder.binding.contributorUsername.isChecked = item.selected!!

        holder.binding.contributorUsername.setText(item.username)

        holder.binding.contributorUsername.setOnClickListener{ l ->

            onClickListener?.onClick(
                uid = item.uid.toString()
            )
        }
        holder.binding.contributorUsername.setOnLongClickListener { l ->

            onItemLongClickListener?.onItemLongClick(
                uid = item.uid.toString()
            )
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int = contributors.size

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        this.onItemLongClickListener = listener
    }
}