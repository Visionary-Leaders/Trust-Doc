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
            downloadAndOpenFile(fileUrl = fileItem.file)
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


    @OptIn(DelicateCoroutinesApi::class)
    private fun downloadAndOpenFile(fileUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {  // Use Dispatchers.IO for IO-bound operations
            try {
                val cacheFile = downloadFile(fileUrl)

                withContext(Dispatchers.Main) { // Switch back to the main thread to show the dialog
                    showFileChooserDialog(cacheFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {  // Ensure exception handling happens on the main thread
                    Toast.makeText(App.currentContext(), "Failed to download file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun downloadFile(fileUrl: String): File {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(fileUrl).build()

            try {
                val response = client.newCall(request).execute()

                // Log the response code for debugging
                Log.d("FileDownload", "Response code: ${response.code}")

                // Check if the response is successful
                if (!response.isSuccessful) {
                    throw Exception("Download failed with response code: ${response.code}")
                }

                // Check for content-type to ensure it's the expected file type
                val contentType = response.header("Content-Type")
                Log.d("FileDownload", "Content-Type: $contentType")

                // Save the downloaded file to the cache directory
                val file = File(App.currentContext()!!.cacheDir, fileItem.file_name)  // Customize the file name and extension
                val outputStream: OutputStream = FileOutputStream(file)

                // Get the input stream and copy it to the output stream
                val inputStream: InputStream = response.body?.byteStream() ?: throw Exception("Failed to read file stream")
                inputStream.copyTo(outputStream)

                inputStream.close()
                outputStream.close()

                // Log the file location
                Log.d("FileDownload", "File downloaded to: ${file.absolutePath}")

                return@withContext file  // Return the downloaded file

            } catch (e: Exception) {
                // Log the exception to understand what went wrong
                Log.e("FileDownload", "Error downloading file: ${e.message}", e)
                throw e  // Rethrow the exception to be caught in the caller
            }
        }
    }

    private fun showFileChooserDialog(file: File) {
        val fileTypes = arrayOf("Image", "PDF")

        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Choose file type")
            .setItems(fileTypes) { _, which ->
                when (which) {
                    0 -> openImageFileWithCache(file)
                    1 -> openPdfWithCache(file)
                }
            }
            .create()

        dialog.show()
    }

    private fun openImageFileWithCache(file: File) {
        val uri = Uri.fromFile(file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        }

        startActivity(intent)
    }

    // PDF faylini cache'dan ochish
    private fun openPdfWithCache(file: File) {
        val uri = Uri.fromFile(file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        }

        startActivity(intent)
    }


    private fun getFileIconAnimation(fileType: String): Int {
        return when (fileType) {
            "pdf" -> R.drawable.ic_doc
            "image" -> R.drawable.ic_image
            else -> R.drawable.ic_doc
        }
    }
}

