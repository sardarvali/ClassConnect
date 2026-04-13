package com.syed.classconnect.ui.assignments

/**
 * Upload progress tracking with pause/resume support
 */
data class UploadProgress(
    val currentBytes: Long = 0,
    val totalBytes: Long = 0,
    val percentProgress: Float = 0f,
    val uploadSpeed: String = "0 MB/s", // Upload speed in MB/s
    val timeRemaining: Long = 0, // in milliseconds
    val isComplete: Boolean = false,
    val error: String? = null
) {
    val displayProgress: Int
        get() = (percentProgress * 100).toInt()

    fun getReadableTimeRemaining(): String {
        val seconds = timeRemaining / 1000
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
            else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
        }
    }
}

/**
 * Represents a file being uploaded
 */
data class UploadingFile(
    val id: String,
    val name: String,
    val size: Long,
    val mimeType: String,
    val progress: UploadProgress = UploadProgress(),
    val isPaused: Boolean = false,
    val uploadedAt: Long = 0
)

/**
 * File preview data before upload
 */
data class FilePreview(
    val fileId: String,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val mimeType: String,
    val thumbnailPath: String? = null, // For images
    val previewUrl: String? = null
)

/**
 * Teacher grading interface enhancements
 */
data class RubricItem(
    val id: String,
    val criteria: String,
    val maxScore: Int,
    val weight: Float = 1f, // 0-1 for weighted rubrics
    val isComplete: Boolean = false
)

/**
 * Teacher feedback with optional audio
 */
data class TeacherFeedback(
    val submissionId: String,
    val textFeedback: String = "",
    val audioFeedbackUrl: String? = null,
    val rubricScores: List<Pair<String, Int>> = emptyList(), // rubricId to score
    val totalScore: Int = 0,
    val feedbackDate: Long = System.currentTimeMillis()
)

/**
 * Split-view grading interface state
 */
data class GradingViewState(
    val submissionContent: String = "",
    val feedbackText: String = "",
    val rubricItems: List<RubricItem> = emptyList(),
    val isRecordingAudio: Boolean = false,
    val previewFiles: List<FilePreview> = emptyList()
)

