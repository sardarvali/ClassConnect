# AuthRepository — All authentication and user management data operations

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/repository/AuthRepository.kt`

---

## 🎯 What This File Does
AuthRepository is the single source of truth for all authentication operations: login, registration (both paths), Google Sign-In, password reset, user profile management, institution creation, and user approval. It wraps Firebase Auth and Firestore operations behind suspend functions and Flows. Every auth-related ViewModel depends on this repository.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Authentication service | Login, register, sign out, current user |
| `com.google.firebase.auth.GoogleAuthProvider` | Firebase Auth | Google credential provider | Converts Google ID token to Firebase credential |
| `com.google.firebase.firestore.FieldValue` | Firestore | Server-side field operations | `serverTimestamp()` for `createdAt` |
| `com.google.firebase.firestore.FirebaseFirestore` | Firestore | Database service | User document CRUD operations |
| `com.syed.classconnect.data.model.Institution` | App | Data model | Institution data class |
| `com.syed.classconnect.data.model.User` | App | Data model | User data class |
| `com.syed.classconnect.util.Constants` | App | Constants | Collection names, role constants |
| `com.syed.classconnect.util.ValidationUtils` | App | Validation | Sanitize input, generate codes |
| `kotlinx.coroutines.Dispatchers` | Coroutines | Thread dispatchers | `Dispatchers.IO` for blocking operations |
| `kotlinx.coroutines.channels.awaitClose` | Coroutines | Flow cleanup | Removes Firestore listener when Flow is cancelled |
| `kotlinx.coroutines.flow.Flow` / `callbackFlow` | Coroutines | Async streams | Real-time user observation |
| `kotlinx.coroutines.tasks.await` | Coroutines Play Services | Task→suspend | Converts Firebase Tasks to suspend calls |
| `kotlinx.coroutines.withContext` | Coroutines | Thread switching | Runs blocking code on IO dispatcher |
| `javax.inject.Inject` / `javax.inject.Singleton` | Hilt DI | DI annotations | Singleton repository provided by Hilt |

---

## 🏗️ Class Structure
```kotlin
@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
)
```
- `@Singleton`: One instance for the entire app lifetime
- `@Inject constructor`: Hilt provides FirebaseAuth and Firestore (from AppModule)

---

## ⚙️ Key Functions

### `login(email, password): Result<User>`
Signs in with Firebase Auth, reads User document from Firestore. Self-heals admin approval if needed.

### `register(name, email, password, role, institutionCode): Result<User>`
Legacy registration (validates institution code, creates account, stores User document).

### `registerWithInstitutionCode(name, email, password, role, code): Result<String>`
**Path A**: Validates institution code → creates Firebase Auth account → sends email verification → stores User doc with `isApproved=false`, `accountType="institution"`.

### `registerIndependent(name, email, password, role): Result<String>`
**Path B**: Creates Firebase Auth account → sends email verification → stores User doc with `isApproved=false`, `accountType="independent"`.

### `signInWithGoogle(idToken): Result<Pair<User, Boolean>>`
Signs in with Google credential. Returns existing user or new user info + `isNewUser` flag.

### `observeCurrentUser(): Flow<User?>`
Real-time Firestore listener on the current user's document. Emits updates whenever the user document changes.

### `approveUser(uid, approved)`
Admin action: sets `isApproved` (and backward-compatible `approved` field) in Firestore.

### `approveIndependentUser(uid): Result<Unit>`
Auto-approves an independent user after email verification — sets `isApproved=true`, `emailVerified=true`.

### `createInstitution(name, adminUid): Result<Institution>`
Creates a new institution with a random 6-character join code.

### `getUsersForInstitutionOnce(institutionId): List<User>`
One-shot server fetch (not cached) of all users in an institution. Used by admin dashboard.

---

## 🔄 Data Flow
```
LoginFragment → AuthViewModel.login() → AuthRepository.login()
    → FirebaseAuth.signInWithEmailAndPassword().await()
    → FirebaseFirestore.get(users/{uid}).await()
    → Returns Result<User>

RegisterFragment → AuthViewModel.register() → AuthRepository.registerWithInstitutionCode()
    → Validates institution code via Firestore query
    → FirebaseAuth.createUserWithEmailAndPassword().await()
    → Sends email verification
    → Stores User document in Firestore
    → Returns Result<String> (uid)
```

---

## ⚠️ Important Notes
- Admin self-healing: `login()` and `signInWithGoogle()` auto-fix `isApproved=false` for admins
- `approveUser()` updates BOTH `isApproved` and `approved` fields for backward compatibility
- `getUsersForInstitutionOnce()` uses `Source.SERVER` to force a server read (bypasses cache)
- `observeCurrentUser()` MUST have `awaitClose { sub.remove() }` or Firestore listener leaks
- `registerWithInstitutionCode()` converts institution code to UPPERCASE before querying

