package com.syed.classconnect.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
<<<<<<< HEAD
    @POST("v1beta/models/gemini-2.0-flash-lite:generateContent")
=======
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
>>>>>>> final
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}

