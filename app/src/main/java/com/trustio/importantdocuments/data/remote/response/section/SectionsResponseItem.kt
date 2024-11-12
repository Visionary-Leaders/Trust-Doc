package com.trustio.importantdocuments.data.remote.response.section

import java.io.Serializable

data class SectionsResponseItem(
    val id: Int,
    val name: String,
    val user: Int
):Serializable