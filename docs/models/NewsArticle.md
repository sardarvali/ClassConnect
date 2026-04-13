# NewsArticle — Data classes for news API responses

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/NewsArticle.kt`

---

## 🎯 What This File Does
This file contains three data classes that model responses from the NewsAPI.org REST API: `NewsArticle` (a single article), `NewsSource` (the article's source), and `NewsResponse` (the paginated API response wrapper). These are used on the student/teacher home screens to display education-related news.

---

## 📦 Imports
This file has NO imports — all types used are Kotlin standard library types (`String`, `Int`, `List`).

---

## 📋 NewsArticle Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `title` | `String` | Article headline |
| `description` | `String?` | Short description (nullable — some articles omit this) |
| `url` | `String` | Full article URL (opened in WebViewActivity) |
| `urlToImage` | `String?` | Thumbnail image URL (nullable) |
| `publishedAt` | `String` | ISO date string of publication |
| `source` | `NewsSource` | Source information (name, ID) |
| `author` | `String?` | Article author (nullable) |
| `content` | `String?` | Article content preview (nullable) |

## 📋 NewsSource Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `id` | `String?` | Source ID (nullable) |
| `name` | `String` | Source display name (e.g., "BBC News") |

## 📋 NewsResponse Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `status` | `String` | API status ("ok" or "error") |
| `totalResults` | `Int` | Total articles matching the query |
| `articles` | `List<NewsArticle>` | List of articles in this page |

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.data.model
// Package: data model layer.
// No imports needed — only Kotlin stdlib types used.

data class NewsArticle(
    val title: String = "",              // Article headline
    val description: String? = "",       // Short description (nullable)
    val url: String = "",                // Full article URL
    val urlToImage: String? = "",        // Thumbnail image URL (nullable)
    val publishedAt: String = "",        // ISO date string
    val source: NewsSource = NewsSource(), // Source info
    val author: String? = null,          // Author name (nullable)
    val content: String? = null          // Content preview (nullable)
)
// Gson automatically maps JSON keys to these field names.
// No @SerializedName needed because field names match JSON keys exactly.

data class NewsSource(
    val id: String? = null,    // Source identifier (nullable)
    val name: String = ""      // Source display name
)

data class NewsResponse(
    val status: String = "",              // "ok" or "error"
    val totalResults: Int = 0,            // Total matching articles
    val articles: List<NewsArticle> = emptyList() // Articles in this page
)
// NewsApiService returns Response<NewsResponse>.
// HomeViewModel extracts articles list and exposes via LiveData.
```

