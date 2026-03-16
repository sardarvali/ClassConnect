package com.syed.classconnect.util

import android.util.Patterns

object ValidationUtils {

    data class PasswordValidation(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean = password.length >= 8

    fun validatePasswordStrength(password: String): PasswordValidation {
        return when {
            password.length < 8 ->
                PasswordValidation(false, "Password must be at least 8 characters")
            !password.any { it.isUpperCase() } ->
                PasswordValidation(false, "Password must contain at least one uppercase letter")
            !password.any { it.isDigit() } ->
                PasswordValidation(false, "Password must contain at least one number")
            !password.any { !it.isLetterOrDigit() } ->
                PasswordValidation(false, "Password must contain at least one special character")
            else -> PasswordValidation(true)
        }
    }

    fun doPasswordsMatch(p1: String, p2: String): Boolean = p1 == p2

    fun isValidClassCode(code: String): Boolean =
        code.length == 6 && code.all { it.isLetterOrDigit() }

    fun isNotEmpty(value: String): Boolean = value.isNotBlank()

    fun passwordStrength(password: String): Int {
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return score // 0-4
    }

    fun sanitize(input: String, maxLength: Int = 2000): String =
        input.trim().take(maxLength)

    fun generateCode(length: Int = 6): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }
}

