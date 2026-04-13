package com.syed.classconnect.ui.ai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.databinding.ItemAiMessageReceivedBinding
import com.syed.classconnect.databinding.ItemAiMessageSentBinding
import io.noties.markwon.Markwon

class AIChatAdapter : ListAdapter<AIChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_AI = 1
    }

    private val currentList2 = mutableListOf<AIChatMessage>()

    override fun getItemViewType(position: Int) =
        if (getItem(position).isUser) TYPE_USER else TYPE_AI

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            UserViewHolder(
                ItemAiMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            AIViewHolder(
                ItemAiMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> holder.bind(getItem(position))
            is AIViewHolder -> holder.bind(getItem(position))
        }
    }

    class UserViewHolder(private val b: ItemAiMessageSentBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: AIChatMessage) {
            b.tvMessage.text = item.text
        }
    }

    class AIViewHolder(private val b: ItemAiMessageReceivedBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: AIChatMessage) {
            val markwon = Markwon.create(b.root.context)
            markwon.setMarkdown(b.tvMessage, item.text)
        }
    }

    fun addErrorMessage(error: String) {
        val list = currentList.toMutableList()
        list.add(AIChatMessage("⚠️ $error", isUser = false))
        submitList(list)
    }

    class DiffCallback : DiffUtil.ItemCallback<AIChatMessage>() {
        override fun areItemsTheSame(a: AIChatMessage, b: AIChatMessage) = a === b
        override fun areContentsTheSame(a: AIChatMessage, b: AIChatMessage) = a == b
    }
}

