package com.syed.classconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syed.classconnect.data.model.Assignment
import com.syed.classconnect.data.model.Submission
import com.syed.classconnect.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun getAssignments(classId: String): Flow<List<Assignment>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ASSIGNMENTS)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Assignment::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

<<<<<<< HEAD
    suspend fun createAssignment(classId: String, assignment: Assignment): Result<String> = runCatching {
        val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ASSIGNMENTS).document()
        ref.set(assignment.copy(id = ref.id)).await()

        // Notify all students in the class
        try {
            val classDoc = firestore.collection(Constants.COLLECTION_CLASSES).document(classId).get().await()
            @Suppress("UNCHECKED_CAST")
            val studentIds = classDoc.get("studentIds") as? List<String> ?: emptyList()
            val className = classDoc.getString("name") ?: ""

            val batch = firestore.batch()
            studentIds.forEach { studentId ->
                val notifRef = firestore.collection(Constants.COLLECTION_NOTIFICATIONS)
                    .document(studentId).collection(Constants.COLLECTION_ITEMS).document()
                batch.set(notifRef, hashMapOf(
                    "title" to "New Assignment in $className",
                    "body" to assignment.title,
                    "type" to "assignment",
                    "referenceId" to classId,
                    "isRead" to false,
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                ))
            }
            batch.commit().await()
        } catch (_: Exception) {
            // Don't fail assignment creation if notification fails
        }

        ref.id
    }
=======
    suspend fun createAssignment(classId: String, assignment: Assignment): Result<String> =
        runCatching {
            val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_ASSIGNMENTS).document()
            ref.set(assignment.copy(id = ref.id)).await()

            // Notify all students in the class
            try {
                val classDoc =
                    firestore.collection(Constants.COLLECTION_CLASSES).document(classId).get()
                        .await()

                @Suppress("UNCHECKED_CAST")
                val studentIds = classDoc.get("studentIds") as? List<String> ?: emptyList()
                val className = classDoc.getString("name") ?: ""

                val batch = firestore.batch()
                studentIds.forEach { studentId ->
                    val notifRef = firestore.collection(Constants.COLLECTION_NOTIFICATIONS)
                        .document(studentId).collection(Constants.COLLECTION_ITEMS).document()
                    batch.set(
                        notifRef, hashMapOf(
                            "title" to "New Assignment in $className",
                            "body" to assignment.title,
                            "type" to "assignment",
                            "referenceId" to classId,
                            "isRead" to false,
                            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )
                    )
                }
                batch.commit().await()
            } catch (_: Exception) {
                // Don't fail assignment creation if notification fails
            }

            ref.id
        }
>>>>>>> final

    suspend fun getAssignmentById(classId: String, assignmentId: String): Assignment? {
        val snap = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId).get().await()
        return snap.toObject(Assignment::class.java)?.copy(id = snap.id)
    }

<<<<<<< HEAD
    suspend fun submitAssignment(classId: String, assignmentId: String, submission: Submission): Result<Unit> = runCatching {
=======
    suspend fun submitAssignment(
        classId: String,
        assignmentId: String,
        submission: Submission
    ): Result<Unit> = runCatching {
>>>>>>> final
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
            .collection(Constants.COLLECTION_SUBMISSIONS).document(submission.studentId)
            .set(submission).await()
    }

<<<<<<< HEAD
    suspend fun getSubmission(classId: String, assignmentId: String, studentId: String): Submission? {
=======
    suspend fun getSubmission(
        classId: String,
        assignmentId: String,
        studentId: String
    ): Submission? {
>>>>>>> final
        val snap = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
            .collection(Constants.COLLECTION_SUBMISSIONS).document(studentId).get().await()
        return snap.toObject(Submission::class.java)
    }

<<<<<<< HEAD
    fun getAllSubmissions(classId: String, assignmentId: String): Flow<List<Submission>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
            .collection(Constants.COLLECTION_SUBMISSIONS)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Submission::class.java)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun gradeSubmission(classId: String, assignmentId: String, studentId: String, grade: Int, feedback: String): Result<Unit> = runCatching {
=======
    fun getAllSubmissions(classId: String, assignmentId: String): Flow<List<Submission>> =
        callbackFlow {
            val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
                .collection(Constants.COLLECTION_SUBMISSIONS)
                .addSnapshotListener { snap, err ->
                    if (err != null) {
                        close(err); return@addSnapshotListener
                    }
                    val list = snap?.documents?.mapNotNull {
                        it.toObject(Submission::class.java)
                    } ?: emptyList()
                    trySend(list)
                }
            awaitClose { sub.remove() }
        }

    suspend fun gradeSubmission(
        classId: String,
        assignmentId: String,
        studentId: String,
        grade: Int,
        feedback: String
    ): Result<Unit> = runCatching {
>>>>>>> final
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
            .collection(Constants.COLLECTION_SUBMISSIONS).document(studentId)
            .update(mapOf("grade" to grade, "feedback" to feedback, "status" to "graded")).await()
    }

<<<<<<< HEAD
    suspend fun deleteAssignment(classId: String, assignmentId: String): Result<Unit> = runCatching {
        // Delete all submissions first
        val submissions = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
            .collection(Constants.COLLECTION_SUBMISSIONS).get().await()
        val batch = firestore.batch()
        submissions.documents.forEach { batch.delete(it.reference) }
        batch.delete(
            firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
        )
        batch.commit().await()
    }
=======
    suspend fun updateAssignment(classId: String, assignment: Assignment): Result<Unit> =
        runCatching {
            firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignment.id)
                .set(assignment).await()
        }

    suspend fun deleteAssignment(classId: String, assignmentId: String): Result<Unit> =
        runCatching {
            // Delete all submissions first
            val submissions = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
                .collection(Constants.COLLECTION_SUBMISSIONS).get().await()
            val batch = firestore.batch()
            submissions.documents.forEach { batch.delete(it.reference) }
            batch.delete(
                firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                    .collection(Constants.COLLECTION_ASSIGNMENTS).document(assignmentId)
            )
            batch.commit().await()
        }
>>>>>>> final
}

