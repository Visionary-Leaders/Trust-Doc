package com.trustio.importantdocuments.viewmodel.imp

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.data.remote.response.RegisterResponse
import com.trustio.importantdocuments.repository.AuthRepository
import com.trustio.importantdocuments.viewmodel.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModelImp @Inject constructor(val repo:AuthRepository):AuthViewModel ,ViewModel(){
    private val _smsState = MutableStateFlow<Result<String>?>(null)
    override val smsState: StateFlow<Result<String>?> = _smsState
    private  val _confirmOtpFake = MutableStateFlow<Result<String>?>(null)
    override val confirmOtpFake: StateFlow<Result<String>?>  = _confirmOtpFake
    private val _registerResponse = MutableStateFlow<Result<RegisterResponse>?>(null)
    override val registerResponse: StateFlow<Result<RegisterResponse>?> =_registerResponse

    override fun sendSms(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            repo.sendSms(phoneNumber, activity).collect { result ->
                _smsState.value = result
            }
        }
    }

    override fun confirmOtpFake(code: String) {
        viewModelScope.launch {
            repo.confirmOtpFake(code).onEach {
                it.onSuccess {
                    _confirmOtpFake.value = Result.success(it)
                }
                it.onFailure {
                    _confirmOtpFake.value = Result.failure(it)
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun registerUser(registerRequest: RegisterRequest) {
        repo.registerUser(registerRequest).onEach {
            it.onSuccess {
                _registerResponse.value = Result.success(it)
            }
            it.onFailure {
                _registerResponse.value = Result.failure(it)
            }
        }.launchIn(viewModelScope)
    }

}