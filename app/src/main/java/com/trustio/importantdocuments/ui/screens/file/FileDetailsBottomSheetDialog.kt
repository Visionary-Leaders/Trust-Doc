package com.trustio.importantdocuments.ui.screens.file

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.app.App
import com.trustio.importantdocuments.databinding.ShowFileSheetBinding
import com.trustio.importantdocuments.utils.LocalData.fileItem
import com.trustio.importantdocuments.utils.convertBytesToMb
import com.trustio.importantdocuments.utils.openFile
import com.trustio.importantdocuments.utils.showSnack
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class FileDetailsBottomSheetDialog() : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ShowFileSheetBinding.inflate(inflater, container, false)

        binding.fileName.text = fileItem.file_name
        binding.fileTypeSize.text = "${fileItem.file_type} • %.2f MB".format(fileItem.file_size.convertBytesToMb())

        binding.lottieFileIcon.setImageResource(getFileIconAnimation(fileItem.file_type))

        binding.btnDownload.setOnClickListener {
            downloadFile(
                fileName = fileItem.file_name,
                url = fileItem.file.toString(),
                binding = binding
            )
            dismiss()
        }

        binding.btnOpen.setOnClickListener {
            openFile(fileUrl = fileItem.file)
            dismiss()
        }



        return binding.root
    }




    private fun downloadFile(url: String, fileName: String, binding: ShowFileSheetBinding) {
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Downloading $fileName")
            setDescription("File is downloading...")

            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val mimeType = when {
                fileName.endsWith(".png") -> "image/png"
                fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> "image/jpeg"
                fileName.endsWith(".pdf") -> "application/pdf"

                else -> "*/*" // Default for unknown types
            }
            setMimeType(mimeType)
        }

        val downloadManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        showSnack(binding.root, "$fileName download started.")
    }


    private fun openFile(fileUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

        intent.setPackage("com.android.chrome")

        val packageManager = requireActivity().packageManager
        val activities = packageManager.queryIntentActivities(intent, 0)

        if (activities.isNotEmpty()) {
            startActivity(intent)
        } else {
            intent.setPackage(null)
            startActivity(intent)
        }
    }


    private fun getFileIconAnimation(fileType: String): Int {
        return when (fileType) {
            "pdf" -> R.drawable.ic_doc
            "image" -> R.drawable.ic_image
            else -> R.drawable.ic_doc
        }
    }
}

