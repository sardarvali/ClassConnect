# AdminViewModel.kt — Shared ViewModel for admin user management, stats, approval, role changes, and class assignment

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/AdminViewModel.kt`

---

## 🎯 What This File Does
`AdminViewModel` is the central ViewModel for all admin functionality: it maintains a persistent real-time Firestore listener for the institution's users, computes admin stats (total users, teachers, students, class count), drives the three user management tabs (Pending, Teachers, Students), handles user approval/rejection, role changes, and class-to-teacher assignment. A `sealed class ApproveResult` signals approve/reject outcomes to the UI for Snackbar display. A `data class AdminStats` holds the computed aggregate counts. A single persistent coroutine `listenerJob` ensures only one Firestore listener runs at a time. Without this ViewModel, all admin management screens are non-functional.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `androidx.lifecycle.LiveData` | AndroidX | Read-only observable | Public state |
| `androidx.lifecycle.MutableLiveData` | AndroidX | Writable observable | Private backing fields |
| `androidx.lifecycle.ViewModel` | AndroidX | Lifecycle-aware state | AdminViewModel extends it |
| `androidx.lifecycle.viewModelScope` | AndroidX | Coroutine scope | All coroutines |
| `com.syed.classconnect.data.model.User` | Project | User data class | List item type |
| `com.syed.classconnect.data.repository.AuthRepository` | Project | User operations | Load users, approve, change role |
| `com.syed.classconnect.data.repository.ClassRepository` | Project | Class operations | Assign teacher to class |
| `com.syed.classconnect.util.NetworkResult` | Project | State wrapper | Loading/Success/Error |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | ViewModel DI | `by viewModels()` |
| `kotlinx.coroutines.Job` | Coroutines | Coroutine reference | Cancel old listener before starting new |
| `kotlinx.coroutines.launch` | Coroutines | Start coroutine | All async calls |
| `timber.log.Timber` | Timber | Logging | Debug/error log |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | `@Inject constructor(...)` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `sealed class ApproveResult`
Two outcomes of an approve/reject operation:
- `Success(uid, approved)`: which user, and whether they were approved (true) or rejected (false)
- `Error(uid, message)`: which user, and the error message
The Fragment observes `_approveResult` and shows a Snackbar based on this.

### `data class AdminStats(totalUsers, teachers, students, classes)`
Computed from the live user list and class count. All fields default to 0 — safe to render before data loads.

### `private var listenerJob: Job?`
A reference to the currently-running Firestore listener coroutine. Before starting a new listener, the old one is cancelled. This prevents duplicate listeners (each would fire independently, doubling data).

### `if (adminUid == uid && listenerJob?.isActive == true) return`
Guard in `loadAdminStats()`: if we're already listening for this admin, do nothing. Prevents redundant restarts on screen rotation.

### `forceReload(uid: String)`
Cancels the current listener and restarts from scratch. Called on error recovery.

### Real-time user classification
After loading all institution users, the ViewModel classifies them into:
- `_pendingUsers` = `isApproved == false && isRejected != true`
- Teachers = `role == "teacher" && isApproved == true`
- Students = `role == "student" && isApproved == true`
These drive the three tabs in `UserManagementFragment`.

### `assignTeacherToClass(classId, teacher)`
Updates both the `ClassRoom` document (sets `teacherId`, `teacherName`) and the `User` document (adds classId to `classIds`). Both are Firestore writes — if one fails, the other may still succeed, creating inconsistency. A future improvement would use a Firestore transaction.

---

## 🏗️ Class Structure
`@HiltViewModel class AdminViewModel @Inject constructor(authRepository, classRepository) : ViewModel()`

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_stats` | `MutableLiveData<AdminStats>` | `private` | Computed user/class totals | Dashboard stats cards |
| `stats` | `LiveData<AdminStats>` | `val` | Read-only | AdminDashboardFragment |
| `_pendingUsers` | `MutableLiveData<NetworkResult<List<User>>>` | `private` | Unapproved users | Pending tab |
| `pendingUsers` | `LiveData<...>` | `val` | Read-only | UserManagementFragment |
| `_users` | `MutableLiveData<NetworkResult<List<User>>>` | `private` | Teachers or students | Active tabs |
| `users` | `LiveData<...>` | `val` | Read-only | UserManagementFragment |
| `_approveResult` | `MutableLiveData<ApproveResult>` | `private` | Last approve/reject outcome | Snackbar trigger |
| `approveResult` | `LiveData<ApproveResult>` | `val` | Read-only | Fragment Snackbar |
| `institutionId` | `String` | `var` with `private set` | Admin's institution ID | Shared with child VMs |
| `listenerJob` | `Job?` | `private var` | Current listener coroutine | Cancel before restart |

---

## ⚙️ Functions

### `loadAdminStats(uid: String)`
**Purpose:** Start real-time listening if not already active.
Calls `startListening(uid)`. Guard: returns if same uid already listening.

### `forceReload(uid: String)`
**Purpose:** Cancel and restart the listener — used after errors.

### `startListening(uid: String)` *(private)*
**Step by step:**
1. Cancels any existing `listenerJob`.
2. Launches a new coroutine in `viewModelScope`.
3. Gets admin user from Firestore; sets `institutionId`.
4. Starts `authRepository.getInstitutionUsersFlow(institutionId)` — a real-time Flow.
5. On each emission: classifies users, sets `_pendingUsers`, `_users`, computes `_stats`.
6. Loads class count from `classRepository.getClassCountForInstitution(institutionId)`.

### `approveUser(uid: String)` / `rejectUser(uid: String)`
**Purpose:** Approve or reject a pending user.
Calls `authRepository.approveUser(uid)` or `rejectUser(uid)`. Posts `ApproveResult.Success` or `Error`.

### `changeUserRole(targetUid, newRole, reason)`
**Purpose:** Change a user's role and log the change.
1. Calls `authRepository.changeRole(targetUid, newRole)`.
2. Calls `authRepository.logRoleChange(institutionId, targetUid, fromRole, newRole, reason, adminUid)`.

### `loadUsersForTab(role: String)`
**Purpose:** Load approved users of a specific role for Teachers/Students tabs.
Sets `_users` with the filtered list.

### `assignTeacherToClass(classId, teacher)`
**Purpose:** Assign a teacher to a class in both Firestore documents.
1. Updates `classes/{classId}` with `teacherId` and `teacherName`.
2. Adds `classId` to `users/{teacher.uid}/classIds`.

---

## 🔄 Data Flow Diagram
```
AdminDashboardFragment → viewModel.loadAdminStats(uid)
        ↓
startListening(uid) starts coroutine
        ↓
authRepository.getInstitutionUsersFlow(instId) — callbackFlow
        ↓
Real-time Firestore listener on users where institutionId == instId
        ↓
On data change:
  _pendingUsers = [unapproved users]    → UserManagementFragment Pending tab
  _users = [teachers/students]          → UserManagementFragment active tabs
  _stats = AdminStats(counts)           → AdminDashboardFragment stats cards
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `AuthRepository` | User CRUD, approval, role change, real-time user Flow |
| `ClassRepository` | Class count, teacher assignment |

