package com.syed.classconnect.data.repository

import com.google.firebase.Timestamp
<<<<<<< HEAD
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
=======
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
>>>>>>> final
import com.syed.classconnect.data.model.AttendanceRecord
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.DateUtils.todayIsoString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(private val firestore: FirebaseFirestore) {

<<<<<<< HEAD
    suspend fun startSession(classId: String, teacherId: String): Result<AttendanceRecord> = runCatching {
        val token = UUID.randomUUID().toString()
        val expiresAt = Timestamp(Date(System.currentTimeMillis() + Constants.QR_EXPIRY_MINUTES * 60 * 1000))
        val date = todayIsoString()
        val record = AttendanceRecord(
            date = date, teacherId = teacherId,
            qrToken = token, qrExpiresAt = expiresAt,
            sessionCreatedAt = Timestamp.now()
        )
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ATTENDANCE).document(date).set(record).await()
        record
    }

    suspend fun markPresent(classId: String, date: String, studentId: String): Result<Unit> = runCatching {
        val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ATTENDANCE).document(date)
        val snap = ref.get().await()
        val record = snap.toObject(AttendanceRecord::class.java) ?: throw Exception("Session not found")
        if (Timestamp.now().toDate().after(record.qrExpiresAt.toDate())) throw Exception("QR code expired")
        if (studentId in record.present) throw Exception("Already marked present")
        ref.update("present", FieldValue.arrayUnion(studentId)).await()
    }

    suspend fun endSession(classId: String, date: String, allStudentIds: List<String>): Result<Unit> = runCatching {
        val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ATTENDANCE).document(date)
        val snap = ref.get().await()
        val record = snap.toObject(AttendanceRecord::class.java) ?: throw Exception("Session not found")
=======
    suspend fun startSession(classId: String, teacherId: String): Result<AttendanceRecord> =
        runCatching {
            val token = UUID.randomUUID().toString()
            val expiresAt =
                Timestamp(Date(System.currentTimeMillis() + Constants.QR_EXPIRY_MINUTES * 60 * 1000))
            val date = todayIsoString()
            val record = AttendanceRecord(
                date = date, teacherId = teacherId,
                qrToken = token, qrExpiresAt = expiresAt,
                sessionCreatedAt = Timestamp.now()
            )
            val sessionRef = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_ATTENDANCE).document(date)
            firestore.runTransaction { tx ->
                val existing = tx.get(sessionRef)
                if (existing.exists()) {
                    throw IllegalStateException("Attendance for today is already completed")
                }
                tx.set(sessionRef, record)
            }.await()
            record
        }

    suspend fun markPresent(classId: String, date: String, studentId: String): Result<Unit> =
        runCatching {
            val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_ATTENDANCE).document(date)
            val snap = ref.get().await()
            val record =
                snap.toObject(AttendanceRecord::class.java) ?: throw Exception("Session not found")
            if (Timestamp.now().toDate()
                    .after(record.qrExpiresAt.toDate())
            ) throw Exception("QR code expired")
            if (studentId in record.present) throw Exception("Already marked present")
            ref.update("present", FieldValue.arrayUnion(studentId)).await()
        }

    suspend fun endSession(
        classId: String,
        date: String,
        allStudentIds: List<String>
    ): Result<Unit> = runCatching {
        val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ATTENDANCE).document(date)
        val snap = ref.get().await()
        val record =
            snap.toObject(AttendanceRecord::class.java) ?: throw Exception("Session not found")
>>>>>>> final
        val absent = allStudentIds.filter { it !in record.present }
        ref.update("absent", absent).await()
    }

    fun observeSession(classId: String, date: String): Flow<AttendanceRecord?> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ATTENDANCE).document(date)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                trySend(snap?.toObject(AttendanceRecord::class.java))
            }
        awaitClose { sub.remove() }
    }

    suspend fun getAttendanceHistory(classId: String): List<AttendanceRecord> {
        val snap = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ATTENDANCE).get().await()
        return snap.documents.mapNotNull {
            it.toObject(AttendanceRecord::class.java)?.copy(date = it.id)
        }.sortedByDescending { it.date }
    }

    suspend fun validateQrToken(classId: String, date: String, token: String): Boolean {
        val snap = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ATTENDANCE).document(date).get().await()
        val record = snap.toObject(AttendanceRecord::class.java) ?: return false
        val notExpired = !Timestamp.now().toDate().after(record.qrExpiresAt.toDate())
        return record.qrToken == token && notExpired
    }
}

