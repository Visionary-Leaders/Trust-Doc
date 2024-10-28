package com.trustio.importantdocuments.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.trustio.importantdocuments.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun showNetworkDialog(context: FragmentActivity, container: View?) {
    val dialog = Dialog(context)
    container?.gone()
    val inflater = LayoutInflater.from(context)
    val dialogView = inflater.inflate(R.layout.no_connection, null)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(false)
    val tryAgainButton =
        dialogView.findViewById<com.google.android.material.card.MaterialCardView>(R.id.try_again)
    val tryAgainTxt = dialogView.findViewById<TextView>(R.id.try_againtxt)
    val tryAgainProgress = dialogView.findViewById<ProgressBar>(R.id.try_again_progress)
    tryAgainButton.setOnClickListener {
        tryAgainTxt.gone()
        tryAgainProgress.visible()
        context.lifecycleScope.launch {
            delay(600)
            if (hasConnection()) {
                container?.visible()
                dialog.dismiss()
                tryAgainTxt.visible()
                tryAgainProgress.gone()

            } else {
                container?.gone()
                tryAgainTxt.visible()
                tryAgainProgress.gone()
                dialog.setContentView(dialogView)
                dialog.show()
            }
        }

    }
    dialog.setContentView(dialogView)

    dialog.show()


}
