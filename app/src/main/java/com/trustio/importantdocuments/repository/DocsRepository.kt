package com.trustio.importantdocuments.repository

import android.net.Uri
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadResponse
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponseItem
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
    // Local Room operations
    suspend fun addBookmark(bookmark: Bookmark)
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun getBookmarksBySection(sectionId: Int): Flow<List<Bookmark>>
    suspend fun removeBookmark(bookmark: Bookmark)
    suspend fun removeBookmarkById(bookmarkId: Int)

    // Remote API calls
    suspend fun fetchRemoteFileItems(): List<FileItem>

    suspend fun doesBookmarkExist(bookmarkId: Int): Boolean

    fun loadAllFiles(sectionList:ArrayList<SectionsResponseItem>):Flow<List<Bookmark>>


}