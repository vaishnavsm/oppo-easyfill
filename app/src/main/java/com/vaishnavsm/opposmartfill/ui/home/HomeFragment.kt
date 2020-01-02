package com.vaishnavsm.opposmartfill.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.vaishnavsm.opposmartfill.FormActivity
import com.vaishnavsm.opposmartfill.PermissionDialog
import com.vaishnavsm.opposmartfill.R
import com.vaishnavsm.opposmartfill.backend.BackendController
import com.vaishnavsm.opposmartfill.backend.DataServer
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.sql.Timestamp
import kotlin.math.roundToInt

class HomeFragment : Fragment(), PermissionDialog.PermissionDialogListener, DataServer.DataServerInterface {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var formIdText: EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val submitButton = root.findViewById<Button>(R.id.button)
        submitButton.setOnClickListener { onFindButtonClick(it) }
        formIdText = root.findViewById(R.id.editText)
        if(BackendController.mBackgroundData.contains("form-id")){
            formIdText.text.insert(0, BackendController.mBackgroundData["form-id"])
        }
        return root
    }

    var form : Map<String, List<String>>? = null
    fun onFindButtonClick(v : View){

        BackendController.mDataServer.getForm(formIdText.text.toString(), context!!, this)

    }

    override fun OnGetFormCallback(lform: Map<String, List<String>>){
        form = lform
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
        else {
            val rseed = (Math.random()*100000).roundToInt()

            val timestamp = Timestamp(System.currentTimeMillis())
            val pk = KeyPairGenerator.getInstance("rsa").genKeyPair().public
            val publicKey = pk.key()
            BackendController.mBlockchainEntryServer.addData((list.toString()+rseed).hash(), "Granted Permissions $list For ${formIdText.text} with salt $rseed and timestamp $timestamp. Added to chain and received transaction public key of counterparty $publicKey")
            BackendController.mBlockchainEntryServer.saveState()
            processWithPermissions(form!!, list)
        }
    }
}

private fun String.hash(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun PublicKey.key() = Base64.encodeToString(this.encoded, Base64.DEFAULT)!!