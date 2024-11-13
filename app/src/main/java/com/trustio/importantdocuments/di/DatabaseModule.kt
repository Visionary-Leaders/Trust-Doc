package com.trustio.importantdocuments.di

import android.app.Application
import androidx.room.Room
import com.trustio.importantdocuments.data.local.room.dao.BookmarkDao
import com.trustio.importantdocuments.data.local.room.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "app_database")
            .build()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao {
        return database.bookmarkDao()
    }


}
