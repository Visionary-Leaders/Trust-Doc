package com.trustio.importantdocuments.repository.imp

import android.annotation.SuppressLint
import android.net.Uri
import com.trustio.importantdocuments.data.local.room.dao.BookmarkDao
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.data.local.shp.AppReference
import com.trustio.importantdocuments.data.remote.api.AuthApi
import com.trustio.importantdocuments.data.remote.api.DocApi
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadResponse
import com.trustio.importantdocuments.data.remote.request.TokenRequest
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponseItem
import com.trustio.importantdocuments.repository.DocsRepository
import com.trustio.importantdocuments.utils.toBookmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class DocsRepositoryImp @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val docApi: DocApi,private val appReference: AppReference,private val authApi:AuthApi) : DocsRepository {
    override  fun addCollection(collectionRequest: CollectionRequest) =
        flow {
            val response = docApi.addCollection(appReference.token, collectionRequest)
            if (response.isSuccessful) emit(Result.success(response.body()!!))
            if (response.code() == 401) {
                val getToken =
                    authApi.getFullToken(TokenRequest(appReference.password, appReference.phone))

                if (getToken.isSuccessful && getToken.body() != null) { // Check if body is not null
                    appReference.token =
                        "Bearer ${getToken.body()!!.access}" // Use !! safely after null check
                    val newResponse = docApi.addCollection(appReference.token, collectionRequest)
                    if (newResponse.isSuccessful && newResponse.body() != null) {
                        emit(Result.success(newResponse.body()!!)) // Use !! safely after null check
                    } else {
                        emit(
                            Result.failure(
                                Exception(
                                    "Failed to add collection: ${
                                        newResponse.errorBody()?.string() ?: "Unknown error"
                                    }"
                                )
                            )
                        )
                    }
                } else {
                    emit(
                        Result.failure(
                            Exception(
                                "Failed to obtain token: ${
                                    getToken.errorBody()?.string() ?: "Unknown error"
                                }"
                            )
                        )
                    )
                }
            } else {
                emit(
                    Result.failure(
                        Exception(
                            response.errorBody()?.string() ?: "Unknown error"
                        )
                    )
                ) // Safe call with fallback
            }
        }.flowOn(Dispatchers.IO)

    @SuppressLint("SuspiciousIndentation")
    override suspend fun getCollections() = flow {
        val response = docApi.getSections(appReference.token!!)
        if (response.isSuccessful) {
            emit(Result.success(response.body()!!))
        } else if (response.code() == 401) {
            val getToken =
                authApi.getFullToken(TokenRequest(appReference.password, appReference.phone))
            if (getToken.isSuccessful) {
                appReference.token = "Bearer ${getToken.body()?.access}"
                val newResponse = docApi.getSections(appReference.token!!)
                emit(Result.success(newResponse.body()!!))
            }
        } else {
            emit(Result.failure(Exception(response.errorBody()?.string())))

        }
    }.flowOn(Dispatchers.IO)

    override suspend fun uploadFile(
        token: String,
        section: String,
        fileUri: Uri,
        fileName: String,
        fileSize: Int,
        fileType: String
    ) = flow {
        emit(handleUploadFile(token, section, fileUri, fileName, fileSize, fileType))
    }

    override fun getAllFiles(sectionId: Int) = flow<Result<List<FileItem>>> {
        val response = docApi.getAllFiles(appReference.token, sectionId)
        if (response.isSuccessful) emit(Result.success(response.body()!!))
        else if (response.code() == 401) {
            val getToken =
                authApi.getFullToken(TokenRequest(appReference.password, appReference.phone))
            if (getToken.isSuccessful) {
                appReference.token = "Bearer ${getToken.body()?.access}"
                val newResponse = docApi.getAllFiles(appReference.token, sectionId)
                emit(Result.success(newResponse.body()!!))
            }
        } else emit(
            Result.failure(
                Exception(
                    when (response.code()) {
                        404 -> "Section not found or you do not have permission to access it."
                        else -> "Server error: ${response.errorBody()?.string()}"
                    }
                )
            )
        )

    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun handleUploadFile(
        token: String,
        section: String,
        fileUri: Uri,
        fileName: String,
        fileSize: Int,
        fileType: String
    ): Result<FileUploadResponse> {
        val file =
            File(fileUri.path ?: return Result.failure(Exception("File not found")))

        val sectionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), section)
        val fileNamePart = RequestBody.create("text/plain".toMediaTypeOrNull(), fileName)
        val fileSizePart = RequestBody.create("text/plain".toMediaTypeOrNull(), fileSize.toString())
        val fileTypePart = RequestBody.create("text/plain".toMediaTypeOrNull(), fileType)

        val filePart = MultipartBody.Part.createFormData(
            "file", file.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        )

      val data  =docApi.uploadFile(
            authToken = token,
            section = sectionPart,
            file = filePart,
            fileName = fileNamePart,
            fileSize = fileSizePart,
            fileType = fileTypePart,
            sectionId = section.toInt()
        )
        return when  {
            data.isSuccessful -> Result.success(data.body()!!)
            data.code() == 401 -> {
                val getToken =authApi.getFullToken(TokenRequest(appReference.password,appReference.phone))
                if (getToken.isSuccessful) {
                    appReference.token = "Bearer ${getToken.body()?.access}"
                    val newResponse = docApi.uploadFile(
                        authToken = "Bearer ${getToken.body()?.access}",
                        section = sectionPart,
                        file = filePart,
                        fileName = fileNamePart,
                        fileSize = fileSizePart,
                        fileType = fileTypePart,
                        sectionId = section.toInt()
                    )
                    Result.success(newResponse.body()!!)
                }
                else {
                    Result.failure(Exception(getToken.errorBody()?.string()))
                }
            }
            else -> {
                Result.failure(Exception(data.errorBody()?.string()))
            }
        }
    }
    override suspend fun addBookmark(bookmark: Bookmark) {
        bookmarkDao.addBookmark(bookmark)
    }

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }

    override fun getBookmarksBySection(sectionId: Int): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksBySection(sectionId)
    }

    override suspend fun removeBookmark(bookmark: Bookmark) {
        bookmarkDao.removeBookmark(bookmark)
    }

    override suspend fun removeBookmarkById(bookmarkId: Int) {
        bookmarkDao.removeBookmarkById(bookmarkId)
    }

    override suspend fun fetchRemoteFileItems(): List<FileItem> {
        TODO("EVERYDAY NOTHING")
    }

    override suspend fun doesBookmarkExist(bookmarkId: Int): Boolean {
        return bookmarkDao.doesBookmarkExist(bookmarkId)
    }

    override fun loadAllFiles(sectionList: ArrayList<SectionsResponseItem>) = flow {
        val bookmarkList = mutableListOf<Bookmark>() // MutableList ishlatish yaxshiroq
        for (section in sectionList) {
            val response = makeRequestWithToken(docApi, section.id)
            response.forEach { item ->
                bookmarkList.add(
                    item.toBookmark(sectionName = section.name)
                )
            }
        }
        emit(bookmarkList) // Natijani bir marta emit qilish
    }

    private suspend fun makeRequestWithToken(docApi: DocApi, sectionId: Int): List<FileItem> {
        var response = docApi.getAllFiles(appReference.token, sectionId)

        if (response.code() == 401) {
            refreshToken()  // Refresh the token if expired
            response =
                docApi.getAllFiles(appReference.token, sectionId)  // Retry with the new token
        }

        return response.body() ?: emptyList()
    }

    suspend fun refreshToken() {
        val getToken = authApi.getFullToken(TokenRequest(appReference.password, appReference.phone))
        if (getToken.isSuccessful) {
            appReference.token = "Bearer ${getToken.body()?.access}"
        }
    }

}