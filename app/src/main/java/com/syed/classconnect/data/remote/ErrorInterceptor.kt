package com.syed.classconnect.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = try {
            chain.proceed(chain.request())
        } catch (e: Exception) {
            Timber.e(e, "Network request failed")
            throw NetworkException(e.message ?: "Unknown network error", e)
        }

        if (!response.isSuccessful) {
            val code = response.code
            response.close()
            when (code) {
                401 -> throw UnauthorizedException("Invalid credentials")
                403 -> throw ForbiddenException("Access denied")
                404 -> throw NotFoundException("Resource not found")
                429 -> throw RateLimitException("Too many requests")
                in 500..599 -> throw ServerException("Server error: $code")
            }
            throw NetworkException("HTTP error: $code")
        }

        return response
    }
}

