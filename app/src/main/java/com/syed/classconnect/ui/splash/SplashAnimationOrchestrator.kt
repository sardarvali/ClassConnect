package com.syed.classconnect.ui.splash

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.doOnLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

class SplashAnimationOrchestrator(
    private val scope: CoroutineScope,
    private val rootView: View,
    private val neuralBackgroundView: NeuralBackgroundView,
    private val particleBurstView: ParticleBurstView,
    private val logoView: TextView,
    private val taglineView: TextView,
    private val loaderTrack: View,
    private val loaderFill: View,
    private val roleChipsLayout: LinearLayout,
    private val onComplete: () -> Unit
) {

    private val animators = mutableListOf<ValueAnimator>()
    private var sequenceJob: Job? = null

    fun start() {
        setupStaticStyles()
        resetInitialState()

        sequenceJob = scope.launch {
            neuralBackgroundView.startConstellationFadeIn()
            delay(600)
            animateLogoElasticIn()
            delay(500)

            val center = getLogoCenter()
            particleBurstView.fireBurst(center.first, center.second)
            delay(300)

            animateTaglineIn()
            delay(400)

            animateLoaderFill()
            neuralBackgroundView.startConnectAnimation()
            delay(400)

            particleBurstView.fireSubjectPills(center.first, center.second)
            delay(800)

            animateChipsIn()
            delay(1200)

            rootView.animate()
                .alpha(0f)
                .setDuration(800)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction { onComplete() }
                .start()
        }
    }

    fun cancel() {
        sequenceJob?.cancel()
        sequenceJob = null
        animators.forEach { it.cancel() }
        animators.clear()
    }

    private fun setupStaticStyles() {
        logoView.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(20f)
            setColor(Color.parseColor("#6650A4"))
            setStroke(dp(1f).toInt(), 0x26FFFFFF)
        }

        loaderTrack.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(1f)
            setColor(0x14FFFFFF)
        }

        loaderFill.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(1f)
            setColor(Color.parseColor("#7F77DD"))
        }

        styleChip(roleChipsLayout.findViewById(com.syed.classconnect.R.id.chip_admin), "#A8C7FA", 0x59A8C7FA)
        styleChip(roleChipsLayout.findViewById(com.syed.classconnect.R.id.chip_teacher), "#9FE1CB", 0x599FE1CB)
        styleChip(roleChipsLayout.findViewById(com.syed.classconnect.R.id.chip_student), "#F2B8EB", 0x59F2B8EB)
    }

    private fun resetInitialState() {
        rootView.alpha = 1f
        logoView.alpha = 0f
        logoView.scaleX = 0f
        logoView.scaleY = 0f
        logoView.rotation = -20f

        taglineView.alpha = 0f
        taglineView.letterSpacing = 0.5f

        loaderFill.layoutParams = loaderFill.layoutParams.apply { width = 0 }
        loaderFill.requestLayout()

        for (i in 0 until roleChipsLayout.childCount) {
            roleChipsLayout.getChildAt(i).apply {
                alpha = 0f
                translationY = dp(8f)
            }
        }
    }

    private fun animateLogoElasticIn() {
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500L
            addUpdateListener { va ->
                val t = va.animatedValue as Float
                val e = easeOutElastic(t)
                logoView.scaleX = e
                logoView.scaleY = e
                logoView.rotation = lerp(-20f, 0f, e)
                logoView.alpha = if (t <= 0.4f) t / 0.4f else 1f
            }
            start()
        }
        animators += anim
    }

    private fun animateTaglineIn() {
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500L
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { va ->
                val t = va.animatedValue as Float
                val e = easeOut3(t)
                taglineView.alpha = e
                taglineView.letterSpacing = lerp(0.5f, 0.12f, e)
            }
            start()
        }
        animators += anim
    }

    private fun animateLoaderFill() {
        loaderTrack.doOnLayout {
            val targetWidth = loaderTrack.width
            val anim = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1400L
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { va ->
                    val progress = va.animatedValue as Float
                    loaderFill.layoutParams = loaderFill.layoutParams.apply {
                        width = (targetWidth * progress).toInt()
                    }
                    loaderFill.requestLayout()
                }
                start()
            }
            animators += anim
        }
    }

    private fun animateChipsIn() {
        for (i in 0 until roleChipsLayout.childCount) {
            val chip = roleChipsLayout.getChildAt(i)
            chip.animate().cancel()
            chip.postDelayed({
                val anim = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 300L
                    addUpdateListener { va ->
                        val t = va.animatedValue as Float
                        chip.translationY = lerp(dp(8f), 0f, easeOutBack(t))
                        chip.alpha = easeOut3(t)
                    }
                    start()
                }
                animators += anim
            }, i * 120L)
        }
    }

    private fun getLogoCenter(): Pair<Float, Float> {
        val cx = logoView.x + logoView.width / 2f
        val cy = logoView.y + logoView.height / 2f
        neuralBackgroundView.setCenter(cx, cy)
        return cx to cy
    }

    private fun styleChip(view: TextView, textColorHex: String, borderColor: Int) {
        view.setTextColor(Color.parseColor(textColorHex))
        view.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(20f)
            setColor(Color.TRANSPARENT)
            setStroke(dp(1f).toInt(), borderColor)
        }
    }

    private fun easeOut3(t: Float): Float {
        val x = t.coerceIn(0f, 1f)
        return 1f - (1f - x).pow(3)
    }

    private fun easeOutBack(t: Float): Float {
        val x = t.coerceIn(0f, 1f) - 1f
        val c = 2.70158f
        return 1f + c * x.pow(3) + (c - 1f) * x.pow(2)
    }

    private fun easeOutElastic(t: Float): Float {
        if (t == 0f || t == 1f) return t
        val x = t.coerceIn(0f, 1f)
        return (2f.pow(-10f * x) * sin((x * 10f - 0.75f) * (2f * PI / 3f)) + 1f).toFloat()
    }

    private fun lerp(start: Float, end: Float, t: Float): Float = start + (end - start) * t

    private fun dp(value: Float): Float = value * rootView.resources.displayMetrics.density
}


