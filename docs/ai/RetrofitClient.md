# RetrofitClient — Retrofit instance configuration

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/di/AppModule.kt` (Retrofit is configured in AppModule, not a separate RetrofitClient object)

---

## 🎯 What This File Does
In this project, Retrofit instances are configured in `AppModule.kt` using Hilt `@Provides` methods rather than a separate `RetrofitClient` singleton. The module creates OkHttpClient with logging interceptor, and two Retrofit instances: one for Gemini AI API and one for NewsAPI.

See [AppModule.md](../di/AppModule.md) for the complete Retrofit configuration documentation.

---

## Key Configuration

```kotlin
// Gemini API Retrofit
Retrofit.Builder()
    .baseUrl("https://generativelanguage.googleapis.com/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(GeminiApiService::class.java)

// News API Retrofit
Retrofit.Builder()
    .baseUrl("https://newsapi.org/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(NewsApiService::class.java)
```

