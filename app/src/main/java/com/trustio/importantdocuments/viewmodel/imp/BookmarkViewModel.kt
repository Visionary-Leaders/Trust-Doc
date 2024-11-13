package com.trustio.importantdocuments.viewmodel.imp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.repository.DocsRepository
import com.trustio.importantdocuments.repository.imp.DocsRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: DocsRepositoryImp
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks

    private val _bookmarkedFileItems = MutableStateFlow<List<FileItem>>(emptyList())
    val bookmarkedFileItems: StateFlow<List<FileItem>> = _bookmarkedFileItems

    fun getAllBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.getAllBookmarks().collect { bookmarksList ->
                _bookmarks.value = bookmarksList
            }
        }
    }

    fun getBookmarksBySection(sectionId: Int) {
        viewModelScope.launch {
            bookmarkRepository.getBookmarksBySection(sectionId).collect { bookmarksList ->
                _bookmarks.value = bookmarksList
            }
        }
    }

    fun doesBookmarkExist(bookmarkId: Int): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = bookmarkRepository.doesBookmarkExist(bookmarkId)
        }
        return result
    }

    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.addBookmark(bookmark)
        }
    }

    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.removeBookmark(bookmark)
        }
    }

}
