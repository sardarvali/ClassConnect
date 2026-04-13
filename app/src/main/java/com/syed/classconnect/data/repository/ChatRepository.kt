package com.syed.classconnect.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syed.classconnect.data.model.ChatMessage
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.FcmHelper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val fcmHelper: FcmHelper
) {

    fun getMessages(classId: String): Flow<List<ChatMessage>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_CHAT)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limitToLast(100)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                val list = snap?.documents?.mapNotNull {
                    it.toObject(ChatMessage::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun sendMessage(classId: String, message: ChatMessage): Result<String> = runCatching {
        val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_CHAT).document()
        ref.set(message.copy(id = ref.id)).await()

        // Create in-app notifications for all class members except sender
        try {
<<<<<<< HEAD
            val classDoc = firestore.collection(Constants.COLLECTION_CLASSES).document(classId).get().await()
=======
            val classDoc =
                firestore.collection(Constants.COLLECTION_CLASSES).document(classId).get().await()

>>>>>>> final
            @Suppress("UNCHECKED_CAST")
            val studentIds = classDoc.get("studentIds") as? List<String> ?: emptyList()
            val teacherId = classDoc.getString("teacherId") ?: ""
            val className = classDoc.getString("name") ?: "Class"

            val recipients = (studentIds + teacherId).filter {
                it.isNotEmpty() && it != message.senderId
            }

            val batch = firestore.batch()
            recipients.forEach { recipientId ->
                val notifRef = firestore
                    .collection(Constants.COLLECTION_NOTIFICATIONS)
                    .document(recipientId)
                    .collection(Constants.COLLECTION_ITEMS)
                    .document()
<<<<<<< HEAD
                batch.set(notifRef, hashMapOf(
                    "title" to "$className Chat",
                    "body" to "${message.senderName}: ${message.text.take(50)}",
                    "type" to "chat",
                    "referenceId" to classId,
                    "isRead" to false,
                    "createdAt" to FieldValue.serverTimestamp()
                ))
=======
                batch.set(
                    notifRef, hashMapOf(
                        "title" to "$className Chat",
                        "body" to "${message.senderName}: ${message.text.take(50)}",
                        "type" to "chat",
                        "referenceId" to classId,
                        "isRead" to false,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
>>>>>>> final
            }
            batch.commit().await()

            // Send FCM push notifications to all recipients
            recipients.forEach { recipientId ->
                try {
                    val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                        .document(recipientId).get().await()
                    val token = userDoc.getString("fcmToken") ?: ""
                    if (token.isNotBlank()) {
                        fcmHelper.sendPush(
                            token = token,
                            title = "$className Chat",
                            body = "${message.senderName}: ${message.text.take(80)}",
                            data = mapOf(
                                "type" to "chat",
                                "classId" to classId,
                                "referenceId" to classId
                            )
                        )
                    }
<<<<<<< HEAD
                } catch (_: Exception) { /* push failure is non-fatal */ }
=======
                } catch (_: Exception) { /* push failure is non-fatal */
                }
>>>>>>> final
            }
        } catch (_: Exception) {
            // Don't fail the message send if notification creation fails
        }

        ref.id
    }

    suspend fun deleteMessage(classId: String, messageId: String): Result<Unit> = runCatching {
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_CHAT).document(messageId)
            .update("isDeleted", true, "text", "This message was deleted").await()
    }

<<<<<<< HEAD
    suspend fun addReaction(classId: String, messageId: String, userId: String, emoji: String): Result<Unit> = runCatching {
=======
    suspend fun addReaction(
        classId: String,
        messageId: String,
        userId: String,
        emoji: String
    ): Result<Unit> = runCatching {
>>>>>>> final
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_CHAT).document(messageId)
            .update("reactions.$userId", emoji).await()
    }

    suspend fun clearAllMessages(classId: String): Result<Unit> = runCatching {
        val snap = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_CHAT).get().await()
        val batch = firestore.batch()
        snap.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }
}

