package com.trustio.importantdocuments.utils

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog

object NoInternetDialogUtil {

    /**
     * Shows a customizable No Internet dialog.
     *
     * @param context The context in which to show the dialog.
     * @param title The title of the dialog, indicating the blocked action.
     * @param onRetry Optional callback to execute retry logic when Retry is pressed.
     */
    fun showNoInternetDialog(
        context: Context,
        title: String,
        onRetry: (() -> Unit)? = null
    ) {
        MaterialDialog(context).show {
            title(text = title)
            message(text = "No internet connection. Please check your connection and try again.")
            positiveButton(text = "Retry") { 
                onRetry?.invoke()
                dismiss()
            }
            negativeButton(text = "Cancel") {
                dismiss()
            }
            cornerRadius(16f) // Set rounded corners for the dialog
        }
    }
}
