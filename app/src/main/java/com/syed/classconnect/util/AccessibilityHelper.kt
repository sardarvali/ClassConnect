package com.syed.classconnect.util

import android.content.Context
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * Accessibility helper for content descriptions and keyboard navigation
 */
class AccessibilityHelper(private val context: Context) {

    /**
     * Sets content description for accessibility
     */
    fun setContentDescription(view: View, descriptionResId: Int) {
        view.contentDescription = context.getString(descriptionResId)
    }

    /**
     * Sets content description for accessibility with dynamic text
     */
    fun setContentDescription(view: View, description: String) {
        view.contentDescription = description
    }

    /**
     * Announces text to screen readers
     */
    fun announceForAccessibility(view: View, text: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.announceForAccessibility(text)
        }
    }

    /**
     * Sets focus navigation order for complex layouts
     */
    fun setFocusNavigation(
        view: View,
        nextUp: Int = View.NO_ID,
        nextDown: Int = View.NO_ID,
        nextLeft: Int = View.NO_ID,
        nextRight: Int = View.NO_ID
    ) {
        if (nextUp != View.NO_ID) view.nextFocusUpId = nextUp
        if (nextDown != View.NO_ID) view.nextFocusDownId = nextDown
        if (nextLeft != View.NO_ID) view.nextFocusLeftId = nextLeft
        if (nextRight != View.NO_ID) view.nextFocusRightId = nextRight
    }

    /**
     * Makes view focusable and accessible
     */
    fun makeAccessible(view: View, description: String? = null) {
        view.isFocusable = true
        view.isClickable = true
        if (description != null) {
            view.contentDescription = description
        }
    }

    /**
     * Marks a view as important for accessibility
     */
    fun setAccessibilityImportant(view: View) {
        ViewCompat.setAccessibilityLiveRegion(
            view,
            ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE
        )
    }
}

/**
 * Accessibility string constants for content descriptions
 */
object AccessibilityStrings {
    const val CD_SUBMIT_ASSIGNMENT = "Submit assignment"
    const val CD_CREATE_CLASS = "Create new class"
    const val CD_TAKE_ATTENDANCE = "Take attendance"
    const val CD_START_CHAT = "Start chat"
    const val CD_COPY_CLASS_CODE = "Copy class code to clipboard"
    const val CD_SCAN_QR_CODE = "Scan QR code for attendance"
    const val CD_ADD_ASSIGNMENT = "Add new assignment"
    const val CD_DELETE_ITEM = "Delete this item"
    const val CD_EDIT_ITEM = "Edit this item"
    const val CD_GRADE_SUBMISSION = "Grade student submission"
}

