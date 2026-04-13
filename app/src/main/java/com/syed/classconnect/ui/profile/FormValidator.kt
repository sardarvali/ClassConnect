package com.syed.classconnect.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Form validation helpers for real-time field validation
 * Shows errors immediately as user types with enhanced UX
 */
class FormValidator {

    private val _fieldErrors = MutableLiveData<Map<String, String?>>(emptyMap())
    val fieldErrors: LiveData<Map<String, String?>> = _fieldErrors

    private val _isFormValid = MutableLiveData(false)
    val isFormValid: LiveData<Boolean> = _isFormValid

    /**
     * Validates email field
     */
    fun validateEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
    }

    /**
     * Validates password field (minimum 8 chars, 1 uppercase, 1 number)
     */
    fun validatePassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() }
    }

    /**
     * Validates class code (typically alphanumeric, 6-10 chars)
     */
    fun validateClassCode(code: String): Boolean {
        return code.matches(Regex("^[A-Z0-9]{6,10}$"))
    }

    /**
     * Validates name field (2+ characters)
     */
    fun validateName(name: String): Boolean {
        return name.trim().length >= 2
    }

    /**
     * Validates phone number
     */
    fun validatePhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^[+]?[0-9]{10,15}$"))
    }

    /**
     * Updates field error state
     */
    fun setFieldError(fieldName: String, errorMessage: String?) {
        val currentErrors = _fieldErrors.value?.toMutableMap() ?: mutableMapOf()
        currentErrors[fieldName] = errorMessage
        _fieldErrors.postValue(currentErrors)
        updateFormValidity()
    }

    /**
     * Clears all field errors
     */
    fun clearAllErrors() {
        _fieldErrors.postValue(emptyMap())
        _isFormValid.postValue(true)
    }

    /**
     * Updates form validity based on current errors
     */
    private fun updateFormValidity() {
        val hasErrors = _fieldErrors.value?.values?.any { it != null } == true
        _isFormValid.postValue(!hasErrors)
    }
}

/**
 * Enhanced error dialog data
 */
data class ErrorDialogData(
    val title: String,
    val message: String,
    val actionText: String = "Retry",
    val icon: Int? = null,
    val isShakeable: Boolean = true,
    val onAction: (() -> Unit)? = null
)

/**
 * Actionable errors with retry logic
 */
sealed class ActionableError {
    data class NetworkError(val retryAction: () -> Unit) : ActionableError()
    data class ValidationError(val fieldErrors: Map<String, String>) : ActionableError()
    data class ServerError(val message: String, val retryAction: (() -> Unit)? = null) : ActionableError()
}

