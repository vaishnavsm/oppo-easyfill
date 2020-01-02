package com.vaishnavsm.opposmartfill.backend

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DataServer {
    interface DataServerInterface{
        fun OnGetFormCallback(form : Map<String, List<String>>)
    }
    fun getForm(formId : String, context: Context, callback : DataServerInterface){
        // (personal or specific, required, suggested value (ML generated)
        val URL = "http://ec2-54-218-8-95.us-west-2.compute.amazonaws.com:8000/get_form/"
        val reqq = Volley.newRequestQueue(context)
        val req = StringRequest(Request.Method.GET, "$URL?form-id=$formId", Response.Listener { response ->
            Log.d("DATA REC", response)
            Log.d("DATA REC", response.length.toString())
            Log.d("DATA REC", response.substring(response.indexOf('{'),response.lastIndexOf('}')+1).length.toString())
            val form = mutableMapOf<String, List<String>>()
            val o = JSONObject(response.substring(1,response.length-1).replace("\\\"","\""))
            for(key in o.keys()){
                val l = o.getJSONArray(key)
                val list = mutableListOf<String>()
                for(i in 0 until l.length()){
                    list.add(l.getString(i))
                }
                form[key] = list.toList()
            }
            callback.OnGetFormCallback(form)
        }, Response.ErrorListener { error ->
            Log.d("DATA ERROR", error.message?:"")
        })
        reqq.add(req)
//        return mapOf(
//
//                "name" to listOf("personal", "no"),
//                "age" to listOf("personal", "no"),
//                "phone" to listOf("personal", "no"),
//                "sex" to listOf("personal", "no"),
//                "loan type" to listOf("specific", "yes"),
//                "loan amount" to listOf("specific", "yes")
//        )
    }
}