package com.syed.classconnect.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.syed.classconnect.data.model.RoleChangeLog
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RoleChangeHistoryViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

<<<<<<< HEAD
    private val _logs = MutableStateFlow<NetworkResult<List<RoleChangeLog>>>(NetworkResult.Loading())
=======
    private val _logs =
        MutableStateFlow<NetworkResult<List<RoleChangeLog>>>(NetworkResult.Loading())
>>>>>>> final
    val logs: StateFlow<NetworkResult<List<RoleChangeLog>>> = _logs

    fun loadLogs() {
        viewModelScope.launch {
            _logs.value = NetworkResult.Loading()
            try {
                val adminUid = Firebase.auth.currentUser?.uid ?: return@launch
                val adminUser = authRepository.getUserById(adminUid) ?: return@launch
                val instId = adminUser.institutionId.ifEmpty { return@launch }
                val snap = Firebase.firestore
                    .collection("institutions").document(instId)
                    .collection("roleChangeLogs")
                    .orderBy("changedAt", Query.Direction.DESCENDING)
                    .get().await()
<<<<<<< HEAD
                val list = snap.documents.mapNotNull { it.toObject(RoleChangeLog::class.java)?.copy(id = it.id) }
=======
                val list = snap.documents.mapNotNull {
                    it.toObject(RoleChangeLog::class.java)?.copy(id = it.id)
                }
>>>>>>> final
                _logs.value = NetworkResult.Success(list)
            } catch (e: Exception) {
                _logs.value = NetworkResult.Error(e.message ?: "Failed to load logs")
            }
        }
    }
}

