package com.vaishnavsm.opposmartfill.backend

class DataServer {
    fun getForm(formId : String) : Map<String, List<String>>{
        return mapOf(
                "name" to listOf("personal", "no"),
                "age" to listOf("personal", "no"),
                "phone" to listOf("personal", "no"),
                "sex" to listOf("personal", "no"),
                "loan type" to listOf("specific", "yes"),
                "loan amount" to listOf("specific", "yes")
        )
    }
}