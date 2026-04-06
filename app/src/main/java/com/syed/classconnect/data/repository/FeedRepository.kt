package com.syed.classconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syed.classconnect.data.model.Announcement
import com.syed.classconnect.data.model.Material
import com.syed.classconnect.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun getAnnouncements(classId: String): Flow<List<Announcement>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ANNOUNCEMENTS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Announcement::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun postAnnouncement(classId: String, announcement: Announcement): Result<String> =
        runCatching {
            val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
                .collection(Constants.COLLECTION_ANNOUNCEMENTS).document()
            ref.set(announcement.copy(id = ref.id)).await()
            ref.id
        }

    suspend fun togglePin(classId: String, announcementId: String, pinned: Boolean) {
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ANNOUNCEMENTS).document(announcementId)
            .update("isPinned", pinned).await()
    }

    suspend fun deleteAnnouncement(classId: String, announcementId: String) {
        firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_ANNOUNCEMENTS).document(announcementId)
            .delete().await()
    }

    fun getMaterials(classId: String): Flow<List<Material>> = callbackFlow {
        val sub = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_MATERIALS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull {
                    it.toObject(Material::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun uploadMaterial(classId: String, material: Material): Result<String> = runCatching {
        val ref = firestore.collection(Constants.COLLECTION_CLASSES).document(classId)
            .collection(Constants.COLLECTION_MATERIALS).document()
        ref.set(material.copy(id = ref.id)).await()
        ref.id
    }
}
