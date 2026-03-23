package com.syed.classconnect.ui.assignments

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.syed.classconnect.R
import com.syed.classconnect.data.model.Assignment
import com.syed.classconnect.data.model.Submission
import com.syed.classconnect.data.repository.AssignmentRepository
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentsViewModel @Inject constructor(
    private val assignmentRepository: AssignmentRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _assignments = MutableLiveData<NetworkResult<List<Assignment>>>()
    val assignments: LiveData<NetworkResult<List<Assignment>>> = _assignments

    private val _submission = MutableLiveData<Submission?>()
    val submission: LiveData<Submission?> = _submission

    private val _submitResult = MutableLiveData<NetworkResult<Unit>>()
    val submitResult: LiveData<NetworkResult<Unit>> = _submitResult

    private val _gradeResult = MutableLiveData<NetworkResult<Unit>>()
    val gradeResult: LiveData<NetworkResult<Unit>> = _gradeResult

    private val _allSubmissions = MutableLiveData<NetworkResult<List<Submission>>>()
    val allSubmissions: LiveData<NetworkResult<List<Submission>>> = _allSubmissions

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> = _userRole

    private val _assignmentDetail = MutableLiveData<Assignment?>()
    val assignmentDetail: LiveData<Assignment?> = _assignmentDetail

    fun loadUserRole(uid: String) {
        viewModelScope.launch {
            _userRole.value = authRepository.getUserById(uid)?.role ?: "student"
        }
    }

    fun loadAssignments(classId: String) {
        _assignments.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                assignmentRepository.getAssignments(classId).collect { list ->
                    _assignments.value = NetworkResult.Success(list)
                }
            } catch (e: Exception) {
                _assignments.value = NetworkResult.Error(e.message ?: context.getString(R.string.error_failed_to_load))
            }
        }
    }

    fun loadAssignmentDetail(classId: String, assignmentId: String) {
        viewModelScope.launch {
            _assignmentDetail.value = assignmentRepository.getAssignmentById(classId, assignmentId)
        }
    }

    fun createAssignment(classId: String, assignment: Assignment) {
        viewModelScope.launch {
            assignmentRepository.createAssignment(classId, assignment)
        }
    }

    fun updateAssignment(classId: String, assignment: Assignment) {
        viewModelScope.launch {
            assignmentRepository.updateAssignment(classId, assignment)
        }
    }

    fun loadSubmission(classId: String, assignmentId: String, studentId: String) {
        viewModelScope.launch {
            _submission.value = assignmentRepository.getSubmission(classId, assignmentId, studentId)
        }
    }

    fun submitAssignment(classId: String, assignmentId: String, submission: Submission) {
        _submitResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = assignmentRepository.submitAssignment(classId, assignmentId, submission)
            _submitResult.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: context.getString(R.string.error_submit_failed)) }
            )
        }
    }

    fun loadAllSubmissions(classId: String, assignmentId: String) {
        _allSubmissions.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                assignmentRepository.getAllSubmissions(classId, assignmentId).collect { list ->
                    _allSubmissions.value = NetworkResult.Success(list)
                }
            } catch (e: Exception) {
                _allSubmissions.value = NetworkResult.Error(e.message ?: context.getString(R.string.error_failed_to_load))
            }
        }
    }

    fun gradeSubmission(classId: String, assignmentId: String, studentId: String, grade: Int, feedback: String) {
        _gradeResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = assignmentRepository.gradeSubmission(classId, assignmentId, studentId, grade, feedback)
            _gradeResult.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: context.getString(R.string.error_grade_failed)) }
            )
        }
    }

    private val _deleteResult = MutableLiveData<NetworkResult<Unit>>()
    val deleteResult: LiveData<NetworkResult<Unit>> = _deleteResult

    fun deleteAssignment(classId: String, assignmentId: String) {
        _deleteResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = assignmentRepository.deleteAssignment(classId, assignmentId)
            _deleteResult.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: context.getString(R.string.error_delete_failed)) }
            )
        }
    }
}
