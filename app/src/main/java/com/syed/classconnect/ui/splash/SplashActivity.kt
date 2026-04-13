package com.syed.classconnect.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
}

