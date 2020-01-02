package com.vaishnavsm.opposmartfill

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.View
import android.widget.*
import com.vaishnavsm.opposmartfill.backend.BackendController
import com.vaishnavsm.opposmartfill.backend.PersonalDataServer
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt
import kotlin.random.Random


class FormActivity : AppCompatActivity(), EditDialog.EditDialogListener {
    var currentlyEditing = -1
    private val dataSet = arrayListOf<List<String>>()
    private lateinit var adapter : ListAdapter
    private lateinit var permissions : ArrayList<String>
    private var finishing = false
    override fun onDialodEntryReturn(data: String) {
        Log.d("DataReturned", "Got Back $data")
        if (currentlyEditing==-1){
            return
        }
        val newData = dataSet[currentlyEditing].toMutableList()
        newData[1] = data
        newData[5] = if(newData[2] == "personal" && newData[1].isNotEmpty()) "1" else if(newData[1].isEmpty()) "0" else (Math.random()*0.65+0.3).toString()
        dataSet[currentlyEditing] = newData.toList()
        adapter.notifyDataSetChanged()
        currentlyEditing = -1

        if(finishing) onSubmitClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        val extras = intent.extras
        if(extras == null || extras.isEmpty()) throw Exception("Form Data Unsatisfactory!")
        val form = extras.getSerializable("FORM") as HashMap<String, List<String>>
        Log.d("FormBuilder", "Got Form $form")
        permissions = extras.getSerializable("GRANTED_PERMISSIONS") as ArrayList<String>
        Log.d("FormBuilder", "Got Permissions $permissions")

        val list = findViewById<ListView>(R.id.field_list)
        adapter = ListAdapter(this, dataSet)
        list.adapter = adapter
        for(v in form){
            val data = when {
                permissions.contains(v.key) -> BackendController.mPersonalDataServer.getData(v.key)
                v.value[2] != "None" -> v.value[2]
                else -> ""
            } ?:""
            adapter.add(listOf(v.key,data)+v.value+listOf(if(v.value[0]=="personal" && data.isNotEmpty()) "1" else if (v.value[0] == "specific" && v.value[2] != "None") "0.6" else "0"))
        }
        list.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            finishing = false
            val dialog = EditDialog(adapter.getItem(i)?.get(1)?:"")
            currentlyEditing = i
            dialog.show(supportFragmentManager, "EditListItem")
        }

        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener {
            onSubmitClick()

        }
    }

    private fun onSubmitClick() {
        finishing = true
        var f = false
        for(item in dataSet){
            if((item[3]=="yes" && item[1].isEmpty())) {
//                Toast.makeText(this, "Please Complete The Form!", Toast.LENGTH_SHORT).show()
                f = true
                val i = adapter.getPosition(item)
                val dialog = EditDialog(adapter.getItem(i)?.get(1)?:"")
                currentlyEditing = i

                dialog.show(supportFragmentManager, "EditListItem")
                break
            }
        }

        if(!f){
            for(item in dataSet){
                if(item[2] == "personal" && permissions.contains(item[0])) BackendController.mPersonalDataServer.addData(item[0], item[1])
            }
            BackendController.mPersonalDataServer.saveState()
            val rseed = (Math.random()*100000).roundToInt()
            val timestamp = Timestamp(System.currentTimeMillis())
            BackendController.mBlockchainEntryServer.addData((dataSet.toString()+rseed).hash(), "Submitted Data $dataSet with salt $rseed, added to chain with timestamp $timestamp")
            BackendController.mBlockchainEntryServer.saveState()
            finish()
        }
    }
}

private fun String.hash(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}
