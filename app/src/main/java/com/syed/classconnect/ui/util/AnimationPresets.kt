package com.syed.classconnect.ui.util

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

/**
 * AnimationPresets - Centralized animation utilities following Material Design motion principles
 *
 * Provides consistent animations across the app with standardized durations and easing curves.
 * Uses Material Design timings:
 * - Short: 150ms (focused interactions)
 * - Medium: 300ms (screen transitions, list animations)
 * - Long: 500ms (complex interactions)
 */
object AnimationPresets {
    // ────────────────────────────────────────────────────────────────────────────
    // DURATION CONSTANTS (milliseconds)
    // ────────────────────────────────────────────────────────────────────────────
    const val DURATION_SHORT = 150L
    const val DURATION_MEDIUM = 300L
    const val DURATION_LONG = 500L
    const val DURATION_EXTRA_LONG = 800L

    // ────────────────────────────────────────────────────────────────────────────
    // STAGGER DELAYS for list item animations
    // ────────────────────────────────────────────────────────────────────────────
    const val STAGGER_DELAY_BASE = 50L
    fun getStaggerDelay(index: Int): Long = (index * STAGGER_DELAY_BASE).toLong()

    // ────────────────────────────────────────────────────────────────────────────
    // SLIDE IN - Entry animation (from bottom to position)
    // ────────────────────────────────────────────────────────────────────────────
    fun slideIn(
        view: View,
        duration: Long = DURATION_MEDIUM,
        delayMs: Long = 0L,
        distance: Float = 20f
    ) {
        view.apply {
            alpha = 0f
            translationY = distance
            postDelayed({
                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(duration)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }, delayMs)
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // FADE IN - Simple opacity animation
    // ────────────────────────────────────────────────────────────────────────────
    fun fadeIn(
        view: View,
        duration: Long = DURATION_MEDIUM,
        delayMs: Long = 0L
    ) {
        view.apply {
            alpha = 0f
            postDelayed({
                animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }, delayMs)
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SCALE UP - Entry animation with scale effect
    // ────────────────────────────────────────────────────────────────────────────
    fun scaleUp(
        view: View,
        duration: Long = DURATION_MEDIUM,
        delayMs: Long = 0L,
        startScale: Float = 0.8f
    ) {
        view.apply {
            alpha = 0f
            scaleX = startScale
            scaleY = startScale
            postDelayed({
                animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }, delayMs)
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // ROTATION - Continuous or single rotation animation
    // ────────────────────────────────────────────────────────────────────────────
    fun rotate(
        view: View,
        duration: Long = DURATION_LONG,
        degrees: Float = 360f
    ) {
        view.animate()
            .rotation(view.rotation + degrees)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    // ────────────────────────────────────────────────────────────────────────────
    // BUTTON PRESS - Quick scale feedback for button clicks
    // ────────────────────────────────────────────────────────────────────────────
    fun buttonPressEffect(
        view: View,
        duration: Long = DURATION_SHORT,
        scale: Float = 0.95f
    ) {
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(duration / 2)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration / 2)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
            .start()
    }

    // ────────────────────────────────────────────────────────────────────────────
    // ELEVATION ON CLICK - Raise elevation for pressed state
    // ────────────────────────────────────────────────────────────────────────────
    fun elevationPulse(
        view: View,
        baseElevation: Float,
        pressedElevation: Float,
        duration: Long = DURATION_SHORT
    ) {
        view.elevation = pressedElevation
        view.postDelayed({
            view.animate()
                .translationZ(baseElevation)
                .setDuration(duration)
                .start()
        }, duration)
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SHIMMER PLACEHOLDER - Pulse animation for loading states
    // ────────────────────────────────────────────────────────────────────────────
    fun shimmerPulse(
        view: View,
        duration: Long = 1500L
    ) {
        view.animate()
            .alpha(0.5f)
            .setDuration(duration / 2)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                view.animate()
                    .alpha(1f)
                    .setDuration(duration / 2)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        if (view.visibility == View.VISIBLE) {
                            shimmerPulse(view, duration)
                        }
                    }
                    .start()
            }
            .start()
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SLIDE OUT - Exit animation (to bottom, fade out)
    // ────────────────────────────────────────────────────────────────────────────
    fun slideOut(
        view: View,
        duration: Long = DURATION_MEDIUM,
        distance: Float = 20f,
        onEnd: (() -> Unit)? = null
    ) {
        view.animate()
            .alpha(0f)
            .translationY(distance)
            .setDuration(duration)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction { onEnd?.invoke() }
            .start()
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SHARED ELEMENT TRANSITION SETUP
    // ────────────────────────────────────────────────────────────────────────────
    fun prepareSharedElementTransition(view: View, transitionName: String) {
        // Note: Actual transition names are set in XML layouts
        // This is a helper for future shared transition implementations
    }

    // ────────────────────────────────────────────────────────────────────────────
    // BOUNCE - Elastic bounce effect
    // ────────────────────────────────────────────────────────────────────────────
    fun bounce(
        view: View,
        distance: Float = 10f,
        duration: Long = DURATION_MEDIUM
    ) {
        val halfDuration = duration / 2
        view.animate()
            .translationY(-distance)
            .setDuration(halfDuration)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                view.animate()
                    .translationY(0f)
                    .setDuration(halfDuration)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
            .start()
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SHAKE - Horizontal shake effect for errors
    // ────────────────────────────────────────────────────────────────────────────
    fun shake(
        view: View,
        distance: Float = 5f,
        duration: Long = DURATION_SHORT
    ) {
        val quarterDuration = duration / 4
        view.animate()
            .translationX(-distance)
            .setDuration(quarterDuration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.animate()
                    .translationX(distance)
                    .setDuration(quarterDuration)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction {
                        view.animate()
                            .translationX(0f)
                            .setDuration(quarterDuration)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .start()
                    }
                    .start()
            }
            .start()
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CHECKMARK ANIMATION - Success animation
    // ────────────────────────────────────────────────────────────────────────────
    fun checkmarkSuccess(
        view: View,
        duration: Long = DURATION_MEDIUM
    ) {
        view.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(duration)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PULSE - Continuous subtle pulse animation
    // ────────────────────────────────────────────────────────────────────────────
    fun pulse(
        view: View,
        minAlpha: Float = 0.7f,
        maxAlpha: Float = 1f,
        duration: Long = 1200L,
        continuous: Boolean = true
    ) {
        fun doPulse() {
            view.animate()
                .alpha(minAlpha)
                .setDuration(duration / 2)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    view.animate()
                        .alpha(maxAlpha)
                        .setDuration(duration / 2)
                        .setInterpolator(DecelerateInterpolator())
                        .withEndAction {
                            if (continuous && view.visibility == View.VISIBLE) {
                                doPulse()
                            }
                        }
                        .start()
                }
                .start()
        }
        doPulse()
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CANCEL ALL - Stop all animations on a view
    // ────────────────────────────────────────────────────────────────────────────
    fun cancelAllAnimations(view: View) {
        view.animate().cancel()
    }
}


