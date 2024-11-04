package com.trustio.importantdocuments.viewmodel

import androidx.lifecycle.MutableLiveData
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import kotlinx.coroutines.flow.MutableStateFlow

interface HomeScreenViewModel {
    val collectionList:MutableLiveData<SectionsResponse>
    val errorResponse :MutableLiveData<String>
    suspend fun loadSections()
}