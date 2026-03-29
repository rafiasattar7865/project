package com.example.campuslife.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.campuslife.R

class SpinnerAdapter(context: Context, items: List<String>) :
    ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_spinner_row, parent, false)

        val itemText = view.findViewById<TextView>(R.id.txtSpinnerItem)
        val value = getItem(position)

        itemText.text = value

        // Slay styling for the text inside the dropdown
        if (value == "+ Add New") {
            itemText.setTextColor(context.getColor(android.R.color.holo_green_dark))
        } else {
            itemText.setTextColor(context.getColor(android.R.color.black))
        }

        return view
    }
}