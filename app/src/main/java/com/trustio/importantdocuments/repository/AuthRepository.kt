package com.trustio.importantdocuments.repository

import android.app.Activity
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun sendSms(phoneNumber: String, activity: Activity): Flow<Result<String>>
    fun confirmOtpFake(code:String):Flow<Result<String>>
    fun registerUser(registerRequest: RegisterRequest):Flow<Result<RegisterResponse>>
}
