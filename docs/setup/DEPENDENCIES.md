# Dependencies — Every Gradle Dependency Explained

---

## 📁 Location

Dependencies are declared in two files:
- `gradle/libs.versions.toml` — Version catalog (centralized version management)
- `app/build.gradle.kts` — Dependency declarations using the catalog

---

## 🎯 What This File Documents

Every single dependency in the project, what it does, why the project needs it, and what version is used. Dependencies are grouped by purpose.

---

## 📦 Complete Dependency Table

### Core Android

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.core:core-ktx` | 1.13.1 | Kotlin extensions for Android core APIs | Simplifies Android API calls with Kotlin idioms (`context.getSystemService<T>()`) |
| `androidx.appcompat:appcompat` | 1.7.0 | Backward-compatible versions of Android UI components | Ensures Material Design works on older Android versions (API 26+) |
| `com.google.android.material:material` | 1.12.0 | Material Design Components (MDC) | Provides MaterialCardView, BottomNavigationView, TextInputLayout, BottomSheetDialogFragment, Snackbar, etc. |
| `androidx.constraintlayout:constraintlayout` | 2.1.4 | Flat layout hierarchy with constraint-based positioning | Used in most XML layouts for performance (single flat view hierarchy instead of nested LinearLayouts) |

### Lifecycle (MVVM Foundation)

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | 2.8.3 | ViewModel + Kotlin coroutine extensions | `viewModelScope`, `by viewModels()` — ViewModel coroutine support |
| `androidx.lifecycle:lifecycle-livedata-ktx` | 2.8.3 | LiveData + Kotlin extensions | `map{}`, `switchMap{}` transformations on LiveData |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.8.3 | Lifecycle-aware coroutine scopes | `lifecycleScope`, `repeatOnLifecycle()` — collecting Flows safely in Fragments |

### Activity & Fragment KTX

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.activity:activity-ktx` | 1.9.0 | Kotlin extensions for Activity | `registerForActivityResult()` — modern replacement for `onActivityResult()` |
| `androidx.fragment:fragment-ktx` | 1.8.1 | Kotlin extensions for Fragment | `by viewModels()`, `by activityViewModels()` — ViewModel delegation |

### Navigation

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.navigation:navigation-fragment-ktx` | 2.8.5 / 2.9.7 | Fragment-based navigation component | `findNavController().navigate()` — type-safe navigation between Fragments |
| `androidx.navigation:navigation-ui-ktx` | 2.8.5 / 2.9.7 | Navigation + UI integration | `setupWithNavController()` — connects BottomNavigationView to NavController |

### Hilt (Dependency Injection)

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.google.dagger:hilt-android` | 2.52 | Hilt DI framework for Android | `@HiltViewModel`, `@AndroidEntryPoint`, `@Inject` — automatic dependency provision |
| `com.google.dagger:hilt-compiler` | 2.52 | Hilt annotation processor (KSP) | Generates DI code at compile time (factories, component implementations) |

### Firebase (Backend-as-a-Service)

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.google.firebase:firebase-bom` | 33.1.0 | Firebase Bill of Materials | Ensures all Firebase libraries use compatible versions (no version conflicts) |
| `firebase-auth-ktx` | (BoM) | Firebase Authentication | Email/password login, Google Sign-In, email verification |
| `firebase-firestore-ktx` | (BoM) | Cloud Firestore | NoSQL database for users, classes, assignments, quizzes, chat, attendance |
| `firebase-storage-ktx` | (BoM) | Firebase Cloud Storage | File uploads: profile photos, assignment submissions, materials |
| `firebase-messaging-ktx` | (BoM) | Firebase Cloud Messaging (FCM) | Push notifications for new assignments, chat messages, announcements |
| `firebase-crashlytics-ktx` | (BoM) | Firebase Crashlytics | Crash reporting — logs crashes in production for debugging |
| `firebase-analytics-ktx` | (BoM) | Firebase Analytics | Usage analytics — tracks screen views, events (optional) |

### Google Sign-In

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.google.android.gms:play-services-auth` | 21.2.0 | Google Sign-In SDK | One-tap Google account sign-in on the login screen |

### Retrofit + OkHttp (Networking)

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.squareup.retrofit2:retrofit` | 2.11.0 | Type-safe HTTP client | Makes REST API calls to Gemini AI and NewsAPI |
| `com.squareup.retrofit2:converter-gson` | 2.11.0 | Gson JSON converter for Retrofit | Automatically converts JSON responses to Kotlin data classes |
| `com.squareup.okhttp3:okhttp` | 4.12.0 | HTTP client (foundation for Retrofit) | Connection pooling, timeouts, HTTP/2 support |
| `com.squareup.okhttp3:logging-interceptor` | 4.12.0 | HTTP request/response logger | Logs all API calls in debug builds for debugging |

### Coroutines

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | 1.8.1 | Coroutines for Android | `Dispatchers.Main`, `Dispatchers.IO` — thread management |
| `org.jetbrains.kotlinx:kotlinx-coroutines-play-services` | 1.8.1 | `.await()` extension for Google Play Tasks | Converts Firebase `Task<T>` to `suspend fun` calls |

### Image Loading

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.github.bumptech.glide:glide` | 4.16.0 | Image loading and caching library | Loads profile photos, news images, material thumbnails with caching |
| `com.github.bumptech.glide:compiler` | 4.16.0 | Glide annotation processor | Generates `GlideApp` class for custom configurations |
| `de.hdodenhof:circleimageview` | 3.1.0 | Circular ImageView widget | Displays profile photos in a circle shape in chat, profile, student lists |

### Animation

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.airbnb.android:lottie` | 6.4.1 | Render After Effects animations | Splash screen animations, loading indicators, empty states |

### Charts

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.github.PhilJay:MPAndroidChart` | v3.1.0 | Charting library | Quiz results charts, attendance statistics, admin dashboard graphs |

### QR Code / Barcode

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.google.zxing:core` | 3.5.3 | QR code generation library | Generates QR codes for attendance sessions |
| `com.journeyapps:zxing-android-embedded` | 4.3.0 | QR code scanner UI | Provides built-in scanner activity for students scanning QR codes |

### CameraX + ML Kit

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.camera:camera-camera2` | 1.4.1 | CameraX Camera2 implementation | Camera access for QR code scanning |
| `androidx.camera:camera-lifecycle` | 1.4.1 | Lifecycle-aware camera management | Automatically starts/stops camera with Fragment lifecycle |
| `androidx.camera:camera-view` | 1.4.1 | CameraX PreviewView | Displays camera preview in the QR scanner layout |
| `com.google.mlkit:barcode-scanning` | 17.3.0 | ML Kit Barcode Scanner | On-device barcode/QR code recognition from camera frames |

### UI Enhancement

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.facebook.shimmer:shimmer` | 0.5.0 | Shimmer loading effect | Skeleton loading placeholders while data loads |
| `io.noties.markwon:core` | 4.6.2 | Markdown renderer for Android | Renders AI responses (Gemini) with formatting in TextViews |
| `io.noties.markwon:ext-strikethrough` | 4.6.2 | Strikethrough extension for Markwon | Supports ~~strikethrough~~ in AI-generated markdown |

### Security

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.biometric:biometric` | 1.1.0 | Biometric authentication API | Fingerprint/face lock when returning from background |
| `androidx.security:security-crypto` | 1.1.0-alpha06 | Encrypted SharedPreferences | Securely stores sensitive preferences (API keys, tokens) |

### Data Storage

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.datastore:datastore-preferences` | 1.1.1 | Modern replacement for SharedPreferences | Stores user preferences (theme, onboarding state) with coroutine support |

### UI Components

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.viewpager2:viewpager2` | 1.1.0 | Swipeable page container | Onboarding screens, class detail tabs (Feed, Assignments, Quizzes, etc.) |
| `androidx.swiperefreshlayout:swiperefreshlayout` | 1.1.0 | Pull-to-refresh | Refresh class list, assignment list, notifications by pulling down |

### Background Work

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `androidx.work:work-runtime-ktx` | 2.9.0 | WorkManager for background tasks | Schedule periodic tasks (widget refresh, notification sync) |

### Logging

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `com.jakewharton.timber:timber` | 5.0.1 | Better logging than `Log.d()` | Automatic tag generation, no-op in release builds, plant different trees |

### Testing

| Dependency | Version | What It Is | Why ClassConnect Needs It |
|-----------|---------|-----------|--------------------------|
| `junit:junit` | 4.13.2 | Unit testing framework | Basic unit tests for ViewModels and Repositories |
| `androidx.test.ext:junit` | 1.2.1 | AndroidX JUnit extensions | `@RunWith(AndroidJUnit4::class)` for instrumented tests |
| `androidx.test.espresso:espresso-core` | 3.6.1 | UI testing framework | Automated UI tests (button clicks, text verification) |
| `com.google.dagger:hilt-android-testing` | 2.52 | Hilt test utilities | `HiltTestRunner` for injecting test dependencies |

---

## 🔧 Gradle Plugins

| Plugin | Version | What It Does |
|--------|---------|-------------|
| `com.android.application` | 8.4.2 | Android Gradle Plugin — builds the Android app |
| `org.jetbrains.kotlin.android` | 2.0.21 | Kotlin compiler for Android |
| `com.google.devtools.ksp` | 2.0.21-1.0.27 | Kotlin Symbol Processing — annotation processing (faster than kapt) |
| `com.google.dagger.hilt.android` | 2.52 | Hilt Gradle plugin — generates Hilt components |
| `com.google.gms.google-services` | 4.4.2 | Google Services plugin — processes `google-services.json` |
| `com.google.firebase.crashlytics` | 3.0.2 | Crashlytics Gradle plugin — uploads mapping files for deobfuscation |

---

## 📝 How the Version Catalog Works

The `gradle/libs.versions.toml` file uses TOML format with three sections:

```toml
[versions]       # Define version numbers once
kotlin = "2.0.21"

[libraries]      # Define library coordinates + link to versions
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }

[plugins]        # Define Gradle plugins
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

In `build.gradle.kts`, you reference them as:
```kotlin
implementation(libs.hilt.android)      // Library
alias(libs.plugins.hilt.android)       // Plugin
```

This ensures all modules use the same versions and prevents version conflicts.

