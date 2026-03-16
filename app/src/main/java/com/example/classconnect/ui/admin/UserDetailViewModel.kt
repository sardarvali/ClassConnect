package com.syed.classconnect.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.syed.classconnect.data.model.RoleChangeLog
import com.syed.classconnect.data.model.User
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<NetworkResult<User>>(NetworkResult.Loading())
    val user: StateFlow<NetworkResult<User>> = _user

    private val _roleChangeState = MutableStateFlow<NetworkResult<String>?>(null)
    val roleChangeState: StateFlow<NetworkResult<String>?> = _roleChangeState

    private var userListener: com.google.firebase.firestore.ListenerRegistration? = null

    fun loadUser(uid: String) {
        // Cancel previous listener if any
        userListener?.remove()
        _user.value = NetworkResult.Loading()
        userListener = Firebase.firestore.collection("users").document(uid)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    _user.value = NetworkResult.Error(err.message ?: "Error")
                    return@addSnapshotListener
                }
                val u = snap?.toObject(User::class.java)
                _user.value = if (u != null) NetworkResult.Success(u)
                              else NetworkResult.Error("User not found")
            }
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
    }

    fun changeUserRole(
        targetUserId: String,
        newRole: String,
        currentRole: String,
        targetUserName: String,
        institutionId: String,
        reason: String = ""
    ) {
        viewModelScope.launch {
            _roleChangeState.value = NetworkResult.Loading()
            try {
                val db = Firebase.firestore
                val batch = db.batch()
                val userRef = db.collection("users").document(targetUserId)

                // 1. Update role field + timestamp + auto-approve if promoted to admin
                val roleUpdateMap = mutableMapOf(
                    "role" to newRole,
                    "roleChangedAt" to FieldValue.serverTimestamp(),
                    "roleChangedBy" to (Firebase.auth.currentUser?.uid ?: "")
                )
                if (newRole == "admin") {
                    roleUpdateMap["isApproved"] = true  // admins are always approved
                }
                batch.update(userRef, roleUpdateMap)

                // 2. If demoting teacher → student: unassign from all classes
                if (currentRole == "teacher" && newRole == "student") {
                    val classes = db.collection("classes")
                        .whereEqualTo("teacherId", targetUserId).get().await()
                    classes.documents.forEach { doc ->
                        batch.update(doc.reference, mapOf(
                            "teacherId" to "",
                            "teacherName" to "[Unassigned]",
                            "studentIds" to FieldValue.arrayUnion(targetUserId)
                        ))
                    }
                }

                // 3. If promoting student → teacher: remove from enrolled classes
                if (currentRole == "student" && newRole == "teacher") {
                    val classes = db.collection("classes")
                        .whereArrayContains("studentIds", targetUserId).get().await()
                    classes.documents.forEach { doc ->
                        batch.update(doc.reference, "studentIds", FieldValue.arrayRemove(targetUserId))
                    }
                }

                batch.commit().await()

                // 4. Log role change
                if (institutionId.isNotEmpty()) {
                    val adminUser = authRepository.getUserById(Firebase.auth.currentUser?.uid ?: "")
                    val log = RoleChangeLog(
                        changedByAdminId = adminUser?.uid ?: "",
                        changedByAdminName = adminUser?.name ?: "",
                        targetUserId = targetUserId,
                        targetUserName = targetUserName,
                        fromRole = currentRole,
                        toRole = newRole,
                        reason = reason
                    )
                    db.collection("institutions").document(institutionId)
                        .collection("roleChangeLogs").add(log).await()
                }

                // 5. Send FCM notification to target user
                sendRoleChangeNotification(targetUserId, newRole)

                _roleChangeState.value = NetworkResult.Success(newRole)
            } catch (e: Exception) {
                _roleChangeState.value = NetworkResult.Error(e.message ?: "Failed to change role")
            }
        }
    }

    private suspend fun sendRoleChangeNotification(targetUserId: String, newRole: String) {
        try {
            val db = Firebase.firestore
            val notif = mapOf(
                "title" to "Your role has been updated",
                "body" to "Your account role has been changed to $newRole by your institution admin.",
                "type" to "role_change",
                "referenceId" to "",
                "isRead" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("notifications").document(targetUserId)
                .collection("items").add(notif).await()
        } catch (_: Exception) { /* notification failure is non-critical */ }
    }
}
