package com.syed.classconnect.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
<<<<<<< HEAD
import com.google.firebase.storage.StorageMetadata
=======
>>>>>>> final
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(private val storage: FirebaseStorage) {

    suspend fun uploadFile(
        institutionId: String, classId: String,
        fileName: String, uri: Uri,
        onProgress: (Int) -> Unit = {}
    ): Result<String> = runCatching {
<<<<<<< HEAD
        val ref = storage.reference.child("institutions/$institutionId/classes/$classId/materials/$fileName")
=======
        val ref =
            storage.reference.child("institutions/$institutionId/classes/$classId/materials/$fileName")
>>>>>>> final
        val uploadTask = ref.putFile(uri)
        uploadTask.addOnProgressListener { snap ->
            val progress = (100.0 * snap.bytesTransferred / snap.totalByteCount).toInt()
            onProgress(progress)
        }.await()
        ref.downloadUrl.await().toString()
    }

    suspend fun uploadProfilePhoto(uid: String, uri: Uri): Result<String> = runCatching {
        val ref = storage.reference.child("profile_photos/$uid.jpg")
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }

<<<<<<< HEAD
    suspend fun uploadSubmission(classId: String, assignmentId: String, studentId: String, uri: Uri): Result<String> = runCatching {
=======
    suspend fun uploadSubmission(
        classId: String,
        assignmentId: String,
        studentId: String,
        uri: Uri
    ): Result<String> = runCatching {
>>>>>>> final
        val ref = storage.reference.child("submissions/$classId/$assignmentId/$studentId")
        ref.putFile(uri).await()
        ref.downloadUrl.await().toString()
    }

    suspend fun deleteFile(url: String): Result<Unit> = runCatching {
        storage.getReferenceFromUrl(url).delete().await()
    }
}

