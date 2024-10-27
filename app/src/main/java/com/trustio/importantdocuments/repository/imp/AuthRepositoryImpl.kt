package com.trustio.importantdocuments.repository.imp

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import com.trustio.importantdocuments.data.remote.api.AuthApi
import com.trustio.importantdocuments.data.remote.request.LoginRequest
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.data.remote.response.ErrorResponse
import com.trustio.importantdocuments.data.remote.response.RegisterResponse
import com.trustio.importantdocuments.repository.AuthRepository
import com.trustio.importantdocuments.utils.sanitizePhoneNumber
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val apiService: AuthApi
) : AuthRepository {




    override fun sendSms(phoneNumber: String, activity: Activity): Flow<Result<String>> = callbackFlow {
        val sanitizedPhoneNumber = sanitizePhoneNumber(phoneNumber)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                trySend(Result.success(verificationId)).isSuccess
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                trySend(Result.success("Verification completed")).isSuccess
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                Log.e("AuthRepository", "onVerificationFailed: ${exception.message}")
                trySend(Result.success("VerificationCompleted")).isSuccess
            }
        }

        firebaseAuth.setLanguageCode("uz")
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(sanitizedPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
        )

        awaitClose { /* Optional cleanup */ }
    }

    override fun confirmOtpFake(code: String) =flow<Result<String>> {
        if ( code =="123456"){
            emit(Result.success("Verification completed"))
        }else {
            emit(Result.failure(Exception("Wrong code")))
        }

    }

    override fun registerUser(registerRequest: RegisterRequest)=flow<Result<RegisterResponse>> {
        emit(handleRegistration(registerRequest))
    }

    override fun loginUser(loginRequest: LoginRequest) =flow<Result<RegisterResponse>> {
        emit(handleLogin(loginRequest))
    }

    private suspend fun  handleLogin(request: LoginRequest): Result<RegisterResponse> {
        val response = apiService.loginUser(request)
        Log.d("ERROR", "handleLogin: ${response.errorBody()?.string()}")
        Log.d("ERROR", "handleLogin: ${response.code()}")
        return when {
            response.isSuccessful -> response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Empty response body"))

            else -> Result.failure(parseError(response.errorBody()?.string()))

        }
    }

    private suspend fun handleRegistration(request: RegisterRequest): Result<RegisterResponse> {
        val response = apiService.registerUser(request)
        Log.d("ERROR", "handleRegistration: ${response.errorBody()?.string()}")
        Log.d("ERROR", "handleRegistration: ${response.code()}")
        return when {
            response.isSuccessful -> response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Empty response body"))

            else -> Result.failure(parseError(response.errorBody()?.string()))
        }
    }

    private fun parseError(errorBody: String?): Exception {
        val errorResponse = errorBody?.let {
            Gson().fromJson(it, ErrorResponse::class.java)
        }
        val errorMessage = errorResponse?.email?.firstOrNull() ?: "Unknown error"
        return Exception(errorMessage)
    }

}
