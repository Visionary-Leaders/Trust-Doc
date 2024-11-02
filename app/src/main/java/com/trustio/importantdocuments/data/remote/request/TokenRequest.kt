package com.trustio.importantdocuments.data.remote.request

data class TokenRequest(
    val password: String,
    val phone_number: String
)