package com.trustio.importantdocuments.repository.imp

import android.annotation.SuppressLint
import android.net.Uri
import com.trustio.importantdocuments.data.local.shp.AppReference
import com.trustio.importantdocuments.data.remote.api.AuthApi
import com.trustio.importantdocuments.data.remote.api.DocApi
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadResponse
import com.trustio.importantdocuments.data.remote.request.TokenRequest
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import com.trustio.importantdocuments.repository.DocsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class DocsRepositoryImp @Inject constructor(private val docApi: DocApi,private val appReference: AppReference,private val authApi:AuthApi) : DocsRepository {
    override  fun addCollection(collectionRequest: CollectionRequest) =
        flow {
            val response = docApi.addCollection(appReference.token, collectionRequest)
            if (response.isSuccessful) emit(Result.success(response.body()!!))
            if (response.code() ==401) {
                val getToken =authApi.getFullToken(TokenRequest(appReference.password,appReference.phone))
                if (getToken.isSuccessful) {
                    appReference.token = "Bearer ${getToken.body()?.access}"
                    val newResponse = docApi.addCollection(appReference.token, collectionRequest)
                    emit(Result.success(newResponse.body()!!))
                }
        } else emit(Result.failure(Exception(response.errorBody()!!.string())))
        }.flowOn(Dispatchers.IO)

    @SuppressLint("SuspiciousIndentation")
    override suspend fun getCollections()
    =flow<Result<SectionsResponse>> {
        val response =docApi.getSections(appReference.token!!)
            if (response.isSuccessful){
                emit(Result.success(response.body()!!))
            }else if (response.code() ==401) {
                val getToken =authApi.getFullToken(TokenRequest(appReference.password,appReference.phone))
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
}