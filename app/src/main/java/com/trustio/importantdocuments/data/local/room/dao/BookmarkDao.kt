package com.trustio.importantdocuments.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :bookmarkId LIMIT 1)")
    suspend fun doesBookmarkExist(bookmarkId: Int): Boolean


    @Query("SELECT * FROM bookmarks")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE section = :sectionId")
    fun getBookmarksBySection(sectionId: Int): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE id = :bookmarkId")
    fun getBookmarkById(bookmarkId: Int): Flow<Bookmark?>

    @Delete
    suspend fun removeBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE id = :bookmarkId")
    suspend fun removeBookmarkById(bookmarkId: Int)
}
