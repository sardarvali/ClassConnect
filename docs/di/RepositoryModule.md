# RepositoryModule.kt — Hilt DI module placeholder for future repository interface bindings

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/di/RepositoryModule.kt`

---

## 🎯 What This File Does
`RepositoryModule` is a Hilt `@Module` installed in `SingletonComponent`. Currently it contains no `@Provides` or `@Binds` methods because all repository classes use `@Inject constructor` directly, so Hilt can auto-wire them without explicit bindings. This module exists as the **conventional location** for repository bindings when they are eventually abstracted behind interfaces — which is the recommended pattern for unit testing (you can swap `ClassRepository` for a `FakeClassRepository` in tests). Without this file, the pattern has no home and future developers would not know where to add interface bindings.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `dagger.Module` | Hilt/Dagger | Marks a class as a DI module | Required for Hilt to process this class |
| `dagger.hilt.InstallIn` | Hilt | Specifies which component this module belongs to | `SingletonComponent` = app-lifetime scope |
| `dagger.hilt.components.SingletonComponent` | Hilt | The root Hilt component | All `@Singleton` bindings live here |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `@Module`
Tells Hilt: "This class provides DI bindings." Hilt will scan it for `@Provides` and `@Binds` methods at compile time and add them to the dependency graph.

### `@InstallIn(SingletonComponent::class)`
Specifies which Hilt component contains this module's bindings:
| Component | Lifetime | Used For |
|-----------|----------|---------|
| `SingletonComponent` | App lifetime | Repositories, Firebase, Retrofit |
| `ActivityComponent` | Activity lifetime | Activity-scoped dependencies |
| `ViewModelComponent` | ViewModel lifetime | ViewModel-scoped dependencies |

Repositories belong in `SingletonComponent` — one instance for the entire app lifetime.

### `abstract class` (not `object` or `class`)
For `@Binds` methods (which bind an interface to its implementation), the module must be `abstract`. `@Binds` methods are also `abstract` — Hilt generates the implementation. If only `@Provides` methods were needed, a regular class or `object` would suffice.

### Why no `@Provides` methods now
All current repositories (`AuthRepository`, `ClassRepository`, etc.) use `@Singleton @Inject constructor(...)`. Hilt sees `@Inject constructor` and automatically knows how to create them — no explicit `@Provides` needed. Example:
```kotlin
// Hilt handles this automatically — no @Provides needed:
@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) { ... }
```

### Future `@Binds` pattern
```kotlin
// When repositories get interfaces (for easier testing):
@Binds
abstract fun bindClassRepository(impl: ClassRepository): IClassRepository
// "When someone asks for IClassRepository, inject ClassRepository"
```

---

## 🏗️ Class Structure
`abstract class RepositoryModule` — no instantiable members.

---

## ⚠️ Important Notes & Gotchas
- **This class is intentionally empty** — that is correct behavior, not a bug or oversight.
- When you want to add a fake/test repository (e.g., for instrumentation tests), extract an interface, add a `@Binds` method here, and create a test module that overrides it.
- The `@InstallIn(SingletonComponent::class)` means bindings here are available everywhere in the app.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
// ═══════════════════════════════════════
// RepositoryModule.kt
// ═══════════════════════════════════════

package com.syed.classconnect.di

import dagger.Module
// @Module annotation — Hilt scans this class for DI binding methods.
import dagger.hilt.InstallIn
// @InstallIn — specifies the Hilt component scope.
import dagger.hilt.components.SingletonComponent
// SingletonComponent — app-lifetime component. Bindings live as long as the app process.

/**
 * RepositoryModule — placeholder for future interface-to-implementation bindings.
 *
 * All concrete Repository classes are annotated with @Singleton + @Inject constructor
 * and are therefore auto-bound by Hilt without explicit @Provides methods.
 * This module exists as the conventional home for any abstract @Binds methods
 * that may be added when repositories are extracted behind interfaces for easier
 * unit testing.
 *
 * Example pattern:
 *   @Binds abstract fun bindClassRepo(impl: ClassRepository): IClassRepository
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // All current repositories use @Inject constructor — no explicit bindings needed.
    // Add @Binds methods here when repository interfaces are introduced.
}
```

---

## 🧩 Related Files

| File | Relationship |
|------|-------------|
| `AppModule.kt` | Provides Firebase, Retrofit, Storage instances — complementary module |
| `AuthRepository.kt` | Example of auto-bound `@Singleton @Inject` repository |
| `ClassRepository.kt` | Example of auto-bound `@Singleton @Inject` repository |
