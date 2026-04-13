package com.syed.classconnect.ui.quiz

/**
 * Progress visualization for quiz taking
 * Shows current question, total questions, and question status indicators
 */
data class QuizProgress(
    val currentQuestion: Int,
    val totalQuestions: Int,
    val answeredCount: Int,
    val bookmarkedCount: Int,
    val questionStatuses: List<QuestionStatus> = emptyList()
)

enum class QuestionStatus {
    NOT_ANSWERED,  // Gray dot
    ANSWERED,      // Blue dot
    BOOKMARKED,    // Orange dot
    FLAGGED        // Red dot (for review)
}

/**
 * Quiz review summary before submission
 */
data class QuizReviewSummary(
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val unansweredQuestions: List<Int>, // question indices
    val bookmarkedQuestions: List<Int>,
    val estimatedScore: Float? = null
)

/**
 * Helper to format progress display
 */
object QuizProgressHelper {
    fun getProgressBarText(current: Int, total: Int): String {
        return "Question $current of $total"
    }

    fun getProgressPercentage(current: Int, total: Int): Float {
        return (current.toFloat() / total) * 100
    }

    fun getStatusColor(status: QuestionStatus): Int {
        return when (status) {
            QuestionStatus.NOT_ANSWERED -> android.R.color.darker_gray
            QuestionStatus.ANSWERED -> android.R.color.holo_blue_dark
            QuestionStatus.BOOKMARKED -> android.R.color.holo_orange_dark
            QuestionStatus.FLAGGED -> android.R.color.holo_red_dark
        }
    }
}

