# AdminClassCardAdapter.kt — Alternate class card adapter

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/AdminClassCardAdapter.kt`

---

## 🎯 What This File Does
`AdminClassCardAdapter` is an alternate class-card renderer that mirrors the data shown by `AdminClassAdapter`, but the current app flow does not reference it from any fragment or screen. It is documented here because the source file exists in the codebase, but it should be treated as an unused alternative implementation unless a screen is later wired to it.

In its current form, unassigned classes show "No Teacher Assigned", an "UNASSIGNED" chip, and an "Assign Teacher" button with an orange color bar. Assigned classes show the teacher's name and the class's own color bar.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.graphics.Color` | Android SDK | Color parsing | `Color.parseColor(c.color)` |
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_admin_class_card.xml` |
| `android.view.ViewGroup` | Android SDK | Parent | `onCreateViewHolder` |
| `androidx.core.view.isVisible` | AndroidX | Visibility shorthand | Show/hide chip and button |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base |
| `com.syed.classconnect.data.model.ClassRoom` | Project | Class data model | Item type |
| `com.syed.classconnect.databinding.ItemAdminClassCardBinding` | ViewBinding | `item_admin_class_card.xml` | `tvClassName`, `tvTeacherName`, `chipUnassigned`, `btnAssignTeacher`, `viewColorBar` |

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.admin

// (imports as listed above)

class AdminClassCardAdapter(
    private val onAssignTeacher: (ClassRoom) -> Unit
) : ListAdapter<ClassRoom, AdminClassCardAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemAdminClassCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAdminClassCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val classRoom = getItem(position)
        with(holder.binding) {
            tvClassName.text = classRoom.name
            val isUnassigned = classRoom.teacherId.isEmpty()
            if (isUnassigned) {
                tvTeacherName.text = "No Teacher Assigned"
                chipUnassigned.isVisible = true
                btnAssignTeacher.isVisible = true
                try { viewColorBar.setBackgroundColor(Color.parseColor("#E65100")) }
                // #E65100 = deep orange — attention color for unassigned classes
                catch (_: Exception) {}
            } else {
                tvTeacherName.text = "Teacher: ${classRoom.teacherName}"
                chipUnassigned.isVisible = false
                btnAssignTeacher.isVisible = false
                try { viewColorBar.setBackgroundColor(Color.parseColor(classRoom.color)) }
                // Use the class's own brand color.
                catch (_: Exception) {}
            }
            btnAssignTeacher.setOnClickListener { onAssignTeacher(classRoom) }
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

