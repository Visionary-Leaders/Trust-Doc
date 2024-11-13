package com.trustio.importantdocuments.ui.screens.file

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.request.FileUploadRequest
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.databinding.FileListScreenBinding
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.convertBytesToMb
import com.trustio.importantdocuments.utils.gone
import com.trustio.importantdocuments.utils.invisible
import com.trustio.importantdocuments.utils.showNetworkDialog
import com.trustio.importantdocuments.utils.showSnack
import com.trustio.importantdocuments.utils.visible
import com.trustio.importantdocuments.viewmodel.imp.HomeScreenViewModelImp
import com.yanzhenjie.recyclerview.SwipeMenuItem
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class FileScreen : BaseFragment<FileListScreenBinding>(FileListScreenBinding::inflate) {

    private val model by viewModels<HomeScreenViewModelImp>()
    private val list = ArrayList<FileItem>()

    private var sectionId: Int = 0
    private val adapter by lazy { FileListAdapter(0) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.fileList.observe(this) {
            val sectionData = arguments?.getString("sectionTitle")
            list.clear()
            list.addAll(it!!)
            val allFileSize = ArrayList<Long>()
            it.let {
                it.onEach {
                    allFileSize.add(it.file_size.toLong())
                }
            }
            binding.infoTxt.text =
                "${allFileSize.size} Items - %.2f MB".format(allFileSize.convertBytesToMb())
            binding.breadcrumbText.text = "$sectionData > Files"


            //Setup Rv

            binding.fileListRv.adapter = adapter.apply {
                submitList(it ?: arrayListOf())

                // RefreshData
                binding.refreshLayout.isRefreshing = false
                binding.shimmerLayout.hideShimmer()
                binding.fileListRv.visible()
                binding.progressBar.invisible()
            }
        }
        model.fileUploadRequest.observe(this) {
            model.loadFileBySection(sectionId)
        }
        model.noInternetLiveData.observe(this) {
            showNetworkDialog(requireActivity(), binding.root)
            binding.fileListRv.visible()
            binding.progressBar.gone()
        }
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        sectionId = arguments?.getInt("sectionId") ?: 0
        model.loadFileBySection(sectionId = sectionId)
        binding.fileListRv.invisible()
        loadView()
        setupRefresh()
        setupUploadFile()
    }

    private fun setupRefresh() {
        sectionId = arguments?.getInt("sectionId") ?: 0
        binding.refreshLayout.setSlingshotDistance(128)
        binding.refreshLayout.setProgressViewEndTarget(false, 128)
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = true
            model.loadFileBySection(sectionId)
            binding.shimmerLayout.showShimmer(true)
        }
    }

    private fun loadView() {
        binding.fileListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                    binding.addFileBtn.hide()
                else
                    binding.addFileBtn.show()

            }
        })

        binding.fileListRv.setSwipeMenuCreator { leftMenu, rightMenu, position ->
            val bookmarkItem = SwipeMenuItem(requireActivity()).apply {

                setBackgroundColor(Color.parseColor("#3358FF"))
                text = "Bookmark"
                setTextColor(Color.WHITE)
                width = 200
                height = ViewGroup.LayoutParams.MATCH_PARENT

                setImage(R.drawable.ic_fav)
            }
            rightMenu.addMenuItem(bookmarkItem)
        }
        binding.fileListRv.setOnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()

            when (menuBridge.position) {
                0 -> {
                    showSnack(binding.root,"Item saved to favorites")
                }
            }
        }
        binding.homeIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.searchResultGrid.setOnClickListener {
            it.alpha = 1f
            binding.searchResultList.alpha = 0.33f
            this.recycler(1)
        }
        binding.searchResultList.setOnClickListener {
            it.alpha = 1f
            binding.searchResultGrid.alpha = 0.33f
            this.recycler(0)
        }
        adapter.setItemClickListener {
            val bottomSheetDialog = FileDetailsBottomSheetDialog(it)
            bottomSheetDialog.show(parentFragmentManager, "FileDetailsBottomSheetDialog")

        }
        adapter.setMeuClickListener {

        }
    }


    private fun setupUploadFile() {
        binding.addFileBtn.setOnClickListener {
            showChooseFileTypeSheet()
        }
    }

    private fun pickPdf() {
        pickPdfLauncher.launch(arrayOf("application/pdf"))
    }


    @SuppressLint("MissingInflatedId")
    private fun showChooseFileTypeSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.choose_file_type_sheet, null)

        val imgOption = view.findViewById<MaterialCardView>(R.id.choose_img_option)
        val docOption = view.findViewById<MaterialCardView>(R.id.choose_doc)
        val pdfOption = view.findViewById<MaterialCardView>(R.id.choose_pdf)
        val buttonClose = view.findViewById<FrameLayout>(R.id.button_close)
        docOption.setOnClickListener {
            bottomSheetDialog.dismiss()
            val options =
                GmsDocumentScannerOptions.Builder().setGalleryImportAllowed(false).setPageLimit(2)
                    .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF).setScannerMode(
                        GmsDocumentScannerOptions.SCANNER_MODE_FULL
                    ).build()

            documentScannerClient = GmsDocumentScanning.getClient(options)

            documentScannerClient?.getStartScanIntent(requireActivity())
                ?.addOnSuccessListener { intentSender ->
                    scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }?.addOnFailureListener { e: Exception ->
                }

        }
        pdfOption.setOnClickListener {
            bottomSheetDialog.dismiss()
            pickPdf()
        }

        imgOption.setOnClickListener {
            bottomSheetDialog.dismiss()
            ImagePicker.with(this).crop().provider(ImageProvider.BOTH).galleryOnly().createIntent {
                getImage.launch(it)
            }

        }

        buttonClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }


    private fun recycler(style: Int) {
        adapter.type = style
        adapter.submitList(list)

        when (adapter.type) {
            0 -> {
                binding.fileListRv.layoutManager = LinearLayoutManager(requireContext())
            }

            else -> {
                binding.fileListRv.layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }
    }

    private val pickPdfLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            val (pdf, size, fileType) = getFileDetailsFromUri(
                requireContext(), uri!!
            ) ?: Triple(null, null, null)
            binding.refreshLayout.isRefreshing = true
            binding.shimmerLayout.showShimmer(true)
            model.uploadFile(
                FileUploadRequest(
                    sectionId!!,
                    pdf!!.toUri().toString(),
                    pdf.name,
                    "pdf"!!,
                    size.toString().toInt()
                )
            )
            Log.d("GGG", "launch: pdf: $pdf, size: $size, fileType: $fileType")

        }


    private var scannerLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val resultCode = result.resultCode
            val result = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            if (resultCode == Activity.RESULT_OK && result != null) {

                result.pdf?.let { pdf ->
                    val pdfUri = pdf.getUri()
                    val (file, size, fileType) = getFileDetailsFromUri(pdfUri) ?: Triple(
                        null,
                        null,
                        null
                    )

                    val pageCount = pdf.getPageCount()
                    Log.d(
                        "GGG",
                        "Pdf page count: $pageCount, file: $file, size: $size, fileType: $fileType"
                    )

                    binding.refreshLayout.isRefreshing = true
                    binding.shimmerLayout.showShimmer(true)
                    model.uploadFile(
                        FileUploadRequest(
                            sectionId,
                            file!!.toUri().toString(),
                            file.name,
                            fileType!!,
                            size.toString().toInt()
                        )
                    )
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                showSnack(binding.root, "Scanning cancelled")
            } else {
                showSnack(binding.root, "Scanning failed")
            }
        }
    private var documentScannerClient: GmsDocumentScanner? = null

    private val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                Log.d("HANDLE picker", "FILE:${imageUri}: ")
                imageUri?.let {
                    val uri = it
                    val (file, size, fileType) = getFileDetailsFromUri(uri) ?: Triple(
                        null,
                        null,
                        null
                    )
                    println("File: $file")
                    println("Size: $size bytes")
                    println("File Type: $fileType")

                    binding.refreshLayout.isRefreshing = true
                    binding.shimmerLayout.showShimmer(true)
                    model.uploadFile(
                        FileUploadRequest(
                            sectionId,
                            file!!.toUri().toString(),
                            file.name,
                            fileType!!,
                            size.toString().toInt()
                        )
                    )

                }
            }
        }


    private fun getFileDetailsFromUri(context: Context, uri: Uri): Triple<File?, Long?, String?>? {
        var fileSize: Long? = null
        var fileName: String? = null
        var fileType: String? = null

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (sizeIndex != -1) {
                    fileSize = cursor.getLong(sizeIndex)
                }
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }

        fileType = context.contentResolver.getType(uri)
        val tempFile = fileName?.let {
            File(context.cacheDir, it).apply {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }

        return Triple(tempFile, fileSize, fileType)
    }

    private fun getFileDetailsFromUri(uri: Uri): Triple<File?, Long?, String?>? {
        if (uri.scheme == "file") {
            val file = File(uri.path ?: return null)

            val fileType = getFileExtension(file)
            val fileSize = file.length()

            return Triple(file, fileSize, fileType)
        } else {
            return null
        }
    }


    private fun getFileExtension(file: File): String? {
        return file.extension.takeIf { it.isNotEmpty() }
    }

}