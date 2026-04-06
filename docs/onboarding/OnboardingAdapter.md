# OnboardingAdapter.kt — RecyclerView adapter for onboarding pages with title, description, and illustration

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/onboarding/OnboardingAdapter.kt`

---

## 🎯 What This File Does
`OnboardingAdapter` renders each page of the `OnboardingFragment`'s ViewPager2. Each page is an `OnboardingPage` (data class defined in this file) with a title, description, and image resource ID. The adapter inflates `item_onboarding_page.xml`, sets the text and image, and returns the view count. It uses plain `RecyclerView.Adapter` (not `ListAdapter`) since the page list is fixed and never changes. Without it, the ViewPager2 in `OnboardingFragment` would have no pages to display.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_onboarding_page.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | Adapter base + ViewHolder | Base class and ViewHolder parent |
| `com.syed.classconnect.databinding.ItemOnboardingPageBinding` | ViewBinding | `item_onboarding_page.xml` | `tvTitle`, `tvDescription`, `ivImage` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `data class OnboardingPage(val title: String, val description: String, val imageRes: Int)`
Defined in this file. A simple data holder:
- `title`: large text at top of the page
- `description`: subtitle below
- `imageRes`: `@DrawableRes Int` — a drawable resource ID like `R.drawable.ic_chat`

### `b.ivImage.setImageResource(page.imageRes)`
Sets a drawable directly from a resource ID without loading from URL. For bundled assets, this is the correct approach (no Glide needed).

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.databinding.ItemOnboardingPageBinding

data class OnboardingPage(val title: String, val description: String, val imageRes: Int)
// Each onboarding page's content.
// imageRes is a drawable resource ID (e.g., R.drawable.ic_ai).

class OnboardingAdapter(private val pages: List<OnboardingPage>) :
    RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {
    // Using plain Adapter (not ListAdapter) since pages never change.

    inner class ViewHolder(private val b: ItemOnboardingPageBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(page: OnboardingPage) {
            b.tvTitle.text = page.title
            b.tvDescription.text = page.description
            b.ivImage.setImageResource(page.imageRes)
            // Bundled drawable — no URL loading needed.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemOnboardingPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(pages[position])

    override fun getItemCount() = pages.size
    // ViewPager2 uses this to know how many pages exist.
}
```

