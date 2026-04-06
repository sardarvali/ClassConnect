# RoleChangeHistoryViewModel.kt — ViewModel that loads the institution's admin role change audit log

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/RoleChangeHistoryViewModel.kt`

---

## 🎯 What This File Does
`RoleChangeHistoryViewModel` fetches the list of `RoleChangeLog` entries for the admin's institution from Firestore. It exposes a `StateFlow<NetworkResult<List<RoleChangeLog>>>` so `RoleChangeHistoryFragment` can show a loading spinner, the log list, or an error state. The logs are stored in `institutions/{institutionId}/roleChangeLogs` and ordered newest-first. Without this ViewModel, `RoleChangeHistoryFragment` has no data.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `androidx.lifecycle.ViewModel` | AndroidX | ViewModel base | This VM extends it |
| `androidx.lifecycle.viewModelScope` | AndroidX | Coroutine scope | `viewModelScope.launch` |
| `com.google.firebase.auth.ktx.auth` | Firebase KTX | Property extension for `FirebaseAuth` | `Firebase.auth` shorthand |
| `com.google.firebase.firestore.Query` | Firebase | Firestore query builder | `Query.Direction.DESCENDING` |
| `com.google.firebase.firestore.ktx.firestore` | Firebase KTX | Property extension for Firestore | `Firebase.firestore` shorthand |
| `com.google.firebase.ktx.Firebase` | Firebase KTX | KTX companion object | Access to `Firebase.auth`, `Firebase.firestore` |
| `com.syed.classconnect.data.model.RoleChangeLog` | Project | Log entry data class | List item type |
| `com.syed.classconnect.data.repository.AuthRepository` | Project | User lookup | `getUserById()` |
| `com.syed.classconnect.util.NetworkResult` | Project | State wrapper | Loading/Success/Error |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | ViewModel DI | `by viewModels()` |
| `kotlinx.coroutines.flow.MutableStateFlow` | Coroutines | Writable state flow | `_logs` |
| `kotlinx.coroutines.flow.StateFlow` | Coroutines | Read-only state flow | `logs` |
| `kotlinx.coroutines.launch` | Coroutines | Start coroutine | `viewModelScope.launch` |
| `kotlinx.coroutines.tasks.await` | Coroutines | Suspend Firebase Task | `.get().await()` |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | `@Inject constructor(...)` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `Firebase.auth.currentUser?.uid` (KTX shorthand)
`Firebase.auth` is a KTX property extension equivalent to `FirebaseAuth.getInstance()`. The `?.uid` is a safe call — null if not logged in.

### `Firebase.firestore` (KTX shorthand)
`Firebase.firestore` = `FirebaseFirestore.getInstance()`. More concise than storing a `@Inject` dependency, but requires the Firebase KTX library.

> Note: Unlike other ViewModels in the project that use `@Inject`ed Firestore, this one uses KTX directly. This is acceptable for simple queries but less testable than dependency injection.

### `institutionId.ifEmpty { return@launch }`
Guard clause using Elvis with `return@launch`. If the admin's institutionId is empty (standalone user), exits the coroutine immediately.

### `.orderBy("changedAt", Query.Direction.DESCENDING)`
Orders documents newest-first. Requires a Firestore composite index on `institutionId` + `changedAt` if also filtering by institutionId.

### `snap.documents.mapNotNull { it.toObject(RoleChangeLog::class.java)?.copy(id = it.id) }`
Maps each document snapshot to a `RoleChangeLog` with its document ID set via `copy(id = it.id)`. `mapNotNull` skips any documents that fail deserialization.

---

## 🏗️ Class Structure
`@HiltViewModel class RoleChangeHistoryViewModel @Inject constructor(authRepository) : ViewModel()`

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_logs` | `MutableStateFlow<NetworkResult<List<RoleChangeLog>>>` | `private` | Log loading state | Initial = Loading |
| `logs` | `StateFlow<NetworkResult<List<RoleChangeLog>>>` | `val` | Read-only | Fragment collects this |

---

## ⚙️ Functions

### `loadLogs()`
**Purpose:** Fetch all role change logs for the admin's institution.
**Step by step:**
1. Sets `_logs.value = NetworkResult.Loading()`.
2. Gets admin UID from `Firebase.auth.currentUser?.uid`. Returns if null.
3. Gets admin's `User` from `authRepository.getUserById(adminUid)`. Returns if null.
4. Gets `instId` from `adminUser.institutionId`. Returns if empty.
5. Queries `institutions/{instId}/roleChangeLogs` ordered by `changedAt` descending.
6. Maps documents to `RoleChangeLog` list.
7. Sets `_logs.value = NetworkResult.Success(list)`.
8. Catches exceptions → `NetworkResult.Error(message)`.

---

## 🔄 Data Flow Diagram
```
RoleChangeHistoryFragment.onViewCreated()
        ↓
viewModel.loadLogs()
        ↓
_logs = Loading → Fragment shows spinner
        ↓
Firestore query: institutions/{instId}/roleChangeLogs orderBy changedAt DESC
        ↓
_logs = Success(list) → Fragment hides spinner, adapter.submitList(list)
```

