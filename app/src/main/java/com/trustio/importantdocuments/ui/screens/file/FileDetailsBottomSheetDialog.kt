package com.trustio.importantdocuments.ui.screens.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.databinding.ShowFileSheetBinding
import com.trustio.importantdocuments.utils.convertBytesToMb

class FileDetailsBottomSheetDialog(private val fileItem: FileItem) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ShowFileSheetBinding.inflate(inflater, container, false)

        binding.fileName.text = fileItem.file_name
        binding.fileTypeSize.text = "${fileItem.file_type} • %.2f MB".format(fileItem.file_size.convertBytesToMb())

        binding.lottieFileIcon.setImageResource(getFileIconAnimation(fileItem.file_type))

        binding.btnDownload.setOnClickListener {
            dismiss()
        }

        binding.btnOpen.setOnClickListener {
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun getFileIconAnimation(fileType: String): Int {
        return when (fileType) {
            "pdf" -> R.drawable.ic_doc
            "image" -> R.drawable.ic_image
            else -> R.drawable.ic_doc
        }
    }
}
