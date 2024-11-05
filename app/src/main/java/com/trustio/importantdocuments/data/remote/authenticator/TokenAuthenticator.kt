package com.trustio.importantdocuments.data.remote.authenticator

import com.trustio.importantdocuments.data.local.shp.AppReference
import com.trustio.importantdocuments.data.remote.api.AuthApi
import com.trustio.importantdocuments.data.remote.request.TokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val appReference: AppReference,
    private val authApi: AuthApi // Inject AuthApi directly
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401 || responseCount(response) >= 2) return null

        // Synchronously request a new token if a 401 response is received
        val phone = appReference.phone
        val password = appReference.password

        val refreshResponse = runBlocking {
            val request = TokenRequest(phone, password)
            authApi.getFullToken(request) // Request a fresh token
        }

        return if (refreshResponse.isSuccessful) {
            // Extract the new access token and update AppReference
            val newAccessToken = refreshResponse.body()?.access
            newAccessToken?.let {
                appReference.token = "Bearer $newAccessToken" // Update stored token

                // Retry the original request with the new token
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            }
        } else {
            // Return null if token refresh fails
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
