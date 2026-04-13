# AIViewModel.kt — Shared ViewModel for AI Study Buddy chat, Lesson Planner, and Quiz AI generation

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/ai/AIViewModel.kt`

---

## 🎯 What This File Does
`AIViewModel` is a single ViewModel serving three AI features: the Study Buddy conversational chat (`AIBuddyFragment`), the Lesson Plan generator (`LessonPlannerFragment`), and the AI quiz question generator inside `CreateQuizFragment`. It maintains a rolling conversation `history` list so Gemini receives context from all previous turns — enabling multi-turn dialogue where the AI remembers what was said earlier. A system prompt pre-primes Gemini to behave as an educational assistant. `AIChatMessage` (defined in this file) is the display model used by `AIChatAdapter`. Without this ViewModel, all three AI features would have no business logic.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `androidx.lifecycle.LiveData` | AndroidX | Read-only observable | Public properties |
| `androidx.lifecycle.MutableLiveData` | AndroidX | Writable observable | Private backing fields |
| `androidx.lifecycle.ViewModel` | AndroidX | Lifecycle-aware state | AIViewModel extends it |
| `androidx.lifecycle.viewModelScope` | AndroidX | Coroutine scope | All coroutines |
| `com.syed.classconnect.data.repository.GeminiRepository` | Project | Gemini API calls | `generateContent()` |
| `com.syed.classconnect.util.NetworkResult` | Project | Sealed state class | Wraps Success/Error |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | ViewModel DI marker | `by viewModels()` |
| `kotlinx.coroutines.delay` | Coroutines | Suspends execution | Rate-limit retry wait |
| `kotlinx.coroutines.launch` | Coroutines | Start coroutine | All async calls |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | `@Inject constructor(...)` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `data class AIChatMessage(text, isUser)`
Defined at the top of this file. The display model for one chat bubble:
- `isUser = true` → right-aligned user bubble
- `isUser = false` → left-aligned AI bubble with Markdown rendering

### `private val history = mutableListOf<GeminiRepository.Message>()`
Keeps the full conversation history. Each call to `sendMessage()` appends user and model turns. On the next call, the full history is prepended before the new prompt — giving Gemini context. `clearConversation()` wipes it.

### `private val systemPrompt = "You are an AI Study Buddy..."`
Injected as the very first message (`role = "user"`) in every API call. This "primes" Gemini to behave as an educational assistant rather than a general-purpose one. Gemini sees this as the first user turn before the real conversation starts.

### `_messages.value = list` (mutable list mutation)
`MutableLiveData<MutableList<AIChatMessage>>` is used. Replacing `.value` with the same list reference may not trigger observers in some cases — here a new assignment `_messages.value = list` is always used to guarantee notification.

### `if (result.code == 429)` — rate limiting
HTTP 429 = "Too Many Requests" from the Gemini API (quota exceeded). The ViewModel waits 2 seconds (`delay(2000)`) then posts the error message. This gives the system a chance to cool down.

### `onResult: (String) -> Unit` callback pattern
`generateLessonPlan()` and `generateQuizQuestions()` use callback lambdas instead of LiveData. The caller (`LessonPlannerFragment`, `CreateQuizFragment`) passes a lambda directly. This avoids needing separate LiveData properties for one-shot outputs.

### `trimIndent()`
Removes leading whitespace from a multi-line string template so the API receives a clean prompt without indentation artifacts.

---

## 🏗️ Class Structure
`@HiltViewModel class AIViewModel @Inject constructor(geminiRepository) : ViewModel()`

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_messages` | `MutableLiveData<MutableList<AIChatMessage>>` | `private` | Chat history for display | Drives AIChatAdapter |
| `messages` | `LiveData<MutableList<AIChatMessage>>` | `val` | Read-only | Observed by AIBuddyFragment |
| `_isLoading` | `MutableLiveData<Boolean>` | `private` | Loading flag | Shows/hides loading indicator |
| `isLoading` | `LiveData<Boolean>` | `val` | Read-only | Observed by fragments |
| `_error` | `MutableLiveData<String?>` | `private` | Error message (null = no error) | Drives error Snackbar |
| `error` | `LiveData<String?>` | `val` | Read-only | Observed by fragments |
| `history` | `MutableList<GeminiRepository.Message>` | `private val` | Conversation context for API | Multi-turn context |
| `systemPrompt` | `String` | `private val` | Educational assistant instruction | Primes Gemini behaviour |

---

## ⚙️ Functions

### `sendMessage(userText: String)`
**Purpose:** Send a user message to Gemini and receive an AI reply.
**Step by step:**
1. Appends `AIChatMessage(userText, isUser=true)` to `_messages` — UI shows user bubble immediately.
2. Sets `_isLoading = true`, clears `_error`.
3. Calls `geminiRepository.generateContent(history = [systemPrompt] + history, prompt = userText)`.
4. On `NetworkResult.Success`: appends user + model turns to `history`. Appends AI reply to `_messages`.
5. On `NetworkResult.Error(429)`: waits 2 seconds then sets `_error`.
6. On other error: sets `_error` immediately.

### `clearConversation()`
**Purpose:** Reset the chat to an empty state.
Clears `history` and sets `_messages` to an empty list.

### `generateLessonPlan(subject, topic, grade, duration, objectives, onResult)`
**Purpose:** Generate a structured lesson plan via Gemini.
**Step by step:**
1. Sets `_isLoading = true`.
2. Builds a detailed prompt template with all 5 parameters.
3. Calls `geminiRepository.generateContent(emptyList(), prompt)` — no conversation history needed.
4. On success: calls `onResult(markdownText)` — the fragment renders this with Markwon.
5. On error: sets `_error`.

### `generateQuizQuestions(topic, onResult)`
**Purpose:** Ask Gemini to generate 5 MCQ questions as a JSON array.
**Step by step:**
1. Builds prompt: "Generate 5 multiple choice questions about '${topic}'...".
2. Calls `geminiRepository.generateContent(emptyList(), prompt)`.
3. On success: calls `onResult(jsonText)` — `CreateQuizFragment` parses the JSON to build `QuizQuestion` objects.

---

## 🔄 Data Flow Diagram
```
AIBuddyFragment: user types message, taps Send
        ↓
viewModel.sendMessage("What is photosynthesis?")
        ↓
_messages adds user bubble → UI shows immediately
_isLoading = true → loading indicator visible
        ↓
GeminiRepository.generateContent(history + systemPrompt, prompt)
        ↓
Retrofit POST to Gemini API: gemini-2.0-flash/generateContent
        ↓
NetworkResult.Success(aiText)
        ↓
history updated, _messages adds AI bubble
_isLoading = false → loading indicator hidden
        ↓
AIBuddyFragment.observe(messages) → AIChatAdapter.submitList()
→ New AI bubble rendered with Markwon Markdown
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `GeminiRepository` | Makes all Gemini API calls |
| `NetworkResult` | State wrapping |

---

## ⚠️ Important Notes & Gotchas
- The `history` list grows unbounded — long conversations will eventually send very large payloads to Gemini, increasing cost and latency. A future optimization would trim history to the last N turns.
- `generateLessonPlan` and `generateQuizQuestions` use callback lambdas rather than LiveData. If the Fragment is destroyed before the coroutine completes, the callback fires into a dead Fragment. This is safe in the current usage but could cause subtle issues if the Fragment is recreated.
- `delay(2000)` on 429 errors blocks the coroutine but NOT the UI thread — the UI remains responsive during the wait.
- The system prompt is sent as `role = "user"` (not a separate `"system"` role) because Gemini's `generateContent` endpoint doesn't have a `systemInstruction` field in the basic setup used here.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.ai

// (imports as listed above)

/** One message in the chat — either from the user or from the AI. */
data class AIChatMessage(val text: String, val isUser: Boolean)
// isUser = true  → shown as a right-aligned bubble in AIChatAdapter (TYPE_USER)
// isUser = false → shown as a left-aligned bubble (TYPE_AI), Markdown-rendered

@HiltViewModel
class AIViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _messages = MutableLiveData<MutableList<AIChatMessage>>(mutableListOf())
    val messages: LiveData<MutableList<AIChatMessage>> = _messages
    // Mutable list initialized to empty. Updated by adding new messages.

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    // null = no error; non-null String = show as error Snackbar.

    private val history = mutableListOf<GeminiRepository.Message>()
    // Conversation turns stored as GeminiRepository.Message(role, text).
    // Not exposed — only this VM reads/writes it.

    private val systemPrompt = "You are an AI Study Buddy for a student. Help with explanations, practice problems, concept summaries, and study tips. Be concise, friendly, and educational."
    // Prepended to every API call as the first "user" turn to set Gemini's behaviour.

    fun sendMessage(userText: String) {
        val list = _messages.value ?: mutableListOf()
        list.add(AIChatMessage(userText, isUser = true))
        _messages.value = list
        // Add user bubble immediately — optimistic UI.

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = geminiRepository.generateContent(
                history = listOf(GeminiRepository.Message("user", systemPrompt)) + history,
                // Prepend system prompt + full history so Gemini has context.
                prompt = userText
            )
            _isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    history.add(GeminiRepository.Message("user", userText))
                    history.add(GeminiRepository.Message("model", result.data))
                    // Record both turns in history for context in the next call.
                    val updated = _messages.value ?: mutableListOf()
                    updated.add(AIChatMessage(result.data, isUser = false))
                    _messages.value = updated
                }
                is NetworkResult.Error -> {
                    if (result.code == 429) {
                        delay(2000)
                        // Rate limited — wait 2s before surfacing the error.
                    }
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }

    fun clearConversation() {
        history.clear()
        _messages.value = mutableListOf()
        // Wipes both history (sent to API) and _messages (shown in UI).
    }

    fun generateLessonPlan(subject: String, topic: String, grade: String, duration: String, objectives: String, onResult: (String) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val prompt = """
                Generate a detailed lesson plan for:
                Subject: $subject, Topic: $topic, Grade/Level: $grade
                Duration: $duration minutes, Objectives: $objectives
                Include: Learning Objectives, Materials, Introduction (5 min),
                Main Activity with timing, Assessment, Homework
            """.trimIndent()
            val result = geminiRepository.generateContent(emptyList(), prompt)
            _isLoading.value = false
            when (result) {
                is NetworkResult.Success -> onResult(result.data)
                // onResult called with raw Markdown text → Fragment renders with Markwon.
                is NetworkResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun generateQuizQuestions(topic: String, onResult: (String) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val prompt = "Generate 5 multiple choice questions about '$topic'. For each: question, 4 options (A-D), correct answer. Format as JSON array."
            val result = geminiRepository.generateContent(emptyList(), prompt)
            _isLoading.value = false
            when (result) {
                is NetworkResult.Success -> onResult(result.data)
                // onResult called with JSON string → CreateQuizFragment parses it.
                is NetworkResult.Error -> _error.value = result.message
                else -> {}
            }
        }
    }
}
```

