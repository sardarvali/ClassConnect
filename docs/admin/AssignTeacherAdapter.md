# AssignTeacherAdapter.kt — RecyclerView adapter for selecting a teacher to assign to a class

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/AssignTeacherAdapter.kt`

---

## 🎯 What This File Does
`AssignTeacherAdapter` displays a list of teacher `User` objects in `AssignTeacherBottomSheet`. Each row shows the teacher's avatar (via Glide), name, email, and current class count. Tapping a row calls `onTeacherSelected(teacher)` which triggers the assignment operation. Without it, the teacher assignment bottom sheet has no content renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_assign_teacher.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder | ViewHolder base |
| `com.bumptech.glide.Glide` | Glide | Image loading library | Avatar loading |
| `com.syed.classconnect.R` | Project | Resource IDs | `ic_profile` placeholder |
| `com.syed.classconnect.data.model.User` | Project | User data class | Item type |
| `com.syed.classconnect.databinding.ItemAssignTeacherBinding` | ViewBinding | `item_assign_teacher.xml` | `tvTeacherName`, `tvTeacherEmail`, `tvClassCount`, `ivAvatar` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### Direct `Glide` usage (not `loadAvatar` extension)
This adapter uses Glide directly rather than the `loadAvatar` extension from `Extensions.kt`. Both approaches work — this one allows fine-grained configuration (explicit `placeholder`, `circleCrop`).

### `teacher.photoUrl.ifEmpty { null }`
If `photoUrl` is an empty string, passes `null` to Glide. Glide treats `null` as "no URL" and shows the placeholder immediately.

### `teacher.classIds.size`
`classIds` is a `List<String>` in the `User` model, representing classes the teacher is currently assigned to. Shows teachers' current workload.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.admin

// (imports as listed above)

class AssignTeacherAdapter(
    private val onTeacherSelected: (User) -> Unit
) : ListAdapter<User, AssignTeacherAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemAssignTeacherBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAssignTeacherBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val teacher = getItem(position)
        with(holder.binding) {
            tvTeacherName.text = teacher.name
            tvTeacherEmail.text = teacher.email
            tvClassCount.text = "${teacher.classIds.size} classes"
            // Shows how many classes this teacher already has — helps admin balance workload.

            Glide.with(root.context)
                .load(teacher.photoUrl.ifEmpty { null })
                // Pass null if photoUrl is empty → Glide shows placeholder.
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(ivAvatar)

            root.setOnClickListener { onTeacherSelected(teacher) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(a: User, b: User) = a.uid == b.uid
            override fun areContentsTheSame(a: User, b: User) = a == b
        }
    }
}
```

