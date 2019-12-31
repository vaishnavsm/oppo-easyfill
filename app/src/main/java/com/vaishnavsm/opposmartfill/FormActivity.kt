package com.vaishnavsm.opposmartfill

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.vaishnavsm.opposmartfill.backend.BackendController
import com.vaishnavsm.opposmartfill.backend.PersonalDataServer
import java.lang.Exception


class FormActivity : AppCompatActivity(), EditDialog.EditDialogListener {
    var currentlyEditing = -1
    private val dataSet = arrayListOf<List<String>>()
    private lateinit var adapter : ListAdapter
    override fun onDialodEntryReturn(data: String) {
        Log.d("DataReturned", "Got Back $data")
        if (currentlyEditing==-1){
            return
        }
        val newData = dataSet[currentlyEditing].toMutableList()
        newData.set(1, data)
        dataSet[currentlyEditing] = newData.toList()
        adapter.notifyDataSetChanged()
        currentlyEditing = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        val extras = intent.extras
        if(extras == null || extras.isEmpty()) throw Exception("Form Data Unsatisfactory!")
        val form = extras.getSerializable("FORM") as HashMap<String, List<String>>
        Log.d("FormBuilder", "Got Form $form")
        val permissions = extras.getSerializable("GRANTED_PERMISSIONS") as ArrayList<String>
        Log.d("FormBuilder", "Got Permissions $permissions")

        val list = findViewById<ListView>(R.id.field_list)
        adapter = ListAdapter(this, dataSet)
        list.adapter = adapter
        for(v in form){
            adapter.add(listOf(v.key,if(permissions.contains(v.key)) BackendController.mPersonalDataServer.getData(v.key)?:"" else "")+v.value)
        }
        list.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val dialog = EditDialog(adapter.getItem(i)?.get(1)?:"")
            currentlyEditing = i
            dialog.show(supportFragmentManager, "EditListItem")
        }

        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener {
            for(item in dataSet){
                if(item[2] == "personal" && permissions.contains(item[0])) BackendController.mPersonalDataServer.addData(item[0], item[1])
            }
            BackendController.mPersonalDataServer.saveState()
            finish()
        }
    }
}
