# ChatAdapter.kt — RecyclerView adapter for class chat with sent/received bubbles and long-press actions

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/chat/ChatAdapter.kt`

---

## 🎯 What This File Does
`ChatAdapter` renders the real-time chat message list inside `ChatFragment`. It has two view types: sent messages (from the current user, right-aligned) and received messages (from other users, left-aligned with sender name and avatar). Soft-deleted messages show "This message was deleted" in italics instead of the original text. Long-pressing any message calls `onLongPress(message)` which opens a bottom sheet for delete/react actions. Without this adapter, the chat tab shows no messages.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | Inflate sent/received layouts |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` param |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` diffing |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter with DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | Two ViewHolder classes |
| `com.syed.classconnect.data.model.ChatMessage` | Project | Message data class | Item type |
| `com.syed.classconnect.databinding.ItemMessageReceivedBinding` | ViewBinding | `item_message_received.xml` | Received bubble layout |
| `com.syed.classconnect.databinding.ItemMessageSentBinding` | ViewBinding | `item_message_sent.xml` | Sent bubble layout |
| `com.syed.classconnect.util.DateUtils.toRelativeTime` | Project | Timestamp → "2 min ago" | Message timestamp |
| `com.syed.classconnect.util.loadAvatar` | Project | Glide extension | Sender avatar circle image |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `currentUserId: String` constructor parameter
Passed in from `ChatFragment` using `auth.currentUser?.uid`. Determines which messages are "sent" (right-aligned, no name/avatar) vs "received" (left-aligned, with name and avatar). This is compared against each `ChatMessage.senderId`.

### `getItemViewType(position: Int)`
Returns `TYPE_SENT` (0) if `message.senderId == currentUserId`, otherwise `TYPE_RECEIVED` (1). RecyclerView calls this before `onCreateViewHolder` to decide which layout to inflate.

### `item.isDeleted` check
```kotlin
b.tvMessage.text = if (item.isDeleted) "This message was deleted" else item.text
```
Soft-delete: the message document remains in Firestore with `isDeleted = true` and text cleared. The adapter renders a placeholder string instead. This preserves message order and timestamps.

### `setOnLongClickListener { onLongPress(item); true }`
Returns `true` to consume the event (prevents it from propagating further). `onLongPress` callback is defined in `ChatFragment` — opens a `MaterialAlertDialog` with options to delete or add a reaction.

### `DiffCallback` — `areItemsTheSame` uses `id`
Each `ChatMessage` has a Firestore document ID. Same ID = same logical item (may have been edited/deleted). `areContentsTheSame` checks `a == b` (data class equality) to detect whether the bubble needs to be redrawn.

---

## 🏗️ Class Structure
```
ChatAdapter(currentUserId, onLongPress) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>
    ├── inner class SentViewHolder(ItemMessageSentBinding)
    ├── inner class ReceivedViewHolder(ItemMessageReceivedBinding)
    └── class DiffCallback : DiffUtil.ItemCallback<ChatMessage>
```

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `currentUserId` | `String` | Constructor param | Logged-in user's UID | Determines sent vs received |
| `onLongPress` | `(ChatMessage) -> Unit` | Constructor param | Long-press callback | Opens delete/react menu |
| `TYPE_SENT` | `Int = 0` | `companion const` | Sent view type constant | `getItemViewType` |
| `TYPE_RECEIVED` | `Int = 1` | `companion const` | Received view type constant | `getItemViewType` |

---

## ⚙️ Functions

### `getItemViewType(position: Int): Int`
Returns `TYPE_SENT` or `TYPE_RECEIVED` based on `senderId == currentUserId`.

### `onCreateViewHolder(parent, viewType): RecyclerView.ViewHolder`
Inflates `item_message_sent.xml` (TYPE_SENT) or `item_message_received.xml` (TYPE_RECEIVED).

### `SentViewHolder.bind(item: ChatMessage)`
Sets `tvMessage` (or deleted placeholder), `tvTime`. Registers `onLongClickListener`.

### `ReceivedViewHolder.bind(item: ChatMessage)`
Sets `tvSenderName`, `tvMessage` (or deleted placeholder), `tvTime`. Loads sender avatar via `loadAvatar`.

---

## 🔄 Data Flow Diagram
```
ChatRepository.getMessages(classId) — Firestore real-time listener
        ↓
New ChatMessage list emitted via Flow
        ↓
ChatViewModel._messages updated
        ↓
ChatFragment observes messages → adapter.submitList(messages)
        ↓
DiffCallback computes diff → RecyclerView updates changed items only
        ↓
User long-presses a bubble
        ↓
onLongPress(message) → ChatFragment shows delete/react dialog
```

---

## ⚠️ Important Notes & Gotchas
- Sent messages intentionally have no sender name/avatar (the user knows they sent it).
- The `currentUserId` is passed at adapter construction — if the user logs out and back in as a different account, the adapter must be recreated with the new UID.
- `isDeleted = true` messages still occupy space in the list — this preserves the conversation flow so "reply context" isn't lost.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.ChatMessage
import com.syed.classconnect.databinding.ItemMessageReceivedBinding
import com.syed.classconnect.databinding.ItemMessageSentBinding
import com.syed.classconnect.util.DateUtils.toRelativeTime
import com.syed.classconnect.util.loadAvatar

class ChatAdapter(
    private val currentUserId: String,     // Logged-in user — determines bubble side
    private val onLongPress: (ChatMessage) -> Unit  // Called on long press for delete/react
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_SENT = 0       // Current user's messages — right side
        private const val TYPE_RECEIVED = 1   // Other users' messages — left side
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position).senderId == currentUserId) TYPE_SENT else TYPE_RECEIVED
    // Compare message sender with logged-in user to pick layout.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_SENT) {
            SentViewHolder(ItemMessageSentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
        } else {
            ReceivedViewHolder(ItemMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SentViewHolder     -> holder.bind(item)
            is ReceivedViewHolder -> holder.bind(item)
        }
    }

    inner class SentViewHolder(private val b: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: ChatMessage) {
            b.tvMessage.text = if (item.isDeleted) "This message was deleted" else item.text
            // Soft-deleted messages show a placeholder — message removed, slot preserved.
            b.tvTime.text = item.timestamp.toRelativeTime()
            b.root.setOnLongClickListener { onLongPress(item); true }
            // true = event consumed, don't propagate to parent views.
        }
    }

    inner class ReceivedViewHolder(private val b: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: ChatMessage) {
            b.tvSenderName.text = item.senderName   // Show who sent this message
            b.tvMessage.text = if (item.isDeleted) "This message was deleted" else item.text
            b.tvTime.text = item.timestamp.toRelativeTime()
            b.ivAvatar.loadAvatar(item.senderPhotoUrl)  // Circular avatar via Glide
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(a: ChatMessage, b: ChatMessage) = a.id == b.id
        // Same Firestore document ID = same logical message.
        override fun areContentsTheSame(a: ChatMessage, b: ChatMessage) = a == b
        // data class equality — checks all fields. Detects deletions and reactions.
    }
}
```
