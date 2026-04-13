package com.syed.classconnect.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.data.model.User
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/** Result of the dual-path registration flow. */
sealed class RegistrationResult {
    object InstitutionPath : RegistrationResult()  // → PendingApprovalFragment
    object IndependentPath : RegistrationResult()  // → EmailVerificationWaitFragment
    data class Error(val message: String) : RegistrationResult()
    object Loading : RegistrationResult()
}

/** Where to route after a successful login. */
sealed class LoginRouteResult {
    object ToMain : LoginRouteResult()                   // approved → MainActivity
    object ToPending : LoginRouteResult()                // institution, not approved
    object ToEmailVerification : LoginRouteResult()      // independent, email not verified
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableLiveData<NetworkResult<User>>()
    val authState: LiveData<NetworkResult<User>> = _authState

    private val _resetState = MutableLiveData<NetworkResult<Unit>>()
    val resetState: LiveData<NetworkResult<Unit>> = _resetState

    private val _registrationResult = MutableStateFlow<RegistrationResult?>(null)
    val registrationResult: StateFlow<RegistrationResult?> = _registrationResult

    private val _loginRoute = MutableLiveData<LoginRouteResult>()
    val loginRoute: LiveData<LoginRouteResult> = _loginRoute

    fun login(email: String, password: String) {
        _authState.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _authState.value = NetworkResult.Success(user)
                    resolvePostLoginRoute(user)
                },
                onFailure = {
                    _authState.value = NetworkResult.Error(it.message ?: "Login failed")
                }
            )
        }
    }

    /**
     * Determines where to navigate after a successful login/sign-in.
     * - Institution user, not approved → ToPending
     * - Independent user, email not verified → ToEmailVerification
     * - Independent user, email verified but Firestore not updated → auto-approve then ToMain
     * - Everyone else approved → ToMain
     */
    fun resolvePostLoginRoute(user: User) {
        viewModelScope.launch {
            when (user.accountType) {
                "independent" -> {
                    // Reload Firebase Auth to get fresh isEmailVerified
<<<<<<< HEAD
                    try { firebaseAuth.currentUser?.reload()?.await() } catch (_: Exception) {}
=======
                    try {
                        firebaseAuth.currentUser?.reload()?.await()
                    } catch (_: Exception) {
                    }
>>>>>>> final
                    val emailVerified = firebaseAuth.currentUser?.isEmailVerified ?: false
                    if (!emailVerified) {
                        _loginRoute.value = LoginRouteResult.ToEmailVerification
                    } else {
                        // Email verified — if Firestore not updated yet, fix it
                        if (!user.isApproved) {
                            authRepository.approveIndependentUser(user.uid)
                        }
                        _loginRoute.value = LoginRouteResult.ToMain
                    }
                }
<<<<<<< HEAD
=======

>>>>>>> final
                else -> {
                    // Institution user — admins bypass the approval check entirely
                    if (!user.isApproved && user.role != "admin") {
                        _loginRoute.value = LoginRouteResult.ToPending
                    } else {
                        _loginRoute.value = LoginRouteResult.ToMain
                    }
                }
            }
        }
    }

    /** Dual-path register: institution code provided → PATH A, blank → PATH B */
<<<<<<< HEAD
    fun register(name: String, email: String, password: String, role: String, institutionCode: String) {
=======
    fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        institutionCode: String
    ) {
>>>>>>> final
        _registrationResult.value = RegistrationResult.Loading
        viewModelScope.launch {
            val trimmedCode = institutionCode.trim()
            if (trimmedCode.isNotEmpty()) {
                // PATH A — institution code provided
                val result = authRepository.registerWithInstitutionCode(
                    name, email, password, role, trimmedCode
                )
                _registrationResult.value = result.fold(
                    onSuccess = { RegistrationResult.InstitutionPath },
                    onFailure = { RegistrationResult.Error(it.message ?: "Registration failed") }
                )
            } else {
                // PATH B — independent user
                val result = authRepository.registerIndependent(name, email, password, role)
                _registrationResult.value = result.fold(
                    onSuccess = { RegistrationResult.IndependentPath },
                    onFailure = { RegistrationResult.Error(it.message ?: "Registration failed") }
                )
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { (user, isNew) ->
                    if (isNew) {
<<<<<<< HEAD
                        _authState.value = NetworkResult.Error("new_google_user:${user.uid}:${user.name}:${user.email}:${user.photoUrl}")
=======
                        _authState.value =
                            NetworkResult.Error("new_google_user:${user.uid}:${user.name}:${user.email}:${user.photoUrl}")
>>>>>>> final
                    } else {
                        _authState.value = NetworkResult.Success(user)
                        resolvePostLoginRoute(user)
                    }
                },
<<<<<<< HEAD
                onFailure = { _authState.value = NetworkResult.Error(it.message ?: "Google sign-in failed") }
=======
                onFailure = {
                    _authState.value = NetworkResult.Error(it.message ?: "Google sign-in failed")
                }
>>>>>>> final
            )
        }
    }

    fun completeGoogleRegistration(user: User) {
        viewModelScope.launch {
            authRepository.saveNewGoogleUser(user)
            _authState.value = NetworkResult.Success(user)
            resolvePostLoginRoute(user)
        }
    }

    fun sendPasswordReset(email: String) {
        _resetState.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            _resetState.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: "Failed to send email") }
            )
        }
    }

    fun logout() = authRepository.logout()
}


