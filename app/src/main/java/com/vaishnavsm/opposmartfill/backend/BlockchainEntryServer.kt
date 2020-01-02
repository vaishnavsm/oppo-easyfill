package com.vaishnavsm.opposmartfill.backend

import android.content.Context
import android.content.SharedPreferences
import java.lang.Exception

class BlockchainEntryServer(context : Context) {

    private val dataMap = mutableMapOf<String, String>()
    private lateinit var input : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    init {
        try{
            input = context.getSharedPreferences("BLOCK", 0)
        } catch (e : Exception) {

        }
        val savedKeys = input.getStringSet("_keyset_", setOf()) ?: setOf<String>()
        for(key in savedKeys){
            dataMap[key] = input.getString(key, "") ?: ""
        }
    }

    fun getData(key : String) : String?{
        return if(dataMap.containsKey(key)) dataMap[key]
        else null
    }

    fun getAllKeys() : Set<String> {
        return dataMap.keys.toSet()
    }

    fun addData(key : String, value : String){
        dataMap[key] = value
    }

    fun saveState(){
        editor = input.edit()
        editor.putStringSet("_keyset_", dataMap.keys.toSet())
        for(key in dataMap.keys.toSet()){
            editor.putString(key, dataMap[key])
        }
        editor.apply()
    }
}