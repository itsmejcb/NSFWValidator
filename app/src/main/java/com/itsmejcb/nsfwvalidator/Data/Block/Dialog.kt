package com.itsmejcb.nsfwvalidator.Data.Block


import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.itsmejcb.nsfwvalidator.R


class Dialog(private val context: Context) {

    fun showDialog(
        title: String?,
        description: String?,
        ok: String?,
        cancel: String?
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_error_handling, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val textTitle = dialogView.findViewById<TextView>(R.id.error_title)
        val textDescription = dialogView.findViewById<TextView>(R.id.error_description)
        val errorOk = dialogView.findViewById<LinearLayout>(R.id.error_ok)
        val errorCancel = dialogView.findViewById<LinearLayout>(R.id.error_cancel)
        val errorOkLabel = dialogView.findViewById<TextView>(R.id.error_ok_label)
        val errorCancelLabel = dialogView.findViewById<TextView>(R.id.error_cancel_label)

        errorOk!!.setOnClickListener {
            when (ok) {
                "Ok" -> alertDialog.dismiss()
                "Sign Up" -> {
                    alertDialog.dismiss()
                }
                "Continue editing" ->{
                    alertDialog.dismiss()
                }
            }
        }
        if (!ok.isNullOrBlank()) {
            errorOkLabel.text = ok
        }

        if (!cancel.isNullOrBlank()) {
            errorCancelLabel.text = cancel
            errorCancel.visibility = View.VISIBLE
        } else {
            errorCancel.visibility = View.GONE
        }

        if (!title.isNullOrBlank()) {
            textTitle.text = title
        }

        if (!description.isNullOrBlank()) {
            textDescription.text = description
        }

        // Map cancel options to actions
        // val cancelActions = mapOf(
        //     "Stop creating account" to {
        //         alertDialog.dismiss()
        //     },
        //     "Learn more" to {
        //         alertDialog.dismiss()
        //     },
        //     "Cancel" to {
        //         alertDialog.dismiss()
        //     },
        //     "Discard" to {
        //         alertDialog.dismiss()
        //     }
        // )
        errorCancel!!.setOnClickListener {
            when (cancel) {
                "Stop creating account" -> {
                    alertDialog.dismiss()
                }
                "Learn more" -> {
                    alertDialog.dismiss()
                }
                "Cancel" ->{
                    alertDialog.dismiss()
                }
                "Discard" ->{
                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.show()
    }
}