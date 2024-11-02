package com.trustio.importantdocuments.data.remote.api

import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.data.remote.response.RegisterResponse
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DocApi {
    @POST("api/sections/sections/")
    suspend fun addCollection(
        @Header("Authorization") token: String,
        @Body collectionRequest: CollectionRequest): Response<CollectionAddResponse>

    @GET("api/sections/sections/")
    suspend fun getSections(@Header("Authorization") token: String): Response<SectionsResponse>

}