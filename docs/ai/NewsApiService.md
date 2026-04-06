# NewsApiService.kt — Retrofit interface for fetching education news from NewsAPI

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/data/remote/NewsApiService.kt`

---

## 🎯 What This File Does
`NewsApiService` is a Retrofit interface with one method: `getEducationNews()`. It makes a GET request to the NewsAPI `/v2/top-headlines` endpoint to fetch current education news, filtered by category and language. The response is deserialized by Gson into `NewsResponse` (which contains a `List<NewsArticle>`). `HomeViewModel` calls this to populate the teacher home screen news section. Without this interface, no news data can be fetched.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `com.syed.classconnect.data.model.NewsResponse` | Project | API response wrapper | Return type |
| `retrofit2.Response` | Retrofit | HTTP response wrapper | Allows checking status code |
| `retrofit2.http.GET` | Retrofit | HTTP GET annotation | Marks method as GET request |
| `retrofit2.http.Query` | Retrofit | URL query parameter | Adds `?apiKey=...` etc. to URL |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `@GET("v2/top-headlines")`
Appended to the base URL configured in `RetrofitClient` (NewsAPI base URL). The full URL becomes: `https://newsapi.org/v2/top-headlines?category=education&language=en&pageSize=10&apiKey={key}`.

### `@Query("apiKey") apiKey: String`
Retrofit automatically appends this as a URL query parameter. Avoids hardcoding the API key in the annotation.

### `suspend fun getEducationNews(...): Response<NewsResponse>`
`suspend` — must be called from a coroutine. `Response<T>` wraps the HTTP response, giving access to both the body and the status code. `HomeViewModel` checks `response.isSuccessful` before accessing `response.body()`.

### Default parameters
```kotlin
@Query("category") category: String = "education",
@Query("language") language: String = "en",
@Query("pageSize") pageSize: Int = 10
```
Default values allow callers to use `getEducationNews(apiKey = key)` without specifying all parameters. The defaults produce sensible results for the teacher home screen.

---

## 🏗️ Class Structure
`interface NewsApiService` — Retrofit generates an implementation at runtime.

---

## ⚙️ Functions

### `getEducationNews(apiKey, category, language, pageSize): Response<NewsResponse>`
Fetches education news headlines. Parameters have sensible defaults.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.data.remote

import com.syed.classconnect.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    // GET https://newsapi.org/v2/top-headlines?...
    suspend fun getEducationNews(
        @Query("apiKey") apiKey: String,
        // Authentication key — passed as URL parameter, not header.
        @Query("category") category: String = "education",
        // Filter by news category — "education" for teacher-relevant news.
        @Query("language") language: String = "en",
        // English articles only.
        @Query("pageSize") pageSize: Int = 10
        // Number of articles to fetch. 10 is sufficient for the home screen cards.
    ): Response<NewsResponse>
    // Response<T> wraps the HTTP response. Use response.body() for the data,
    // response.isSuccessful for the status check, response.code() for HTTP status.
}
```

