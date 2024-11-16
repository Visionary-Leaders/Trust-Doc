package com.trustio.importantdocuments.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.repository.imp.DocsRepositoryImp
import com.trustio.importantdocuments.utils.LocalData
import com.trustio.importantdocuments.utils.hasConnection
import com.trustio.importantdocuments.viewmodel.SearchViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModelImp @Inject constructor(private val repo: DocsRepositoryImp) : SearchViewModel,
    ViewModel() {
    override val searchFileList: MutableLiveData<List<Bookmark>> = MutableLiveData()
    override val noInternetLiveData: MutableLiveData<Unit> = MutableLiveData()
    override val bookmarkList: MutableLiveData<List<Bookmark>> = MutableLiveData()

    override fun loadAllFile() {
        if (hasConnection()) {
            repo.loadAllFiles(LocalData.list).onEach {
                searchFileList.postValue(it)
            }.launchIn(viewModelScope)
        } else {
            noInternetLiveData.value = Unit
        }
    }
    override fun loadLocalFile() {
        repo.getAllBookmarks().onEach {
            bookmarkList.postValue(it)
        }.launchIn(viewModelScope)
    }
    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch  {
            repo.removeBookmark(bookmark)
        }
    }
    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch  {
            repo.addBookmark(bookmark)
        }
    }
}