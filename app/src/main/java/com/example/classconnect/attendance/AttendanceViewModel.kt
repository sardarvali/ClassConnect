package com.syed.classconnect.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.classconnect.data.model.AttendanceRecord
import com.syed.classconnect.data.repository.AttendanceRepository
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.util.DateUtils.todayIsoString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _attendanceRecord = MutableLiveData<AttendanceRecord?>()
    val attendanceRecord: LiveData<AttendanceRecord?> = _attendanceRecord

    private val _markResult = MutableLiveData<Result<Unit>?>()
    val markResult: LiveData<Result<Unit>?> = _markResult

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> = _userRole

    private val _history = MutableLiveData<List<AttendanceRecord>>()
    val history: LiveData<List<AttendanceRecord>> = _history

    fun loadUserRole(uid: String) {
        viewModelScope.launch {
            _userRole.value = authRepository.getUserById(uid)?.role ?: "student"
        }
    }

    fun startSession(classId: String, teacherId: String) {
        viewModelScope.launch {
            val result = attendanceRepository.startSession(classId, teacherId)
            result.getOrNull()?.let { record ->
                observeSession(classId, record.date)
            }
        }
    }

    private fun observeSession(classId: String, date: String) {
        viewModelScope.launch {
            attendanceRepository.observeSession(classId, date).collect { record ->
                _attendanceRecord.value = record
            }
        }
    }

    fun markPresent(classId: String, date: String, studentId: String) {
        viewModelScope.launch {
            _markResult.value = attendanceRepository.markPresent(classId, date, studentId)
        }
    }

    fun endSession(classId: String, date: String, allStudentIds: List<String>) {
        viewModelScope.launch {
            attendanceRepository.endSession(classId, date, allStudentIds)
            _attendanceRecord.value = null
        }
    }

    fun loadHistory(classId: String) {
        viewModelScope.launch {
            _history.value = attendanceRepository.getAttendanceHistory(classId)
        }
    }
}

