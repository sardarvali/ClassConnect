package com.syed.classconnect.data.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class QuizQuestion(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val marks: Int = 1
) : Serializable

data class Quiz(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val durationMinutes: Int = 30,
    val totalMarks: Int = 0,
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    var published: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
) : Serializable

data class QuizAttempt(
    val studentId: String = "",
    val startedAt: Timestamp = Timestamp.now(),
    val submittedAt: Timestamp = Timestamp.now(),
    val answers: Map<String, Int> = emptyMap(),
    val score: Int = 0,
    val totalMarks: Int = 0,
    val studentName: String = "",
    val studentEmail: String = "",
    var draft: Boolean = false
)
