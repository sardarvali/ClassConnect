package com.syed.classconnect.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.syed.classconnect.R
import com.syed.classconnect.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.auth_nav_host) as NavHostFragment
        val navController = navHost.navController

        when (intent.getStringExtra("destination")) {
            "pending" -> navController.navigate(R.id.pendingApprovalFragment)
            "email_verification" -> navController.navigate(R.id.emailVerificationWaitFragment)
        }
        if (intent.getBooleanExtra("pending", false)) {
            navController.navigate(R.id.pendingApprovalFragment)
        }
    }
}



