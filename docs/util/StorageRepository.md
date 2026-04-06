# StorageRepository.kt — Firebase Storage operations for file uploads and deletions

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/data/repository/StorageRepository.kt`

---

## 🎯 What This File Does
`StorageRepository` handles all Firebase Storage file operations in the app: uploading class materials (with progress callback), uploading profile photos, uploading assignment submission files, and deleting files by URL. It returns `Result<String>` where the string is the download URL, allowing callers to use `.fold()` or `.getOrNull()` for error handling. It is `@Singleton` — shared by all features that need file storage. Without it, no file uploads (materials, photos, submissions) would work.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.net.Uri` | Android SDK | File URI | Passed in from file picker result |
| `com.google.firebase.storage.FirebaseStorage` | Firebase | Cloud storage SDK | `storage.reference`, `putFile`, `downloadUrl` |
| `com.google.firebase.storage.StorageMetadata` | Firebase | File metadata class | (Imported but not used — available for future metadata) |
| `kotlinx.coroutines.tasks.await` | Coroutines | Suspend Firebase Task | `putFile().await()`, `downloadUrl.await()` |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | `@Inject constructor(...)` |
| `javax.inject.Singleton` | Javax / Hilt | One instance per app | `@Singleton` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `runCatching { }` — Result-wrapped try/catch
`runCatching` is a Kotlin standard library function that executes the lambda and returns:
- `Result.success(value)` if it completes normally
- `Result.failure(exception)` if it throws

```kotlin
suspend fun uploadFile(...): Result<String> = runCatching {
    val ref = storage.reference.child("path/file.pdf")
    ref.putFile(uri).await()
    ref.downloadUrl.await().toString()
    // Last expression in the lambda = the value in Result.success(...)
}
```

### Storage path structure
```
institutions/{institutionId}/classes/{classId}/materials/{fileName}
profile_photos/{uid}.jpg
submissions/{classId}/{assignmentId}/{studentId}
```

### `ref.putFile(uri).await()`
`putFile(uri)` returns `UploadTask`. `.await()` suspends until the upload completes.

### `ref.downloadUrl.await().toString()`
After upload, `downloadUrl` returns a `Task<Uri>` that resolves to the public download URL. `.toString()` converts `Uri` to a string like `https://firebasestorage.googleapis.com/...`.

### `addOnProgressListener { snap -> }`
Progress callback: `snap.bytesTransferred` / `snap.totalByteCount` gives a 0–1 fraction. Multiplied by 100 and cast to Int for a percentage. Note: this is called on the Firebase SDK's thread.

### `storage.getReferenceFromUrl(url).delete().await()`
`getReferenceFromUrl` converts a download URL back to a `StorageReference`. `.delete()` removes the file from Cloud Storage.

---

## 🏗️ Class Structure
`@Singleton class StorageRepository @Inject constructor(private val storage: FirebaseStorage)`

---

## ⚙️ Functions

### `uploadFile(institutionId, classId, fileName, uri, onProgress): Result<String>`
Uploads a material file with progress tracking. Returns download URL.

### `uploadProfilePhoto(uid, uri): Result<String>`
Uploads a circular profile photo. Returns download URL.

### `uploadSubmission(classId, assignmentId, studentId, uri): Result<String>`
Uploads an assignment submission file. Returns download URL.

### `deleteFile(url): Result<Unit>`
Deletes a file from Cloud Storage by its download URL.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(private val storage: FirebaseStorage) {

    suspend fun uploadFile(
        institutionId: String, classId: String,
        fileName: String, uri: Uri,
        onProgress: (Int) -> Unit = {}
        // Default no-op progress callback — callers can pass a real one.
    ): Result<String> = runCatching {
        val ref = storage.reference.child(
            "institutions/$institutionId/classes/$classId/materials/$fileName")
        // Build the storage path: institution → class → materials folder → file name.

        val uploadTask = ref.putFile(uri)
        uploadTask.addOnProgressListener { snap ->
            val progress = (100.0 * snap.bytesTransferred / snap.totalByteCount).toInt()
            onProgress(progress)
            // Reports 0–100 progress to the caller (e.g., for a ProgressBar).
        }.await()
        // .await() suspends until the upload finishes (or throws on failure).

        ref.downloadUrl.await().toString()
        // After upload completes, get the public download URL.
        // This is what gets stored in Firestore as the material's fileUrl.
    }

    suspend fun uploadProfilePhoto(uid: String, uri: Uri): Result<String> = runCatching {
        val ref = storage.reference.child("profile_photos/$uid.jpg")
        // One photo per UID — overwrites on re-upload.
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }

    suspend fun uploadSubmission(
        classId: String, assignmentId: String, studentId: String, uri: Uri
    ): Result<String> = runCatching {
        val ref = storage.reference.child("submissions/$classId/$assignmentId/$studentId")
        // Path uniquely identifies: which class, which assignment, which student.
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }

    suspend fun deleteFile(url: String): Result<Unit> = runCatching {
        storage.getReferenceFromUrl(url).delete().await()
        // getReferenceFromUrl converts download URL back to a StorageReference.
        // .delete() removes the file from Cloud Storage permanently.
    }
}
```

