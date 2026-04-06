package com.syed.classconnect.data.model

import com.google.firebase.Timestamp

data class RoleChangeLog(
    val id: String = "",
    val changedByAdminId: String = "",
    val changedByAdminName: String = "",
    val targetUserId: String = "",
    val targetUserName: String = "",
    val fromRole: String = "",
    val toRole: String = "",
    val reason: String = "",
    val changedAt: Timestamp = Timestamp.now()
)

