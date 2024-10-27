package com.trustio.importantdocuments.data.remote.response
data class RegisterResponse(
    val message: String?
)

data class ErrorResponse(
    val email: List<String>?
)
