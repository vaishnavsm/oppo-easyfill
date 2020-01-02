package com.vaishnavsm.opposmartfill.backend

import android.content.Context

object BackendController {
    var mDataServer : DataServer = DataServer()
    lateinit var mPersonalDataServer : PersonalDataServer
    lateinit var mBlockchainEntryServer: BlockchainEntryServer
    var mBackgroundData = mutableMapOf<String, String>()
}