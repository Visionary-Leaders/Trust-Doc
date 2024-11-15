package com.trustio.importantdocuments.repository

import android.app.Activity
import com.trustio.importantdocuments.data.remote.request.LoginRequest
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.data.remote.response.RegisterResponse
import com.trustio.importantdocuments.utils.ResultApp
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun sendOtp(phoneNumber: String, activity: Activity): Flow<ResultApp>
    fun confirmOtpFake(code:String):Flow<Result<String>>
    fun registerUser(registerRequest: RegisterRequest):Flow<Result<RegisterResponse>>
//    fun registerUserWithEmail(email:String,activity: Activity):Flow<Result<Unit>>
    fun loginUser(loginRequest: LoginRequest):Flow<Result<RegisterResponse>>

}
