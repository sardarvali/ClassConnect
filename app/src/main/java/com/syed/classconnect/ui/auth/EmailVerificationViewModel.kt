package com.syed.classconnect.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class VerificationState {
    object Polling : VerificationState()
    object Verified : VerificationState()
    data class Error(val message: String) : VerificationState()
}

@HiltViewModel
class EmailVerificationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<VerificationState>(VerificationState.Polling)
    val state: StateFlow<VerificationState> = _state

    private var pollingJob: Job? = null

    fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(5_000)
                try {
                    Firebase.auth.currentUser?.reload()?.await()
                    val verified = Firebase.auth.currentUser?.isEmailVerified ?: false
                    if (verified) {
                        updateFirestoreOnVerification()
                        _state.value = VerificationState.Verified
                        break
                    }
                } catch (_: Exception) {
                    // transient — keep polling
                }
            }
        }
    }

<<<<<<< HEAD
    fun stopPolling() { pollingJob?.cancel() }

    fun resendEmail() {
        viewModelScope.launch {
            try { Firebase.auth.currentUser?.sendEmailVerification()?.await() }
            catch (_: Exception) {}
=======
    fun stopPolling() {
        pollingJob?.cancel()
    }

    fun resendEmail() {
        viewModelScope.launch {
            try {
                Firebase.auth.currentUser?.sendEmailVerification()?.await()
            } catch (_: Exception) {
            }
>>>>>>> final
        }
    }

    fun signOutAndClear() {
        stopPolling()
        Firebase.auth.signOut()
    }

    private suspend fun updateFirestoreOnVerification() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        try {
            Firebase.firestore.collection("users").document(uid)
                .update(mapOf("emailVerified" to true, "isApproved" to true)).await()
<<<<<<< HEAD
        } catch (_: Exception) {}
=======
        } catch (_: Exception) {
        }
>>>>>>> final
    }
}
