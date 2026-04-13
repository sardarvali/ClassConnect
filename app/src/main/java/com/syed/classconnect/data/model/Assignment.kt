package com.syed.classconnect.data.model

import com.google.firebase.Timestamp

data class Assignment(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: Timestamp = Timestamp.now(),
    val totalMarks: Int = 100,
    val createdAt: Timestamp = Timestamp.now(),
    val attachmentUrl: String = ""
)

data class Submission(
    val studentId: String = "",
    val studentName: String = "",
    val submittedAt: Timestamp = Timestamp.now(),
    val fileUrl: String = "",
    val textAnswer: String = "",
    val grade: Int = -1,
    val feedback: String = "",
    val status: String = "submitted" // "submitted" | "graded" | "late"
)

