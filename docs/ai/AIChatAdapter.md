# AIChatAdapter.kt — RecyclerView adapter for the AI chat interface with dual view types and Markdown rendering

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/ai/AIChatAdapter.kt`

---

## 🎯 What This File Does
`AIChatAdapter` renders the AI Buddy chat conversation. It has two view types: user messages (right-aligned bubbles) and AI responses (left-aligned bubbles). AI responses are rendered as Markdown using the `Markwon` library — this means the AI can return **bold**, *italic*, code blocks, bullet lists, etc., which are all rendered as rich text. The `addErrorMessage()` helper appends a prefixed error message to the list. Without this adapter, the AI chat screen would have no message renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | Inflate message layouts |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base |
| `com.syed.classconnect.databinding.ItemAiMessageReceivedBinding` | ViewBinding | `item_ai_message_received.xml` | AI message bubble |
| `com.syed.classconnect.databinding.ItemAiMessageSentBinding` | ViewBinding | `item_ai_message_sent.xml` | User message bubble |
| `io.noties.markwon.Markwon` | Markwon | Markdown rendering library | Renders AI Markdown responses |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `AIChatMessage`
A data class (defined in `AIViewModel.kt` or nearby) with `text: String` and `isUser: Boolean`.

### Dual view type pattern
```kotlin
companion object {
    private const val TYPE_USER = 0
    private const val TYPE_AI = 1
}
override fun getItemViewType(position: Int) = if (getItem(position).isUser) TYPE_USER else TYPE_AI
```
`getItemViewType()` tells RecyclerView which layout to inflate. `onCreateViewHolder` uses `viewType` to choose between the two layouts.

### `Markwon.create(context)`
Creates a Markwon instance configured with default plugins. `markwon.setMarkdown(textView, markdownString)` parses the markdown and applies `SpannableString` formatting to the `TextView` — so `**bold**` renders as **bold** text, not as literal asterisks.

### `addErrorMessage(error: String)`
Gets the current list (immutable from `ListAdapter.currentList`), adds an error message with "⚠️" prefix, and calls `submitList()` to update the adapter.

### `a === b` in `areItemsTheSame`
`===` is referential equality — the exact same object in memory. AI chat messages are transient (created in the ViewModel and never edited), so referential identity is a valid "same item" check.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.ai

// (imports as listed above)

class AIChatAdapter : ListAdapter<AIChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_AI = 1
    }

    private val currentList2 = mutableListOf<AIChatMessage>()
    // Note: this field is unused — `currentList` (from ListAdapter) is used instead.

    override fun getItemViewType(position: Int) = if (getItem(position).isUser) TYPE_USER else TYPE_AI
    // User messages → inflate sent layout; AI messages → received layout.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            UserViewHolder(ItemAiMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            AIViewHolder(ItemAiMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> holder.bind(getItem(position))
            is AIViewHolder -> holder.bind(getItem(position))
        }
    }

    inner class UserViewHolder(private val b: ItemAiMessageSentBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: AIChatMessage) { b.tvMessage.text = item.text }
        // Plain text for user messages.
    }

    inner class AIViewHolder(private val b: ItemAiMessageReceivedBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: AIChatMessage) {
            val markwon = Markwon.create(b.root.context)
            markwon.setMarkdown(b.tvMessage, item.text)
            // Parses Markdown syntax and renders as styled SpannableString.
            // Supports: **bold**, *italic*, `code`, ```code blocks```, bullet lists, etc.
        }
    }

    fun addErrorMessage(error: String) {
        val list = currentList.toMutableList()
        list.add(AIChatMessage("⚠️ $error", isUser = false))
        submitList(list)
        // Appends an error AI "message" to the chat.
    }

    class DiffCallback : DiffUtil.ItemCallback<AIChatMessage>() {
        override fun areItemsTheSame(a: AIChatMessage, b: AIChatMessage) = a === b
        // Referential equality — same object = same item.
        override fun areContentsTheSame(a: AIChatMessage, b: AIChatMessage) = a == b
    }
}
```

