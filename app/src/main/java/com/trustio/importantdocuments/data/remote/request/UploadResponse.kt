package com.trustio.importantdocuments.data.remote.request
data class FileUploadResponse(
    val id: Int,
    val section: Int,
    val file: String,
    val file_name: String,
    val file_type: String,
    val file_size: Int,
    val user: Int
)
data class FileUploadRequest(
    val section: Int,
    val file: String,
    val file_name: String,
    val file_type: String,
    val file_size: Int
)
