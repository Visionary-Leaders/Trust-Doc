package com.trustio.importantdocuments.data.remote.response

data class TokenResponse(
    val access: String,
    val refresh: String
)