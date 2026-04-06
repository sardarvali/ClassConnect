package com.syed.classconnect.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.syed.classconnect.R

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// ── SHOW / HIDE WITH ANIMATION ──────────────────────────────────────

/** Fade + slide-up to show a view */
fun View.showAnimated(duration: Long = 300L) {
    if (isVisible) return
    alpha = 0f
    translationY = 30f
    visibility = View.VISIBLE
    animate().alpha(1f).translationY(0f)
        .setDuration(duration)
        .setInterpolator(FastOutSlowInInterpolator())
        .start()
}

/** Fade + slide-down to hide a view */
fun View.hideAnimated(duration: Long = 250L) {
    if (!isVisible) return
    animate().alpha(0f).translationY(30f)
        .setDuration(duration)
        .setInterpolator(FastOutSlowInInterpolator())
        .withEndAction { visibility = View.GONE; translationY = 0f }
        .start()
}

/** Slide down to show (for banners, bottom bars) */
fun View.slideDown(duration: Long = 350L) {
    if (isVisible) return
    translationY = -height.toFloat()
    alpha = 0f
    visibility = View.VISIBLE
    animate().translationY(0f).alpha(1f)
        .setDuration(duration)
        .setInterpolator(FastOutSlowInInterpolator())
        .start()
}

/** Slide up to hide (for banners) */
fun View.slideUp(duration: Long = 300L) {
    if (!isVisible) return
    animate().translationY(-height.toFloat()).alpha(0f)
        .setDuration(duration)
        .setInterpolator(FastOutSlowInInterpolator())
        .withEndAction { visibility = View.GONE; translationY = 0f }
        .start()
}

// ── BOTTOM NAV HIDE/SHOW ON SCROLL ─────────────────────────────────

/** Animate the floating bottom nav card show/hide on RecyclerView scroll */
fun RecyclerView.setupNavHideOnScroll(navContainer: View) {
    var isNavVisible = true
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            if (dy > 12 && isNavVisible) {
                isNavVisible = false
                navContainer.animate()
                    .translationY(navContainer.height.toFloat() + 40f)
                    .alpha(0f)
                    .setDuration(280)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .start()
            } else if (dy < -12 && !isNavVisible) {
                isNavVisible = true
                navContainer.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(350)
                    .setInterpolator(OvershootInterpolator(0.5f))
                    .start()
            }
        }
    })
}

// ── SNACKBAR ────────────────────────────────────────────────────────

fun Fragment.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    if (this is com.google.android.material.bottomsheet.BottomSheetDialogFragment) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
            .show()
        return
    }
    view?.let {
        val snackbar = Snackbar.make(it, message, duration)
        // Anchor to bottom nav to prevent overlap
        requireActivity().findViewById<View>(R.id.bottom_nav_container)
            ?.let { navView ->
                snackbar.anchorView = navView
            }
        snackbar.show()
    }
}

fun Fragment.showSuccessSnackbar(message: String) {
    val v = view ?: return
    val snackbar = Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
    snackbar.view.background =
        ContextCompat.getDrawable(requireContext(), R.drawable.bg_snackbar_success)
    val tv = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    tv?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
    tv?.compoundDrawablePadding = 8
    // Anchor to bottom nav to prevent overlap
    requireActivity().findViewById<View>(R.id.bottom_nav_container)?.let { navView ->
        snackbar.anchorView = navView
    }
    snackbar.show()
}

fun Fragment.showErrorSnackbar(message: String) {
    val v = view ?: return
    val snackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG)
    snackbar.view.background =
        ContextCompat.getDrawable(requireContext(), R.drawable.bg_snackbar_error)
    // Anchor to bottom nav to prevent overlap
    requireActivity().findViewById<View>(R.id.bottom_nav_container)?.let { navView ->
        snackbar.anchorView = navView
    }
    snackbar.show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

// ── IMAGE LOADING ──────────────────────────────────────────────────

fun ImageView.loadAvatar(url: String?) {
    Glide.with(this)
        .load(url?.takeIf { it.isNotBlank() })
        .apply(RequestOptions.circleCropTransform())
        .placeholder(R.drawable.ic_profile)
        .error(R.drawable.ic_profile)
        .into(this)
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_placeholder)
        .into(this)
}

// ── COLOR ──────────────────────────────────────────────────────────

fun String.toColorInt(): Int = try {
    Color.parseColor(this)
} catch (e: Exception) {
    Color.parseColor("#1E6FFF")
}

fun String.toColorStateList(): ColorStateList = ColorStateList.valueOf(toColorInt())

// ── TOUCH / PRESS EFFECTS ──────────────────────────────────────────

/** Press-scale effect for cards and interactive views */
fun View.addPressEffect() {
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start()

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                    .setInterpolator(OvershootInterpolator(2.5f)).start()
        }
        false
    }
}

/** Deeper press effect for larger cards */
fun View.addDeepPressEffect() {
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                v.animate().scaleX(0.94f).scaleY(0.94f)
                    .setDuration(120).start()

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(250)
                    .setInterpolator(OvershootInterpolator(3f)).start()
        }
        false
    }
}

// ── ENTRANCE ANIMATIONS ────────────────────────────────────────────

/** Staggered fade-in + slide-up entrance for a list of views */
fun List<View>.animateEntrance(startDelay: Long = 0L, stepDelay: Long = 80L) {
    forEachIndexed { index, view ->
        view.alpha = 0f
        view.translationY = 40f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(420)
            .setStartDelay(startDelay + index * stepDelay)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()
    }
}

/** Scale-in entrance from center (great for avatars, icons, empty state images) */
fun View.animateScaleIn(startDelay: Long = 0L) {
    alpha = 0f; scaleX = 0.5f; scaleY = 0.5f
    animate().alpha(1f).scaleX(1f).scaleY(1f)
        .setDuration(500)
        .setStartDelay(startDelay)
        .setInterpolator(OvershootInterpolator(1.5f))
        .start()
}

/** Slide-in from bottom with spring overshoot (for cards, sections) */
fun View.animateSlideUp(startDelay: Long = 0L, distance: Float = 60f) {
    alpha = 0f; translationY = distance
    animate().alpha(1f).translationY(0f)
        .setDuration(500)
        .setStartDelay(startDelay)
        .setInterpolator(OvershootInterpolator(1.2f))
        .start()
}

/** Cross-fade between two views (e.g. loading → content) */
fun crossFade(fadeIn: View, fadeOut: View, duration: Long = 300L) {
    fadeIn.alpha = 0f
    fadeIn.visibility = View.VISIBLE
    fadeIn.animate().alpha(1f).setDuration(duration).start()
    fadeOut.animate().alpha(0f).setDuration(duration)
        .withEndAction { fadeOut.visibility = View.GONE }
        .start()
}

// ── VALUE / STAT ANIMATIONS ────────────────────────────────────────

/** Animate an integer stat value from 0 to [targetValue] */
fun animateStatValue(textView: TextView, targetValue: Int, duration: Long = 1000L) {
    val animator = ValueAnimator.ofInt(0, targetValue)
    animator.duration = duration
    animator.interpolator = DecelerateInterpolator(1.5f)
    animator.addUpdateListener { textView.text = it.animatedValue.toString() }
    animator.start()
}

/** Typewriter-style text reveal for titles and headings */
fun TextView.animateTypewriter(fullText: String, charDelay: Long = 35L) {
    text = ""
    val handler = Handler(Looper.getMainLooper())
    fullText.forEachIndexed { index, _ ->
        handler.postDelayed({ text = fullText.substring(0, index + 1) }, charDelay * index)
    }
}

/** Animate a percentage value (e.g. quiz score) with counting effect */
fun TextView.animatePercentage(from: Int, to: Int, suffix: String = "%", duration: Long = 1200L) {
    ValueAnimator.ofInt(from, to).apply {
        this.duration = duration
        interpolator = DecelerateInterpolator(2f)
        addUpdateListener { text = "${it.animatedValue}$suffix" }
        start()
    }
}

// ── FAB / FLOATING ELEMENT ANIMATIONS ──────────────────────────────

/** FAB pop-in entrance animation */
fun View.animateFabEntrance(startDelay: Long = 300L) {
    alpha = 0f; scaleX = 0f; scaleY = 0f
    animate().alpha(1f).scaleX(1f).scaleY(1f)
        .setDuration(400)
        .setStartDelay(startDelay)
        .setInterpolator(OvershootInterpolator(2f))
        .start()
}

/** Ripple-scale pulse (like notification badge, live indicator) */
fun View.animatePulse(scaleAmount: Float = 1.15f) {
    val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, scaleAmount, 1f)
    val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, scaleAmount, 1f)
    AnimatorSet().apply {
        playTogether(scaleX, scaleY)
        duration = 800
        interpolator = AccelerateDecelerateInterpolator()
        start()
    }
}

/** Continuous breathing animation (for loading states, indicators) */
fun View.animateBreathing() {
    ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("alpha", 1f, 0.5f),
        PropertyValuesHolder.ofFloat("scaleX", 1f, 0.97f),
        PropertyValuesHolder.ofFloat("scaleY", 1f, 0.97f)
    ).apply {
        duration = 1200
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        interpolator = AccelerateDecelerateInterpolator()
        start()
    }
}

// ── MICRO-INTERACTIONS ─────────────────────────────────────────────

/** Bell shake for new notifications */
fun View.shakeAnimation() {
    ObjectAnimator.ofFloat(
        this, "rotation",
        0f, -15f, 15f, -10f, 10f, -5f, 5f, 0f
    ).apply {
        duration = 600
        interpolator = DecelerateInterpolator()
        start()
    }
}

/** Quick horizontal wiggle (for validation errors) */
fun View.wiggle() {
    ObjectAnimator.ofFloat(
        this, "translationX",
        0f, -12f, 12f, -8f, 8f, -4f, 4f, 0f
    ).apply {
        duration = 400
        interpolator = DecelerateInterpolator()
        start()
    }
}

/** Bounce animation (for success states, checkmarks) */
fun View.animateBounce() {
    scaleX = 0f; scaleY = 0f
    animate().scaleX(1f).scaleY(1f)
        .setDuration(500)
        .setInterpolator(OvershootInterpolator(3f))
        .start()
}

/** Celebrate animation — scale + rotation (for achievements, quiz results) */
fun View.animateCelebrate() {
    scaleX = 0f; scaleY = 0f; rotation = -30f
    animate()
        .scaleX(1f).scaleY(1f).rotation(0f)
        .setDuration(600)
        .setInterpolator(OvershootInterpolator(2.5f))
        .start()
}

/** Elevation pulse on card when input inside gains focus */
fun MaterialCardView.pulseElevation(elevated: Boolean) {
    ObjectAnimator.ofFloat(
        this, "cardElevation",
        if (elevated) 0f else 8f,
        if (elevated) 8f else 0f
    ).apply { duration = 200; start() }
}

/** Stroke color glow animation for focused cards */
fun MaterialCardView.animateStrokeGlow(colorFrom: Int, colorTo: Int) {
    ValueAnimator.ofArgb(colorFrom, colorTo).apply {
        duration = 300
        addUpdateListener { strokeColor = it.animatedValue as Int }
        start()
    }
}

// ── LAYOUT / CONTAINER ANIMATIONS ──────────────────────────────────

/** Expand a view's height from 0 to its measured height */
fun View.animateExpand(duration: Long = 300L) {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = measuredHeight
    layoutParams.height = 0
    visibility = View.VISIBLE
    ValueAnimator.ofInt(0, targetHeight).apply {
        this.duration = duration
        interpolator = FastOutSlowInInterpolator()
        addUpdateListener {
            layoutParams.height = it.animatedValue as Int
            requestLayout()
        }
        start()
    }
}

/** Collapse a view's height to 0 and hide */
fun View.animateCollapse(duration: Long = 250L) {
    val initialHeight = measuredHeight
    ValueAnimator.ofInt(initialHeight, 0).apply {
        this.duration = duration
        interpolator = FastOutSlowInInterpolator()
        addUpdateListener {
            layoutParams.height = it.animatedValue as Int
            requestLayout()
            if (it.animatedValue as Int == 0) visibility = View.GONE
        }
        start()
    }
}

// ── BOTTOM SHEET / DIALOG ANIMATIONS ────────────────────────────────

/** Premium bottom sheet dismiss animation: slide down + fade out */
fun BottomSheetDialogFragment.dismissWithPremiumAnimation(duration: Long = 300L) {
    val animSet = AnimatorSet()
    val viewToAnimate = view ?: run { dismiss(); return }
    val slideDown = ObjectAnimator.ofPropertyValuesHolder(
        viewToAnimate,
        PropertyValuesHolder.ofFloat("translationY", 0f, viewToAnimate.height.toFloat())
    ).apply {
        this.duration = duration
        interpolator = android.view.animation.AccelerateInterpolator(1.2f)
    }
    val fadeOut = ObjectAnimator.ofFloat(viewToAnimate, "alpha", 1f, 0f).apply {
        this.duration = duration - 50
    }
    animSet.playTogether(slideDown, fadeOut)
    animSet.addListener(object : android.animation.AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: android.animation.Animator) {
            dismiss()
        }
    })
    animSet.start()
}

