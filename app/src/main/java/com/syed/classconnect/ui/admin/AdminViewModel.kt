package com.syed.classconnect.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.classconnect.data.model.User
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.ClassRepository
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class AdminStats(
    val totalUsers: Int = 0,
    val teachers: Int = 0,
    val students: Int = 0,
    val classes: Int = 0
)

sealed class ApproveResult {
    data class Success(val uid: String, val approved: Boolean) : ApproveResult()
    data class Error(val uid: String, val message: String) : ApproveResult()
}

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _stats = MutableLiveData<AdminStats>()
    val stats: LiveData<AdminStats> = _stats

    // Pending tab list — always reflects live Firestore state
    private val _pendingUsers = MutableLiveData<NetworkResult<List<User>>>()
    val pendingUsers: LiveData<NetworkResult<List<User>>> = _pendingUsers

    // Teachers / Students tabs
    private val _users = MutableLiveData<NetworkResult<List<User>>>()
    val users: LiveData<NetworkResult<List<User>>> = _users

    // Result of approve / reject — used only to show Snackbar
    private val _approveResult = MutableLiveData<ApproveResult>()
    val approveResult: LiveData<ApproveResult> = _approveResult

    var institutionId: String = ""
        private set

    private var adminUid: String = ""

    // Single persistent listener job — only one runs at a time
    private var listenerJob: Job? = null

    /**
     * Starts (or restarts) the real-time Firestore listener for all institution users.
     * The listener automatically updates _pendingUsers whenever isApproved / isRejected changes.
     */
    fun loadAdminStats(uid: String) {
        if (adminUid == uid && listenerJob?.isActive == true) return  // already listening
        startListening(uid)
    }

    /** Force restarts the listener — used on error to restore state. */
    fun forceReload(uid: String) {
        listenerJob?.cancel()
        listenerJob = null
        adminUid = ""
        startListening(uid)
    }

    private fun startListening(uid: String) {
        adminUid = uid
        listenerJob?.cancel()
        listenerJob = viewModelScope.launch {
            val adminUser = authRepository.getUserById(uid) ?: return@launch
            institutionId = adminUser.institutionId
            Timber.d("AdminViewModel: institutionId=$institutionId")
            _pendingUsers.postValue(NetworkResult.Loading())
            try {
                classRepository.getUsersForInstitution(institutionId).collect { allUsers ->
                    Timber.d("AdminViewModel: got ${allUsers.size} users from Firestore")
                    val teachers = allUsers.count { it.role == Constants.ROLE_TEACHER }
                    val students = allUsers.count { it.role == Constants.ROLE_STUDENT }
                    _stats.postValue(AdminStats(allUsers.size, teachers, students, 0))

                    // Pending = not approved AND not rejected AND not admin
                    val pending = allUsers.filter {
                        !it.isApproved && !it.isRejected && it.role != Constants.ROLE_ADMIN
                    }
                    Timber.d("AdminViewModel: pending count=${pending.size}")
                    _pendingUsers.postValue(NetworkResult.Success(pending))
                }
            } catch (e: Exception) {
                Timber.e(e, "AdminViewModel: listener error")
                _pendingUsers.postValue(NetworkResult.Error(e.message ?: "Failed to load users"))
            }
        }
    }

    fun loadUsers(institutionId: String, role: String? = null) {
        _users.postValue(NetworkResult.Loading())
        viewModelScope.launch {
            try {
                classRepository.getUsersForInstitution(institutionId, role).collect { list ->
                    // Teachers/Students/Admins tabs only show approved users
                    var approved = list.filter { it.isApproved }
                    // Don't include the currently logged-in admin in the Admins tab
                    if (role == Constants.ROLE_ADMIN) {
                        val currentUid =
                            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                        approved = approved.filter { it.uid != currentUid }
                    }
                    _users.postValue(NetworkResult.Success(approved))
                }
            } catch (e: Exception) {
                _users.postValue(NetworkResult.Error(e.message ?: "Failed"))
            }
        }
    }

    /**
     * Approve or reject a pending user.
     * The Firestore snapshot listener will automatically update _pendingUsers
     * when the document changes — no need to manually refresh.
     */
    fun approveUser(uid: String, approved: Boolean) {
        viewModelScope.launch {
            try {
                Timber.d("AdminViewModel: approveUser uid=$uid approved=$approved")
                // 1. Write to Firestore
                authRepository.approveUser(uid, approved)
                Timber.d("AdminViewModel: approveUser write SUCCESS uid=$uid")

                // 2. Immediately re-fetch the full user list to refresh UI
                //    (don't wait for snapshot listener — do it directly)
                if (institutionId.isNotEmpty()) {
                    val allUsers = authRepository.getUsersForInstitutionOnce(institutionId)
                    Timber.d("AdminViewModel: re-fetched ${allUsers.size} users after approve")
                    val teachers = allUsers.count { it.role == Constants.ROLE_TEACHER }
                    val students = allUsers.count { it.role == Constants.ROLE_STUDENT }
                    _stats.postValue(AdminStats(allUsers.size, teachers, students, 0))
                    val pending = allUsers.filter {
                        !it.isApproved && !it.isRejected && it.role != Constants.ROLE_ADMIN
                    }
                    Timber.d("AdminViewModel: new pending count=${pending.size}")
                    _pendingUsers.postValue(NetworkResult.Success(pending))
                }

                // 3. Notify fragment for Snackbar
                _approveResult.postValue(ApproveResult.Success(uid, approved))
            } catch (e: Exception) {
                Timber.e(e, "AdminViewModel: approveUser FAILED uid=$uid")
                _approveResult.postValue(
                    ApproveResult.Error(
                        uid,
                        e.message ?: "Failed to update user"
                    )
                )
            }
        }
    }
}
