package com.trustio.importantdocuments.data.remote.request
data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val password: String
)
