# NewsAdapter.kt — Horizontal news card adapter for the teacher home screen

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/home/NewsAdapter.kt`

---

## 🎯 What This File Does
`NewsAdapter` displays education news articles in a horizontal RecyclerView on the `TeacherHomeFragment`. Each card shows the article headline, source name, and thumbnail image (loaded via the `loadImage` Glide extension). Tapping opens the article via `onClick`. Without this adapter, the teacher home screen's news section would have no content renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | Inflates XML layouts | Creates `item_news.xml` |
| `android.view.ViewGroup` | Android SDK | Parent view container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | List diff algorithm | `ListAdapter` base |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter with diff support | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder type |
| `com.syed.classconnect.data.model.NewsArticle` | Project | News article data class | Item type |
| `com.syed.classconnect.databinding.ItemNewsBinding` | ViewBinding | `item_news.xml` typed access | `tvHeadline`, `tvSource`, `ivNewsImage` |
| `com.syed.classconnect.util.loadImage` | Project | Glide extension function | Loads image URL into ImageView |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `loadImage(url: String?)`
Extension function on `ImageView` from `Extensions.kt`. Calls `Glide.with(this).load(url).into(this)` with a placeholder. Handles null URLs gracefully (shows placeholder).

### `DiffUtil` with `a.url == b.url`
News articles don't have a unique ID field — the article URL is used as the stable identifier.

---

## 🏗️ Class Structure
`NewsAdapter(onClick) : ListAdapter<NewsArticle, VH>(DIFF)` — one ViewHolder type, non-null onClick.

---

## ⚙️ Functions

### `bind(article: NewsArticle)`
Sets `tvHeadline` = title, `tvSource` = source name, loads image via `loadImage()`, sets click listener.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.home

// (imports as listed above)

/** Horizontal news-card adapter for the teacher home screen. */
class NewsAdapter(
    private val onClick: (NewsArticle) -> Unit
    // Non-nullable — tapping a news card always opens the article.
) : ListAdapter<NewsArticle, NewsAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemNewsBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(article: NewsArticle) {
            b.tvHeadline.text = article.title
            b.tvSource.text   = article.source.name
            b.ivNewsImage.loadImage(article.urlToImage)
            // loadImage: Glide extension — handles null URL with placeholder.
            b.root.setOnClickListener { onClick(article) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<NewsArticle>() {
            override fun areItemsTheSame(a: NewsArticle, b: NewsArticle) = a.url == b.url
            // URL is the stable unique identifier for news articles.
            override fun areContentsTheSame(a: NewsArticle, b: NewsArticle) = a == b
        }
    }
}
```

