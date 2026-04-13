package com.syed.classconnect.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Announcement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    @get:PropertyName("isPinned")
    @set:PropertyName("isPinned")
    var isPinned: Boolean = false
)

data class Material(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "link", // "pdf" | "link" | "video" | "image"
    val url: String = "",
    val uploadedBy: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
