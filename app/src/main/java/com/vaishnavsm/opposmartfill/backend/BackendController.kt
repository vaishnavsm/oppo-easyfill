package com.vaishnavsm.opposmartfill.backend

import android.content.Context

object BackendController {
    var mDataServer : DataServer = DataServer()
    lateinit var mPersonalDataServer : PersonalDataServer
    var mBackgroundData = mutableMapOf<String, String>()
}