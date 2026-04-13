# ClassAdapter.kt — RecyclerView adapter for the class list with gradient color bars and stagger entrance animation

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/classes/ClassAdapter.kt`

---

## 🎯 What This File Does
`ClassAdapter` renders a list of `ClassRoom` objects as vertical cards in `ClassListFragment`. Each card shows the class name, subject, teacher name, and student count. A colored gradient bar on the left side cycles through 8 predefined gradients based on card position. Cards fade in with a staggered entrance animation (50ms delay between cards). Tapping navigates to `ClassDetailActivity`. Without this adapter, the class list screen would have no way to render the list of classes.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.graphics.drawable.GradientDrawable` | Android SDK | Programmatic gradient | Left color-accent bar on each card |
| `android.view.LayoutInflater` | Android SDK | XML → View | Inflate `item_class_card.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.core.content.ContextCompat` | AndroidX Core | Color resource resolution | `getColor()` from resource ID |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base |
| `com.syed.classconnect.R` | Project | Resource IDs | Color resources for gradients |
| `com.syed.classconnect.data.model.ClassRoom` | Project | Class data model | List item type |
| `com.syed.classconnect.databinding.ItemClassCardBinding` | ViewBinding | `item_class_card.xml` | `tvClassName`, `tvSubject`, `tvTeacher`, `tvStudentCount`, `viewColorBar` |
| `com.syed.classconnect.util.addPressEffect` | Project | Press animation extension | Scale-down on press |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `GradientDrawable.Orientation.TOP_BOTTOM`
Creates a vertical gradient on `viewColorBar` — the narrow colored strip on the left of each card. Each position gets a different color pair from the 8 predefined pairs.

### `"${item.studentIds.size} students"`
String template: shows current enrolled student count.

### Staggered entrance animation
```kotlin
binding.root.alpha = 0f
binding.root.animate()
    .alpha(1f).setDuration(300).setStartDelay(position * 50L).start()
```
Position 0 → 0ms delay, position 1 → 50ms, etc.

---

## 🏗️ Class Structure
`ClassAdapter(onClick) : ListAdapter<ClassRoom, ViewHolder>(DiffCallback())`

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.classes

// (imports as listed above)

class ClassAdapter(private val onClick: (ClassRoom) -> Unit) :
    ListAdapter<ClassRoom, ClassAdapter.ViewHolder>(DiffCallback()) {

    private val gradients = listOf(/* 8 color pairs */)

    inner class ViewHolder(private val binding: ItemClassCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClassRoom, position: Int) {
            binding.tvClassName.text = item.name
            binding.tvSubject.text = item.subject
            binding.tvTeacher.text = item.teacherName
            binding.tvStudentCount.text = "${item.studentIds.size} students"

            val (startColor, endColor) = gradients[position % gradients.size]
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    ContextCompat.getColor(binding.root.context, startColor),
                    ContextCompat.getColor(binding.root.context, endColor)
                )
            )
            binding.viewColorBar.background = gradient
            // The thin left strip of the card gets this gradient.

            binding.root.addPressEffect()
            binding.root.setOnClickListener { onClick(item) }

            binding.root.alpha = 0f
            binding.root.animate()
                .alpha(1f).setDuration(300).setStartDelay(position * 50L).start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemClassCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    class DiffCallback : DiffUtil.ItemCallback<ClassRoom>() {
        override fun areItemsTheSame(a: ClassRoom, b: ClassRoom) = a.id == b.id
        override fun areContentsTheSame(a: ClassRoom, b: ClassRoom) = a == b
    }
}
```

