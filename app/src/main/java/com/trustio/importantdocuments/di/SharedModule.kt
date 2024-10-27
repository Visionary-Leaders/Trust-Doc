package com.trustio.importantdocuments.di

import android.content.Context
import com.trustio.importantdocuments.data.local.shp.AppReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedModule {

    @Provides
    @Singleton
    fun provideAppReference(@ApplicationContext context: Context): AppReference {
        return AppReference(context)
    }
}