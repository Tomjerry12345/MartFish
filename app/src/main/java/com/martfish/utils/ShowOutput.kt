package com.martfish.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.martfish.R
import dmax.dialog.SpotsDialog

fun showLogAssert(tag: String, msg: String) {
    Log.println(Log.ASSERT, tag, msg)
}

fun showSnackbar(view: View, text: String, type: String) {
    val snackBar = Snackbar.make(
        view, text,
        Snackbar.LENGTH_LONG
    ).setAction("Action", null)
//    snackBar.setActionTextColor(Color.BLUE)
    val snackBarView = snackBar.view
    if (type == "succes")
        snackBarView.setBackgroundColor(ContextCompat.getColor(view.context, R.color.blue_light))
    else
        snackBarView.setBackgroundColor(Color.RED)

    snackBar.show()
}

fun showDialog(context: Context, text: String): AlertDialog {
    return SpotsDialog
        .Builder().setContext(context)
        .setMessage(text)
        .build()
}