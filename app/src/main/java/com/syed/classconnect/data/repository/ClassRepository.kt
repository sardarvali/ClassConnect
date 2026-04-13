package com.syed.classconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.data.model.User
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.ValidationUtils
<<<<<<< HEAD
=======
import kotlinx.coroutines.Dispatchers
>>>>>>> final
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
<<<<<<< HEAD
=======
import kotlinx.coroutines.withContext
>>>>>>> final
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getClassesForStudent(userId: String): Flow<List<ClassRoom>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES)
            .whereArrayContains("studentIds", userId)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                val list = snap?.documents?.mapNotNull {
                    it.toObject(ClassRoom::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    fun getClassesForTeacher(teacherId: String): Flow<List<ClassRoom>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES)
            .whereEqualTo("teacherId", teacherId)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                val list = snap?.documents?.mapNotNull {
                    it.toObject(ClassRoom::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    fun getAllClasses(institutionId: String): Flow<List<ClassRoom>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES)
            .whereEqualTo("institutionId", institutionId)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                val list = snap?.documents?.mapNotNull {
                    it.toObject(ClassRoom::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun getClassById(classId: String): ClassRoom? {
<<<<<<< HEAD
        val snap = firestore.collection(Constants.COLLECTION_CLASSES).document(classId).get().await()
        return snap.toObject(ClassRoom::class.java)?.copy(id = snap.id)
    }

=======
        val snap =
            firestore.collection(Constants.COLLECTION_CLASSES).document(classId).get().await()
        return snap.toObject(ClassRoom::class.java)?.copy(id = snap.id)
    }

    suspend fun updateClassSchedule(classId: String, schedule: Map<String, String>): Result<Unit> =
        runCatching {
            firestore.collection(Constants.COLLECTION_CLASSES)
                .document(classId)
                .update("schedule", schedule)
                .await()
        }

    suspend fun updateClassFields(classId: String, fields: Map<String, Any>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                firestore.collection(Constants.COLLECTION_CLASSES).document(classId).update(fields)
                    .await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

>>>>>>> final
    suspend fun createClass(classRoom: ClassRoom): Result<Pair<String, String>> = runCatching {
        val ref = firestore.collection(Constants.COLLECTION_CLASSES).document()
        val code = ValidationUtils.generateCode(6)
        ref.set(classRoom.copy(id = ref.id, classCode = code)).await()
        Pair(ref.id, code)
    }

    suspend fun joinClass(classCode: String, studentId: String): Result<ClassRoom> = runCatching {
        val snap = firestore.collection(Constants.COLLECTION_CLASSES)
            .whereEqualTo("classCode", classCode.uppercase()).get().await()
        if (snap.isEmpty) throw Exception("Class not found")
        val doc = snap.documents[0]
        val classRoom = doc.toObject(ClassRoom::class.java)!!.copy(id = doc.id)
        if (studentId in classRoom.studentIds) throw Exception("Already joined")
        val updated = classRoom.studentIds + studentId
        doc.reference.update("studentIds", updated).await()
        // update user's classIds
        firestore.collection(Constants.COLLECTION_USERS).document(studentId)
<<<<<<< HEAD
            .update("classIds", com.google.firebase.firestore.FieldValue.arrayUnion(classRoom.id)).await()
=======
            .update("classIds", com.google.firebase.firestore.FieldValue.arrayUnion(classRoom.id))
            .await()
>>>>>>> final
        classRoom
    }

    suspend fun getClassPreviewByCode(code: String): ClassRoom? {
<<<<<<< HEAD
        val snap = firestore.collection(Constants.COLLECTION_CLASSES)
            .whereEqualTo("classCode", code.uppercase()).get().await()
        return snap.documents.firstOrNull()?.let {
            it.toObject(ClassRoom::class.java)?.copy(id = it.id)
=======
        return try {
            val snap = firestore.collection(Constants.COLLECTION_CLASSES)
                .whereEqualTo("classCode", code.uppercase()).get().await()
            snap.documents.firstOrNull()?.let {
                it.toObject(ClassRoom::class.java)?.copy(id = it.id)
            }
        } catch (e: Exception) {
            null
>>>>>>> final
        }
    }

    /** Alias used by AdminClassesViewModel */
<<<<<<< HEAD
    fun getAllClassesForInstitution(institutionId: String): Flow<List<ClassRoom>> = getAllClasses(institutionId)

    fun getUsersForInstitution(institutionId: String, role: String? = null): Flow<List<User>> = callbackFlow {
        var query: Query = firestore.collection(Constants.COLLECTION_USERS)
            .whereEqualTo("institutionId", institutionId)
        if (role != null) query = query.whereEqualTo("role", role)
        val sub = query.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull {
                it.toObject(User::class.java)?.copy(uid = it.id)
            } ?: emptyList()
            // Use offer with conflation — never drop, never block
            trySend(list)
        }
        awaitClose { sub.remove() }
    }
}

=======
    fun getAllClassesForInstitution(institutionId: String): Flow<List<ClassRoom>> =
        getAllClasses(institutionId)

    fun getUsersForInstitution(institutionId: String, role: String? = null): Flow<List<User>> =
        callbackFlow {
            var query: Query = firestore.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("institutionId", institutionId)
            if (role != null) query = query.whereEqualTo("role", role)
            val sub = query.addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull {
                    it.toObject(User::class.java)?.copy(uid = it.id)
                } ?: emptyList()
                // Use offer with conflation — never drop, never block
                trySend(list)
            }
            awaitClose { sub.remove() }
        }
}
>>>>>>> final
