package com.vaishnavsm.opposmartfill.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.vaishnavsm.opposmartfill.FormActivity
import com.vaishnavsm.opposmartfill.PermissionDialog
import com.vaishnavsm.opposmartfill.R
import com.vaishnavsm.opposmartfill.backend.BackendController

class HomeFragment : Fragment(), PermissionDialog.PermissionDialogListener {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        homeViewModel =
//            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        val submitButton = root.findViewById<Button>(R.id.button)
        submitButton.setOnClickListener { onFindButtonClick(it) }
        return root
    }

    var form : Map<String, List<String>>? = null
    fun onFindButtonClick(v : View){
        form = BackendController.mDataServer.getForm("abc")
        val neededPermissions = form!!.entries.filter{ it.value[0] == "personal"  }.map{ it.key }
        if(neededPermissions.isNotEmpty()){
            val permissions_dialog = PermissionDialog(neededPermissions)
            permissions_dialog.setTargetFragment(this, 1)
            permissions_dialog.show(fragmentManager?.beginTransaction(), "permissions")
        }
        else{
            processWithPermissions(form!!, neededPermissions)
        }
    }

    private fun processWithPermissions(form: Map<String, List<String>>, neededPermissions: List<String>) {
        Log.d("Proceeding", "Going To Fill Form View With Permissions: $neededPermissions")
        val intent = Intent(context, FormActivity::class.java)
        intent.putExtra("FORM", HashMap(form))
        intent.putExtra("GRANTED_PERMISSIONS", ArrayList(neededPermissions))
        activity?.startActivity(intent)
    }

    override fun onDialogGrantClick(list: List<String>) {
        if(form.isNullOrEmpty()) throw Exception("Form Was Blank When Continue Was Called")
        else processWithPermissions(form!!, list)
    }
}