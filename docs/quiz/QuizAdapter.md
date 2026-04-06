# QuizAdapter.kt — RecyclerView adapter for the quiz list with published/draft status chip

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/quiz/QuizAdapter.kt`

---

## 🎯 What This File Does
`QuizAdapter` renders a list of `Quiz` objects in `QuizListFragment`. Each card shows the quiz title, description, duration (minutes), total marks, and a "Published"/"Draft" status chip. Both tap (navigate/attempt) and optional long-press (delete, teacher only) are supported. Without it, the quiz list screen has no content.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_quiz.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder type |
| `com.syed.classconnect.data.model.Quiz` | Project | Quiz data class | Item type |
| `com.syed.classconnect.databinding.ItemQuizBinding` | ViewBinding | `item_quiz.xml` access | `tvTitle`, `tvDescription`, `tvDuration`, `tvMarks`, `chipStatus` |

---

## ⚙️ Functions

### `bind(item: Quiz)`
Sets all text fields. Sets `chipStatus.text` to "Published" or "Draft" based on `item.isPublished`. Sets click and long-click listeners.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.quiz

// (imports as listed above)

class QuizAdapter(
    private val onClick: (Quiz) -> Unit,
    private val onLongPress: ((Quiz) -> Unit)? = null
) : ListAdapter<Quiz, QuizAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemQuizBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Quiz) {
            b.tvTitle.text = item.title
            b.tvDescription.text = item.description
            b.tvDuration.text = "${item.durationMinutes} min"
            b.tvMarks.text = "${item.totalMarks} marks"
            b.chipStatus.text = if (item.isPublished) "Published" else "Draft"
            // Chip shows publication state — only published quizzes are attemptable by students.
            b.root.setOnClickListener { onClick(item) }
            b.root.setOnLongClickListener {
                onLongPress?.invoke(item)
                onLongPress != null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Quiz>() {
        override fun areItemsTheSame(a: Quiz, b: Quiz) = a.id == b.id
        override fun areContentsTheSame(a: Quiz, b: Quiz) = a == b
    }
}
```

