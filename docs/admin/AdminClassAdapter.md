# AdminClassAdapter.kt — RecyclerView list adapter for admin class management with unassigned teacher highlighting

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/AdminClassAdapter.kt`

---

## 🎯 What This File Does
`AdminClassAdapter` displays classes in the `AdminClassesFragment` as a vertical list. Each item shows the class name, subject, teacher name (or "Unassigned"), student count, and a colored left border. Unassigned classes show an orange border, an "Unassigned" badge, and an "Assign Teacher" button. Assigned classes show the class's own color. Tapping "Assign Teacher" triggers the `onAssignTeacher` callback to open `AssignTeacherBottomSheet`. Without this adapter, the admin class management screen has no content renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_admin_class.xml` |
| `android.view.ViewGroup` | Android SDK | Parent | `onCreateViewHolder` |
| `androidx.core.view.isVisible` | AndroidX | `view.isVisible = bool` shorthand | Show/hide badge and button |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base |
| `com.syed.classconnect.data.model.ClassRoom` | Project | Class data model | Item type |
| `com.syed.classconnect.databinding.ItemAdminClassBinding` | ViewBinding | `item_admin_class.xml` | All item views |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `isVisible` (KTX extension)
`view.isVisible = true` is equivalent to `view.visibility = View.VISIBLE`. `view.isVisible = false` sets `GONE`. More concise than the raw `visibility` property.

### `c.teacherName.ifEmpty { "Unassigned" }`
If `teacherName` is an empty string, substitutes "Unassigned".

### Color logic
```kotlin
val borderColor = if (isUnassigned) {
    root.context.getColor(R.color.warning)  // Orange for unassigned
} else {
    try { android.graphics.Color.parseColor(c.color) }
    catch (_: Exception) { root.context.getColor(R.color.primary) }
    // Parse the class's hex color; fall back to primary if invalid.
}
```

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.admin

// (imports as listed above)

class AdminClassAdapter(
    private val onAssignTeacher: (ClassRoom) -> Unit
) : ListAdapter<ClassRoom, AdminClassAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemAdminClassBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAdminClassBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = getItem(position)
        with(holder.binding) {
            tvClassName.text = c.name
            tvSubject.text = c.subject
            tvTeacherName.text = c.teacherName.ifEmpty { "Unassigned" }
            tvStudentCount.text = "${c.studentIds.size} students"

            val isUnassigned = c.teacherId.isEmpty()
            tvUnassignedBadge.isVisible = isUnassigned
            // "UNASSIGNED" chip badge — only visible when no teacher.
            btnAssignTeacher.isVisible = isUnassigned
            // Button only appears for classes without a teacher.
            btnAssignTeacher.setOnClickListener { onAssignTeacher(c) }

            val borderColor = if (isUnassigned) {
                root.context.getColor(R.color.warning)  // Orange accent
            } else {
                try { android.graphics.Color.parseColor(c.color) }
                catch (_: Exception) { root.context.getColor(R.color.primary) }
            }
            viewColorBar.setBackgroundColor(borderColor)
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ClassRoom>() {
            override fun areItemsTheSame(a: ClassRoom, b: ClassRoom) = a.id == b.id
            override fun areContentsTheSame(a: ClassRoom, b: ClassRoom) = a == b
        }
    }
}
```

