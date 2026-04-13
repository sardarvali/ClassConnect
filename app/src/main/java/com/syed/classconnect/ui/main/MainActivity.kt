package com.syed.classconnect.ui.main

<<<<<<< HEAD
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
=======
import android.content.SharedPreferences
>>>>>>> final
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
<<<<<<< HEAD
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import com.syed.classconnect.R
import com.syed.classconnect.databinding.ActivityMainBinding
import com.syed.classconnect.util.BiometricHelper
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
=======
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.syed.classconnect.R
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.databinding.ActivityMainBinding
import com.syed.classconnect.sensor.AppLifecycleObserver
import com.syed.classconnect.util.BiometricHelper
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint
>>>>>>> final
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var authRepository: AuthRepository

    /** Tracks whether we are returning from background (onStop→onStart). */
    private var wasInBackground = false
    /** Prevents showing biometric prompt during initial onCreate. */
    private var initialLaunch = true
    /** Blocks interaction while biometric prompt is showing. */
    private var isLocked = false
    /** Time when the app went to background (onStop). */
    private var backgroundTimestamp = 0L
    /** Minimum time in background (ms) before requiring biometric. */
    private val minBackgroundDuration = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
=======
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var authRepository: AuthRepository
    @Inject
    lateinit var prefs: SharedPreferences
    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver

    /** Prevents showing biometric prompt during initial onCreate. */
    private var initialLaunch = true

    /** Blocks interaction while biometric prompt is showing. */
    private var isLocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
>>>>>>> final
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
=======
        // Status bar: push fragment container below status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainNavHost) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = bars.top)
            insets
        }
        // Navigation bar + keyboard: keep bottom nav above system nav bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavContainer) { v, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val lp =
                v.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
            lp.bottomMargin =
                navBars.bottom + resources.getDimensionPixelSize(R.dimen.nav_margin_bottom)
            v.layoutParams = lp
            insets
        }

>>>>>>> final
        val navHost = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHost.navController

        setupBottomNavigation()
        handleDeepLink()
        saveFcmToken()
<<<<<<< HEAD
        requestNotificationPermission()
=======
>>>>>>> final
        observeUnreadNotifications()
    }

    private fun observeUnreadNotifications() {
        val uid = auth.currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("notifications")
            .document(uid)
            .collection("items")
            .whereEqualTo("isRead", false)
            .addSnapshotListener { snap, _ ->
                val count = snap?.size() ?: 0
                updateNotificationBadge(count)
            }
    }

    private fun updateNotificationBadge(count: Int) {
        try {
            val badge = binding.bottomNav.getOrCreateBadge(R.id.notificationsFragment)
            if (count > 0) {
                badge.isVisible = true
                badge.number = count
                badge.maxCharacterCount = 3
            } else {
                badge.isVisible = false
                badge.clearNumber()
            }
        } catch (_: Exception) {
            // Badge setup may fail if menu not yet inflated
        }
    }

    private fun saveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
<<<<<<< HEAD
            CoroutineScope(Dispatchers.IO).launch {
=======
            lifecycleScope.launch(Dispatchers.IO) {
>>>>>>> final
                authRepository.updateFcmToken(uid, token)
            }
        }
    }

<<<<<<< HEAD
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

=======
>>>>>>> final
    // ── Biometric lock on resume ──────────────────────────────────────

    override fun onStart() {
        super.onStart()
<<<<<<< HEAD
        if (wasInBackground && !initialLaunch) {
            val timeInBackground = System.currentTimeMillis() - backgroundTimestamp
            if (timeInBackground >= minBackgroundDuration) {
                checkBiometricLock()
            }
        }
        wasInBackground = false
        initialLaunch = false
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            wasInBackground = true
            backgroundTimestamp = System.currentTimeMillis()
        }
    }

    private fun checkBiometricLock() {
        val prefs = getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
=======
        if (!initialLaunch && appLifecycleObserver.shouldShowBiometric && !isLocked) {
            checkBiometricLock()
        }
        initialLaunch = false
    }

    private fun checkBiometricLock() {
>>>>>>> final
        val enabled = prefs.getBoolean(Constants.PREF_BIOMETRIC_ENABLED, false)
        if (!enabled) return

        val status = BiometricHelper.canAuthenticate(this)
        if (status != BiometricHelper.BiometricStatus.AVAILABLE) return

        // Lock the screen
        isLocked = true
        binding.biometricOverlay.visibility = View.VISIBLE

        BiometricHelper.authenticate(
            activity = this,
            onSuccess = {
                isLocked = false
<<<<<<< HEAD
=======
                appLifecycleObserver.resetBiometricFlag()
>>>>>>> final
                binding.biometricOverlay.visibility = View.GONE
            },
            onError = { msg ->
                // User cancelled — close the app
                if (msg.isEmpty()) {
                    finishAffinity()
                } else {
                    // Retry on other errors
                    finishAffinity()
                }
            },
            onFailed = {
                // Wrong fingerprint — prompt stays open, user can retry
            }
        )
    }

    private fun setupBottomNavigation() {
<<<<<<< HEAD
        CoroutineScope(Dispatchers.Main).launch {
=======
        lifecycleScope.launch {
>>>>>>> final
            val uid = auth.currentUser?.uid ?: return@launch
            val user = authRepository.getUserById(uid) ?: return@launch
            val menuRes = when (user.role) {
                Constants.ROLE_TEACHER -> R.menu.bottom_nav_teacher
                Constants.ROLE_ADMIN -> R.menu.bottom_nav_admin
                else -> R.menu.bottom_nav_student
            }
            val startDest = when (user.role) {
                Constants.ROLE_TEACHER -> R.id.teacherHomeFragment
                Constants.ROLE_ADMIN -> R.id.adminDashboardFragment
                else -> R.id.studentHomeFragment
            }
            val navGraph = navController.navInflater.inflate(R.navigation.nav_main)
            navGraph.setStartDestination(startDest)
            navController.graph = navGraph
            binding.bottomNav.menu.clear()
            binding.bottomNav.inflateMenu(menuRes)
            binding.bottomNav.setupWithNavController(navController)

            // Entrance animation for the floating nav bar
            binding.bottomNavContainer.translationY = 200f
            binding.bottomNavContainer.alpha = 0f
            binding.bottomNavContainer.animate()
                .translationY(0f).alpha(1f)
                .setDuration(500)
                .setStartDelay(300)
                .setInterpolator(OvershootInterpolator(0.8f))
                .start()

            // Icon bounce on tab selection
            binding.bottomNav.setOnItemSelectedListener { item ->
                val iconView = binding.bottomNav.findViewById<View>(item.itemId)
                iconView?.animate()
                    ?.scaleX(1.3f)?.scaleY(1.3f)?.setDuration(100)
                    ?.withEndAction {
                        iconView.animate().scaleX(1f).scaleY(1f).setDuration(180)
                            .setInterpolator(OvershootInterpolator(3f)).start()
                    }?.start()
                // Let NavigationUI handle the actual navigation
                val handled = navController.popBackStack(item.itemId, false)
                if (!handled) navController.navigate(item.itemId)
                true
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideNav = destination.id in listOf(R.id.onboardingFragment)
            if (hideNav && binding.bottomNavContainer.visibility == View.VISIBLE) {
                binding.bottomNavContainer.animate()
                    .translationY(binding.bottomNavContainer.height.toFloat() + 40f)
                    .alpha(0f)
                    .setDuration(280)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .withEndAction { binding.bottomNavContainer.visibility = View.GONE }
                    .start()
            } else if (!hideNav && binding.bottomNavContainer.visibility == View.GONE) {
                binding.bottomNavContainer.visibility = View.VISIBLE
                binding.bottomNavContainer.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(400)
                    .setInterpolator(OvershootInterpolator(0.8f))
                    .start()
            }
        }
    }

    private fun handleDeepLink() {
        intent?.data?.let { uri ->
            when (uri.host) {
                "ai_buddy" -> navController.navigate(R.id.aiBuddyFragment)
                "my_classes" -> navController.navigate(R.id.classListFragment)
<<<<<<< HEAD
                "attendance" -> { /* navigate to attendance */ }
=======
                "attendance" -> { /* navigate to attendance */
                }
>>>>>>> final
            }
        }
    }
}
