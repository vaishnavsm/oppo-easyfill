package com.vaishnavsm.opposmartfill

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.*
import kotlin.collections.ArrayList


class EditDialog(private val currText : String) : DialogFragment() {
    private lateinit var listener: EditDialogListener
    private  lateinit var text : EditText

    interface EditDialogListener {
        fun onDialodEntryReturn(data : String)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {

            val builder = AlertDialog.Builder(it)
            // Set the dialog title
            builder.setTitle(R.string.dialog_enter_text)
            val inflater = requireActivity().layoutInflater
            val root = inflater.inflate(R.layout.dialog_edit, null)
            builder.setView(root)

            text = root.findViewById<EditText>(R.id.entry_dialog)
            if(currText.isNotBlank()) {
                text.text.clear()
                text.text.append(currText)
            }


            builder.setPositiveButton(R.string.yes
            ) { _, _ ->
                listener.onDialodEntryReturn(text.text.toString())
            }
                .setNegativeButton(R.string.no) {_, _ -> }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            try {
                startActivityForResult(intent, 100)
            } catch (e : ActivityNotFoundException) {
                Toast.makeText(context?.applicationContext, "Speech Recognition Unsupported :(", Toast.LENGTH_SHORT).show()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as EditDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement EditDialogListener"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            if(resultCode == RESULT_OK && data != null){
                val result : ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                Log.d("RECOGNIZER", "Got $result")
//                val text = context.findViewById<EditText>(R.id.entry_dialog)
                if(result[0].isNotBlank()){
                    text.text.clear()
                    text.text.append(result[0])
                }
                else {
                    Log.d("It's Null", "Yeah No")
                }
            }
        }
    }
}
