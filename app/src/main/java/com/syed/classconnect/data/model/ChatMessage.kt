package com.syed.classconnect.data.model

import com.google.firebase.Timestamp

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderPhotoUrl: String = "",
    val text: String = "",
    val attachmentUrl: String = "",
    val attachmentType: String = "", // "image" | "pdf"
    val timestamp: Timestamp = Timestamp.now(),
    val isDeleted: Boolean = false,
    val reactions: Map<String, String> = emptyMap() // userId → emoji
)

