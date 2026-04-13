package com.syed.classconnect.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val role: String = "student", // "admin" | "teacher" | "student"
    val institutionId: String = "",

    @field:PropertyName("isApproved")
    var isApproved: Boolean = false,

    @field:PropertyName("isRejected")
    var isRejected: Boolean = false,

    val fcmToken: String = "",
    val classIds: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val bio: String = "",
    // Prompt 2 additions
    val accountType: String = "institution",  // "institution" | "independent"

    @field:PropertyName("emailVerified")
    var emailVerified: Boolean = false,

    val roleChangedAt: Timestamp? = null,
    val roleChangedBy: String = ""
)
