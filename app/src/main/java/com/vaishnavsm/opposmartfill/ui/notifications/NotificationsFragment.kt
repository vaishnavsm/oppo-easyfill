package com.vaishnavsm.opposmartfill.ui.notifications

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
import com.vaishnavsm.opposmartfill.R
import com.vaishnavsm.opposmartfill.backend.BackendController

class NotificationsFragment : Fragment() {
    private lateinit var listAdapter: ArrayAdapter<String>
    private val listData = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        listAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, listData)

        val list = root.findViewById<ListView>(R.id.notificationList)
        list.adapter = listAdapter

        for(key in BackendController.mBlockchainEntryServer.getAllKeys()){
            listAdapter.add("Hash: $key | Message: ${BackendController.mBlockchainEntryServer.getData(key)}")
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        listAdapter.clear()
        for(key in BackendController.mBlockchainEntryServer.getAllKeys()){
            listAdapter.add("Hash: $key | Message: ${BackendController.mBlockchainEntryServer.getData(key)}")
        }
    }
}