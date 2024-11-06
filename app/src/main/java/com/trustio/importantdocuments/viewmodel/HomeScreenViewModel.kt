package com.trustio.importantdocuments.viewmodel

import androidx.lifecycle.MutableLiveData
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadResponse
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import kotlinx.coroutines.flow.MutableStateFlow

interface HomeScreenViewModel {
    val collectionAddedResponse : MutableLiveData<CollectionAddResponse>
    val collectionList:MutableLiveData<SectionsResponse>
    val fileUploadRequest:MutableLiveData<FileUploadResponse>
    val errorResponse :MutableLiveData<String>
    suspend fun loadSections()
     fun addCollection(collectionRequest: CollectionRequest)
     fun uploadFile (request: FileUploadRequest)
}