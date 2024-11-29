package com.hk210.callmicmonitor.alert

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.hk210.callmicmonitor.R

object Alert {

    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    /**
     * Shows an alert dialog.
     * @param title for the dialog
     * @param message for the dialog
     * @param onGrant Callback for positive button click
     * @param onDeny Callback for negative button click
     */
    fun showDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        onGrant: (DialogInterface) -> Unit,
        onDeny: (DialogInterface) -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog: DialogInterface, _: Int -> onGrant(dialog) }
            .setNegativeButton(negativeButtonText) { dialog: DialogInterface, _: Int -> onDeny(dialog) }
            .setCancelable(false)
            .show()
    }
}
