# ChatViewModel — Chat state management

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/chat/ChatViewModel.kt`

---

## 🎯 What This File Does
ChatViewModel manages real-time chat state: message loading, sending, deletion, reactions, mute toggle, and chat clearing. Collects messages via Flow from ChatRepository.

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `messages` | `LiveData<NetworkResult<List<ChatMessage>>>` | Real-time message list |
| `currentUser` | `LiveData<User?>` | Current user info for sending |
| `classDetail` | `LiveData<ClassRoom?>` | Class info (for permissions) |
| `isMuted` | `LiveData<Boolean>` | Chat notification mute state |
| `clearResult` | `LiveData<NetworkResult<Unit>>` | Clear chat result |

---

## ⚙️ Key Functions
- `loadMessages(classId)`: Collects real-time Flow from ChatRepository
- `sendMessage(classId, message)`: Writes message to Firestore
- `deleteMessage(classId, messageId)`: Soft-deletes message
- `addReaction(classId, messageId, userId, emoji)`: Adds emoji reaction
- `clearChat(classId)`: Deletes all messages (teacher only)
- `toggleMute()`: Toggles local mute flag

