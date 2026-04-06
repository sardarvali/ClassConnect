# ChatFragment — Real-time class chat room

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/chat/ChatFragment.kt`

---

## 🎯 What This File Does
ChatFragment provides real-time messaging within a class. Features text messages, emoji reactions, message deletion (soft delete), and mute toggle. Messages are ordered by timestamp and auto-scroll to the latest. Teachers can clear all chat history.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.os.Bundle` | Android SDK | Saved state | Fragment lifecycle |
| `android.view.*` | Android SDK | View classes | Fragment inflation |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This class extends Fragment |
| `androidx.fragment.app.viewModels` | AndroidX KTX | ViewModel delegation | `by viewModels()` |
| `androidx.recyclerview.widget.LinearLayoutManager` | AndroidX | RecyclerView layout | Vertical chat message list |
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Auth | Get current user for message sending |
| `com.syed.classconnect.data.model.ChatMessage` | App | Data model | Message data class |
| `com.syed.classconnect.databinding.FragmentChatBinding` | ViewBinding | Type-safe views | Layout binding |
| `com.syed.classconnect.util.*` | App | Extensions | show/hide, toasts, NetworkResult |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | DI | Enables injection |

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
1. Sets up RecyclerView with ChatAdapter + LinearLayoutManager (stackFromEnd=true)
2. Loads current user info and messages
3. Sets send button click listener: validates text → creates ChatMessage → sends
4. Observes messages LiveData for real-time updates
5. Auto-scrolls to bottom on new messages

### Message sending:
Creates `ChatMessage(senderId, senderName, senderPhotoUrl, text, timestamp=Timestamp.now())`
→ `viewModel.sendMessage(classId, message)` → ChatRepository writes to Firestore

### Message actions:
- Long press → delete option (if sender or teacher)
- Tap reaction button → `viewModel.addReaction(classId, messageId, uid, emoji)`
- Teacher: clear chat option → `viewModel.clearChat(classId)`

---

## 🔄 Data Flow
```
ChatFragment.onViewCreated()
    → viewModel.loadMessages(classId)
    → ChatRepository.getMessages(classId) → callbackFlow with addSnapshotListener
    → Real-time message list → ChatAdapter updates → auto-scroll to bottom

User types message → taps send
    → viewModel.sendMessage(classId, chatMessage)
    → ChatRepository.sendMessage() → Firestore add()
    → SnapshotListener fires → new message appears in list
```

---

## 🧩 This File Depends On

| Dependency | Why |
|-----------|-----|
| `ChatViewModel` | Message loading, sending, deletion, reactions |
| `ChatAdapter` | Displays message bubbles |
| `ChatRepository` | Firestore message operations |

