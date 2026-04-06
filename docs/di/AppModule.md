# AppModule — Hilt dependency injection module

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/di/AppModule.kt`

---

## 🎯 What This File Does
AppModule is a Hilt `@Module` that provides all third-party dependencies: Firebase instances, OkHttp client, Retrofit instances (Gemini AI + NewsAPI), and API services. All dependencies are `@Singleton` scoped — one instance for the app's lifetime.

---

## 📦 Imports

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Auth service | Login, register, sign out |
| `com.google.firebase.firestore.FirebaseFirestore` | Firestore | Database | All data operations |
| `com.google.firebase.storage.FirebaseStorage` | Firebase Storage | File storage | Profile photos, submissions |
| `com.squareup.okhttp3.OkHttpClient` / `logging.HttpLoggingInterceptor` | OkHttp | HTTP client | Network requests with logging |
| `retrofit2.Retrofit` / `converter.gson.GsonConverterFactory` | Retrofit | REST client | API service creation |
| `com.syed.classconnect.data.remote.GeminiApiService` | App | API interface | Gemini AI calls |
| `com.syed.classconnect.data.remote.NewsApiService` | App | API interface | News API calls |
| `dagger.Module` / `hilt.InstallIn` / `components.SingletonComponent` | Hilt | DI | Module registration |
| `dagger.Provides` / `javax.inject.Singleton` | Hilt | DI | Provider methods |

---

## 📋 @Provides Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `provideFirebaseAuth()` | `FirebaseAuth` | `FirebaseAuth.getInstance()` |
| `provideFirestore()` | `FirebaseFirestore` | `FirebaseFirestore.getInstance()` |
| `provideStorage()` | `FirebaseStorage` | `FirebaseStorage.getInstance()` |
| `provideOkHttpClient()` | `OkHttpClient` | With logging interceptor (BODY level for debug) |
| `provideGeminiApi(okHttp)` | `GeminiApiService` | Retrofit with base URL `https://generativelanguage.googleapis.com/` |
| `provideNewsApi(okHttp)` | `NewsApiService` | Retrofit with base URL `https://newsapi.org/` |

---

## 📝 Full Annotated Source Code

```kotlin
@Module
// @Module: tells Hilt this class contains @Provides methods.
@InstallIn(SingletonComponent::class)
// @InstallIn(SingletonComponent): these dependencies live for the app's lifetime.
// SingletonComponent = application-scoped. Other options: ActivityComponent, FragmentComponent.
object AppModule {
// object: Kotlin singleton. Hilt modules with only @Provides can be objects.

    @Provides @Singleton
    // @Provides: tells Hilt "call this function when someone needs a FirebaseAuth".
    // @Singleton: create only ONE instance, reuse everywhere.
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    // FirebaseAuth.getInstance(): returns the singleton Firebase Auth instance.

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            // BODY: logs full request/response bodies. Use NONE in production.
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideGeminiApi(okHttpClient: OkHttpClient): GeminiApiService =
        // Hilt automatically provides the OkHttpClient from provideOkHttpClient().
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)

    @Provides @Singleton
    fun provideNewsApi(okHttpClient: OkHttpClient): NewsApiService =
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
}
```

---

## ⚠️ Important Notes
- `HttpLoggingInterceptor.Level.BODY` should be set to `NONE` in production builds (logs sensitive data)
- All Firebase instances use default project (configured via `google-services.json`)
- Two separate Retrofit instances are needed because Gemini and NewsAPI have different base URLs
- Repository classes use `@Inject constructor` + `@Singleton` — they're automatically provided by Hilt without @Provides

