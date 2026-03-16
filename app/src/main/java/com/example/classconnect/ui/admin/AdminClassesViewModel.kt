package com.syed.classconnect.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.data.model.User
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.ClassRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminClassesViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _classes = MutableStateFlow<NetworkResult<List<ClassRoom>>>(NetworkResult.Loading())
    val classes: StateFlow<NetworkResult<List<ClassRoom>>> = _classes

    private val _teachers = MutableStateFlow<NetworkResult<List<User>>>(NetworkResult.Loading())
    val teachers: StateFlow<NetworkResult<List<User>>> = _teachers

    private val _assignState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val assignState: StateFlow<NetworkResult<Unit>?> = _assignState

    private var institutionId: String = ""
    private var cachedClasses: List<ClassRoom> = emptyList()
    private var showUnassigned = false

    fun loadData() {
        viewModelScope.launch {
            val uid = Firebase.auth.currentUser?.uid ?: return@launch
            val admin = authRepository.getUserById(uid) ?: return@launch
            institutionId = admin.institutionId
            classRepository.getAllClasses(institutionId).collect { list ->
                cachedClasses = list
                applyFilter()
            }
        }
    }

    fun setFilter(unassignedOnly: Boolean) {
        showUnassigned = unassignedOnly
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = if (showUnassigned) cachedClasses.filter { it.teacherId.isNullOrEmpty() || it.teacherName == "[Unassigned]" }
                       else cachedClasses
        _classes.value = NetworkResult.Success(filtered)
    }

    fun loadTeachersForInstitution() {
        viewModelScope.launch {
            try {
                classRepository.getUsersForInstitution(institutionId, "teacher").collect { list ->
                    _teachers.value = NetworkResult.Success(list)
                }
            } catch (e: Exception) {
                _teachers.value = NetworkResult.Error(e.message ?: "Failed")
            }
        }
    }

    fun assignTeacher(classId: String, className: String, teacher: User) {
        viewModelScope.launch {
            _assignState.value = NetworkResult.Loading()
            try {
                val db = Firebase.firestore
                db.collection("classes").document(classId)
                    .update(mapOf("teacherId" to teacher.uid, "teacherName" to teacher.name)).await()
                db.collection("users").document(teacher.uid)
                    .update("classIds", FieldValue.arrayUnion(classId)).await()
                // Notification
                val notif = mapOf(
                    "title" to "You've been assigned to a class",
                    "body" to "You are now the teacher for $className.",
                    "type" to "assignment", "referenceId" to classId,
                    "isRead" to false, "createdAt" to FieldValue.serverTimestamp()
                )
                db.collection("notifications").document(teacher.uid)
                    .collection("items").add(notif).await()
                _assignState.value = NetworkResult.Success(Unit)
            } catch (e: Exception) {
                _assignState.value = NetworkResult.Error(e.message ?: "Failed to assign teacher")
            }
        }
    }
}
