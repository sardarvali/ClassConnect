package com.syed.classconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syed.classconnect.data.model.Quiz
import com.syed.classconnect.data.model.QuizAttempt
import com.syed.classconnect.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun getQuizzes(classId: String): Flow<List<Quiz>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Quiz::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    /** Students only see published quizzes */
    fun getQuizzesForStudent(classId: String): Flow<List<Quiz>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES)
<<<<<<< HEAD
            .whereEqualTo("isPublished", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
=======
            .whereEqualTo("published", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Quiz::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun createQuiz(classId: String, quiz: Quiz): Result<String> = runCatching {
        val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES).document()
        ref.set(quiz.copy(id = ref.id)).await()
        ref.id
    }

    suspend fun getQuizById(classId: String, quizId: String): Quiz? {
        val snap = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES).document(quizId).get().await()
        return snap.toObject(Quiz::class.java)?.copy(id = snap.id)
    }

<<<<<<< HEAD
    suspend fun submitAttempt(classId: String, quizId: String, attempt: QuizAttempt): Result<Unit> = runCatching {
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES).document(quizId)
            .collection(Constants.COLLECTION_ATTEMPTS).document(attempt.studentId)
            .set(attempt).await()
    }
=======
    suspend fun submitAttempt(classId: String, quizId: String, attempt: QuizAttempt): Result<Unit> =
        runCatching {
            firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_QUIZZES).document(quizId)
                .collection(Constants.COLLECTION_ATTEMPTS).document(attempt.studentId)
                .set(attempt).await()
        }
>>>>>>> final

    suspend fun getAttempt(classId: String, quizId: String, studentId: String): QuizAttempt? {
        val snap = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES).document(quizId)
            .collection(Constants.COLLECTION_ATTEMPTS).document(studentId).get().await()
        return snap.toObject(QuizAttempt::class.java)
    }

    fun getAllAttempts(classId: String, quizId: String): Flow<List<QuizAttempt>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES).document(quizId)
            .collection(Constants.COLLECTION_ATTEMPTS)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(QuizAttempt::class.java) } ?: emptyList()
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { it.toObject(QuizAttempt::class.java) }
                    ?: emptyList()
>>>>>>> final
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun publishQuiz(classId: String, quizId: String, published: Boolean) {
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES).document(quizId)
<<<<<<< HEAD
            .update("isPublished", published).await()
=======
            .update("published", published).await()
    }

    suspend fun updateQuiz(classId: String, quiz: Quiz): Result<Unit> = runCatching {
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES).document(quiz.id)
            .set(quiz).await()
>>>>>>> final
    }

    suspend fun deleteQuiz(classId: String, quizId: String): Result<Unit> = runCatching {
        // Delete all attempts first
        val attempts = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_QUIZZES).document(quizId)
            .collection(Constants.COLLECTION_ATTEMPTS).get().await()
        val batch = firestore.batch()
        attempts.documents.forEach { batch.delete(it.reference) }
        batch.delete(
            firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_QUIZZES).document(quizId)
        )
        batch.commit().await()
    }
}
<<<<<<< HEAD

=======
>>>>>>> final
