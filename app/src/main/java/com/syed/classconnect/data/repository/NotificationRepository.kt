package com.syed.classconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syed.classconnect.data.model.AppNotification
import com.syed.classconnect.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun getNotifications(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_NOTIFICATIONS).document(userId)
            .collection(Constants.COLLECTION_ITEMS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull {
                    it.toObject(AppNotification::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun markAsRead(userId: String, notificationId: String) {
        firestore.collection(Constants.COLLECTION_NOTIFICATIONS).document(userId)
            .collection(Constants.COLLECTION_ITEMS).document(notificationId)
            .update("isRead", true).await()
    }

    suspend fun markAllAsRead(userId: String) {
        val snap = firestore.collection(Constants.COLLECTION_NOTIFICATIONS).document(userId)
            .collection(Constants.COLLECTION_ITEMS)
            .whereEqualTo("isRead", false).get().await()
        val batch = firestore.batch()
        snap.documents.forEach { batch.update(it.reference, "isRead", true) }
        batch.commit().await()
    }

    suspend fun deleteNotification(userId: String, notificationId: String) {
        firestore.collection(Constants.COLLECTION_NOTIFICATIONS).document(userId)
            .collection(Constants.COLLECTION_ITEMS).document(notificationId).delete().await()
    }

    suspend fun addNotification(userId: String, notification: AppNotification) {
        val ref = firestore.collection(Constants.COLLECTION_NOTIFICATIONS).document(userId)
            .collection(Constants.COLLECTION_ITEMS).document()
        ref.set(notification.copy(id = ref.id)).await()
    }

    fun getUnreadCount(userId: String): Flow<Int> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_NOTIFICATIONS).document(userId)
            .collection(Constants.COLLECTION_ITEMS).whereEqualTo("isRead", false)
            .addSnapshotListener { snap, _ -> trySend(snap?.size() ?: 0) }
        awaitClose { sub.remove() }
    }
}

