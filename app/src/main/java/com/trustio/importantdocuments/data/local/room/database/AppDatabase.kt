package com.trustio.importantdocuments.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trustio.importantdocuments.data.local.room.dao.BookmarkDao
import com.trustio.importantdocuments.data.local.room.entity.Bookmark

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
