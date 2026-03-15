package com.example.classconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var login: Button
    lateinit var forgot: TextView
    lateinit var signupLink: TextView
    lateinit var verify: TextView
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        forgot = findViewById(R.id.forgot)
        signupLink = findViewById(R.id.tvSignupLink)
        verify=findViewById(R.id.verify)
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
            forgotPassword()
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

        verify.setOnClickListener {
            sendVerificationLink()
        }
    }

    private fun loginUser(e: String, p: String) {
        auth.signInWithEmailAndPassword(e, p)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "Welcome to ClassConnect!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {

                        Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                } else {
                    Log.d("LOGIN_DEBUG", "Email: [$e]")
                    Log.e("LoginError", "Auth Failed", task.exception)
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun forgotPassword() {
        val e = email.text.toString().trim()
        if (e.isNotEmpty()) {
            auth.sendPasswordResetEmail(e)
                .addOnSuccessListener {
                    Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendVerificationLink(){
        var user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Email is sent. Kindly verify"+ task.exception.toString(), Toast.LENGTH_SHORT).show()
                auth.signOut()
            }
        }


    }
}