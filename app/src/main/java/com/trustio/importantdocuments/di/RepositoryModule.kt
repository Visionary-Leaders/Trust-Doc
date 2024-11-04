package com.trustio.importantdocuments.di

import com.trustio.importantdocuments.repository.imp.AuthRepositoryImpl
import com.trustio.importantdocuments.repository.AuthRepository
import com.trustio.importantdocuments.repository.DocsRepository
import com.trustio.importantdocuments.repository.imp.DocsRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    @Binds
    abstract fun bindDocRepository(
        docRepositoryImp: DocsRepositoryImp
    ): DocsRepository
}
