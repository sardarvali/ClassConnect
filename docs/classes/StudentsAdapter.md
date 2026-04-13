# StudentsAdapter.kt — RecyclerView adapter for the enrolled students list in a class

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/classes/StudentsAdapter.kt`

---

## 🎯 What This File Does
`StudentsAdapter` renders a list of `User` objects (enrolled students) in `StudentsFragment`. Each row shows the student's avatar (loaded via `loadAvatar` extension), name, and email. It has no click handler — it is a pure display adapter. Without it, the Students tab in `ClassDetailActivity` would have no way to render the enrolled student list.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | Inflate `item_student_row.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base |
| `com.syed.classconnect.data.model.User` | Project | User data class | Item type |
| `com.syed.classconnect.databinding.ItemStudentRowBinding` | ViewBinding | `item_student_row.xml` | `tvName`, `tvEmail`, `ivAvatar` |
| `com.syed.classconnect.util.loadAvatar` | Project | Glide extension | Loads profile photo with circular crop and fallback |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `loadAvatar(url: String?)`
Extension function on `ImageView` from `Extensions.kt`. Uses Glide to load the profile photo URL with a circular crop and a default avatar placeholder when URL is null or empty.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.classes

// (imports as listed above)

class StudentsAdapter :
    ListAdapter<User, StudentsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemStudentRowBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: User) {
            b.tvName.text = item.name
            b.tvEmail.text = item.email
            b.ivAvatar.loadAvatar(item.photoUrl)
            // loadAvatar: Glide with circular crop + ic_profile placeholder.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemStudentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(a: User, b: User) = a.uid == b.uid
        // Same user = same Firestore UID.
        override fun areContentsTheSame(a: User, b: User) = a == b
    }
}
```

