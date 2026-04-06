package com.syed.classconnect.data.model

import com.google.firebase.Timestamp

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "", // "assignment" | "quiz" | "chat" | "attendance" | "announcement"
    val referenceId: String = "",
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

