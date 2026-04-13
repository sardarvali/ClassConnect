package com.syed.classconnect.data.repository

import com.syed.classconnect.BuildConfig
import com.syed.classconnect.data.remote.GeminiApiService
import com.syed.classconnect.data.remote.GeminiContent
import com.syed.classconnect.data.remote.GeminiPart
import com.syed.classconnect.data.remote.GeminiRequest
import com.syed.classconnect.util.NetworkResult
<<<<<<< HEAD
import retrofit2.HttpException
=======
>>>>>>> final
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepository @Inject constructor(private val api: GeminiApiService) {

    data class Message(val role: String, val text: String)

    suspend fun generateContent(
        history: List<Message>,
        prompt: String
    ): NetworkResult<String> = try {
        val contents = history.map { GeminiContent(it.role, listOf(GeminiPart(it.text))) } +
                listOf(GeminiContent("user", listOf(GeminiPart(prompt))))
        val response = api.generateContent(BuildConfig.GEMINI_API_KEY, GeminiRequest(contents))
        if (response.isSuccessful) {
            val text = response.body()?.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text ?: ""
            NetworkResult.Success(text)
        } else {
            when (response.code()) {
                404 -> NetworkResult.Error("AI model not available. Please update the app.", 404)
                429 -> NetworkResult.Error("AI is busy, please try again in a moment.", 429)
                401 -> NetworkResult.Error("Invalid API key. Contact support.", 401)
<<<<<<< HEAD
                else -> NetworkResult.Error("AI request failed (${response.code()}). Try again.", response.code())
=======
                else -> NetworkResult.Error(
                    "AI request failed (${response.code()}). Try again.",
                    response.code()
                )
>>>>>>> final
            }
        }
    } catch (e: IOException) {
        NetworkResult.Error("No internet connection. Check your network.")
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "Something went wrong. Please try again.")
    }
}

