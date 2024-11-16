package com.trustio.importantdocuments.viewmodel

import androidx.lifecycle.MutableLiveData
import com.trustio.importantdocuments.data.local.room.entity.Bookmark

interface SearchViewModel {
    val searchFileList :MutableLiveData<List<Bookmark>>
    val noInternetLiveData: MutableLiveData<Unit>
    val bookmarkList:MutableLiveData<List<Bookmark>>
    fun loadAllFile()
    fun loadLocalFile()
}