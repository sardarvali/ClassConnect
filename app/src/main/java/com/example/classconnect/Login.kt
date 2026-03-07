package com.example.classconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var login: MaterialButton
    private lateinit var verify: MaterialButton
    private lateinit var forgot: TextView
    private lateinit var signupLink: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        email    = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login    = findViewById(R.id.login)
        forgot   = findViewById(R.id.forgot)
        signupLink = findViewById(R.id.tvSignupLink)
        verify   = findViewById(R.id.verify)

        login.setOnClickListener {
            val e = email.text.toString().trim()
            val p = password.text.toString().trim()
            if (e.isNotEmpty() && p.isNotEmpty()) {
                loginUser(e, p)
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }

        forgot.setOnClickListener {
            val currentEmail = email.text.toString().trim()
            val intent = Intent(this, ForgotPassword::class.java)
            if (currentEmail.isNotEmpty()) intent.putExtra("email", currentEmail)
            startActivity(intent)
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

        // "Resend Verification Email" button — signs in silently, sends mail, signs out
        verify.setOnClickListener {
            val e = email.text.toString().trim()
            val p = password.text.toString().trim()
            when {
                e.isEmpty() -> Toast.makeText(this, "Enter your email address first", Toast.LENGTH_SHORT).show()
                p.isEmpty() -> Toast.makeText(this, "Enter your password first", Toast.LENGTH_SHORT).show()
                else        -> resendVerificationEmail(e, p)
            }
        }
    }

    private fun loginUser(e: String, p: String) {
        login.isEnabled = false
        login.text = getString(R.string.btn_signing_in)

        auth.signInWithEmailAndPassword(e, p)
            .addOnCompleteListener { task ->
                login.isEnabled = true
                login.text = getString(R.string.btn_sign_in)

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                               Toast.makeText(this, "Welcome to ClassConnect!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        auth.signOut()
                        showVerificationDialog(e, p)
                    }
                } else {
                    Log.e("LoginError", "Auth Failed", task.exception)
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showVerificationDialog(e: String, p: String) {
        AlertDialog.Builder(this)
            .setTitle("Email Not Verified")
            .setMessage("Your email address has not been verified yet.\n\nPlease check your inbox or click below to resend the verification email.")
            .setPositiveButton("Resend Email") { _, _ ->
                resendVerificationEmail(e, p)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resendVerificationEmail(e: String, p: String) {
        verify.isEnabled = false
        verify.text = getString(R.string.btn_sending_verification)

        auth.signInWithEmailAndPassword(e, p)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Already verified — just let them in
                        verify.isEnabled = true
                        verify.text = getString(R.string.btn_resend_verification)
                        Toast.makeText(this, "Email already verified! You can sign in.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    } else {
                        user?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                verify.isEnabled = true
                                verify.text = getString(R.string.btn_resend_verification)
                                auth.signOut()
                                Toast.makeText(
                                    this,
                                    "Verification email sent to $e. Please check your inbox.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            ?.addOnFailureListener { err ->
                                verify.isEnabled = true
                                verify.text = getString(R.string.btn_resend_verification)
                                auth.signOut()
                                Toast.makeText(this, "Failed to send: ${err.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    verify.isEnabled = true
                    verify.text = getString(R.string.btn_resend_verification)
                    Toast.makeText(
                        this,
                        "Could not sign in: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}