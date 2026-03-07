package com.syed.classconnect.ui.classes.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.Announcement
import com.syed.classconnect.databinding.ItemAnnouncementBinding
import com.syed.classconnect.util.DateUtils.toRelativeTime

class FeedAdapter(
    private val onPinClick: (Announcement) -> Unit
) : ListAdapter<FeedItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnouncementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? AnnouncementViewHolder)?.bind((getItem(position) as FeedItem.AnnouncementItem).announcement)
    }

    inner class AnnouncementViewHolder(private val binding: ItemAnnouncementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Announcement) {
            binding.tvTitle.text = item.title
            binding.tvBody.text = item.body
            binding.tvAuthor.text = item.authorName
            binding.tvTime.text = item.createdAt.toRelativeTime()
            binding.ivPin.isSelected = item.isPinned
            binding.ivPin.setOnClickListener { onPinClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(a: FeedItem, b: FeedItem): Boolean =
            (a is FeedItem.AnnouncementItem && b is FeedItem.AnnouncementItem && a.announcement.id == b.announcement.id)
        override fun areContentsTheSame(a: FeedItem, b: FeedItem) = a == b
    }
}

