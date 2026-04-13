package com.syed.classconnect.ui.splash

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class NeuralBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private data class Node(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        val radius: Float,
        val targetAlpha: Float,
        var currentAlpha: Float,
        var pulsePhase: Float,
        val color: Int,
        val activationMs: Float
    )

    private val random = Random(2026)
    private val nodeCount = 28
    private val nodeColors = intArrayOf(
        Color.parseColor("#7F77DD"),
        Color.parseColor("#A8C7FA"),
        Color.parseColor("#F2B8EB"),
        Color.parseColor("#9FE1CB")
    )

    private val nodes = ArrayList<Node>(nodeCount)

    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 0.5f
    }
    private val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val connectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1f)
        strokeCap = Paint.Cap.ROUND
        color = Color.parseColor("#B09DF8")
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#B09DF8")
    }
    private val scanlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private var scanlineShader: LinearGradient? = null
    private var centerGlowShader: RadialGradient? = null
    private val centerGlowMatrix = Matrix()

    private val frameAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 16L
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { invalidate() }
    }

    private var centerX = 0f
    private var centerY = 0f

    private var constellationStartMs = 0L
    private var connectStartMs = -1L

    fun setCenter(cx: Float, cy: Float) {
        centerX = cx
        centerY = cy
    }

    fun startConstellationFadeIn() {
        constellationStartMs = SystemClock.elapsedRealtime()
        if (!frameAnimator.isStarted) frameAnimator.start()
    }

    fun startConnectAnimation() {
        connectStartMs = SystemClock.elapsedRealtime()
    }

    override fun onDetachedFromWindow() {
        if (frameAnimator.isStarted) frameAnimator.cancel()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (centerX == 0f && centerY == 0f) {
            centerX = w * 0.5f
            centerY = h * 0.42f
        }
        buildNodes(w.toFloat(), h.toFloat())
        buildScanlineShader()
        buildCenterGlowShader()
    }

    private fun buildNodes(w: Float, h: Float) {
        nodes.clear()
        val velocity = dp(0.3f)
        for (i in 0 until nodeCount) {
            val radius = dp(0.6f + random.nextFloat() * 1.8f)
            val target = 0.3f + random.nextFloat() * 0.6f
            nodes += Node(
                x = random.nextFloat() * w,
                y = random.nextFloat() * h,
                vx = (-velocity + random.nextFloat() * velocity * 2f),
                vy = (-velocity + random.nextFloat() * velocity * 2f),
                radius = radius,
                targetAlpha = target,
                currentAlpha = 0f,
                pulsePhase = random.nextFloat() * 2f * PI.toFloat(),
                color = nodeColors[random.nextInt(nodeColors.size)],
                activationMs = (i / nodeCount.toFloat()) * 400f
            )
        }
    }

    private fun buildScanlineShader() {
        val bandHeight = dp(60f)
        scanlineShader = LinearGradient(
            0f,
            0f,
            0f,
            bandHeight,
            intArrayOf(0x007F77DD, 0x147F77DD, 0x007F77DD),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        scanlinePaint.shader = scanlineShader
    }

    private fun buildCenterGlowShader() {
        centerGlowShader = RadialGradient(
            0f,
            0f,
            dp(120f),
            intArrayOf(0x1F6650A4, 0x006650A4),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        glowPaint.shader = centerGlowShader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.clipRect(0f, 0f, width.toFloat(), height.toFloat())
        if (nodes.isEmpty() || constellationStartMs == 0L) return

        val now = SystemClock.elapsedRealtime()
        val elapsed = (now - constellationStartMs).coerceAtLeast(0L).toFloat()
        val edgeDistance = dp(80f)

        for (node in nodes) {
            node.x += node.vx
            node.y += node.vy

            if (node.x < 0f || node.x > width) {
                node.vx *= -1f
                node.x = node.x.coerceIn(0f, width.toFloat())
            }
            if (node.y < 0f || node.y > height) {
                node.vy *= -1f
                node.y = node.y.coerceIn(0f, height.toFloat())
            }

            node.pulsePhase += 0.025f
            if (elapsed >= node.activationMs) {
                node.currentAlpha = min(node.targetAlpha, node.currentAlpha + 0.015f)
            }
        }

        for (i in 0 until nodes.size) {
            val a = nodes[i]
            for (j in i + 1 until nodes.size) {
                val b = nodes[j]
                val d = hypot(a.x - b.x, a.y - b.y)
                if (d <= edgeDistance) {
                    val alpha = ((1f - (d / edgeDistance)) * 0.35f * min(a.currentAlpha, b.currentAlpha))
                    edgePaint.color = Color.argb((alpha * 255f).toInt(), 208, 188, 255)
                    canvas.drawLine(a.x, a.y, b.x, b.y, edgePaint)
                }
            }
        }

        var maxAlpha = 0f
        for (node in nodes) {
            maxAlpha = maxOf(maxAlpha, node.currentAlpha)
            val pulse = 0.7f + 0.3f * sin(node.pulsePhase)
            val alpha = (node.currentAlpha * pulse).coerceIn(0f, 1f)
            nodePaint.color = node.color
            nodePaint.alpha = (alpha * 255f).toInt()
            canvas.drawCircle(node.x, node.y, node.radius, nodePaint)
        }

        if (maxAlpha > 0f) {
            centerGlowMatrix.reset()
            centerGlowMatrix.setTranslate(centerX, centerY)
            centerGlowShader?.setLocalMatrix(centerGlowMatrix)
            canvas.drawCircle(centerX, centerY, dp(120f), glowPaint)
        }

        val sweepProgress = (elapsed / 500f).coerceIn(0f, 1f)
        val scanTop = (height * sweepProgress) - dp(30f)
        canvas.save()
        canvas.translate(0f, scanTop)
        canvas.drawRect(0f, 0f, width.toFloat(), dp(60f), scanlinePaint)
        canvas.restore()

        if (connectStartMs > 0L) {
            drawConnectPhase(canvas, now)
        }
    }

    private fun drawConnectPhase(canvas: Canvas, nowMs: Long) {
        val progress = ((nowMs - connectStartMs) / 1200f).coerceIn(0f, 1f)
        if (progress <= 0f) return
        val eased = easeOut3(progress)
        val reach = dp(150f)
        val t = nowMs / 1000f

        for ((index, node) in nodes.withIndex()) {
            val dx = centerX - node.x
            val dy = centerY - node.y
            val dist = hypot(dx, dy)
            if (dist > reach || dist == 0f) continue

            val nx = dx / dist
            val ny = dy / dist
            val lineLen = dist * 0.35f * eased
            val endX = node.x + nx * lineLen
            val endY = node.y + ny * lineLen

            val pulse = ((sin(t * 3f + index * 0.7f) + 1f) * 0.5f)
            val lineAlpha = (eased * 0.35f * pulse * node.currentAlpha).coerceIn(0f, 1f)
            connectPaint.alpha = (lineAlpha * 255f).toInt()
            canvas.drawLine(node.x, node.y, endX, endY, connectPaint)

            val dotT = ((sin(t * 2f + index) + 1f) * 0.5f)
            val dotX = lerp(node.x, endX, dotT)
            val dotY = lerp(node.y, endY, dotT)
            dotPaint.alpha = (lineAlpha * 1.5f * 255f).toInt().coerceAtMost(255)
            canvas.drawCircle(dotX, dotY, dp(1.2f), dotPaint)
        }
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    private fun easeOut3(v: Float): Float {
        val t = v.coerceIn(0f, 1f)
        return 1f - (1f - t) * (1f - t) * (1f - t)
    }

    private fun lerp(start: Float, end: Float, t: Float): Float = start + (end - start) * t
}


