package com.syed.classconnect.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.R
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
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate app name and tagline with stagger
        binding.tvAppName.alpha = 0f
        binding.tvAppName.scaleX = 0.8f
        binding.tvAppName.scaleY = 0.8f
        binding.tvTagline.alpha = 0f
        binding.tvTagline.translationY = 20f
        binding.tvVersion.alpha = 0f

        // Logo / icon entrance (if present)
        binding.root.findViewWithTag<View>("splash_icon")?.let { icon ->
            icon.alpha = 0f; icon.scaleX = 0f; icon.scaleY = 0f
            icon.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setStartDelay(300).setDuration(600)
                .setInterpolator(OvershootInterpolator(2f)).start()
        }

        binding.tvAppName.animate()
            .alpha(1f).scaleX(1f).scaleY(1f).translationYBy(-15f)
            .setStartDelay(600).setDuration(600)
            .setInterpolator(OvershootInterpolator(1.5f)).start()

        binding.tvTagline.animate()
            .alpha(1f).translationY(0f)
            .setStartDelay(900).setDuration(500)
            .setInterpolator(DecelerateInterpolator(2f)).start()

        binding.tvVersion.animate()
            .alpha(0.5f)
            .setStartDelay(1200).setDuration(400).start()

        lifecycleScope.launch {
            delay(2200)
            navigateNext()
        }
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
                try { firebaseUser.reload().await() } catch (_: Exception) { /* ignore reload failure */ }
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
=======
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.syed.classconnect.R
import com.syed.classconnect.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var orchestrator: SplashAnimationOrchestrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val rootView = findViewById<android.view.View>(R.id.splash_root)
        val neuralBackground = findViewById<NeuralBackgroundView>(R.id.neural_background)
        val particleBurst = findViewById<ParticleBurstView>(R.id.particle_burst)
        val logoView = findViewById<TextView>(R.id.logo_view)
        val tagline = findViewById<TextView>(R.id.tagline)
        val loaderTrack = findViewById<android.view.View>(R.id.loader_track)
        val loaderFill = findViewById<android.view.View>(R.id.loader_fill)
        val roleChips = findViewById<LinearLayout>(R.id.role_chips)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(rootView)

        orchestrator = SplashAnimationOrchestrator(
            scope = lifecycleScope,
            rootView = rootView,
            neuralBackgroundView = neuralBackground,
            particleBurstView = particleBurst,
            logoView = logoView,
            taglineView = tagline,
            loaderTrack = loaderTrack,
            loaderFill = loaderFill,
            roleChipsLayout = roleChips,
            onComplete = { goToMain() }
        )
        orchestrator?.start()
    }

    override fun onDestroy() {
        orchestrator?.cancel()
        orchestrator = null
        super.onDestroy()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
>>>>>>> final
}

