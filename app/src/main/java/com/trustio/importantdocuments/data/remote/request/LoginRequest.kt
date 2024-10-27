package com.trustio.importantdocuments.data.remote.request

data class LoginRequest(
    val phone_number:String,
    val password:String,
)