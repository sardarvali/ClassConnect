package com.syed.classconnect.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.ChatMessage
import com.syed.classconnect.databinding.ItemMessageReceivedBinding
import com.syed.classconnect.databinding.ItemMessageSentBinding
import com.syed.classconnect.util.DateUtils.toRelativeTime
import com.syed.classconnect.util.loadAvatar

class ChatAdapter(
    private val currentUserId: String,
    private val onLongPress: (ChatMessage) -> Unit
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_SENT = 0
        private const val TYPE_RECEIVED = 1
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position).senderId == currentUserId) TYPE_SENT else TYPE_RECEIVED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_SENT) {
            SentViewHolder(ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ReceivedViewHolder(ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SentViewHolder -> holder.bind(item)
            is ReceivedViewHolder -> holder.bind(item)
        }
    }

    inner class SentViewHolder(private val b: ItemMessageSentBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: ChatMessage) {
            b.tvMessage.text = if (item.isDeleted) "This message was deleted" else item.text
            b.tvTime.text = item.timestamp.toRelativeTime()
            b.root.setOnLongClickListener { onLongPress(item); true }
        }
    }

    inner class ReceivedViewHolder(private val b: ItemMessageReceivedBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: ChatMessage) {
            b.tvSenderName.text = item.senderName
            b.tvMessage.text = if (item.isDeleted) "This message was deleted" else item.text
            b.tvTime.text = item.timestamp.toRelativeTime()
            b.ivAvatar.loadAvatar(item.senderPhotoUrl)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(a: ChatMessage, b: ChatMessage) = a.id == b.id
        override fun areContentsTheSame(a: ChatMessage, b: ChatMessage) = a == b
    }
}

