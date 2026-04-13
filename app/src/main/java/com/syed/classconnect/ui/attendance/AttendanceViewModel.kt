package com.syed.classconnect.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.classconnect.data.model.AttendanceRecord
import com.syed.classconnect.data.repository.AttendanceRepository
import com.syed.classconnect.data.repository.AuthRepository
<<<<<<< HEAD
import com.syed.classconnect.util.DateUtils.todayIsoString
=======
import com.syed.classconnect.data.repository.ClassRepository
import com.syed.classconnect.util.ScheduleUtils
>>>>>>> final
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
<<<<<<< HEAD
    private val authRepository: AuthRepository
=======
    private val authRepository: AuthRepository,
    private val classRepository: ClassRepository
>>>>>>> final
) : ViewModel() {

    private val _attendanceRecord = MutableLiveData<AttendanceRecord?>()
    val attendanceRecord: LiveData<AttendanceRecord?> = _attendanceRecord

    private val _markResult = MutableLiveData<Result<Unit>?>()
    val markResult: LiveData<Result<Unit>?> = _markResult

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> = _userRole

    private val _history = MutableLiveData<List<AttendanceRecord>>()
    val history: LiveData<List<AttendanceRecord>> = _history

<<<<<<< HEAD
=======
    private val _attendanceError = MutableLiveData<String?>()
    val attendanceError: LiveData<String?> = _attendanceError

    private val _timingAlertMessage = MutableLiveData<String?>()
    val timingAlertMessage: LiveData<String?> = _timingAlertMessage

>>>>>>> final
    fun loadUserRole(uid: String) {
        viewModelScope.launch {
            _userRole.value = authRepository.getUserById(uid)?.role ?: "student"
        }
    }

    fun startSession(classId: String, teacherId: String) {
        viewModelScope.launch {
<<<<<<< HEAD
=======
            val classRoom = classRepository.getClassById(classId)
            if (classRoom == null) {
                _attendanceError.value = "Class not found"
                return@launch
            }
            if (classRoom.teacherId != teacherId) {
                _attendanceError.value = "Only the assigned teacher can start attendance"
                return@launch
            }
            if (!ScheduleUtils.isNowWithinAnySlot(classRoom.schedule)) {
                _timingAlertMessage.value = classTimingMessage(classRoom.schedule)
                return@launch
            }

>>>>>>> final
            val result = attendanceRepository.startSession(classId, teacherId)
            result.getOrNull()?.let { record ->
                observeSession(classId, record.date)
            }
<<<<<<< HEAD
=======
            result.exceptionOrNull()?.let { _attendanceError.value = it.message ?: "Failed to start session" }
>>>>>>> final
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
<<<<<<< HEAD
=======
            val classRoom = classRepository.getClassById(classId)
            if (classRoom == null) {
                _markResult.value = Result.failure(Exception("Class not found"))
                return@launch
            }
            if (!ScheduleUtils.isNowWithinAnySlot(classRoom.schedule)) {
                _markResult.value = Result.failure(
                    Exception("Attendance can be marked only during scheduled class time")
                )
                return@launch
            }
>>>>>>> final
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
<<<<<<< HEAD
            _history.value = attendanceRepository.getAttendanceHistory(classId)
=======
            val classRoom = classRepository.getClassById(classId)
            val records = attendanceRepository.getAttendanceHistory(classId)
            _history.value = classRoom?.let { room ->
                records.filter { ScheduleUtils.isDateScheduled(room.schedule, it.date) }
            } ?: records
        }
    }

    fun clearAttendanceError() {
        _attendanceError.value = null
    }

    fun clearTimingAlert() {
        _timingAlertMessage.value = null
    }

    private fun classTimingMessage(schedule: Map<String, String>): String {
        val todaySlots = ScheduleUtils.slotsForDay(schedule)
        return if (todaySlots.isNotEmpty()) {
            "Attendance starts only in class timing: ${todaySlots.joinToString(" | ")}"
        } else {
            val weekly = ScheduleUtils.weeklyRows(schedule)
                .joinToString("\n") { (day, slot) -> "$day: $slot" }
            if (weekly.isNotBlank()) {
                "Attendance starts only in class timing:\n$weekly"
            } else {
                "Attendance timing is not configured for this class"
            }
>>>>>>> final
        }
    }
}

