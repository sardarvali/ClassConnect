package com.syed.classconnect.ui.util

import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.RecyclerView

/**
 * Animation utilities for micro-interactions
 */
object AnimationUtils {

    /**
     * Creates a bounce-in animation for list items
     */
    fun getBounceInAnimation(): Animation {
        val animationSet = AnimationSet(true)

        val scaleAnimation = ScaleAnimation(
            0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 600
        }

        animationSet.addAnimation(scaleAnimation)
        animationSet.duration = 600
        return animationSet
    }

    /**
     * Creates a slide-in animation from left
     */
    fun getSlideInLeftAnimation(): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 400
        }
    }

    /**
     * Creates a slide-in animation from right
     */
    fun getSlideInRightAnimation(): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 400
        }
    }

    /**
     * Creates a fade-in animation
     */
    fun getFadeInAnimation(): Animation {
        return android.view.animation.AlphaAnimation(0f, 1f).apply {
            duration = 300
        }
    }

    /**
     * Creates a rotation animation
     */
    fun getRotateAnimation(): Animation {
        return RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 800
        }
    }

    /**
     * Creates pulse animation for attention
     */
    fun getPulseAnimation(): Animation {
        val animationSet = AnimationSet(true)

        val scaleUp = ScaleAnimation(
            1f, 1.1f, 1f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            repeatMode = Animation.REVERSE
            repeatCount = 3
        }

        animationSet.addAnimation(scaleUp)
        return animationSet
    }

    /**
     * Creates shake animation for error indication
     */
    fun getShakeAnimation(): Animation {
        return TranslateAnimation(
            0f, 10f, 0f, 0f
        ).apply {
            duration = 50
            repeatCount = 10
            repeatMode = Animation.REVERSE
        }
    }
}

/**
 * Page transition animations
 */
object PageTransitionAnimations {

    /**
     * Creates a fade transition between pages
     */
    fun getFadeTransition(): Pair<Int, Int> {
        // Return enter/exit animations for page transitions
        return Pair(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * Creates a slide transition between pages
     */
    fun getSlideTransition(): Pair<Int, Int> {
        return Pair(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }
}

