# AIBuddyViewModel — See AIViewModel

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/ai/AIViewModel.kt`

---

The project uses a single `AIViewModel` for all AI features. `AIBuddyViewModel` is not a separate class.

See **[AIViewModel.md](AIViewModel.md)** for complete documentation.

### Study Buddy–specific usage

`AIBuddyFragment` uses `AIViewModel` for:
- `sendMessage(userText)` — sends a message and receives a multi-turn AI reply
- `clearConversation()` — resets chat history
- `messages: LiveData<MutableList<AIChatMessage>>` — drives `AIChatAdapter`
- `isLoading: LiveData<Boolean>` — shows/hides typing indicator
- `error: LiveData<String?>` — displays error Snackbar

### `AIChatMessage` data class

Defined at the top of `AIViewModel.kt`:
```kotlin
data class AIChatMessage(val text: String, val isUser: Boolean)
```
- `isUser = true` → right-aligned user bubble (TYPE_USER in AIChatAdapter)
- `isUser = false` → left-aligned AI bubble with Markwon Markdown rendering

Clears message list and history for fresh conversation.

---

## 📝 Data class in this file

```kotlin
data class AIChatMessage(val text: String, val isUser: Boolean)
// Simple message model: text content + whether it's from the user or AI.
// isUser=true → displayed on right side (sent), false → left side (received)
```

---

## ⚠️ Important Notes
- System prompt sets the AI personality: "Study Buddy for students"
- Conversation history is maintained for context (AI remembers previous questions)
- 429 rate limit errors trigger a 2-second delay before showing the error
- Quiz generation asks for JSON format — may need parsing on the UI side

