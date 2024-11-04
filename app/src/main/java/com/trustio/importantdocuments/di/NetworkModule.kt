package com.trustio.importantdocuments.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.trustio.importantdocuments.data.local.shp.AppReference
import com.trustio.importantdocuments.data.remote.api.AuthApi
import com.trustio.importantdocuments.data.remote.api.DocApi
import com.trustio.importantdocuments.data.remote.authenticator.TokenAuthenticator
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

    private const val BASE_URL = "https://sobirjonabdurasulov011.pythonanywhere.com/"

    // Provide a separate Retrofit for AuthApi to avoid dependency cycles
    @[Provides Singleton Named("authRetrofit")]
    fun provideAuthRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @[Provides Singleton]
    fun provideAuthApi(@Named("authRetrofit") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @[Provides Singleton]
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        appReference: AppReference,
        @Named("authRetrofit") authRetrofit: Retrofit
    ): OkHttpClient {
        val authApi = authRetrofit.create(AuthApi::class.java) // Use auth-specific retrofit instance
        return OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor(context))
            .authenticator(TokenAuthenticator(appReference, authApi))
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", appReference.token)
                    .build()
                chain.proceed(request)
            }
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @[Provides Singleton Named("mainRetrofit")]
    fun provideMainRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @[Provides Singleton]
    fun provideDocsApi(@Named("mainRetrofit") retrofit: Retrofit): DocApi =
        retrofit.create(DocApi::class.java)
}
