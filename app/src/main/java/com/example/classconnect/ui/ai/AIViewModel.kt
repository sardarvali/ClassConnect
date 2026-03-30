package com.syed.classconnect.ui.ai

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.data.model.Assignment
import com.syed.classconnect.data.model.ChatMessage
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.data.model.Quiz
import com.syed.classconnect.data.model.QuizQuestion
import com.syed.classconnect.data.repository.AssignmentRepository
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.ChatRepository
import com.syed.classconnect.data.repository.ClassRepository
import com.syed.classconnect.data.repository.GeminiRepository
import com.syed.classconnect.data.repository.QuizRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.Calendar
import javax.inject.Inject

data class AIChatMessage(val text: String, val isUser: Boolean)

data class AIQuizSuggestion(
    val title: String,
    val description: String,
    val questions: List<QuizQuestion>
)

/** One-shot result event for share / create actions */
sealed class AIActionResult {
    object Loading : AIActionResult()
    data class Success(val message: String) : AIActionResult()
    data class Error(val message: String) : AIActionResult()
}

@HiltViewModel
class AIViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val classRepository: ClassRepository,
    private val chatRepository: ChatRepository,
    private val assignmentRepository: AssignmentRepository,
    private val quizRepository: QuizRepository,
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    // ── classes available to current user (teacher-owned or student-enrolled) ──
    private val _availableClasses = MutableLiveData<List<ClassRoom>>(emptyList())
    val availableClasses: LiveData<List<ClassRoom>> = _availableClasses
    // Backward-compatible alias used by existing UI code.
    val teacherClasses: LiveData<List<ClassRoom>> = availableClasses
    private var classesJob: Job? = null

    // ── one-shot action result ───────────────────────────────────────────────
    private val _actionResult = MutableLiveData<AIActionResult?>()
    val actionResult: LiveData<AIActionResult?> = _actionResult

    fun clearActionResult() { _actionResult.value = null }

    // ── User role (for teacher-only features) ────────────────────────────────
    private val _userRole = MutableLiveData<String>("student")
    val userRole: LiveData<String> = _userRole

    fun loadUserRole(uid: String) {
        viewModelScope.launch {
            _userRole.value = authRepository.getUserById(uid)?.role ?: "student"
            loadClassesForCurrentUser()
        }
    }

    // ── AI content variants (for assignment/quiz suggestions) ────────────────
    fun generateContentVariants(
        type: String,          // "assignment" | "quiz"
        context: String,       // current title + description
        onResult: (List<Pair<String, String>>) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val prompt = when (type) {
                "quiz" -> """
                    You are a teacher assistant. Given this quiz context: $context
                    Generate exactly 3 different quiz topic variations.
                    Return ONLY a JSON array, no markdown:
                    [{"title":"...","description":"..."},{"title":"...","description":"..."},{"title":"...","description":"..."}]
                """.trimIndent()
                else -> """
                    You are a teacher assistant. Given this assignment: $context
                    Generate exactly 3 improved/varied versions of this assignment.
                    Return ONLY a JSON array, no markdown:
                    [{"title":"...","description":"..."},{"title":"...","description":"..."},{"title":"...","description":"..."}]
                """.trimIndent()
            }
            val result = geminiRepository.generateContent(emptyList(), prompt)
            _isLoading.value = false
            when (result) {
                is NetworkResult.Success -> onResult(parseVariants(result.data))
                is NetworkResult.Error   -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun generateQuizSuggestionVariants(
        context: String,
        onResult: (List<AIQuizSuggestion>) -> Unit
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val prompt = """
                You are helping a teacher build a classroom quiz.
                Using this quiz context:
                $context

                Generate exactly 3 different quiz options.
                Return ONLY a JSON array, no markdown and no explanation.
                Each object must have:
                - "title": string
                - "description": string
                - "questions": array with 4 to 6 objects

                Each question object must have:
                - "question": string
                - "options": array of exactly 4 short strings
                - "correctIndex": integer from 0 to 3

                Example:
                [
                  {
                    "title": "Linear Search Basics",
                    "description": "Checks understanding of the linear search algorithm.",
                    "questions": [
                      {"question": "...", "options": ["...", "...", "...", "..."], "correctIndex": 0}
                    ]
                  }
                ]
            """.trimIndent()
            val result = geminiRepository.generateContent(emptyList(), prompt)
            _isLoading.value = false
            when (result) {
                is NetworkResult.Success -> onResult(parseQuizSuggestions(result.data))
                is NetworkResult.Error -> _error.value = result.message
                else -> Unit
            }
        }
    }

    private fun parseVariants(raw: String): List<Pair<String, String>> {
        return try {
            val cleaned = cleanJsonPayload(raw)
            val arr = org.json.JSONArray(cleaned)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                obj.getString("title") to obj.getString("description")
            }
        } catch (_: Exception) { emptyList() }
    }

    private fun parseQuizSuggestions(raw: String): List<AIQuizSuggestion> {
        return try {
            val arr = JSONArray(cleanJsonPayload(raw))
            (0 until arr.length()).mapNotNull { index ->
                val obj = arr.getJSONObject(index)
                val questions = parseQuizQuestions(obj.getJSONArray("questions").toString())
                if (questions.isEmpty()) {
                    null
                } else {
                    AIQuizSuggestion(
                        title = obj.optString("title").ifBlank { "AI Quiz ${index + 1}" },
                        description = obj.optString("description"),
                        questions = questions
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private val _messages = MutableLiveData<MutableList<AIChatMessage>>(mutableListOf())
    val messages: LiveData<MutableList<AIChatMessage>> = _messages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val history = mutableListOf<GeminiRepository.Message>()
    private val systemPrompt = "You are an AI Study Buddy for a student. Help with explanations, practice problems, concept summaries, and study tips. Be concise, friendly, and educational."

    fun sendMessage(userText: String) {
        val list = _messages.value ?: mutableListOf()
        list.add(AIChatMessage(userText, isUser = true))
        _messages.value = list
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = geminiRepository.generateContent(
                history = listOf(GeminiRepository.Message("user", systemPrompt)) + history,
                prompt = userText
            )
            _isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    history.add(GeminiRepository.Message("user", userText))
                    history.add(GeminiRepository.Message("model", result.data))
                    val updated = _messages.value ?: mutableListOf()
                    updated.add(AIChatMessage(result.data, isUser = false))
                    _messages.value = updated
                }
                is NetworkResult.Error -> {
                    if (result.code == 429) {
                        delay(2000)
                        _error.value = result.message
                    } else {
                        _error.value = result.message
                    }
                }
                else -> {}
            }
        }
    }

    fun clearConversation() {
        history.clear()
        _messages.value = mutableListOf()
    }

    // Lesson planner
    fun generateLessonPlan(subject: String, topic: String, grade: String, duration: String, objectives: String, onResult: (String) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val prompt = """
                Generate a detailed lesson plan for:
                Subject: $subject
                Topic: $topic
                Grade/Level: $grade
                Duration: $duration minutes
                Objectives: $objectives
                
                Include: Learning Objectives, Required Materials, Introduction (5 min), 
                Main Activity breakdown with timing, Assessment method, Homework/Follow-up
                Format the response with clear sections.
            """.trimIndent()
            val result = geminiRepository.generateContent(emptyList(), prompt)
            _isLoading.value = false
            when (result) {
                is NetworkResult.Success -> onResult(result.data)
                is NetworkResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    // Quiz AI generation
    fun generateQuizQuestions(topic: String, onResult: (String) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val prompt = """
                Generate exactly 5 multiple-choice questions about "$topic".
                Return ONLY a JSON array, no markdown, no explanation.
                Each element must have:
                  "question": string,
                  "options": array of exactly 4 strings (plain text, no "A)" prefix),
                  "correctIndex": integer 0-3
                Example:
                [{"question":"...","options":["opt1","opt2","opt3","opt4"],"correctIndex":0}]
            """.trimIndent()
            val result = geminiRepository.generateContent(emptyList(), prompt)
            _isLoading.value = false
            when (result) {
                is NetworkResult.Success -> onResult(result.data)
                is NetworkResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    // ── Load classes for current role ────────────────────────────────────────
    fun loadClassesForCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        classesJob?.cancel()
        classesJob = viewModelScope.launch {
            try {
                val role = authRepository.getUserById(uid)?.role ?: "student"
                _userRole.value = role
                val flow = if (role == "teacher" || role == "admin") {
                    classRepository.getClassesForTeacher(uid)
                } else {
                    classRepository.getClassesForStudent(uid)
                }
                flow.collect { list -> _availableClasses.value = list }
            } catch (_: Exception) {
                _availableClasses.value = emptyList()
            }
        }
    }

    // ── Legacy entrypoint kept for existing calls ────────────────────────────
    fun loadTeacherClasses() {
        loadClassesForCurrentUser()
    }

    // ── Share AI text to a class chat ─────────────────────────────────────────
    fun shareToChat(classId: String, content: String) {
        val user = auth.currentUser ?: return
        _actionResult.value = AIActionResult.Loading
        viewModelScope.launch {
            val userModel = authRepository.getUserById(user.uid)
            val msg = ChatMessage(
                senderId = user.uid,
                senderName = userModel?.name ?: user.displayName ?: "Teacher",
                senderPhotoUrl = userModel?.photoUrl ?: "",
                text = "📚 AI Generated Content:\n\n$content",
                timestamp = Timestamp.now()
            )
            val result = chatRepository.sendMessage(classId, msg)
            _actionResult.value = result.fold(
                onSuccess = { AIActionResult.Success("Shared to class chat ✓") },
                onFailure = { AIActionResult.Error(it.message ?: "Failed to share") }
            )
        }
    }

    // ── Create assignment from AI content ────────────────────────────────────
    fun createAssignmentFromAI(
        classId: String,
        title: String,
        description: String,
        marks: Int
    ) {
        _actionResult.value = AIActionResult.Loading
        val due = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 7) }
        viewModelScope.launch {
            val assignment = Assignment(
                title = title,
                description = description,
                dueDate = Timestamp(due.time),
                totalMarks = marks,
                createdAt = Timestamp.now()
            )
            val result = assignmentRepository.createAssignment(classId, assignment)
            _actionResult.value = result.fold(
                onSuccess = { AIActionResult.Success("Assignment created ✓") },
                onFailure = { AIActionResult.Error(it.message ?: "Failed to create assignment") }
            )
        }
    }

    fun createAssignmentAndPostFromAI(
        classId: String,
        title: String,
        description: String,
        marks: Int
    ) {
        val user = auth.currentUser ?: return
        _actionResult.value = AIActionResult.Loading
        val due = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 7) }
        viewModelScope.launch {
            val assignment = Assignment(
                title = title,
                description = description,
                dueDate = Timestamp(due.time),
                totalMarks = marks,
                createdAt = Timestamp.now()
            )
            val createResult = assignmentRepository.createAssignment(classId, assignment)
            createResult.fold(
                onSuccess = {
                    val userModel = authRepository.getUserById(user.uid)
                    val senderName = userModel?.name ?: user.displayName ?: "User"
                    val msg = ChatMessage(
                        senderId = user.uid,
                        senderName = senderName,
                        senderPhotoUrl = userModel?.photoUrl ?: "",
                        text = "New AI assignment posted: $title",
                        timestamp = Timestamp.now()
                    )
                    val chatResult = chatRepository.sendMessage(classId, msg)
                    _actionResult.value = chatResult.fold(
                        onSuccess = { AIActionResult.Success("Assignment created and posted to class chat ✓") },
                        onFailure = { AIActionResult.Error("Assignment created, but chat post failed") }
                    )
                },
                onFailure = {
                    _actionResult.value = AIActionResult.Error(it.message ?: "Failed to create assignment")
                }
            )
        }
    }

    // ── Generate quiz via AI, then save it ──────────────────────────────────
    fun createQuizFromAI(
        classId: String,
        topic: String,
        quizTitle: String,
        durationMinutes: Int,
        sourceContent: String = ""
    ) {
        _actionResult.value = AIActionResult.Loading
        viewModelScope.launch {
            val promptContext = sourceContent.ifBlank { topic }
            val prompt = """
                You are creating a classroom quiz from this AI-generated lesson material:
                $promptContext

                Focus area/title: "$topic"

                Generate exactly 5 multiple-choice questions that assess understanding of the content above.
                Return ONLY a JSON array, no markdown, no extra text.
                Each element must be in this format:
                {"question":"...","options":["opt1","opt2","opt3","opt4"],"correctIndex":0}
                Make options concise, clear, and classroom appropriate.
            """.trimIndent()
            val genResult = geminiRepository.generateContent(emptyList(), prompt)
            if (genResult is NetworkResult.Error) {
                _actionResult.value = AIActionResult.Error(genResult.message)
                return@launch
            }
            val rawJson = (genResult as NetworkResult.Success).data
            val aiQuestions = parseQuizQuestions(rawJson)
            if (aiQuestions.isEmpty()) {
                _actionResult.value = AIActionResult.Error("Could not parse quiz questions. Try again.")
                return@launch
            }
            val quiz = Quiz(
                title = quizTitle,
                description = "AI Generated Quiz for $topic",
                durationMinutes = durationMinutes,
                totalMarks = aiQuestions.sumOf { it.marks },
                published = false,
                questions = aiQuestions
            )
            quizRepository.createQuiz(classId, quiz).fold(
                onSuccess = { AIActionResult.Success("Quiz created as draft ✓ — publish it from the Quiz tab") },
                onFailure = { AIActionResult.Error(it.message ?: "Failed to create quiz") }
            ).also { _actionResult.value = it }
        }
    }

    private fun parseQuizQuestions(raw: String): List<QuizQuestion> {
        return try {
            val cleaned = cleanJsonPayload(raw)
            val arr = JSONArray(cleaned)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val optArr = obj.getJSONArray("options")
                val opts = (0 until optArr.length()).map { optArr.getString(it) }
                QuizQuestion(
                    question = obj.getString("question"),
                    options = opts,
                    correctIndex = obj.optInt("correctIndex", 0),
                    marks = 1
                )
            }
        } catch (_: Exception) { emptyList() }
    }

    private fun cleanJsonPayload(raw: String): String {
        val trimmed = raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val firstArray = trimmed.indexOf('[')
        val lastArray = trimmed.lastIndexOf(']')
        if (firstArray >= 0 && lastArray > firstArray) {
            return trimmed.substring(firstArray, lastArray + 1)
        }
        val firstObject = trimmed.indexOf('{')
        val lastObject = trimmed.lastIndexOf('}')
        return if (firstObject >= 0 && lastObject > firstObject) {
            trimmed.substring(firstObject, lastObject + 1)
        } else {
            trimmed
        }
    }
}


