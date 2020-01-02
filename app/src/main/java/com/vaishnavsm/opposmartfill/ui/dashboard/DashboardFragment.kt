package com.vaishnavsm.opposmartfill.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.vaishnavsm.opposmartfill.ListAdapter
import com.vaishnavsm.opposmartfill.R
import com.vaishnavsm.opposmartfill.backend.BackendController

class DashboardFragment : Fragment() {

    private lateinit var recyclerAdapter: ArrayAdapter<String>
    private val recyclerData = arrayListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val listView = root.findViewById<ListView>(R.id.personalData)

        recyclerAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1 , recyclerData)

        listView.adapter = recyclerAdapter

        for(key in BackendController.mPersonalDataServer.getAllKeys()){
            recyclerAdapter.add("$key -> ${BackendController.mPersonalDataServer.getData(key)}")
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        recyclerAdapter.clear()
        for(key in BackendController.mPersonalDataServer.getAllKeys()){
            recyclerAdapter.add("$key -> ${BackendController.mPersonalDataServer.getData(key)}")
        }
    }
}