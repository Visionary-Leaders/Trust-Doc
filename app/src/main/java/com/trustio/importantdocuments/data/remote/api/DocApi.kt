package com.trustio.importantdocuments.data.remote.api

import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.FileUploadResponse
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface DocApi {
    @POST("api/sections/sections/")
    suspend fun addCollection(
        @Header("Authorization") token: String,
        @Body collectionRequest: CollectionRequest): Response<CollectionAddResponse>

    @GET("api/sections/sections/")
    suspend fun getSections(@Header("Authorization") token: String): Response<SectionsResponse>

    @Multipart
    @POST("api/sections/{sectionId}/files/")
    suspend fun uploadFile(
        @Header("Authorization") authToken: String,
        @Path("sectionId") sectionId: Int,
        @Part("section") section: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("file_name") fileName: RequestBody,
        @Part("file_size") fileSize: RequestBody,
        @Part("file_type") fileType: RequestBody
    ): Response<FileUploadResponse>

}