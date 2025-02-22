package com.example.gtk_maps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView

class AdapterSuggestion(private val context: Context, private val resource: Int, private var objects: MutableList<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listItemView: View? = convertView
        if (listItemView== null){
            listItemView = LayoutInflater.from(context).inflate(resource,parent,false)
        }
        var currentItem = getItem(position)
        var textView = listItemView?.findViewById<TextView>(R.id.layout_autocomplete_content)
        textView?.text = currentItem


        return listItemView!!
    }
}