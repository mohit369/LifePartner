package com.newlifepartner.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import com.newlifepartner.R

class CustomProgressBar(private val context: Context) {

    private val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog,null)
    }

    private val progressBar: ProgressBar by lazy {
        dialogView.findViewById<ProgressBar>(R.id.progressBar)
    }

    private val dialog: AlertDialog by lazy {
        AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    // Optional: You can add methods to update the progress bar if needed
    fun setProgress(progress: Int) {
        progressBar.progress = progress
    }
}
