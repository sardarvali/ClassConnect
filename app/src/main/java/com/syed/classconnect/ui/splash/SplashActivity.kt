package com.syed.classconnect.ui.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.databinding.ActivitySplashBinding
import com.syed.classconnect.ui.auth.AuthActivity
import com.syed.classconnect.ui.main.MainActivity
import com.syed.classconnect.ui.permissions.PermissionsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashAnimators = mutableListOf<Animator>()
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Prime the splash layers for a premium staggered reveal
        binding.splashContent.alpha = 0f
        binding.splashContent.translationY = 28f
        binding.splashContent.scaleX = 0.98f
        binding.splashContent.scaleY = 0.98f

        binding.splashContent.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(120)
            .setDuration(700)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        binding.root.findViewWithTag<View>("splash_glow_primary")?.let {
            startBreathingGlow(it, scaleStart = 0.96f, scaleEnd = 1.08f, alphaStart = 0.28f, alphaEnd = 0.56f, duration = 2400L, startDelay = 0L)
        }
        binding.root.findViewWithTag<View>("splash_glow_secondary")?.let {
            startBreathingGlow(it, scaleStart = 0.94f, scaleEnd = 1.06f, alphaStart = 0.18f, alphaEnd = 0.38f, duration = 2800L, startDelay = 260L)
        }
        binding.root.findViewWithTag<View>("splash_glass_card")?.let { startFloatingCard(it) }
        binding.root.findViewWithTag<View>("splash_logo_halo")?.let { startHaloPulse(it) }
        binding.root.findViewWithTag<View>("splash_version_badge")?.let { startBadgePulse(it) }

        binding.tvAppName.alpha = 0f
        binding.tvAppName.scaleX = 0.8f
        binding.tvAppName.scaleY = 0.8f
        binding.tvTagline.alpha = 0f
        binding.tvTagline.translationY = 16f

        binding.ivLogo.alpha = 0f
        binding.ivLogo.scaleX = 0f
        binding.ivLogo.scaleY = 0f
        binding.ivLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(220)
            .setDuration(650)
            .setInterpolator(OvershootInterpolator(1.8f))
            .start()

        binding.tvAppName.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setStartDelay(520)
            .setDuration(500)
            .setInterpolator(OvershootInterpolator(1.3f))
            .start()

        binding.tvTagline.animate()
            .alpha(1f).translationY(0f)
            .setStartDelay(760).setDuration(420)
            .setInterpolator(DecelerateInterpolator(2f)).start()

        lifecycleScope.launch {
            delay(2200)
            navigateNext()
        }
    }

    override fun onDestroy() {
        splashAnimators.forEach { it.cancel() }
        splashAnimators.clear()
        super.onDestroy()
    }

    private fun startBreathingGlow(
        view: View,
        scaleStart: Float,
        scaleEnd: Float,
        alphaStart: Float,
        alphaEnd: Float,
        duration: Long,
        startDelay: Long
    ) {
        view.scaleX = scaleStart
        view.scaleY = scaleStart
        view.alpha = alphaStart

        listOf(
            ObjectAnimator.ofFloat(view, View.SCALE_X, scaleStart, scaleEnd),
            ObjectAnimator.ofFloat(view, View.SCALE_Y, scaleStart, scaleEnd),
            ObjectAnimator.ofFloat(view, View.ALPHA, alphaStart, alphaEnd)
        ).forEach { animator ->
            animator.duration = duration
            animator.startDelay = startDelay
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.repeatMode = ValueAnimator.REVERSE
            animator.repeatCount = ValueAnimator.INFINITE
            animator.start()
            splashAnimators += animator
        }
    }

    private fun startFloatingCard(view: View) {
        view.translationY = 0f
        listOf(
            ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -10f, 0f),
            ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.015f, 1f),
            ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.015f, 1f)
        ).forEach { animator ->
            animator.duration = 3600L
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.start()
            splashAnimators += animator
        }
    }

    private fun startHaloPulse(view: View) {
        view.scaleX = 0.92f
        view.scaleY = 0.92f
        listOf(
            ObjectAnimator.ofFloat(view, View.SCALE_X, 0.92f, 1.04f, 0.92f),
            ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.92f, 1.04f, 0.92f),
            ObjectAnimator.ofFloat(view, View.ALPHA, 0.75f, 1f, 0.75f)
        ).forEach { animator ->
            animator.duration = 2600L
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.start()
            splashAnimators += animator
        }
    }

    private fun startBadgePulse(view: View) {
        view.alpha = 0f
        view.translationY = 10f
        val anim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 0.88f),
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 10f, 0f)
            )
            duration = 700L
            startDelay = 920L
            interpolator = OvershootInterpolator(1.1f)
            start()
        }
        splashAnimators += anim
    }

    private suspend fun navigateNext() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            goToAuth()
            return
        }

        val userDoc = authRepository.getUserById(firebaseUser.uid)
        if (userDoc == null) {
            goToAuth()
            return
        }

        when (userDoc.accountType) {
            "independent" -> {
                // Reload to get fresh emailVerified state
                try {
                    firebaseUser.reload().await()
                } catch (_: Exception) { /* ignore reload failure */
                }
                when {
                    !firebaseUser.isEmailVerified -> {
                        // Still needs to verify email
                        goToAuth(destination = AuthDestination.EMAIL_VERIFICATION)
                    }

                    !userDoc.isApproved -> {
                        // Verified but Firestore not updated yet — fix it and go to main
                        authRepository.approveIndependentUser(firebaseUser.uid)
                        goToMain()
                    }

                    else -> goToMain()
                }
            }

            else -> {
                // Institution user — admins bypass the approval check entirely
                when {
                    userDoc.role == "admin" -> {
                        // Self-heal: make sure admin isApproved = true in Firestore
                        if (!userDoc.isApproved) {
                            authRepository.approveUser(firebaseUser.uid, true)
                        }
                        goToMain()
                    }

                    !userDoc.isApproved -> goToAuth(destination = AuthDestination.PENDING_APPROVAL)
                    else -> goToMain()
                }
            }
        }
    }

    private fun goToAuth(destination: AuthDestination = AuthDestination.LOGIN) {
        val intent = Intent(this, AuthActivity::class.java).apply {
            val key = when (destination) {
                AuthDestination.LOGIN -> null
                AuthDestination.PENDING_APPROVAL -> "pending"
                AuthDestination.EMAIL_VERIFICATION -> "email_verification"
            }
            if (key != null) putExtra("destination", key)
        }
        startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out)
        finish()
    }

    private fun goToMain() {
        if (!PermissionsActivity.alreadyRequested(this)) {
            startActivity(Intent(this, PermissionsActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        @Suppress("DEPRECATION")
        overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out)
        finish()
    }

    enum class AuthDestination { LOGIN, PENDING_APPROVAL, EMAIL_VERIFICATION }
}

