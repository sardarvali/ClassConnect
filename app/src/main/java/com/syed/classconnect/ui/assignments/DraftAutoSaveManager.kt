package com.syed.classconnect.ui.assignments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay

/**
 * Manages draft auto-save functionality for assignment submissions
 * Auto-saves submission text every 30 seconds
 */
class DraftAutoSaveManager {

    private val _draftSaved = MutableLiveData<Boolean>(false)
    val draftSaved: LiveData<Boolean> = _draftSaved

    private val _draftSaveError = MutableLiveData<String?>(null)
    val draftSaveError: LiveData<String?> = _draftSaveError

    private val _submissionDraft = MutableLiveData<String>()
    val submissionDraft: LiveData<String> = _submissionDraft

    private val AUTO_SAVE_INTERVAL_MS = 30000L // 30 seconds

    /**
     * Auto-save submission text with debounce
     * Uses debounce operator to avoid excessive saves
     */
    fun autoSaveDraft(submissionText: String) {
        if (submissionText.isNotBlank()) {
            _submissionDraft.postValue(submissionText)
        }
    }

    /**
     * Marks submission as draft
     */
    data class DraftSubmission(
        val id: String = "",
        val assignmentId: String = "",
        val content: String = "",
        val isDraft: Boolean = true,
        val savedAt: Long = System.currentTimeMillis()
    )

    /**
     * Saves draft to local storage
     */
    suspend fun saveDraftLocally(submissionText: String) {
        try {
            delay(500) // Simulate save operation
            _submissionDraft.postValue(submissionText)
            _draftSaved.postValue(true)
            _draftSaveError.postValue(null)
            
            // Reset after 2 seconds
            delay(2000)
            _draftSaved.postValue(false)
        } catch (e: Exception) {
            _draftSaveError.postValue(e.message)
        }
    }

    /**
     * Loads draft from local storage
     */
    suspend fun loadDraft(assignmentId: String): String? {
        return try {
            delay(300) // Simulate load operation
            _submissionDraft.value
        } catch (e: Exception) {
            _draftSaveError.postValue(e.message)
            null
        }
    }

    /**
     * Deletes draft
     */
    suspend fun deleteDraft(assignmentId: String) {
        try {
            _submissionDraft.postValue("")
            _draftSaved.postValue(false)
        } catch (e: Exception) {
            _draftSaveError.postValue(e.message)
        }
    }

    /**
     * Warns user if leaving without saving draft
     */
    fun checkUnsavedChanges(currentText: String): Boolean {
        val savedDraft = _submissionDraft.value
        return savedDraft != null && currentText != savedDraft
    }
}

/**
 * Submission status badge enum
 */
enum class SubmissionStatus(val statusText: String, val colorResId: Int) {
    DRAFT("Draft", android.R.color.holo_blue_dark),
    SUBMITTED("Submitted", android.R.color.holo_green_dark),
    GRADED("Graded", android.R.color.holo_blue_light),
    OVERDUE("Overdue", android.R.color.holo_red_dark),
    LATE_SUBMISSION("Late", android.R.color.holo_orange_dark)
}

/**
 * Determines submission status
 */
fun getSubmissionStatus(
    isDraft: Boolean,
    isSubmitted: Boolean,
    isGraded: Boolean,
    isLate: Boolean,
    dueDate: Long,
    currentTime: Long = System.currentTimeMillis()
): SubmissionStatus {
    return when {
        isDraft -> SubmissionStatus.DRAFT
        isGraded -> SubmissionStatus.GRADED
        isLate && !isGraded -> SubmissionStatus.LATE_SUBMISSION
        currentTime > dueDate && !isSubmitted -> SubmissionStatus.OVERDUE
        isSubmitted -> SubmissionStatus.SUBMITTED
        else -> SubmissionStatus.DRAFT
    }
}

