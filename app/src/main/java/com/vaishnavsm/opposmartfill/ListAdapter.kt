package com.vaishnavsm.opposmartfill

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListAdapter(context : Context, list : ArrayList<List<String>>) : ArrayAdapter<List<String>>(context, R.layout.list_item, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        return super.getView(position, convertView, parent)
        val inflater= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
        val personal = view.findViewById<TextView>(R.id.item_personal)
        val name = view.findViewById<TextView>(R.id.item_name)
        val value = view.findViewById<TextView>(R.id.item_value)

        personal.text = this.getItem(position)?.get(2) ?: ""
        name.text = this.getItem(position)?.get(0) ?: ""
        value.text = this.getItem(position)?.get(1) ?: ""

        return view

    }
}