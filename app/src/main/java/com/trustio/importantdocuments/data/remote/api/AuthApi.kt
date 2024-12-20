package com.trustio.importantdocuments.data.remote.api

import com.trustio.importantdocuments.data.remote.request.LoginRequest
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.data.remote.request.TokenRequest
import com.trustio.importantdocuments.data.remote.response.RegisterResponse
import com.trustio.importantdocuments.data.remote.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @POST("api/accounts/register/")
    suspend fun registerUser(@Body requestBody: RegisterRequest): Response<RegisterResponse>
    @POST("api/accounts/login/")
    suspend fun loginUser(@Body requestBody: LoginRequest): Response<RegisterResponse>

    @POST("api/token/")
    suspend fun  getFullToken(@Body requestBody:TokenRequest):Response<TokenResponse>
}


//api/register/