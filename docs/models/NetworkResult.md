# NetworkResult — Sealed class for representing Loading/Success/Error states

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/util/NetworkResult.kt`

---

## 🎯 What This File Does
`NetworkResult<T>` is a sealed class that represents the three possible states of any asynchronous operation: Loading (in progress), Success (data received), or Error (something went wrong). Every ViewModel in the project uses this class to communicate state to the View layer. Without this file, each screen would need its own ad-hoc way to represent loading/error states.

---

## 📦 Imports
This file has NO imports — it uses only Kotlin standard library types.

---

## 🔑 Kotlin & Android Keywords

### `sealed class` — Closed Type Hierarchy
```kotlin
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}
```
A `sealed class` restricts which classes can extend it — all subclasses must be in the same file. The compiler knows EVERY possible subtype, making `when` expressions exhaustive (no `else` branch needed):

```kotlin
when (result) {
    is NetworkResult.Success -> showData(result.data)   // Smart cast: result.data is T
    is NetworkResult.Error   -> showError(result.message)
    is NetworkResult.Loading -> showSpinner()
    // No else needed — compiler knows all cases
}
```

### `<T>` — Generics (Type Parameter)
`<T>` is a placeholder for any type. This lets ONE class work with ALL data types:
```kotlin
NetworkResult<User>              // holds a User
NetworkResult<List<ClassRoom>>   // holds a list of ClassRooms
NetworkResult<Unit>              // holds nothing (for operations that don't return data)
```

### `data class` vs `class`
- `Success` and `Error` are `data class` because they hold data (we want `equals()`, `toString()`)
- `Loading` is a plain `class` because it holds no data

---

## 🏗️ Class Structure

```kotlin
sealed class NetworkResult<T>
├── data class Success<T>(val data: T)              // Operation succeeded, data available
├── data class Error<T>(val message: String, val code: Int? = null)  // Operation failed
└── class Loading<T>()                               // Operation in progress
```

---

## Why Sealed Class Instead of These Alternatives?

### Alternative 1: Boolean flags (BAD)
```kotlin
// Problem: can have impossible states (isLoading=true AND data!=null AND error!=null)
var isLoading = false
var data: List<ClassRoom>? = null
var errorMessage: String? = null
```
Three independent variables = 8 possible combinations, many of which are invalid.

### Alternative 2: Exception throwing (BAD)
```kotlin
// Problem: exceptions are invisible in function signatures
suspend fun getClasses(): List<ClassRoom> {
    throw IOException("Network error")  // caller might forget to catch
}
```
The caller has no compile-time reminder to handle errors.

### Alternative 3: Sealed class (GOOD — what we use)
```kotlin
sealed class NetworkResult<T> { ... }
```
- Only ONE state at a time (impossible states are... impossible)
- Compiler enforces handling all cases in `when`
- Type-safe: `Success.data` is strongly typed as `T`

---

## The Generic Type Parameter T — Why It Matters

```kotlin
// Without generics: need separate classes for each data type
class UserResult(val data: User)
class ClassListResult(val data: List<ClassRoom>)
class UnitResult(val data: Unit)
// Problem: duplicated code for every type

// With generics: ONE class works for all types
NetworkResult<User>              // result holding a User
NetworkResult<List<ClassRoom>>   // result holding a list of classes
NetworkResult<String>            // result holding a string
NetworkResult<Unit>              // result for operations with no return value
```

---

## Smart Cast Inside `when{}`

```kotlin
val result: NetworkResult<User> = viewModel.authState.value

when (result) {
    is NetworkResult.Success -> {
        // Smart cast! result is now NetworkResult.Success<User>
        // result.data is User — no casting needed
        showUser(result.data)
    }
    is NetworkResult.Error -> {
        // Smart cast! result is now NetworkResult.Error<User>
        // result.message is String, result.code is Int?
        showError(result.message)
        if (result.code == 429) showRetryHint()
    }
    is NetworkResult.Loading -> {
        showProgressBar()
    }
}
```

The `is` keyword performs a type check AND a smart cast simultaneously. After `is NetworkResult.Success`, the compiler knows `result` is `Success` and lets you access `.data` without manual casting.

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.util
// Package: utility layer. NetworkResult is used across all layers.

sealed class NetworkResult<T> {
// sealed class: only Success, Error, Loading can extend this.
// <T>: generic type parameter — replaced with actual types when used.
// Example: NetworkResult<User>, NetworkResult<List<Quiz>>

    data class Success<T>(val data: T) : NetworkResult<T>()
    // Success: operation completed, data is available.
    // val data: T — the actual data. Type is whatever T was specified as.
    // data class: auto-generates equals(), hashCode(), toString().
    //   toString() → "Success(data=User(name=Arjun, ...))"
    // : NetworkResult<T>() — Success IS a NetworkResult.

    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    // Error: operation failed with an error message.
    // val message: String — human-readable error description.
    // val code: Int? — optional HTTP status code (e.g., 429 for rate limit, 404 for not found).
    // Nullable (?) because not all errors have HTTP codes (e.g., network timeout).

    class Loading<T> : NetworkResult<T>()
    // Loading: operation is in progress, no data yet.
    // Regular class (not data class) because it holds no data.
    // Each Loading() creates a new instance, but that's fine — we only check `is Loading`.
}
// Used throughout the app:
// - ViewModels expose LiveData<NetworkResult<T>> or MutableLiveData<NetworkResult<T>>
// - Fragments observe and use `when` to show loading/data/error
// - Repositories return NetworkResult from API calls (GeminiRepository)
```

---

## 🧩 This File Depends On
Nothing — this is a standalone utility class with zero imports.

---

## ⚠️ Important Notes
- `Error.code` was added for Gemini API rate limiting (429) — allows UI to show "try again later" instead of generic error
- `Loading` is a regular `class`, not `data class` — creating `Loading()` instances is fine since we only check `is Loading`, never compare Loading instances
- This pattern is sometimes called "Resource" or "UiState" in other Android projects

