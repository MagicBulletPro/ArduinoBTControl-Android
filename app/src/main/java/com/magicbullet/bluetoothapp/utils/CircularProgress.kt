package com.magicbullet.bluetoothapp.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.magicbullet.bluetoothapp.R

class CircularProgress(private val context: Activity) : MaterialAlertDialogBuilder(context) {
    private var waitDialogBox: Dialog

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_wait, null)
        setView(view)
        background = ColorDrawable(Color.TRANSPARENT)
        setCancelable(false)
        waitDialogBox = create()
    }

    fun showDialog() {
        context.runOnUiThread { waitDialogBox.show() }
    }

    fun dismiss() {
        Handler(Looper.getMainLooper()).postDelayed({
            waitDialogBox.dismiss()
        }, 1000)
    }
}