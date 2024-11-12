package com.trustio.importantdocuments.repository

import android.net.Uri
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadResponse
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import com.trustio.importantdocuments.utils.ResultApp
import kotlinx.coroutines.flow.Flow

interface DocsRepository {
    fun addCollection(collectionRequest: CollectionRequest): Flow<Result<CollectionAddResponse>>
   suspend fun getCollections():Flow<Result<SectionsResponse>>
    suspend fun uploadFile(
        token: String,
        section: String,
        fileUri: Uri,
        fileName: String,
        fileSize: Int,
        fileType: String
    ):Flow<Result<FileUploadResponse>>

    fun getAllFiles(sectionId: Int): Flow<Result<List<FileItem>>>

}