package com.trustio.importantdocuments.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponseItem
import com.trustio.importantdocuments.databinding.MainScreenBinding
import com.trustio.importantdocuments.ui.screens.adapter.BottomNavAdapter
import com.trustio.importantdocuments.ui.screens.home.adapter.CollectionAdapter
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.LocalData
import com.trustio.importantdocuments.utils.showSnack
import com.trustio.importantdocuments.viewmodel.imp.HomeScreenViewModelImp
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.io.File

@AndroidEntryPoint
class MainScreen: BaseFragment<MainScreenBinding>(MainScreenBinding::inflate) {
    private val model by viewModels<HomeScreenViewModelImp>()
    private val adapter by lazy { BottomNavAdapter(requireActivity()) }
    // Image picker result launcher
    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            Log.d("HANDLE picker", "FILE:${imageUri}: ")
            showSnack(binding.root, "File format: $imageUri")
            // Handle the image URI (e.g., display it in an ImageView or upload it)
            imageUri?.let {
                val uri = it
                uri?.let {
                    val (fileFormat, fileSizeMB) = getFileDetails(requireContext(),it)
                    showSnack(binding.root, "File format: $fileFormat\nFile size: $fileSizeMB MB")
                }
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
        model.errorResponse.observe(this) {
            showSnack(binding.root.rootView, it)
        }
        model.collectionAddedResponse.observe(this) {
            showSnack(binding.root.rootView, "Folder added successfully")
            setupBottomNavigation()
        }
    }
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

    @SuppressLint("MissingInflatedId")
    private fun showChooseFileTypeSheet(data: SectionsResponseItem) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.choose_file_type_sheet, null)

        val imgOption = view.findViewById<MaterialCardView>(R.id.choose_img_option)
        val buttonClose = view.findViewById<FrameLayout>(R.id.button_close)

        imgOption.setOnClickListener {
            bottomSheetDialog.dismiss()
             ImagePicker.with(this)
                .crop()
                .provider(ImageProvider.BOTH)
                .galleryOnly()
                .createIntent {
                    getImage.launch(it)

                }

        }

        buttonClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }
    private fun openImagePicker() {
//        ImagePicker.with(requireActivity())
//            .galleryOnly().createIntentFromDialog {
//                val uri = it.data
//                uri?.let {
//                    val (fileFormat, fileSizeMB) = getFileDetails(it)
//                }
//            }

    }
    // Function to get file details from the URI
    private fun getFileDetails(context: Context, uri: Uri): Pair<String, Float> {
        val contentResolver: ContentResolver = context.contentResolver
        var fileSize: Float = 0.0f
        var fileFormat: String = "unknown"

        // Get file size and format using a ContentResolver query
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (cursor.moveToFirst()) {
                // Retrieve file size in MB
                val sizeBytes = cursor.getLong(sizeIndex)
                fileSize = sizeBytes / (1024 * 1024).toFloat()

                // Retrieve file format
                val displayName = cursor.getString(nameIndex)
                fileFormat = displayName.substringAfterLast('.', "unknown")
            }
        }

        return fileFormat to fileSize
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val filePath = it.getString(columnIndex)
                it.close()
                return filePath
            }
            it.close()
        }
        return null
    }

    private fun getFileFormat(uri: Uri): String {
        val mimeType = requireContext().contentResolver.getType(uri)
        return mimeType ?: "Unknown"
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