# AssignmentsAdapter.kt — RecyclerView adapter for assignments with color-coded urgency chips and stagger animation

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/assignments/AssignmentsAdapter.kt`

---

## 🎯 What This File Does
`AssignmentsAdapter` renders a list of `Assignment` objects in `AssignmentsFragment`. Each card shows the title, due date, total marks, a status chip, and a colored urgency bar on the left. The chip and bar change color dynamically: red for overdue, amber for ≤3 days, green for ample time. Cards support both a tap (navigate to detail) and a long-press (delete, teacher only). Cards fade in with a 40ms stagger. Without this adapter, the assignments list screen would have no content renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_assignment.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.core.content.ContextCompat` | AndroidX | Color resolution | `getColor()` from resource ID |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base |
| `com.syed.classconnect.R` | Project | Resource IDs | Semantic color resources |
| `com.syed.classconnect.data.model.Assignment` | Project | Assignment data class | Item type |
| `com.syed.classconnect.databinding.ItemAssignmentBinding` | ViewBinding | `item_assignment.xml` | All item views |
| `com.syed.classconnect.util.DateUtils.daysUntil` | Project | Days remaining | Urgency calculation |
| `com.syed.classconnect.util.DateUtils.isOverdue` | Project | Returns `true` if past due | Overdue chip |
| `com.syed.classconnect.util.DateUtils.toDisplayDate` | Project | Format timestamp | Due date text |
| `com.syed.classconnect.util.addPressEffect` | Project | Press animation | Scale-down on tap |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `android.content.res.ColorStateList.valueOf(color: Int)`
`ColorStateList` wraps a color so it can be applied to a `Chip`'s `chipBackgroundColor` property. The `Chip` widget requires a `ColorStateList` rather than a plain `Int` color.

### `setOnLongClickListener { ... }`
Returns `true` if the event was consumed (prevents short-click from also firing). Returns `false` to pass the event up the chain.

### `isOverdue()` vs `daysUntil()`
- `isOverdue()`: `true` if the timestamp is before `now` (regardless of amount).
- `daysUntil()`: returns the whole number of days. Negative = overdue, 0 = today, positive = future.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.assignments

// (imports as listed above)

class AssignmentsAdapter(
    private val onItemClick: (Assignment) -> Unit,
    private val onLongPress: ((Assignment) -> Unit)? = null
    // Long press is only connected for teachers (delete action).
) : ListAdapter<Assignment, AssignmentsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemAssignmentBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Assignment, position: Int) {
            b.tvTitle.text = item.title
            b.tvDueDate.text = "Due: ${item.dueDate.toDisplayDate()}"
            b.tvMarks.text = "${item.totalMarks} marks"

            val ctx = b.root.context
            val days = item.dueDate.daysUntil()
            when {
                item.dueDate.isOverdue() -> {
                    // Red: past due
                    b.chipStatus.text = "Overdue"
                    b.chipStatus.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_error_surface))
                    b.chipStatus.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_error))
                    b.viewUrgencyBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.semantic_error))
                }
                days == 0L -> {
                    // Amber: due today
                    b.chipStatus.text = "Due Today"
                    // ...
                }
                days <= 3 -> {
                    // Amber: ≤3 days
                    b.chipStatus.text = "$days days left"
                    // ...
                }
                else -> {
                    // Green: plenty of time
                    b.chipStatus.text = "$days days left"
                    // ...
                }
            }

            b.root.addPressEffect()
            b.root.setOnClickListener { onItemClick(item) }
            b.root.setOnLongClickListener {
                onLongPress?.invoke(item)
                onLongPress != null
                // Returns true (consumed) only if a long-press handler exists.
            }

            b.root.alpha = 0f
            b.root.animate().alpha(1f).setDuration(300).setStartDelay(position * 40L).start()
        }
    }

    // ... onCreateViewHolder, onBindViewHolder, DiffCallback
}
```

