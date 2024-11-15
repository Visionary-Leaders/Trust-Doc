package com.trustio.importantdocuments.viewmodel.imp

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.data.local.shp.AppReference
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadResponse
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import com.trustio.importantdocuments.repository.imp.DocsRepositoryImp
import com.trustio.importantdocuments.utils.hasConnection
import com.trustio.importantdocuments.viewmodel.HomeScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModelImp @Inject constructor(
    private val repo: DocsRepositoryImp,
    private val appReference: AppReference
) : HomeScreenViewModel, ViewModel() {
    override val collectionAddedResponse: MutableLiveData<CollectionAddResponse> = MutableLiveData()
    override val collectionList: MutableLiveData<SectionsResponse> = MutableLiveData()
    override val fileUploadRequest: MutableLiveData<FileUploadResponse> = MutableLiveData()
    override val errorResponse: MutableLiveData<String> = MutableLiveData()
    override val fileList: MutableLiveData<List<FileItem>> = MutableLiveData()
    override val noInternetLiveData: MutableLiveData<Unit> = MutableLiveData()

    init {
        viewModelScope.launch {
            loadSections()
        }
    }
    override suspend fun loadSections() {
      if (hasConnection()){
          repo.getCollections().onEach {
              it.onFailure {
                  errorResponse.value=it.message.toString()

              }
              it.onSuccess {
                  collectionList.value=it
              }
          }.launchIn(viewModelScope)
      }else {
          noInternetLiveData .value =Unit
      }
    }

    override  fun addCollection(collectionRequest: CollectionRequest) {
      if (hasConnection()) {
          repo.addCollection(collectionRequest).onEach {
              it.onFailure {
                  errorResponse.value=it.message.toString()
              }
              it.onSuccess {
                  collectionAddedResponse.value=it
              }
          }.launchIn(viewModelScope)
      }else {
          noInternetLiveData .value =Unit
      }
    }

    override fun uploadFile(request: FileUploadRequest) {
        if (hasConnection()) {
            viewModelScope.launch {
                repo.uploadFile(
                    appReference.token!!,
                    request.section.toString(),
                    request.file.toUri(),
                    request.file_name,
                    request.file_size,
                    request.file_type
                ).onEach {
                    it.onSuccess {
                        fileUploadRequest.value=it
                    }
                    it.onFailure {
                        errorResponse.value = it.message
                    }
                }.launchIn(viewModelScope)
            }
        } else {
            noInternetLiveData.value = Unit
        }
    }


    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks

    private val _bookmarkedFileItems = MutableStateFlow<List<FileItem>>(emptyList())
    val bookmarkedFileItems: StateFlow<List<FileItem>> = _bookmarkedFileItems

    fun getAllBookmarks() {
        viewModelScope.launch {
            repo.getAllBookmarks().collect { bookmarksList ->
                _bookmarks.value = bookmarksList
            }
        }
    }

    fun getBookmarksBySection(sectionId: Int) {
        viewModelScope.launch {
            repo.getBookmarksBySection(sectionId).collect { bookmarksList ->
                _bookmarks.value = bookmarksList
            }
        }
    }

    fun doesBookmarkExist(bookmarkId: Int): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = repo.doesBookmarkExist(bookmarkId)
        }
        return result
    }

    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repo.addBookmark(bookmark)
        }
    }

    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repo.removeBookmark(bookmark)
        }
    }


    override fun loadFileBySection(sectionId: Int) {
        repo.getAllFiles(sectionId).onEach {
            it.onSuccess {
                fileList.postValue(it)

            }
            it.onFailure {
                errorResponse.postValue(it.message)

            }
        }.launchIn(viewModelScope)
    }
}