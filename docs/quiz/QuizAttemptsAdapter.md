# QuizAttemptsAdapter.kt — RecyclerView adapter for displaying student quiz attempt results (teacher view)

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/quiz/QuizAttemptsAdapter.kt`

---

## 🎯 What This File Does
`QuizAttemptsAdapter` renders a list of `QuizAttempt` objects in the teacher's `QuizResultsFragment`. Each row shows the student name (or truncated ID), their score, percentage, and submission timestamp. The percentage text is color-coded: green ≥80%, amber ≥50%, red <50%. It is a read-only display adapter with no click handlers. Without it, the quiz results table has no renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_quiz_attempt.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base |
| `com.syed.classconnect.data.model.QuizAttempt` | Project | Quiz attempt data class | Item type |
| `com.syed.classconnect.databinding.ItemQuizAttemptBinding` | ViewBinding | `item_quiz_attempt.xml` | `tvStudentId`, `tvScore`, `tvPercentage`, `tvSubmittedAt` |
| `com.syed.classconnect.util.DateUtils.toDisplayDateTime` | Project | Timestamp → "Mar 6, 2026 14:30" | Submission time |
| `kotlin.math.roundToInt` | Kotlin Math | Rounds Double to Int | Percentage calculation |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `attempt.studentName.ifBlank { attempt.studentId.take(8) + "…" }`
`ifBlank` returns the receiver if it is not blank, otherwise evaluates the lambda. Displays the student name if populated, otherwise shows the first 8 characters of their UID + "…" as a fallback.

### `(attempt.score * 100.0 / attempt.totalMarks).roundToInt()`
Calculates percentage. Multiplied by `100.0` (Double) to get decimal precision before `roundToInt()`. Guarded by `if (attempt.totalMarks == 0)` to prevent division by zero.

### `resources.getColor(colorRes, null)`
The two-arg version (API 23+). The `null` theme is fine here since we're using semantic color resources that don't change with theme variants.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.quiz

// (imports as listed above)

/**
 * Adapter for the teacher's quiz results screen — one row per student attempt.
 */
class QuizAttemptsAdapter :
    ListAdapter<QuizAttempt, QuizAttemptsAdapter.ViewHolder>(DIFF) {

    class ViewHolder(private val b: ItemQuizAttemptBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(attempt: QuizAttempt) {
            b.tvStudentId.text = attempt.studentName.ifBlank { attempt.studentId.take(8) + "…" }
            // Show student name if available, otherwise first 8 chars of UID.

            b.tvScore.text = "${attempt.score} / ${attempt.totalMarks}"

            val pct = if (attempt.totalMarks == 0) 0
                      else (attempt.score * 100.0 / attempt.totalMarks).roundToInt()
            b.tvPercentage.text = "$pct%"

            b.tvSubmittedAt.text = attempt.submittedAt.toDisplayDateTime()
            // "Mar 6, 2026 14:30"

            // Color-code percentage
            val colorRes = when {
                pct >= 80 -> R.color.semantic_success  // Green: excellent
                pct >= 50 -> R.color.semantic_warning  // Amber: passing
                else      -> R.color.semantic_error    // Red: failing
            }
            b.tvPercentage.setTextColor(b.root.context.resources.getColor(colorRes, null))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemQuizAttemptBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<QuizAttempt>() {
            override fun areItemsTheSame(a: QuizAttempt, b: QuizAttempt) = a.studentId == b.studentId
            // One attempt per student per quiz.
            override fun areContentsTheSame(a: QuizAttempt, b: QuizAttempt) = a == b
        }
    }
}
```

