package com.vaishnavsm.opposmartfill.backend

import android.content.Context
import java.lang.Exception

class PersonalDataServer(context : Context) {

    private val dataMap = mutableMapOf<String, String>()
    init {
        try{
            val inputStream = context.getSharedPreferences("DATA", 0)
        } catch (e : Exception) {

        }
        dataMap["name"] = "Dummy Name"
    }

    fun getData(key : String) : String?{
        return if(dataMap.containsKey(key)) dataMap[key]
        else null
    }

    fun addData(key : String, value : String){
        dataMap[key] = value
    }
}