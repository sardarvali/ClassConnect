# GeminiApiService — Retrofit interface for Gemini AI API

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/remote/GeminiApiService.kt`

---

## 🎯 What This File Does
GeminiApiService is a Retrofit interface that defines the HTTP endpoint for the Gemini 2.0 Flash generative AI API. It sends a list of conversation contents and receives a generated text response. Used by GeminiRepository.

---

## 📦 Imports

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `retrofit2.Response` | Retrofit | HTTP response wrapper | Wraps the API response for status code checking |
| `retrofit2.http.Body` / `POST` / `Query` | Retrofit | HTTP annotations | Defines the API endpoint |

---

## 📝 Full Annotated Source Code

```kotlin
interface GeminiApiService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    // POST request to Gemini's generateContent endpoint.
    // v1beta = API version. gemini-2.0-flash = model name.
    suspend fun generateContent(
        @Query("key") apiKey: String,
        // API key passed as a URL query parameter: ?key=YOUR_KEY
        @Body request: GeminiRequest
        // Request body: contains the conversation contents.
    ): Response<GeminiResponse>
    // Returns Response<GeminiResponse> for manual status code handling.
}

// Supporting data classes for request/response:
data class GeminiRequest(val contents: List<GeminiContent>)
data class GeminiContent(val role: String, val parts: List<GeminiPart>)
data class GeminiPart(val text: String)
data class GeminiResponse(val candidates: List<GeminiCandidate>?)
data class GeminiCandidate(val content: GeminiContent?)
```

---

## ⚠️ Important Notes
- Base URL is `https://generativelanguage.googleapis.com/` (set in RetrofitClient/AppModule)
- Model name `gemini-2.0-flash` — update if Google releases new versions
- API key is passed as query param, not header — Gemini's specific requirement
- Response parsing: `response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text`

