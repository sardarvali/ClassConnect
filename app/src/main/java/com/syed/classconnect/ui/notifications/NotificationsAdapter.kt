package com.syed.classconnect.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.R
import com.syed.classconnect.data.model.AppNotification
import com.syed.classconnect.databinding.ItemNotificationBinding
import com.syed.classconnect.util.DateUtils.toRelativeTime
import com.syed.classconnect.util.addPressEffect
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show

class NotificationsAdapter(private val onRead: (AppNotification) -> Unit) :
    ListAdapter<AppNotification, NotificationsAdapter.ViewHolder>(DiffCallback()) {

<<<<<<< HEAD
    inner class ViewHolder(private val b: ItemNotificationBinding) : RecyclerView.ViewHolder(b.root) {
=======
    inner class ViewHolder(private val b: ItemNotificationBinding) :
        RecyclerView.ViewHolder(b.root) {
>>>>>>> final
        fun bind(item: AppNotification, position: Int) {
            b.tvTitle.text = item.title
            b.tvBody.text = item.body
            b.tvTime.text = item.createdAt.toRelativeTime()
            if (item.isRead) {
                b.viewUnread.hide()
                b.root.alpha = 0.7f
            } else {
                b.viewUnread.show()
                b.viewUnread.setBackgroundColor(
<<<<<<< HEAD
                    ContextCompat.getColor(b.root.context, R.color.brand_primary))
=======
                    ContextCompat.getColor(b.root.context, R.color.brand_primary)
                )
>>>>>>> final
                b.root.alpha = 1f
            }
            b.root.addPressEffect()
            b.root.setOnClickListener { onRead(item) }
            // Stagger entrance
            b.root.translationY = 20f
            b.root.animate().translationY(0f).alpha(1f)
                .setDuration(300).setStartDelay(position * 40L).start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
<<<<<<< HEAD
        ViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
=======
        ViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
>>>>>>> final

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    class DiffCallback : DiffUtil.ItemCallback<AppNotification>() {
        override fun areItemsTheSame(a: AppNotification, b: AppNotification) = a.id == b.id
        override fun areContentsTheSame(a: AppNotification, b: AppNotification) = a == b
    }
}
