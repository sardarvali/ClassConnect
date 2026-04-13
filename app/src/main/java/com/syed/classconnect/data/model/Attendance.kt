package com.syed.classconnect.data.model

import com.google.firebase.Timestamp

data class AttendanceRecord(
    val date: String = "",
    val teacherId: String = "",
    val qrToken: String = "",
    val qrExpiresAt: Timestamp = Timestamp.now(),
    val present: List<String> = emptyList(),
    val absent: List<String> = emptyList(),
    val sessionCreatedAt: Timestamp = Timestamp.now()
)

