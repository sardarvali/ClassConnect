package com.syed.classconnect.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.Announcement
import com.syed.classconnect.databinding.ItemAnnouncementBinding
import com.syed.classconnect.util.DateUtils.toRelativeTime
import com.syed.classconnect.util.addPressEffect

/** Recent announcements list on the student home screen. */
class RecentAnnouncementsAdapter(
    private val onClick: ((Announcement) -> Unit)? = null
) : ListAdapter<Announcement, RecentAnnouncementsAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemAnnouncementBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(a: Announcement) {
            b.tvTitle.text = a.title
            b.tvBody.text = a.body
            b.tvAuthor.text = a.authorName
            b.tvTime.text = a.createdAt.toRelativeTime()

            b.ivDelete.visibility = android.view.View.GONE
            b.ivPin.visibility =
                if (a.isPinned) android.view.View.VISIBLE else android.view.View.GONE
            b.ivPin.isEnabled = false
            if (a.isPinned) {
                b.ivPin.setColorFilter(android.graphics.Color.parseColor("#4F46E5"))
            } else {
                b.ivPin.clearColorFilter()
            }

            b.root.addPressEffect()
            b.root.setOnClickListener { onClick?.invoke(a) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Announcement>() {
            override fun areItemsTheSame(a: Announcement, b: Announcement) = a.id == b.id
            override fun areContentsTheSame(a: Announcement, b: Announcement) = a == b
        }
    }
}
