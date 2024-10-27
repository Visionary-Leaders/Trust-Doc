package com.trustio.importantdocuments.viewmodel

import android.app.Activity
import com.trustio.importantdocuments.data.remote.request.LoginRequest
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.StateFlow

interface AuthViewModel {
    val smsState: StateFlow<Result<String>?>
    val confirmOtpFake: StateFlow<Result<String>?>
    val registerResponse :StateFlow<Result<RegisterResponse>?>
    val loginResponse :StateFlow<Result<RegisterResponse>?>
    fun sendSms(phoneNumber: String, activity: Activity)
    fun confirmOtpFake(code:String)
    fun registerUser(registerRequest: RegisterRequest)
    fun loginUser(loginRequest: LoginRequest)
}
