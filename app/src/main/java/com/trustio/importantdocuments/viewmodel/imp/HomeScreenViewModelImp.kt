package com.trustio.importantdocuments.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import com.trustio.importantdocuments.repository.imp.DocsRepositoryImp
import com.trustio.importantdocuments.viewmodel.HomeScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModelImp @Inject constructor(private val repo:DocsRepositoryImp) : HomeScreenViewModel,ViewModel() {
    override val collectionAddedResponse: MutableLiveData<CollectionAddResponse> = MutableLiveData()
    override val collectionList: MutableLiveData<SectionsResponse> = MutableLiveData()
    override val errorResponse: MutableLiveData<String> = MutableLiveData()
    init {
        viewModelScope.launch {
            loadSections()
        }
    }
    override suspend fun loadSections() {
        repo.getCollections().onEach {
            it.onFailure {
                errorResponse.value=it.message.toString()

            }
            it.onSuccess {
                collectionList.value=it
            }
        }.launchIn(viewModelScope)
    }

    override  fun addCollection(collectionRequest: CollectionRequest) {
        repo.addCollection(collectionRequest).onEach {
            it.onFailure {
                errorResponse.value=it.message.toString()
            }
            it.onSuccess {
                collectionAddedResponse.value=it
            }
        }.launchIn(viewModelScope)
    }
}