package com.trustio.importantdocuments.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.trustio.importantdocuments.data.remote.api.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @[Provides Singleton]
    fun getOkHTTPClient(@ApplicationContext context: Context): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ChuckerInterceptor(context))
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)

        .build()


    @[Provides Singleton Named("testApi")]
    fun getTestAuthApi(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://sobirjonabdurasulov011.pythonanywhere.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @[Provides Singleton Named("grossuzApi")]
    fun getGrossUzRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://gross.uz//")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    @Provides
    fun getAuthApi(@Named("testApi") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)



}