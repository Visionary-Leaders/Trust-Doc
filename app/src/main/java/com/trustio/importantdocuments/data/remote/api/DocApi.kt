package com.trustio.importantdocuments.data.remote.api

import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.data.remote.response.CollectionAddResponse
import com.trustio.importantdocuments.data.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DocApi {
    @POST("api/sections/")
    suspend fun addCollection(@Body collectionRequest: CollectionRequest): Response<CollectionAddResponse>

}