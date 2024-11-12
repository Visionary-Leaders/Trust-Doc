package com.trustio.importantdocuments.data.remote.response.file

data class FileItem(
    val id: Int,
    val section: Int,
    val file: String,
    val file_name: String,
    val file_type: String,
    val file_size: Int,
    val user: Int
)

// ErrorResponse.kt
data class ErrorResponse(
    val detail: String
)
