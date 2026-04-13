package com.syed.classconnect.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * AccessibilityHelper - Centralized accessibility utilities for screen readers and assistive tech
 *
 * Provides consistent methods for:
 * - Setting content descriptions
 * - Managing accessibility announcements
 * - Haptic feedback with accessibility awareness
 * - Focus management
 */
object AccessibilityHelper {

    // ────────────────────────────────────────────────────────────────────────────
    // CONTENT DESCRIPTIONS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Set content description on a view from string resource
     */
    fun setContentDescription(view: View, descriptionResId: Int) {
        view.contentDescription = view.context.getString(descriptionResId)
    }

    /**
     * Set content description on a view with formatted string
     */
    fun setContentDescription(view: View, descriptionResId: Int, vararg formatArgs: Any) {
        view.contentDescription = view.context.getString(descriptionResId, *formatArgs)
    }

    /**
     * Set content description directly from string
     */
    fun setContentDescription(view: View, description: String) {
        view.contentDescription = description
    }

    /**
     * Check if a view has content description
     */
    fun hasContentDescription(view: View): Boolean {
        return !view.contentDescription.isNullOrEmpty()
    }

    // ────────────────────────────────────────────────────────────────────────────
    // ANNOUNCEMENTS FOR SCREEN READERS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Announce a message via accessibility framework
     * @param view The view to announce from
     * @param message The message to announce
     * @param priority Either AccessibilityEvent.TYPE_ANNOUNCEMENT or TYPE_NOTIFICATION
     */
    fun announce(
        view: View,
        message: String,
        priority: Int = AccessibilityEvent.TYPE_ANNOUNCEMENT
    ) {
        view.announceForAccessibility(message)
    }

    /**
     * Announce a message with resource ID
     */
    fun announce(view: View, messageResId: Int) {
        val message = view.context.getString(messageResId)
        announce(view, message)
    }

    /**
     * Announce formatted message
     */
    fun announce(view: View, messageResId: Int, vararg formatArgs: Any) {
        val message = view.context.getString(messageResId, *formatArgs)
        announce(view, message)
    }

    // ────────────────────────────────────────────────────────────────────────────
    // FOCUS MANAGEMENT
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Move accessibility focus to a view
     */
    fun moveFocus(view: View) {
        view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        view.requestFocus()
    }

    /**
     * Clear focus from a view
     */
    fun clearFocus(view: View) {
        ViewCompat.setAccessibilityLiveRegion(
            view,
            ViewCompat.ACCESSIBILITY_LIVE_REGION_NONE
        )
    }

    /**
     * Set live region for dynamic content updates
     * @param view The view containing dynamic content
     * @param liveRegion One of ACCESSIBILITY_LIVE_REGION_POLITE, ASSERTIVE, or NONE
     */
    fun setLiveRegion(view: View, liveRegion: Int) {
        ViewCompat.setAccessibilityLiveRegion(view, liveRegion)
    }

    /**
     * Set live region to POLITE (announces changes when user stops interacting)
     */
    fun setLiveRegionPolite(view: View) {
        setLiveRegion(view, ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE)
    }

    /**
     * Set live region to ASSERTIVE (announces changes immediately)
     */
    fun setLiveRegionAssertive(view: View) {
        setLiveRegion(view, ViewCompat.ACCESSIBILITY_LIVE_REGION_ASSERTIVE)
    }

    // ────────────────────────────────────────────────────────────────────────────
    // MINIMUM TOUCH TARGET SIZE
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Ensure view has minimum 48dp touch target (Material Design spec)
     * If not, expand hitRect via touch delegate
     */
    fun ensureMinimumTouchTarget(
        view: View,
        minSizeDp: Int = 48
    ) {
        view.post {
            val minSizePx = (minSizeDp * view.context.resources.displayMetrics.density).toInt()
            if (view.width < minSizePx || view.height < minSizePx) {
                val extraPx = (minSizePx - maxOf(view.width, view.height)) / 2
                // Increase touch target by adjusting parent's touch delegate
                (view.parent as? View)?.let { parent ->
                    val rect = android.graphics.Rect(
                        view.left - extraPx,
                        view.top - extraPx,
                        view.right + extraPx,
                        view.bottom + extraPx
                    )
                    val delegate = android.view.TouchDelegate(rect, view)
                    parent.touchDelegate = delegate
                }
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // HAPTIC FEEDBACK (with accessibility awareness)
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Provide haptic feedback if enabled in settings
     * Should respect user accessibility preferences
     */
    fun performHapticFeedback(
        view: View,
        feedbackType: Int = View.HAPTIC_FEEDBACK_ENABLED
    ) {
        if (isHapticFeedbackEnabled(view.context)) {
            view.performHapticFeedback(feedbackType)
        }
    }

    /**
     * Strong haptic feedback for important interactions
     */
    fun vibrate(context: Context, durationMs: Long = 200) {
        if (!isHapticFeedbackEnabled(context)) return

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    durationMs,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }

    /**
     * Check if haptic feedback is enabled
     */
    private fun isHapticFeedbackEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE)
                as? AccessibilityManager
        return am?.isEnabled == true
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SCREEN READER DETECTION
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Check if screen reader is currently enabled
     */
    fun isScreenReaderEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE)
                as? AccessibilityManager
        return am?.isEnabled == true && am.isTouchExplorationEnabled
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SEMANTIC ACTIONS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Add a custom accessibility action to a view
     */
    fun addCustomAccessibilityAction(
        view: View,
        label: String,
        action: (view: View) -> Boolean
    ) {
        ViewCompat.addAccessibilityAction(view, label) { view, _ -> action(view) }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // BATCH OPERATIONS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Set content descriptions for multiple views from a map
     * @param descriptionsMap Map of View to description resource ID
     */
    fun setContentDescriptions(descriptionsMap: Map<View, Int>) {
        descriptionsMap.forEach { (view, descResId) ->
            setContentDescription(view, descResId)
        }
    }

    /**
     * Set all EditText views to have proper content descriptions
     */
    fun setupEditTextAccessibility(editText: android.widget.EditText, hint: String) {
        editText.contentDescription = hint
        editText.hint = hint
    }

    /**
     * Make a view invisible to accessibility services
     * Use for decorative elements only
     */
    fun hideFromAccessibility(view: View) {
        ViewCompat.setImportantForAccessibility(
            view,
            ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO
        )
    }

    /**
     * Make a view visible to accessibility services (default)
     */
    fun showToAccessibility(view: View) {
        ViewCompat.setImportantForAccessibility(
            view,
            ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO
        )
    }
}



