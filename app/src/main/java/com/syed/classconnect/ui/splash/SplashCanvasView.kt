package com.syed.classconnect.ui.splash

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.TypedValue
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.syed.classconnect.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SplashCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val blobBluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val blobPurplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val blobGreenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val orbitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val starsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.white)
    }
    private val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val logoCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val logoTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD)
        color = ContextCompat.getColor(context, R.color.white)
    }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        color = ContextCompat.getColor(context, R.color.text_primary)
    }
    private val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
        color = ContextCompat.getColor(context, R.color.text_secondary)
    }
    private val titleShimmerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
    }

    private val brandPrimary = ContextCompat.getColor(context, R.color.brand_primary)
    private val brandAccent = ContextCompat.getColor(context, R.color.brand_accent)
    private val tintBlue = ContextCompat.getColor(context, R.color.premium_tint_blue)
    private val tintPurple = ContextCompat.getColor(context, R.color.premium_tint_purple)
    private val tintGreen = ContextCompat.getColor(context, R.color.premium_tint_green)

    private var widthF = 0f
    private var heightF = 0f
    private var centerX = 0f
    private var centerY = 0f

    private var blobBlueRadius = 0f
    private var blobPurpleRadius = 0f
    private var blobGreenRadius = 0f
    private var haloRadius = 0f
    private var orbitRadius = 0f

    private var blobBlueShader: RadialGradient? = null
    private var blobPurpleShader: RadialGradient? = null
    private var blobGreenShader: RadialGradient? = null
    private var haloShader: RadialGradient? = null
    private var vignetteShader: RadialGradient? = null
    private var titleShimmerShader: LinearGradient? = null

    private val blobBlueMatrix = Matrix()
    private val blobPurpleMatrix = Matrix()
    private val blobGreenMatrix = Matrix()
    private val haloMatrix = Matrix()
    private val titleShimmerMatrix = Matrix()

    private val starCount = 22
    private val starX = FloatArray(starCount)
    private val starY = FloatArray(starCount)
    private val starR = FloatArray(starCount)
    private val starPhase = FloatArray(starCount)
    private val starSpeed = FloatArray(starCount)

    private var phase = 0f
    private val animation = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 4200L
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            phase = it.animatedFraction
            invalidate()
        }
    }

    private val cardRect = RectF()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimationLoop()
    }

    override fun onDetachedFromWindow() {
        stopAnimationLoop()
        super.onDetachedFromWindow()
    }

    fun startAnimationLoop() {
        if (!animation.isStarted) {
            animation.start()
        }
    }

    fun stopAnimationLoop() {
        if (animation.isStarted) {
            animation.cancel()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthF = w.toFloat()
        heightF = h.toFloat()
        centerX = widthF * 0.5f
        centerY = heightF * 0.42f

        backgroundPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            heightF,
            ContextCompat.getColor(context, R.color.premium_bg_top),
            ContextCompat.getColor(context, R.color.premium_bg_bottom),
            Shader.TileMode.CLAMP
        )

        blobBlueRadius = widthF * 0.40f
        blobPurpleRadius = widthF * 0.34f
        blobGreenRadius = widthF * 0.28f
        haloRadius = widthF * 0.24f
        orbitRadius = widthF * 0.20f

        blobBlueShader = RadialGradient(0f, 0f, blobBlueRadius, tintBlue, 0x004FC3F7, Shader.TileMode.CLAMP)
        blobPurpleShader = RadialGradient(0f, 0f, blobPurpleRadius, tintPurple, 0x00A855F7, Shader.TileMode.CLAMP)
        blobGreenShader = RadialGradient(0f, 0f, blobGreenRadius, tintGreen, 0x00A5D6AA, Shader.TileMode.CLAMP)
        haloShader = RadialGradient(0f, 0f, haloRadius, brandAccent, 0x0000D4FF, Shader.TileMode.CLAMP)
        vignetteShader = RadialGradient(
            centerX,
            centerY,
            widthF * 0.85f,
            0x00000000,
            0x22091A33,
            Shader.TileMode.CLAMP
        )

        blobBluePaint.shader = blobBlueShader
        blobPurplePaint.shader = blobPurpleShader
        blobGreenPaint.shader = blobGreenShader
        haloPaint.shader = haloShader
        vignettePaint.shader = vignetteShader

        titleShimmerShader = LinearGradient(
            -widthF,
            0f,
            0f,
            0f,
            intArrayOf(0x00FFFFFF, 0x99FFFFFF.toInt(), 0x00FFFFFF),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        titleShimmerPaint.shader = titleShimmerShader

        val random = Random(42)
        for (i in 0 until starCount) {
            starX[i] = widthF * (0.08f + random.nextFloat() * 0.84f)
            starY[i] = heightF * (0.08f + random.nextFloat() * 0.84f)
            starR[i] = dp(0.8f + random.nextFloat() * 1.7f)
            starPhase[i] = random.nextFloat() * 2f * PI.toFloat()
            starSpeed[i] = 0.6f + random.nextFloat() * 1.1f
        }

        orbitPaint.strokeWidth = dp(1.5f)
        orbitPaint.color = 0x55FFFFFF
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val wave = (phase * 2f * PI).toFloat()
        val drift = (phase * 2f * PI * 0.5f).toFloat()

        canvas.drawRect(0f, 0f, widthF, heightF, backgroundPaint)

        val blueX = widthF * 0.22f + sin(wave) * (widthF * 0.06f)
        val blueY = heightF * 0.22f + cos(wave) * (heightF * 0.035f)
        blobBlueMatrix.reset()
        blobBlueMatrix.setTranslate(blueX, blueY)
        blobBlueShader?.setLocalMatrix(blobBlueMatrix)
        canvas.drawCircle(blueX, blueY, blobBlueRadius, blobBluePaint)

        val purpleX = widthF * 0.80f + cos(wave * 0.8f) * (widthF * 0.055f)
        val purpleY = heightF * 0.75f + sin(wave * 0.9f) * (heightF * 0.04f)
        blobPurpleMatrix.reset()
        blobPurpleMatrix.setTranslate(purpleX, purpleY)
        blobPurpleShader?.setLocalMatrix(blobPurpleMatrix)
        canvas.drawCircle(purpleX, purpleY, blobPurpleRadius, blobPurplePaint)

        val greenX = widthF * 0.54f + sin(drift + 1.4f) * (widthF * 0.035f)
        val greenY = heightF * 0.16f + cos(drift + 0.6f) * (heightF * 0.025f)
        blobGreenMatrix.reset()
        blobGreenMatrix.setTranslate(greenX, greenY)
        blobGreenShader?.setLocalMatrix(blobGreenMatrix)
        canvas.drawCircle(greenX, greenY, blobGreenRadius, blobGreenPaint)

        for (i in 0 until starCount) {
            val twinkle = 0.35f + 0.65f * ((sin(wave * starSpeed[i] + starPhase[i]) + 1f) * 0.5f)
            starsPaint.alpha = (twinkle * 185f).toInt()
            canvas.drawCircle(starX[i], starY[i], starR[i], starsPaint)
        }

        val haloScale = 0.92f + 0.14f * ((sin(wave) + 1f) * 0.5f)
        haloMatrix.reset()
        haloMatrix.setScale(haloScale, haloScale)
        haloMatrix.postTranslate(centerX, centerY)
        haloShader?.setLocalMatrix(haloMatrix)
        haloPaint.alpha = (190 + (55 * ((sin(wave) + 1f) * 0.5f))).toInt()
        canvas.drawCircle(centerX, centerY, haloRadius * haloScale, haloPaint)

        val orbitScale = 1f + 0.02f * sin(wave * 0.9f)
        orbitPaint.alpha = (100 + 40 * ((sin(wave * 0.7f) + 1f) * 0.5f)).toInt()
        canvas.drawCircle(centerX, centerY, orbitRadius * orbitScale, orbitPaint)
        val dotAngle = wave * 1.8f
        val dotX = centerX + cos(dotAngle) * orbitRadius * orbitScale
        val dotY = centerY + sin(dotAngle) * orbitRadius * orbitScale
        orbitPaint.style = Paint.Style.FILL
        orbitPaint.alpha = 180
        canvas.drawCircle(dotX, dotY, dp(3f), orbitPaint)
        orbitPaint.style = Paint.Style.STROKE

        val logoRadius = widthF * 0.14f
        val logoScale = 0.98f + 0.03f * ((sin(wave * 0.85f) + 1f) * 0.5f)
        logoCirclePaint.color = brandPrimary
        cardRect.set(
            centerX - (logoRadius * logoScale),
            centerY - (logoRadius * logoScale),
            centerX + (logoRadius * logoScale),
            centerY + (logoRadius * logoScale)
        )
        canvas.drawRoundRect(cardRect, logoRadius, logoRadius, logoCirclePaint)

        logoTextPaint.alpha = (220 + 35 * ((sin(wave) + 1f) * 0.5f)).toInt()
        logoTextPaint.textSize = widthF * 0.09f
        val logoBaseY = centerY - ((logoTextPaint.descent() + logoTextPaint.ascent()) / 2f)
        canvas.drawText("CC", centerX, logoBaseY, logoTextPaint)

        titlePaint.textSize = sp(32f)
        subtitlePaint.textSize = sp(14f)
        titleShimmerPaint.textSize = titlePaint.textSize

        val titleY = centerY + logoRadius + dp(54f)
        canvas.drawText(context.getString(R.string.app_name), centerX, titleY, titlePaint)

        titleShimmerMatrix.reset()
        titleShimmerMatrix.setTranslate((-widthF) + (2f * widthF * phase), titleY)
        titleShimmerShader?.setLocalMatrix(titleShimmerMatrix)
        titleShimmerPaint.alpha = 165
        canvas.drawText(context.getString(R.string.app_name), centerX, titleY, titleShimmerPaint)

        val subtitleY = titleY + dp(28f)
        subtitlePaint.alpha = (170 + 60 * ((sin(wave * 0.6f) + 1f) * 0.5f)).toInt()
        canvas.drawText(context.getString(R.string.app_tagline), centerX, subtitleY, subtitlePaint)

        canvas.drawRect(0f, 0f, widthF, heightF, vignettePaint)
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    private fun sp(value: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics)
}



