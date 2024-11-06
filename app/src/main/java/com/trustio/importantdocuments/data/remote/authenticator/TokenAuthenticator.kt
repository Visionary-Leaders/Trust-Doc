package com.trustio.importantdocuments.data.remote.authenticator

import com.trustio.importantdocuments.data.local.shp.AppReference
import com.trustio.importantdocuments.data.remote.api.AuthApi
import com.trustio.importantdocuments.data.remote.api.DocApi
import com.trustio.importantdocuments.data.remote.request.TokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit

class TokenAuthenticator(
    private val appReference: AppReference,
    private val retrofit: AuthApi // Retrofit instance for making API calls
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null
        val phone = appReference.phone
        val password = appReference.password

        val refreshResponse = runBlocking {
            val request = TokenRequest(phone, password)
            retrofit.getFullToken(request)
        }

        return if (refreshResponse.isSuccessful) {
            val newAccessToken = refreshResponse.body()?.access
            newAccessToken?.let {
                appReference.token ="Bearer $newAccessToken"
                response.request.newBuilder()
                    .header("Authorization", "$newAccessToken")
                    .build()
            }
        } else {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
