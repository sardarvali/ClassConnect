package com.syed.classconnect.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.NewsArticle
import com.syed.classconnect.databinding.ItemNewsBinding
<<<<<<< HEAD
=======
import com.syed.classconnect.util.addPressEffect
>>>>>>> final
import com.syed.classconnect.util.loadImage

/** Horizontal news-card adapter for the teacher home screen. */
class NewsAdapter(
    private val onClick: (NewsArticle) -> Unit
) : ListAdapter<NewsArticle, NewsAdapter.VH>(DIFF) {

<<<<<<< HEAD
    inner class VH(private val b: ItemNewsBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(article: NewsArticle) {
            b.tvHeadline.text = article.title
            b.tvSource.text   = article.source.name
            b.ivNewsImage.loadImage(article.urlToImage)
            b.root.setOnClickListener { onClick(article) }
=======
    private val animatedUrls = mutableSetOf<String>()

    inner class VH(private val b: ItemNewsBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(article: NewsArticle, position: Int) {
            b.tvHeadline.text = article.title
            b.tvSource.text = article.source.name
            b.ivNewsImage.loadImage(article.urlToImage)

            b.root.addPressEffect()
            b.root.setOnClickListener { onClick(article) }

            val key = article.url
            if (animatedUrls.add(key)) {
                b.root.alpha = 0f
                b.root.translationY = 24f
                b.root.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(260)
                    .setStartDelay((position.coerceAtMost(6) * 35L))
                    .start()
            } else {
                b.root.alpha = 1f
                b.root.translationY = 0f
            }
>>>>>>> final
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

<<<<<<< HEAD
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
=======
    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position), position)
>>>>>>> final

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<NewsArticle>() {
            override fun areItemsTheSame(a: NewsArticle, b: NewsArticle) = a.url == b.url
            override fun areContentsTheSame(a: NewsArticle, b: NewsArticle) = a == b
        }
    }
}
<<<<<<< HEAD

=======
>>>>>>> final
