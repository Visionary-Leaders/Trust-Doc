package com.trustio.importantdocuments.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadRequest
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponseItem
import com.trustio.importantdocuments.databinding.MainScreenBinding
import com.trustio.importantdocuments.ui.screens.adapter.BottomNavAdapter
import com.trustio.importantdocuments.ui.screens.home.adapter.CollectionAdapter
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.LocalData
import com.trustio.importantdocuments.utils.showNetworkDialog
import com.trustio.importantdocuments.utils.showSnack
import com.trustio.importantdocuments.viewmodel.imp.HomeScreenViewModelImp
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.io.File

@AndroidEntryPoint
class MainScreen: BaseFragment<MainScreenBinding>(MainScreenBinding::inflate) {
    private val model by viewModels<HomeScreenViewModelImp>()

    private val pickPdfLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            val (pdf, size, fileType) = getFileDetailsFromUri(
                requireContext(), uri!!
            ) ?: Triple(null, null, null)
            model.uploadFile(
                FileUploadRequest(
                    selectedSection?.id!!,
                    pdf!!.toUri().toString(),
                    pdf.name,
                    fileType!!,
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
                result.pages?.let { pages ->
                    for (page in pages) {
                        val imageUri = pages.get(0).getImageUri()
                        val (file, size, fileType) = getFileDetailsFromUri(imageUri) ?: Triple(
                            null,
                            null,
                            null
                        )
                        showSnack(
                            binding.root,
                            "Image: $imageUri Image Type: $fileType, File: $file, Size: $size bytes"
                        )
                    }
                }
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
                    model.uploadFile(
                        FileUploadRequest(
                            selectedSection?.id!!,
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


    private var selectedSection: SectionsResponseItem? = null
    private val adapter by lazy { BottomNavAdapter(requireActivity()) }
    private val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                Log.d("HANDLE picker", "FILE:${imageUri}: ")
                showSnack(binding.root, "File format: $imageUri")
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
                    model.uploadFile(
                        FileUploadRequest(
                            selectedSection?.id!!,
                            file!!.toUri().toString(),
                            file.name,
                            fileType!!,
                            size.toString().toInt()
                        )
                    )

                }
            }
        }




    private val PERMISSION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
        model.noInternetLiveData.observe(this ){
            showNetworkDialog(requireActivity(),null)
        }
        model.fileUploadRequest.observe(this) {
            showSnack(binding.root.rootView, "File Uploaded Successfully")
        }
        model.errorResponse.observe(this) {
            showSnack(binding.root.rootView, it)
        }
        model.collectionAddedResponse.observe(this) {
            showSnack(binding.root.rootView, "Folder added successfully")
            setupBottomNavigation()
        }
    }

    //
    @SuppressLint("InflateParams")
    override fun onViewCreate(savedInstanceState: Bundle?) {
        binding.fab.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(
                R.layout.folder_bottom_sheet, null
            )

            val normalMapOption = view.findViewById<MaterialCardView>(R.id.choose_img_option)
            val satelliteMapOption = view.findViewById<MaterialCardView>(R.id.satellite_map_option)
            val buttonClose = view.findViewById<FrameLayout>(R.id.button_close)
            normalMapOption.setOnClickListener {
                bottomSheetDialog.dismiss()
                showAddFolderSheet()

            }
            satelliteMapOption.setOnClickListener {
                bottomSheetDialog.dismiss()
                showChooseFolderSheet()
            }
            buttonClose.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

        }

        setupBottomNavigation()
    }
    private fun pickPdf() {
        pickPdfLauncher.launch(arrayOf("application/pdf"))
    }

    @SuppressLint("MissingInflatedId")
    private fun showChooseFileTypeSheet(data: SectionsResponseItem) {
        selectedSection = data
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
                    .setResultFormats(RESULT_FORMAT_PDF).setScannerMode(SCANNER_MODE_FULL).build()

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

    @SuppressLint("MissingInflatedId")
    private fun showChooseFolderSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(
            R.layout.choose_folder_for_add_file, null
        )
        val foldersRv = view.findViewById<RecyclerView>(R.id.foldersRv)
        val adapter = CollectionAdapter(true)
        foldersRv.adapter = adapter
        adapter.submitList(LocalData.list)
        adapter.setItemClickListener {
            bottomSheetDialog.dismiss()
            showChooseFileTypeSheet(it)
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

    }
    private fun showAddFolderSheet(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(
            R.layout.add_folder_sheet, null
        )

        val cancelBtn = view.findViewById<AppCompatButton>(R.id.cancel_btn)
        val submitBtn = view.findViewById<AppCompatButton>(R.id.submit_btn)
        val addFolderTxt =view.findViewById<AppCompatEditText>(R.id.add_folder_txt)

        cancelBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        submitBtn.setOnClickListener {
            if (addFolderTxt.text.toString().isNotEmpty()){
                model.addCollection(CollectionRequest(addFolderTxt.text.toString()))
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

    }


    private fun setupBottomNavigation() {
        val mainViewPager = binding.navHost
        mainViewPager.adapter= adapter
        mainViewPager.isUserInputEnabled = false
        binding.homeNavigation.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> {
                        mainViewPager.currentItem=0
                        //

                    }
                    1 -> {
                        mainViewPager.currentItem=1
                    }
                    2 -> {
                        mainViewPager.currentItem=2
                    }
                    3->{
                        mainViewPager.currentItem=3
                    }
                    else -> {
                        mainViewPager.currentItem = 4
                    }
                }
            }
        })
    }

}