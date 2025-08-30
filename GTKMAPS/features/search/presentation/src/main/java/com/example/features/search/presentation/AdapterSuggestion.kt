package com.example.features.search.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.features.search.presentation.R

/** [com.example.travel_mate.AdapterSuggestion]
 * Defines an adapter for the [com.google.android.material.textfield.MaterialAutoCompleteTextView]
 * responsible for searching for possible start points.
 *
 * Accepts the [context], the layout resource of the items and the items' [List] as parameters
 */
class AdapterSuggestion(private val context: Context, private val resource: Int, private var objects: ArrayList<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getItem(position: Int): String? {
        return super.getItem(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listItemView: View? = convertView
        if (listItemView== null){
            listItemView = LayoutInflater.from(context).inflate(resource,parent,false)
        }
        val currentItem = getItem(position)
        val textView = listItemView?.findViewById<TextView>(com.example.features.search.presentation.R.id.layout_autocomplete_content)
        textView?.text = currentItem


        return listItemView!!
    }
}