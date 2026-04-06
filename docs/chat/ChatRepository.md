# ChatRepository — Data access for chat messages

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/repository/ChatRepository.kt`

---

## 🎯 What This File Does
ChatRepository handles all Firestore operations for class chat: real-time message retrieval, sending, soft-deleting, reactions, and clearing all messages.

---

## ⚙️ Key Functions

| Function | Returns | Description |
|----------|---------|-------------|
| `getMessages(classId)` | `Flow<List<ChatMessage>>` | Real-time messages ordered by timestamp |
| `sendMessage(classId, message)` | `Result<Unit>` | Adds message document |
| `deleteMessage(classId, messageId)` | `Result<Unit>` | Soft delete: sets isDeleted=true, clears text |
| `addReaction(classId, messageId, userId, emoji)` | `Result<Unit>` | Updates reactions map |
| `clearAllMessages(classId)` | `Result<Unit>` | Batch deletes all messages |

---

## ⚠️ Important Notes
- Messages are ordered by `timestamp` ascending (oldest first, newest at bottom)
- `deleteMessage()` is a SOFT delete — sets `isDeleted=true` and replaces text, doesn't remove document
- `clearAllMessages()` batches deletes in chunks (Firestore batch limit: 500)
- `getMessages()` uses `callbackFlow` with `awaitClose { sub.remove() }` to prevent listener leaks

