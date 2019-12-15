package com.vaishnavsm.opposmartfill

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kotlin.ClassCastException as ClassCastException1


class PermissionDialog(private val neededPermissions: List<String>) : DialogFragment() {
    private lateinit var listener: PermissionDialogListener


    interface PermissionDialogListener {
        fun onDialogGrantClick(list : List<String>)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val selectedItems = ArrayList<Int>() // Where we track the selected items

            val builder = AlertDialog.Builder(it)
            // Set the dialog title
            builder.setTitle(R.string.dialog_permission_head)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(neededPermissions.toTypedArray(), null
                ) { _, which, isChecked ->
                    if (isChecked) {
                        // If the user checked the item, add it to the selected items
                        selectedItems.add(which)
                    } else if (selectedItems.contains(which)) {
                        // Else, if the item is already in the array, remove it
                        selectedItems.remove(Integer.valueOf(which))
                    }
                }
                // Set the action buttons
                .setPositiveButton(R.string.dialog_grant_permission
                ) { _, id -> run {
                    listener.onDialogGrantClick(
                        selectedItems.map { entry -> neededPermissions[entry] }.toList()
                    )
                }
                }
                .setNegativeButton(R.string.cancel
                ) { _, id ->
                    listener.onDialogGrantClick(listOf())
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = targetFragment as PermissionDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

}
