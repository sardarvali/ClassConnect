package com.syed.classconnect.data.model

import com.google.firebase.Timestamp
<<<<<<< HEAD
=======
import java.io.Serializable
>>>>>>> final

data class QuizQuestion(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val marks: Int = 1
<<<<<<< HEAD
)
=======
) : Serializable
>>>>>>> final

data class Quiz(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val durationMinutes: Int = 30,
    val totalMarks: Int = 0,
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
<<<<<<< HEAD
    val isPublished: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
)
=======
    var published: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
) : Serializable
>>>>>>> final

data class QuizAttempt(
    val studentId: String = "",
    val startedAt: Timestamp = Timestamp.now(),
    val submittedAt: Timestamp = Timestamp.now(),
    val answers: Map<String, Int> = emptyMap(),
    val score: Int = 0,
    val totalMarks: Int = 0,
    val studentName: String = "",
    val studentEmail: String = "",
<<<<<<< HEAD
    val isDraft: Boolean = false
)

=======
    var draft: Boolean = false
)
>>>>>>> final
