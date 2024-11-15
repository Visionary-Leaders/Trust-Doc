package com.trustio.importantdocuments.ui.screens.favorite

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.databinding.FavoriteScreenBinding
import com.trustio.importantdocuments.ui.screens.file.FileDetailsBottomSheetDialog
import com.trustio.importantdocuments.ui.screens.file.FileListAdapter
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.LocalData
import com.trustio.importantdocuments.utils.invisible
import com.trustio.importantdocuments.utils.showSnack
import com.trustio.importantdocuments.utils.toFileItem
import com.trustio.importantdocuments.utils.toFileItemWithSection
import com.trustio.importantdocuments.utils.visible
import com.trustio.importantdocuments.viewmodel.imp.FavoriteViewModelImp
import com.yanzhenjie.recyclerview.SwipeMenuItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteScreen : BaseFragment<FavoriteScreenBinding>(FavoriteScreenBinding::inflate) {
    private val model by viewModels<FavoriteViewModelImp>()
    private var bookmarkList = ArrayList<Bookmark>()
    private val adapter by lazy { FileListAdapter(0, arrayListOf()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        model.bookmarks.observe(this) {
            bookmarkList.clear()
            bookmarkList.addAll(it)
            adapter.submitList(it.map { it.toFileItemWithSection(it.sectionName) })
            loadView()
            binding.fileListRv.adapter = adapter
            binding.fileListRv.visible()
            binding.progressBar.invisible()
        }
    }


    override fun onViewCreate(savedInstanceState: Bundle?) {
        binding.fileListRv.invisible()
        setUpSwipeMenu()
        model.getAllBookmarks()
    }

    private fun setUpSwipeMenu() {
        binding.fileListRv.setSwipeMenuCreator { leftMenu, rightMenu, position ->
            val bookmarkItem = createBookmarkMenuItem()
            rightMenu.addMenuItem(bookmarkItem)
        }

        binding.fileListRv.setOnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            handleMenuClick(menuBridge.position, adapterPosition)
        }
    }

    private fun handleMenuClick(menuPosition: Int, adapterPosition: Int) {
        when (menuPosition) {
            0 -> toggleBookmark(adapterPosition)
        }
    }

    private fun toggleBookmark(adapterPosition: Int) {
        val currentItem = bookmarkList[adapterPosition]
        removeBookmark(currentItem, adapterPosition) // Pass the position for direct removal
    }

    private fun removeBookmark(item: Bookmark, position: Int) {
        model.removeBookmark(item)
        bookmarkList.removeAt(position)
        adapter.notifyItemRemoved(position)

        showSnack(binding.root, "Item removed from Favorites")
    }

    private fun createBookmarkMenuItem(): SwipeMenuItem {
        return SwipeMenuItem(requireActivity()).apply {
            setBackgroundColor(Color.parseColor("#C14D4D"))
            setTextColor(Color.WHITE)
            width = 200
            height = ViewGroup.LayoutParams.MATCH_PARENT
            text = "Remove"
            setImage(R.drawable.ic_delete)
        }
    }


    private fun loadView() {
        adapter.setItemClickListener {
            LocalData.fileItem = it
            val bottomSheetDialog = FileDetailsBottomSheetDialog()
            bottomSheetDialog.show(parentFragmentManager, "FileDetailsBottomSheetDialog")

        }
        adapter.setMeuClickListener {}
    }


}