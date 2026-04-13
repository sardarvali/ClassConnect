# Architecture — MVVM + Repository Pattern

---

## 📁 Overview

ClassConnect follows the **MVVM (Model-View-ViewModel)** architecture pattern with a **Repository layer** for data access. This is the recommended architecture by Google for Android apps. Every file in the project fits into one of these layers.

---

## 🎯 Why MVVM?

| Problem | MVVM Solution |
|---------|--------------|
| UI freezes during network calls | ViewModel runs coroutines on background threads |
| Data lost on screen rotation | ViewModel survives configuration changes |
| Business logic mixed with UI code | ViewModel handles logic; Fragment only shows data |
| Hard to test | Repository can be swapped with fakes in tests |
| Spaghetti code | Clear separation of concerns across layers |

---

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────────┐
│                    VIEW LAYER                        │
│         Fragments + Activities + Adapters            │
│  (Only: show data, capture input, navigate)          │
└──────────────────┬──────────────────────────────────┘
                   │ observes StateFlow / LiveData
                   │ calls ViewModel functions
┌──────────────────▼──────────────────────────────────┐
│                 VIEWMODEL LAYER                      │
│    (Business logic, state management, coroutines)    │
│    Survives screen rotation. Never references View.  │
└──────────────────┬──────────────────────────────────┘
                   │ calls suspend functions
                   │ collects Flow
┌──────────────────▼──────────────────────────────────┐
│                REPOSITORY LAYER                      │
│     (All data: Firestore, Retrofit, SharedPrefs)     │
│     Returns: Flow<T> for real-time, Result<T> once   │
└──────────┬─────────────────────────┬────────────────┘
           │                         │
┌──────────▼──────────┐   ┌──────────▼──────────────┐
│  Firebase           │   │  Retrofit (REST APIs)    │
│  Auth, Firestore,   │   │  Gemini AI, NewsAPI      │
│  Storage, FCM       │   │                          │
└─────────────────────┘   └──────────────────────────┘
```

---

## 📦 Layer-by-Layer Explanation

### 1. VIEW LAYER — `ui/` package

**What it contains:** Fragments, Activities, Adapters, XML layouts.

**Rules:**
- ✅ Observe LiveData / StateFlow from ViewModel
- ✅ Call ViewModel functions on user interaction
- ✅ Navigate between screens
- ✅ Show/hide loading indicators
- ❌ Never access Firestore or Retrofit directly
- ❌ Never hold data that survives rotation
- ❌ Never contain business logic

**Example — LoginFragment observes auth state:**
```kotlin
viewModel.authState.observe(viewLifecycleOwner) { result ->
    when (result) {
        is NetworkResult.Loading -> showLoading(true)
        is NetworkResult.Success -> navigateToMain()
        is NetworkResult.Error -> showError(result.message)
    }
}
```

### 2. VIEWMODEL LAYER — `ui/*/ViewModel.kt` files

**What it contains:** One ViewModel per screen (or shared across related screens).

**Rules:**
- ✅ Hold UI state in LiveData or StateFlow
- ✅ Launch coroutines in `viewModelScope`
- ✅ Call Repository suspend functions
- ✅ Transform data before exposing to View
- ❌ Never reference Views, Context, or Activity
- ❌ Never import anything from `android.view.*`

**Example — AuthViewModel handles login:**
```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableLiveData<NetworkResult<User>>()
    val authState: LiveData<NetworkResult<User>> = _authState

    fun login(email: String, password: String) {
        _authState.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { _authState.value = NetworkResult.Success(it) },
                onFailure = { _authState.value = NetworkResult.Error(it.message ?: "Failed") }
            )
        }
    }
}
```

### 3. REPOSITORY LAYER — `data/repository/` package

**What it contains:** One Repository per data domain (Auth, Class, Chat, etc.).

**Rules:**
- ✅ Access Firestore, Firebase Auth, Retrofit APIs
- ✅ Return `Flow<T>` for real-time data (Firestore listeners)
- ✅ Return `Result<T>` for one-shot operations
- ✅ Use `callbackFlow` to bridge Firebase callbacks to Kotlin Flow
- ✅ Run on `Dispatchers.IO` for blocking operations
- ❌ Never reference Views or Android UI components
- ❌ Never hold any state (stateless data access layer)

**Example — ClassRepository provides real-time class list:**
```kotlin
@Singleton
class ClassRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getClassesForStudent(userId: String): Flow<List<ClassRoom>> = callbackFlow {
        val sub = firestore.collection("classes")
            .whereArrayContains("studentIds", userId)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull {
                    it.toObject(ClassRoom::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }
}
```

### 4. MODEL LAYER — `data/model/` package

**What it contains:** Data classes representing Firestore documents and API responses.

**Rules:**
- ✅ Use `data class` for automatic `equals()`, `hashCode()`, `copy()`
- ✅ All fields have default values (required for Firestore deserialization)
- ✅ Use `@field:PropertyName` for fields with `is` prefix (Firestore naming issue)
- ❌ No business logic in models (they are pure data holders)

### 5. DI LAYER — `di/` package

**What it contains:** Hilt modules that provide dependencies.

**How it works:**
- `AppModule` provides Firebase instances, Retrofit, OkHttp, and API services
- `RepositoryModule` is a placeholder for future interface bindings
- All repositories use `@Inject constructor` and `@Singleton` — Hilt auto-binds them

---

## 🔄 Data Flow — End-to-End Example

**Scenario: Student views their class list**

```
1. StudentHomeFragment.onViewCreated()
   │
2. viewModel.loadClasses(userId)
   │
3. HomeViewModel.loadClasses() {
   │   viewModelScope.launch {
   │       classRepository.getClassesForStudent(uid).collect { list ->
   │           _classes.value = list  // MutableLiveData
   │       }
   │   }
   │ }
   │
4. ClassRepository.getClassesForStudent(uid) → callbackFlow {
   │   Firestore addSnapshotListener → trySend(list)
   │   awaitClose { listener.remove() }
   │ }
   │
5. Firestore sends initial data + real-time updates
   │
6. Flow emits → ViewModel updates LiveData
   │
7. Fragment's observer receives new list
   │
8. adapter.submitList(classList) → RecyclerView updates
```

---

## 📂 Package Structure

```
com.syed.classconnect/
├── ClassConnectApp.kt              ← Application class (@HiltAndroidApp)
├── data/
│   ├── model/                      ← Data classes (User, ClassRoom, Quiz, etc.)
│   ├── remote/                     ← Retrofit API service interfaces
│   └── repository/                 ← Repository classes (data access)
├── di/                             ← Hilt dependency injection modules
├── sensor/                         ← Hardware sensor handlers
├── service/                        ← Background services (BLE, FCM)
├── ui/
│   ├── admin/                      ← Admin dashboard, user management
│   ├── ai/                         ← AI Buddy, Lesson Planner
│   ├── assignments/                ← Assignment CRUD, submissions, grading
│   ├── attendance/                 ← QR-based attendance
│   ├── auth/                       ← Login, Register, Email Verification
│   ├── chat/                       ← Real-time class chat
│   ├── classes/                    ← Class list, detail, feed
│   ├── home/                       ← Student/Teacher home screens
│   ├── main/                       ← MainActivity with bottom nav
│   ├── notifications/              ← In-app notifications
│   ├── onboarding/                 ← First-time user onboarding
│   ├── permissions/                ← Runtime permission requests
│   ├── profile/                    ← User profile & settings
│   ├── quiz/                       ← Quiz creation, attempt, results
│   ├── splash/                     ← Splash screen with routing logic
│   └── webview/                    ← In-app browser
├── util/                           ← Extension functions, constants, helpers
└── widget/                         ← Home screen timetable widget
```

---

## 🧩 Dependency Injection with Hilt

Hilt is the dependency injection framework used throughout the project. Here's how it works:

1. **`ClassConnectApp`** is annotated with `@HiltAndroidApp` — this triggers Hilt code generation
2. **`AppModule`** provides all third-party dependencies (Firebase, Retrofit, OkHttp)
3. **Repository classes** use `@Inject constructor` — Hilt auto-creates them
4. **ViewModels** use `@HiltViewModel` + `@Inject constructor` — Hilt provides dependencies
5. **Fragments/Activities** use `@AndroidEntryPoint` — enables `by viewModels()` with Hilt

---

## 🔑 Key Design Decisions

| Decision | Why |
|----------|-----|
| LiveData in ViewModels (not just StateFlow) | Works naturally with `observe()` in Fragments; lifecycle-aware |
| `callbackFlow` for Firestore | Bridges callback-based Firebase API into Kotlin coroutines/Flow |
| `NetworkResult<T>` sealed class | Represents Loading/Success/Error states; exhaustive `when` |
| `@Singleton` on repositories | One instance shared across all ViewModels; consistent caching |
| ViewBinding (not Compose) | Stable, well-understood; XML layouts with type-safe view access |
| `FieldValue.serverTimestamp()` | Server-side timestamps prevent clock skew issues across devices |

---

## ⚠️ Important Architecture Rules

1. **Never pass Context to a ViewModel** — use `AndroidViewModel` only as last resort
2. **Never call `observe()` in `onCreate()`** — always use `viewLifecycleOwner` in Fragments
3. **Always null `_binding` in `onDestroyView()`** — prevents memory leaks
4. **Always use `awaitClose` in `callbackFlow`** — prevents Firestore listener leaks
5. **Use `viewModelScope` for coroutines** — auto-cancels when ViewModel is destroyed

