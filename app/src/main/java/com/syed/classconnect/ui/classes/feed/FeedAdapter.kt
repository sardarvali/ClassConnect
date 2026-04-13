package com.syed.classconnect.ui.classes.feed

import android.view.LayoutInflater
<<<<<<< HEAD
=======
import android.view.View
>>>>>>> final
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.Announcement
import com.syed.classconnect.databinding.ItemAnnouncementBinding
import com.syed.classconnect.util.DateUtils.toRelativeTime

class FeedAdapter(
<<<<<<< HEAD
    private val onPinClick: (Announcement) -> Unit
) : ListAdapter<FeedItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
=======
    private val onPinClick: (Announcement) -> Unit,
    private val onDeleteClick: (Announcement) -> Unit,
    private val currentUserId: String,
    private val isTeacherOrAdmin: Boolean
) : ListAdapter<FeedItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
>>>>>>> final
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
<<<<<<< HEAD
            binding.ivPin.setOnClickListener { onPinClick(item) }
=======

            // visually update pin status
            if (item.isPinned) {
                binding.ivPin.setColorFilter(android.graphics.Color.parseColor("#4F46E5")) // Primary color typical
            } else {
                binding.ivPin.clearColorFilter()
            }

            // Show pin/delete options based on roles
            val canManage = isTeacherOrAdmin || item.authorId == currentUserId

            // Student should see the pin icon if it is pinned, but unclickable if they can't manage
            if (isTeacherOrAdmin) {
                binding.ivPin.visibility = View.VISIBLE
                binding.ivPin.isEnabled = true
            } else {
                binding.ivPin.visibility = if (item.isPinned) View.VISIBLE else View.GONE
                binding.ivPin.isEnabled = false
            }

            binding.ivDelete.visibility = if (canManage) View.VISIBLE else View.GONE

            binding.ivPin.setOnClickListener { onPinClick(item) }
            binding.ivDelete.setOnClickListener { onDeleteClick(item) }
>>>>>>> final
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(a: FeedItem, b: FeedItem): Boolean =
            (a is FeedItem.AnnouncementItem && b is FeedItem.AnnouncementItem && a.announcement.id == b.announcement.id)
<<<<<<< HEAD
        override fun areContentsTheSame(a: FeedItem, b: FeedItem) = a == b
    }
}

=======

        override fun areContentsTheSame(a: FeedItem, b: FeedItem) = a == b
    }
}
>>>>>>> final
