package com.syed.classconnect.data.model

import com.google.firebase.Timestamp

data class ClassRoom(
    val id: String = "",
    val name: String = "",
    val subject: String = "",
    val description: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val institutionId: String = "",
    val studentIds: List<String> = emptyList(),
    val classCode: String = "",
    val schedule: Map<String, String> = emptyMap(),
    val color: String = "#1565C0",
    val createdAt: Timestamp = Timestamp.now()
)

