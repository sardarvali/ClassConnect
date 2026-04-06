package com.syed.classconnect.data.model

data class NewsArticle(
    val title: String = "",
    val description: String? = "",
    val url: String = "",
    val urlToImage: String? = "",
    val publishedAt: String = "",
    val source: NewsSource = NewsSource(),
    val author: String? = null,
    val content: String? = null
)

data class NewsSource(
    val id: String? = null,
    val name: String = ""
)

data class NewsResponse(
    val status: String = "",
    val totalResults: Int = 0,
    val articles: List<NewsArticle> = emptyList()
)

