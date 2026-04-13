package com.syed.classconnect.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.classconnect.data.model.Quiz
import com.syed.classconnect.data.model.QuizAttempt
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.QuizRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _quizzes = MutableLiveData<NetworkResult<List<Quiz>>>()
    val quizzes: LiveData<NetworkResult<List<Quiz>>> = _quizzes

    private val _quizDetail = MutableLiveData<Quiz?>()
    val quizDetail: LiveData<Quiz?> = _quizDetail

    private val _attempt = MutableLiveData<QuizAttempt?>()
    val attempt: LiveData<QuizAttempt?> = _attempt

    private val _submitResult = MutableLiveData<NetworkResult<Unit>>()
    val submitResult: LiveData<NetworkResult<Unit>> = _submitResult

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> = _userRole

    private val _allAttempts = MutableLiveData<NetworkResult<List<QuizAttempt>>>()
    val allAttempts: LiveData<NetworkResult<List<QuizAttempt>>> = _allAttempts

    fun loadUserRole(uid: String) {
        viewModelScope.launch {
            _userRole.value = authRepository.getUserById(uid)?.role ?: "student"
        }
    }

<<<<<<< HEAD
    fun loadQuizzes(classId: String) {
        _quizzes.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                val role = _userRole.value ?: "student"
=======
    fun loadQuizzes(classId: String, forRole: String? = null) {
        _quizzes.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                val role = forRole ?: _userRole.value ?: "student"
>>>>>>> final
                val flow = if (role == "teacher" || role == "admin") {
                    quizRepository.getQuizzes(classId)
                } else {
                    quizRepository.getQuizzesForStudent(classId)
                }
                flow.collect { list ->
                    _quizzes.value = NetworkResult.Success(list)
                }
            } catch (e: Exception) {
                _quizzes.value = NetworkResult.Error(e.message ?: "Failed to load")
            }
        }
    }

    fun loadQuizDetail(classId: String, quizId: String) {
        viewModelScope.launch {
            _quizDetail.value = quizRepository.getQuizById(classId, quizId)
        }
    }

    fun loadAttempt(classId: String, quizId: String, studentId: String) {
        viewModelScope.launch {
            _attempt.value = quizRepository.getAttempt(classId, quizId, studentId)
        }
    }

    fun submitAttempt(classId: String, quizId: String, attempt: QuizAttempt) {
        _submitResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = quizRepository.submitAttempt(classId, quizId, attempt)
            _submitResult.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: "Submit failed") }
            )
        }
    }

    fun createQuiz(classId: String, quiz: Quiz) {
        viewModelScope.launch { quizRepository.createQuiz(classId, quiz) }
    }

<<<<<<< HEAD
=======
    fun updateQuiz(classId: String, quiz: Quiz) {
        viewModelScope.launch { quizRepository.updateQuiz(classId, quiz) }
    }

>>>>>>> final
    fun loadAllAttempts(classId: String, quizId: String) {
        _allAttempts.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                quizRepository.getAllAttempts(classId, quizId).collect { list ->
                    _allAttempts.value = NetworkResult.Success(list)
                }
            } catch (e: Exception) {
                _allAttempts.value = NetworkResult.Error(e.message ?: "Failed")
            }
        }
    }

    private val _deleteResult = MutableLiveData<NetworkResult<Unit>>()
    val deleteResult: LiveData<NetworkResult<Unit>> = _deleteResult

    fun deleteQuiz(classId: String, quizId: String) {
        _deleteResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = quizRepository.deleteQuiz(classId, quizId)
            _deleteResult.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: "Delete failed") }
            )
        }
    }

    fun publishQuiz(classId: String, quizId: String, publish: Boolean) {
        viewModelScope.launch {
            quizRepository.publishQuiz(classId, quizId, publish)
        }
    }

    /** Check if a student has already submitted (non-draft) an attempt for a quiz */
    fun hasStudentAttempted(
        classId: String,
        quizId: String,
        studentId: String,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val attempt = quizRepository.getAttempt(classId, quizId, studentId)
                callback(attempt != null)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    private val _attemptedQuizIds = MutableLiveData<Set<String>>(emptySet())
    val attemptedQuizIds: LiveData<Set<String>> = _attemptedQuizIds

    /** Load which quizzes the student has already attempted */
    fun loadAttemptedQuizIds(classId: String, studentId: String) {
        viewModelScope.launch {
            try {
<<<<<<< HEAD
                val quizSnap = quizRepository.getQuizzes(classId)
=======
                val quizSnap = quizRepository.getQuizzesForStudent(classId)
>>>>>>> final
                quizSnap.collect { quizzes ->
                    val attempted = mutableSetOf<String>()
                    quizzes.forEach { quiz ->
                        val attempt = quizRepository.getAttempt(classId, quiz.id, studentId)
                        if (attempt != null) attempted.add(quiz.id)
                    }
                    _attemptedQuizIds.value = attempted
                }
            } catch (_: Exception) {
                _attemptedQuizIds.value = emptySet()
            }
        }
    }
}

