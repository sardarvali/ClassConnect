package com.example.classconnect

import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Signup: AppCompatActivity() {

    lateinit var fullName: EditText
    lateinit var regNumber: EditText
    lateinit var section: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var btnRegister: Button
    lateinit var tvLoginLink: TextView
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        fullName = findViewById(R.id.etFullName)
        regNumber = findViewById(R.id.etRegNumber)
        section = findViewById(R.id.etSection)
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            val e = email.text.toString().trim()
            val p = password.text.toString().trim()
            val name = fullName.text.toString().trim()

            if (e.isNotEmpty() && p.isNotEmpty() && name.isNotEmpty()) {
                registerUser(e, p, name)
            } else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun registerUser(e: String, p: String, name: String) {

        auth.createUserWithEmailAndPassword(e, p)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val user = auth.currentUser
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {

                        user.sendEmailVerification()

                        Toast.makeText(
                            this,
                            "Registration successful! Please verify your email.",
                            Toast.LENGTH_LONG
                        ).show()

                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}

