package com.syed.classconnect.ui.splash

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ParticleBurstView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Shape { CIRCLE, SQUARE, DIAMOND, TRIANGLE }

    data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        val radius: Float,
        val color: Int,
        var alpha: Float,
        var rotation: Float,
        val spin: Float,
        val shape: Shape,
        val trail: ArrayDeque<PointF>,
        val gravity: Float,
        var life: Float,
        val maxLife: Float
    )

    data class Ring(
        val cx: Float,
        val cy: Float,
        val color: Int,
        val maxRadius: Float,
        val lineWidth: Float,
        val delayMs: Long,
        var lifeMs: Long = 0L
    )

    data class Beam(
        val angle: Float,
        val maxLength: Float,
        val color: Int,
        var currentLength: Float = 0f,
        var alpha: Float = 0f,
        var life: Float = 0f
    )

    private data class SubjectPill(
        val label: String,
        val color: Int,
        val startDelayMs: Long,
        var x: Float,
        var y: Float,
        val vx: Float,
        val vy: Float,
        var lifeMs: Long = 0L,
        var active: Boolean = false,
        var alive: Boolean = true
    )

    private val random = Random(144)

    private val palette = intArrayOf(
        Color.parseColor("#6650A4"),
        Color.parseColor("#7F77DD"),
        Color.parseColor("#D0BCFF"),
        Color.parseColor("#A8C7FA"),
        Color.parseColor("#9FE1CB"),
        Color.parseColor("#F2B8EB"),
        Color.parseColor("#B69DF8"),
        Color.parseColor("#E8DEF8")
    )

    private val beamPalette = intArrayOf(
        Color.parseColor("#B69DF8"),
        Color.parseColor("#A8C7FA"),
        Color.parseColor("#9FE1CB"),
        Color.parseColor("#F2B8EB")
    )

    private val pillColors = intArrayOf(
        Color.parseColor("#A8C7FA"),
        Color.parseColor("#9FE1CB"),
        Color.parseColor("#F2B8EB"),
        Color.parseColor("#D0BCFF")
    )

    private val particles = mutableListOf<Particle>()
    private val rings = mutableListOf<Ring>()
    private val beams = mutableListOf<Beam>()
    private val pills = mutableListOf<SubjectPill>()

    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val beamPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = dp(2f)
    }
    private val tipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private val pillBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val pillStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = dp(0.5f) }
    private val pillTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        textSize = sp(8f)
    }

    private val trianglePath = Path()
    private val pillRect = RectF()

    private var centerX = 0f
    private var centerY = 0f

    private var burstStartMs = -1L
    private var pillsStartMs = -1L
    private var lastFrameMs = 0L

    private val frameAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 16L
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            val now = SystemClock.elapsedRealtime()
            if (lastFrameMs == 0L) lastFrameMs = now
            val deltaSec = ((now - lastFrameMs).coerceAtLeast(1L)) / 1000f
            lastFrameMs = now
            tick(deltaSec, now)
            invalidate()
        }
    }

    fun fireBurst(cx: Float, cy: Float) {
        centerX = cx
        centerY = cy
        burstStartMs = SystemClock.elapsedRealtime()
        lastFrameMs = burstStartMs
        particles.clear()
        rings.clear()
        beams.clear()

        repeat(120) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = dp(1.6f + random.nextFloat() * 7.2f)
            val radius = dp(1.5f + random.nextFloat() * 4.5f)
            particles += Particle(
                x = cx,
                y = cy,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed,
                radius = radius,
                color = palette[random.nextInt(palette.size)],
                alpha = 1f,
                rotation = random.nextFloat() * 2f * PI.toFloat(),
                spin = (-0.2f + random.nextFloat() * 0.4f),
                shape = Shape.entries[random.nextInt(Shape.entries.size)],
                trail = ArrayDeque(),
                gravity = dp(0.06f + random.nextFloat() * 0.08f),
                life = 0f,
                maxLife = 0.7f + random.nextFloat() * 0.7f
            )
        }

        val ringColors = intArrayOf(
            Color.parseColor("#7F77DD"),
            Color.parseColor("#A8C7FA"),
            Color.parseColor("#D0BCFF"),
            Color.parseColor("#F2B8EB")
        )
        val ringRadii = floatArrayOf(dp(80f), dp(110f), dp(140f), dp(160f))
        val ringWidths = floatArrayOf(dp(2f), dp(1.5f), dp(1f), dp(0.5f))
        val ringDelay = longArrayOf(0L, 120L, 240L, 360L)
        for (i in 0..3) {
            rings += Ring(cx, cy, ringColors[i], ringRadii[i], ringWidths[i], ringDelay[i])
        }

        for (i in 0 until 8) {
            val angle = (i * 45f).toRadians()
            beams += Beam(
                angle = angle,
                maxLength = dp(55f + random.nextFloat() * 35f),
                color = beamPalette[i % beamPalette.size]
            )
        }

        if (!frameAnimator.isStarted) frameAnimator.start()
    }

    fun fireSubjectPills(cx: Float, cy: Float) {
        centerX = cx
        centerY = cy
        pillsStartMs = SystemClock.elapsedRealtime()
        pills.clear()

        val labels = listOf("Math", "Science", "Eng", "History", "Art", "CS")
        for (i in labels.indices) {
            val baseAngle = i * 60f
            val offset = -9f + random.nextFloat() * 18f
            val radius = dp(70f + random.nextFloat() * 35f)
            val angle = (baseAngle + offset).toRadians()
            pills += SubjectPill(
                label = labels[i],
                color = pillColors[i % pillColors.size],
                startDelayMs = i * 80L,
                x = cx + cos(angle) * radius,
                y = cy + sin(angle) * radius,
                vx = dp((-0.4f + random.nextFloat() * 0.8f)),
                vy = dp(-(0.3f + random.nextFloat() * 0.3f))
            )
        }

        if (!frameAnimator.isStarted) frameAnimator.start()
    }

    override fun onDetachedFromWindow() {
        if (frameAnimator.isStarted) frameAnimator.cancel()
        super.onDetachedFromWindow()
    }

    private fun tick(deltaSec: Float, nowMs: Long) {
        val frameScale = deltaSec * 60f

        if (burstStartMs > 0L) {
            particles.removeAll { p ->
                p.x += p.vx * frameScale
                p.y += p.vy * frameScale
                p.vy += p.gravity * frameScale
                p.rotation += p.spin * frameScale
                p.life += deltaSec

                p.trail.addLast(PointF(p.x, p.y))
                while (p.trail.size > 10) p.trail.removeFirst()

                val progress = (p.life / p.maxLife).coerceIn(0f, 1f)
                p.alpha = if (progress < 0.5f) 1f else easeOut3(1f - ((progress - 0.5f) / 0.5f))
                progress >= 1f
            }

            val ringElapsed = nowMs - burstStartMs
            rings.removeAll { ring ->
                if (ringElapsed < ring.delayMs) return@removeAll false
                ring.lifeMs = ringElapsed - ring.delayMs
                ring.lifeMs > 550L
            }

            beams.removeAll { beam ->
                beam.life += deltaSec
                beam.life >= 0.5f
            }
        }

        if (pillsStartMs > 0L) {
            val pillElapsed = nowMs - pillsStartMs
            pills.forEach { pill ->
                if (!pill.active && pillElapsed >= pill.startDelayMs) {
                    pill.active = true
                    pill.lifeMs = 0L
                }
                if (!pill.active || !pill.alive) return@forEach
                pill.lifeMs += (deltaSec * 1000f).toLong()
                pill.x += pill.vx * frameScale
                pill.y += pill.vy * frameScale
                if (pill.lifeMs > 1600L) pill.alive = false
            }
            pills.removeAll { !it.alive }
        }

        if (particles.isEmpty() && rings.isEmpty() && beams.isEmpty() && pills.isEmpty() && frameAnimator.isStarted) {
            frameAnimator.cancel()
            burstStartMs = -1L
            pillsStartMs = -1L
            lastFrameMs = 0L
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.clipRect(0f, 0f, width.toFloat(), height.toFloat())
        drawRings(canvas)
        drawBeams(canvas)
        drawParticles(canvas)
        drawPills(canvas)
    }

    private fun drawParticles(canvas: Canvas) {
        for (p in particles) {
            val baseAlpha = (p.alpha * 255f).toInt().coerceIn(0, 255)
            if (p.trail.size > 1) {
                var previous: PointF? = null
                var index = 0
                val total = p.trail.size
                for (point in p.trail) {
                    if (previous != null) {
                        val t = index / total.toFloat()
                        trailPaint.color = p.color
                        trailPaint.alpha = (baseAlpha * t * 0.3f).toInt().coerceIn(0, 255)
                        trailPaint.strokeWidth = p.radius * t * 0.8f
                        canvas.drawLine(previous.x, previous.y, point.x, point.y, trailPaint)
                    }
                    previous = point
                    index++
                }
            }

            particlePaint.color = p.color
            particlePaint.alpha = baseAlpha
            canvas.save()
            canvas.translate(p.x, p.y)
            canvas.rotate(p.rotation.toDegrees())

            when (p.shape) {
                Shape.CIRCLE -> canvas.drawCircle(0f, 0f, p.radius, particlePaint)
                Shape.SQUARE -> canvas.drawRect(-p.radius, -p.radius, p.radius, p.radius, particlePaint)
                Shape.DIAMOND -> {
                    canvas.rotate(45f)
                    canvas.drawRect(-p.radius, -p.radius, p.radius, p.radius, particlePaint)
                }
                Shape.TRIANGLE -> {
                    trianglePath.reset()
                    trianglePath.moveTo(0f, -p.radius)
                    trianglePath.lineTo(p.radius * 0.87f, p.radius * 0.5f)
                    trianglePath.lineTo(-p.radius * 0.87f, p.radius * 0.5f)
                    trianglePath.close()
                    canvas.drawPath(trianglePath, particlePaint)
                }
            }
            canvas.restore()
        }
    }

    private fun drawRings(canvas: Canvas) {
        val now = SystemClock.elapsedRealtime()
        for (ring in rings) {
            if (burstStartMs < 0) continue
            val totalElapsed = now - burstStartMs
            if (totalElapsed < ring.delayMs) continue

            val life = ((totalElapsed - ring.delayMs) / 550f).coerceIn(0f, 1f)
            val eased = easeOut3(life)
            val radius = lerp(dp(6f), ring.maxRadius, eased)
            val alpha = ((1f - life) * 0.7f * 255f).toInt().coerceIn(0, 255)

            ringPaint.color = ring.color
            ringPaint.alpha = alpha
            ringPaint.strokeWidth = ring.lineWidth
            canvas.drawCircle(ring.cx, ring.cy, radius, ringPaint)
        }
    }

    private fun drawBeams(canvas: Canvas) {
        for (beam in beams) {
            val progress = (beam.life / 0.5f).coerceIn(0f, 1f)
            val eased = easeOut3(progress)
            beam.currentLength = lerp(0f, beam.maxLength, eased)
            beam.alpha = if (progress < 0.5f) progress * 2f else (1f - (progress - 0.5f) * 2f)

            val endX = centerX + cos(beam.angle) * beam.currentLength
            val endY = centerY + sin(beam.angle) * beam.currentLength

            beamPaint.color = beam.color
            beamPaint.alpha = (beam.alpha * 255f).toInt().coerceIn(0, 255)
            canvas.drawLine(centerX, centerY, endX, endY, beamPaint)

            tipPaint.color = beam.color
            tipPaint.alpha = (beam.alpha * 255f).toInt().coerceIn(0, 255)
            canvas.drawCircle(endX, endY, dp(2f), tipPaint)
        }
    }

    private fun drawPills(canvas: Canvas) {
        for (pill in pills) {
            if (!pill.active) continue

            val progress = (pill.lifeMs / 1600f).coerceIn(0f, 1f)
            val alpha = when {
                progress <= 0.4f -> progress / 0.4f
                progress >= 0.7f -> 1f - ((progress - 0.7f) / 0.3f)
                else -> 1f
            }.coerceIn(0f, 1f)

            val textWidth = pillTextPaint.measureText(pill.label)
            val left = pill.x - textWidth / 2f - dp(8f)
            val right = pill.x + textWidth / 2f + dp(8f)
            val top = pill.y - dp(9f)
            val bottom = pill.y + dp(9f)
            pillRect.set(left, top, right, bottom)

            val bgAlpha = (alpha * 0.12f * 255f).toInt().coerceIn(0, 255)
            val strokeAlpha = (alpha * 0.50f * 255f).toInt().coerceIn(0, 255)
            val textAlpha = (alpha * 255f).toInt().coerceIn(0, 255)

            pillBgPaint.color = pill.color
            pillBgPaint.alpha = bgAlpha
            canvas.drawRoundRect(pillRect, dp(9f), dp(9f), pillBgPaint)

            pillStrokePaint.color = pill.color
            pillStrokePaint.alpha = strokeAlpha
            canvas.drawRoundRect(pillRect, dp(9f), dp(9f), pillStrokePaint)

            pillTextPaint.color = pill.color
            pillTextPaint.alpha = textAlpha
            val textY = pill.y - ((pillTextPaint.descent() + pillTextPaint.ascent()) / 2f)
            canvas.drawText(pill.label, pill.x, textY, pillTextPaint)
        }
    }

    private fun easeOut3(v: Float): Float {
        val t = v.coerceIn(0f, 1f)
        return 1f - (1f - t) * (1f - t) * (1f - t)
    }

    private fun lerp(start: Float, end: Float, t: Float): Float = start + (end - start) * t

    private fun Float.toRadians(): Float = this * PI.toFloat() / 180f

    private fun Float.toDegrees(): Float = this * 180f / PI.toFloat()

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    private fun sp(value: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics)
}


