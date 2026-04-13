# FeedRepository.kt — Firestore repository for real-time announcements and class materials

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/data/repository/FeedRepository.kt`

---

## 🎯 What This File Does
`FeedRepository` is the data access layer for the class feed — it handles reading and writing `Announcement` and `Material` documents in Firestore subcollections under `classes/{classId}/announcements` and `classes/{classId}/materials`. It exposes real-time `Flow` streams via `callbackFlow` so the UI automatically updates when data changes. It is `@Singleton` — one instance shared across all ViewModels that need feed data. Without it, announcements would not load, post, or update in real time.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `com.google.firebase.firestore.FirebaseFirestore` | Firebase | Firestore database | All read/write operations |
| `com.google.firebase.firestore.Query` | Firebase | Firestore query builder | `Query.Direction.DESCENDING` |
| `com.syed.classconnect.data.model.Announcement` | Project | Announcement data class | Document mapping |
| `com.syed.classconnect.data.model.Material` | Project | Material data class | Document mapping |
| `com.syed.classconnect.util.Constants` | Project | Firestore collection name constants | `COLLECTION_CLASSES`, `COLLECTION_ANNOUNCEMENTS`, etc. |
| `kotlinx.coroutines.channels.awaitClose` | Coroutines | Close callbackFlow | Removes Firestore listeners on Flow cancellation |
| `kotlinx.coroutines.flow.Flow` | Coroutines | Reactive stream | Return type of real-time queries |
| `kotlinx.coroutines.flow.callbackFlow` | Coroutines | Convert callbacks to Flow | Wrap Firestore `addSnapshotListener` |
| `kotlinx.coroutines.tasks.await` | Coroutines | Suspend Firebase Task | `set().await()`, `update().await()` |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | `@Inject constructor(...)` |
| `javax.inject.Singleton` | Javax / Hilt | One instance per app | `@Singleton` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `callbackFlow { }` — Converting Firestore Listeners to Flow
Firestore's `addSnapshotListener` is callback-based. `callbackFlow` wraps it in a Flow:
```kotlin
fun getAnnouncements(classId: String): Flow<List<Announcement>> = callbackFlow {
    val sub = firestore.collection(...).addSnapshotListener { snap, err ->
        if (err != null) { close(err); return@addSnapshotListener }
        trySend(snap?.documents?.mapNotNull { ... } ?: emptyList())
    }
    awaitClose { sub.remove() }  // CRITICAL: removes listener when Flow is cancelled
}
```
- `trySend(value)`: emits a value to the Flow without throwing if the Flow is cancelled.
- `close(err)`: terminates the Flow with an error.
- `awaitClose { sub.remove() }`: runs when the collector cancels. Removes the Firestore listener — prevents memory leaks and unnecessary Firestore reads.

### `runCatching { }` + `Result<T>`
Wraps a suspend block in a try/catch and returns `Result<T>`:
- `Result.success(value)` if no exception.
- `Result.failure(exception)` if an exception is thrown.
Callers use `.fold(onSuccess, onFailure)` or `getOrNull()`.

### `.toObject(Announcement::class.java)?.copy(id = it.id)`
`toObject()` deserializes the Firestore document to an `Announcement`. The `copy(id = it.id)` pattern sets the document's auto-generated ID on the object (since the `id` field isn't stored in the document body by default).

### `ref.set(announcement.copy(id = ref.id)).await()`
Before writing, the document ID is set on the object using `copy()`. This ensures the `id` field inside the document matches the document's Firestore key — important for future reads.

---

## 🏗️ Class Structure
`@Singleton class FeedRepository @Inject constructor(private val firestore: FirebaseFirestore)`

---

## ⚙️ Functions

### `getAnnouncements(classId: String): Flow<List<Announcement>>`
**Purpose:** Real-time stream of announcements for a class, sorted newest-first.
**Returns:** `Flow` that emits on every Firestore change.
**Step by step:**
1. Creates a `callbackFlow`.
2. Adds a snapshot listener on `classes/{classId}/announcements` ordered by `createdAt` descending.
3. On error: closes Flow with the error.
4. On data: maps each document to `Announcement` with `copy(id = it.id)`, emits via `trySend`.
5. `awaitClose` removes the listener when the Flow is cancelled.

### `postAnnouncement(classId, announcement): Result<String>`
**Purpose:** Write a new announcement to Firestore.
**Returns:** `Result<String>` where the string is the new document ID.
**Step by step:**
1. Creates a new document reference (auto-ID) in `classes/{classId}/announcements`.
2. Sets the document with `announcement.copy(id = ref.id)`.
3. Returns the document ID.

### `togglePin(classId, announcementId, pinned)`
**Purpose:** Update the `isPinned` boolean of an announcement.
**Step by step:** Calls `update("isPinned", pinned)` on the document.

### `getMaterials(classId: String): Flow<List<Material>>`
**Purpose:** Real-time stream of materials for a class, sorted newest-first.
Same pattern as `getAnnouncements` but for `classes/{classId}/materials`.

### `uploadMaterial(classId, material): Result<String>`
**Purpose:** Write a new material metadata document to Firestore.
Same pattern as `postAnnouncement` but for materials.
> Note: Actual file upload (to Firebase Storage) is handled by `StorageRepository`. This function only writes the metadata.

---

## 🔄 Data Flow Diagram
```
FeedViewModel.loadFeed(classId)
        ↓
FeedRepository.getAnnouncements(classId)
        ↓
callbackFlow → Firestore addSnapshotListener
        ↓
snapshot arrives → trySend(List<Announcement>)
        ↓
FeedViewModel.collect → sort → _feedItems.value = sorted
        ↓
FeedFragment.observe → adapter.submitList(items)
        ↓ (when Fragment is destroyed)
Flow cancelled → awaitClose → sub.remove() → Firestore listener removed
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `FirebaseFirestore` | Database read/write |
| `Constants` | Collection path constants |
| `Announcement`, `Material` | Data models |

---

## ⚠️ Important Notes & Gotchas
- `awaitClose { sub.remove() }` is CRITICAL. Without it, the Firestore listener continues firing even after the Fragment is destroyed, causing memory leaks and unnecessary data reads.
- `trySend` (not `send`) is used because the Flow might be closed (cancelled) between when `addSnapshotListener` fires and when we try to emit — `trySend` handles this safely.
- `getMaterials` and `uploadMaterial` are present for the planned materials feature but may not yet be connected to a ViewModel or Fragment UI.

