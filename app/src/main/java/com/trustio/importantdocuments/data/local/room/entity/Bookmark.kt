package com.trustio.importantdocuments.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey val id: Int,
    val section: Int,
    val file: String,
    val fileName: String,
    val fileType: String,
    val fileSize: Int,
    val user: Int,
    val sectionName: String // Add the section name
)
