package com.trustio.importantdocuments.viewmodel.imp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.data.local.shp.AppReference
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.repository.imp.DocsRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModelImp @Inject constructor(
    private val repo: DocsRepositoryImp,
    private val appReference: AppReference
): ViewModel() {

    private val _bookmarks = MutableLiveData<List<Bookmark>>(emptyList())
    val bookmarks: LiveData<List<Bookmark>> = _bookmarks
    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repo.removeBookmark(bookmark)
        }
    }


    fun getAllBookmarks() {
        viewModelScope.launch {
            repo.getAllBookmarks().collect { bookmarksList ->
                _bookmarks.value = bookmarksList
            }
        }
    }
}