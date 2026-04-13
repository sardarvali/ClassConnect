# GeminiRepository.kt — Repository that sends chat history and prompts to the Gemini API and returns structured results

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/data/repository/GeminiRepository.kt`

---

## 🎯 What This File Does
`GeminiRepository` is the data layer for all AI functionality. It takes a conversation history (list of `Message` objects with `role` and `text`) plus a new user prompt, builds a `GeminiRequest`, sends it to the Gemini API via Retrofit, and returns a `NetworkResult<String>` containing the AI's text response. It handles HTTP error codes (404, 429, 401) with user-friendly messages and wraps network errors. It is `@Singleton` — one instance shared between `AIBuddyFragment`'s and `LessonPlannerFragment`'s ViewModels. Without it, neither AI feature would work.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `com.syed.classconnect.BuildConfig` | Project | Build-time config | `GEMINI_API_KEY` |
| `com.syed.classconnect.data.remote.GeminiApiService` | Project | Retrofit interface | Makes the HTTP call |
| `com.syed.classconnect.data.remote.GeminiContent` | Project | Request data class | Builds request contents |
| `com.syed.classconnect.data.remote.GeminiPart` | Project | Request data class | Wraps text in part |
| `com.syed.classconnect.data.remote.GeminiRequest` | Project | Request wrapper | Top-level request object |
| `com.syed.classconnect.util.NetworkResult` | Project | Sealed class for state | Return type |
| `retrofit2.HttpException` | Retrofit | HTTP error exception | Caught in error handling |
| `java.io.IOException` | Java | Network error | Catches "no internet" scenarios |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | `@Inject constructor(...)` |
| `javax.inject.Singleton` | Javax / Hilt | One instance per app | `@Singleton` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `data class Message(val role: String, val text: String)`
A local data class (nested inside `GeminiRepository`) representing one message in the conversation history. `role` is either `"user"` or `"model"`. Used to pass conversation history from the ViewModel.

### Building the request with history
```kotlin
val contents = history.map { GeminiContent(it.role, listOf(GeminiPart(it.text))) } +
               listOf(GeminiContent("user", listOf(GeminiPart(prompt))))
```
Converts each `Message` in history to a `GeminiContent`, then appends the new user `prompt`. This sends the full conversation to Gemini so it can maintain context.

### HTTP status code handling
```kotlin
when (response.code()) {
    404 -> NetworkResult.Error("AI model not available...")
    429 -> NetworkResult.Error("AI is busy...")
    401 -> NetworkResult.Error("Invalid API key...")
    else -> NetworkResult.Error("AI request failed (${response.code()})...")
}
```
Each error code gets a specific user-friendly message rather than a raw HTTP error.

### `try { } catch (e: IOException)` + `catch (e: Exception)`
- `IOException`: covers `UnknownHostException` (no DNS), `ConnectException` (no network), `SocketTimeoutException`.
- `Exception`: catches any other unexpected error (Gson parsing, etc.).

---

## 🏗️ Class Structure
`@Singleton class GeminiRepository @Inject constructor(private val api: GeminiApiService)`

---

## ⚙️ Functions

### `generateContent(history: List<Message>, prompt: String): NetworkResult<String>`
**Purpose:** Send a prompt with conversation history to Gemini and return the AI text.
**Returns:** `NetworkResult.Success(text)` or `NetworkResult.Error(message)`.
**Step by step:**
1. Maps `history` to `List<GeminiContent>`.
2. Appends the new user `prompt` as the last `GeminiContent`.
3. Calls `api.generateContent(BuildConfig.GEMINI_API_KEY, GeminiRequest(contents))`.
4. If `response.isSuccessful`: extracts text from `candidates[0].content.parts[0].text`.
5. If not successful: maps status code to user-friendly error message.
6. `IOException` → "No internet connection".
7. Other exception → uses exception message.

---

## 🔄 Data Flow Diagram
```
AIViewModel.sendMessage(prompt)
        ↓
GeminiRepository.generateContent(history, prompt)
        ↓
Builds GeminiRequest from history + prompt
        ↓
GeminiApiService.generateContent(apiKey, request) — Retrofit POST
        ↓
                 ┌─────────────────────────────┐
                 │  response.isSuccessful        │
                 │  YES → extract text → Success │
                 │  NO  → status code → Error    │
                 └─────────────────────────────┘
        ↓
NetworkResult<String> returned to ViewModel
        ↓
ViewModel updates chat list → Fragment renders new message
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `GeminiApiService` | Makes the Retrofit HTTP call |
| `BuildConfig.GEMINI_API_KEY` | API authentication |
| `GeminiRequest/Content/Part` | Request serialization |
| `NetworkResult` | State wrapping |

---

## ⚠️ Important Notes & Gotchas
- The API key comes from `BuildConfig.GEMINI_API_KEY` which is set in `local.properties` as `gemini.api.key`. If not set, the key will be an empty string and all requests will get a 401 error.
- The Gemini model name is configured in `GeminiApiService` — if the model is unavailable (404), the error message tells the user to update the app.
- `response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text` — this long chain extracts the text safely. If any level returns null, the expression returns null and the result text will be an empty string `""`.

