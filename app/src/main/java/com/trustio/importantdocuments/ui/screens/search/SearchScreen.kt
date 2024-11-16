package com.trustio.importantdocuments.ui.screens.search

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.databinding.SearchScreenBinding
import com.trustio.importantdocuments.ui.screens.file.FileDetailsBottomSheetDialog
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.LocalData
import com.trustio.importantdocuments.utils.containsFileItem
import com.trustio.importantdocuments.utils.gone
import com.trustio.importantdocuments.utils.showNetworkDialog
import com.trustio.importantdocuments.utils.showSnack
import com.trustio.importantdocuments.utils.toFileItem
import com.trustio.importantdocuments.utils.visible
import com.trustio.importantdocuments.viewmodel.imp.SearchViewModelImp
import com.yanzhenjie.recyclerview.SwipeMenuItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchScreen : BaseFragment<SearchScreenBinding>(SearchScreenBinding::inflate) {
    private val model by viewModels<SearchViewModelImp>()
    private val lastRequestedPosition = -1
    private val bookmarks = ArrayList<Bookmark>()
    private val list = ArrayList<Bookmark>()
    private val adapter by lazy { SearchAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.searchFileList.observe(this, loadAllFilesObserver)
        model.bookmarkList.observe(this, bookmarkListObserver)
        model.noInternetLiveData.observe(this, notInternetObserver)
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        setUpSwipeMenu()
        model.loadAllFile()
        model.loadLocalFile()
        setUpSearch()
        requireActivity().onBackPressedDispatcher.addCallback {
            if (binding.searchBar.isSearchOpened) {
                adapter.submitList(list)
                binding.searchBar.closeSearch()
            } else {
                findNavController().popBackStack()
            }
        }
    }


    private val bookmarkListObserver = Observer<List<Bookmark>> {
        bookmarks.clear()
        bookmarks.addAll(it)
        setupRefresh()
    }

    private val notInternetObserver = Observer<Unit> {
        showNetworkDialog(requireActivity(), binding.root)

    }

    private val loadAllFilesObserver = Observer<List<Bookmark>> {
        binding.apply {
            binding.progressBar.gone()
            binding.swipeRefresh.isRefreshing = false
            binding.fileListRv.adapter = adapter
            binding.mainContainer.visible()
            list.clear()
            list.addAll(it)
            adapter.submitList(it as ArrayList<Bookmark>)
        }
        adapter.setItemClickListener {
            LocalData.fileItem = it.toFileItem()
            val bottomSheetDialog = FileDetailsBottomSheetDialog()
            bottomSheetDialog.show(parentFragmentManager, "FileDetailsBottomSheetDialog")

        }

    }

    private fun setupRefresh() {
        binding.swipeRefresh.setSlingshotDistance(128)
        binding.swipeRefresh.setProgressViewEndTarget(false, 128)
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            model.loadAllFile()
            model.loadLocalFile()
        }

    }


    private fun setUpSearch() {
        binding.searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("GG", "beforeTextChanged:${p0} ")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("GGG", "onTextChanged: ${p0}")
                filterList(p0.toString(), list)
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("GGG", "afterTextChanged:${p0} ")
            }

        })
    }

    private fun convertCyrillicToLatin(text: String): String {
        return text.replace("а", "a").replace("б", "b").replace("в", "v").replace("г", "g")
            .replace("д", "d").replace("е", "e").replace("ё", "yo").replace("ж", "j")
            .replace("з", "z").replace("и", "i").replace("й", "y").replace("к", "k")
            .replace("л", "l").replace("м", "m").replace("н", "n").replace("о", "o")
            .replace("п", "p").replace("р", "r").replace("с", "s").replace("т", "t")
            .replace("у", "u").replace("ф", "f").replace("х", "x").replace("ц", "ts")
            .replace("ч", "ch").replace("ш", "sh").replace("щ", "shch").replace("ъ", "'")
            .replace("ы", "y").replace("ь", "'").replace("э", "e").replace("ю", "yu")
            .replace("я", "ya")
    }

    private fun isCyrillic(text: String): Boolean {
        return text.any { it in 'а'..'я' || it in 'А'..'Я' }
    }

    private fun filterList(query: String, yourListData: ArrayList<Bookmark>) {
        val convertedQuery = if (isCyrillic(query)) convertCyrillicToLatin(query) else query
        val filteredList = ArrayList<Bookmark>()
         yourListData.onEach {
            if (it.fileName.contains(convertedQuery, ignoreCase = true) ||
                it.sectionName.contains(convertedQuery, ignoreCase = true) ||
                it.fileType.contains(convertedQuery, ignoreCase = true)
            ) {
                filteredList.add(it)
            }

        }
        adapter.submitList(if (query.isEmpty()) list else filteredList)
    }

    private fun setUpSwipeMenu() {

        binding.fileListRv.setSwipeMenuCreator { leftMenu, rightMenu, position ->
            val bookmarkItem = createBookmarkMenuItem(position)
            val downloadItem = createDownloadMenuItem()

            rightMenu.addMenuItem(bookmarkItem)
            rightMenu.addMenuItem(downloadItem)
        }

        binding.fileListRv.setOnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            handleMenuClick(menuBridge.position, adapterPosition)
        }
    }

    private fun createBookmarkMenuItem(position: Int): SwipeMenuItem {
        val isBookmarked = bookmarks.containsFileItem(list[position].toFileItem())
        return SwipeMenuItem(requireActivity()).apply {
            setBackgroundColor(Color.parseColor("#3358FF"))
            setTextColor(Color.WHITE)
            width = 200
            height = ViewGroup.LayoutParams.MATCH_PARENT
            text = if (isBookmarked) "Bookmarked" else "Bookmark"
            setImage(if (isBookmarked) R.drawable.ic_favorite_fill else R.drawable.ic_fav)

        }
    }

    private fun createDownloadMenuItem(): SwipeMenuItem {
        return SwipeMenuItem(requireActivity()).apply {
            setBackgroundColor(Color.parseColor("#234F46E5"))
            text = "Download"
            setTextColor(Color.WHITE)
            width = 200
            height = ViewGroup.LayoutParams.MATCH_PARENT
            setImage(R.drawable.ic_download)
        }
    }

    private fun handleMenuClick(menuPosition: Int, adapterPosition: Int) {
        when (menuPosition) {
            0 -> toggleBookmark(adapterPosition)
            1 -> initiateDownload(adapterPosition)
        }
    }

    private fun toggleBookmark(adapterPosition: Int) {
        val currentItem = list[adapterPosition]
        val isBookmarked = bookmarks.containsFileItem(currentItem.toFileItem())

        if (isBookmarked) {
            removeBookmark(currentItem.toFileItem(), adapterPosition)
        } else {
            addBookmark(currentItem, adapterPosition)
        }
    }

    private fun removeBookmark(item: FileItem, position: Int) {
        val bookmark = bookmarks.find { it.id == item.id } ?: return
        showSnack(binding.root, "Item removed from Favorites")
        model.removeBookmark(bookmark)
        bookmarks.remove(bookmark)
        adapter.notifyItemChanged(position)
    }

    private fun addBookmark(item: Bookmark, position: Int) {
        val bookmarkData = item

        model.addBookmark(bookmarkData)
        bookmarks.add(bookmarkData)
        adapter.notifyItemChanged(position)
        showSnack(binding.root, "Item added to Favorites")
    }


    private fun initiateDownload(adapterPosition: Int) {
        val fileItem = list[adapterPosition]
        val fileUrl = fileItem.file
        val fileName = fileItem.fileName


        downloadFile(fileUrl, fileName)

    }

    private fun downloadFile(url: String, fileName: String) {
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

    override fun onResume() {
        super.onResume()
        binding.swipeRefresh.isRefreshing = true
        model.loadLocalFile()
        model.loadAllFile()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initiateDownload(lastRequestedPosition)
        } else {
            showSnack(binding.root, "Storage permission is required for download.")
        }

    }

}
