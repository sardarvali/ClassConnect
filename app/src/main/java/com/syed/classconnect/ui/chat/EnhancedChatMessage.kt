package com.syed.classconnect.ui.chat

/**
 * Enhances chat messages with reactions and typing indicators
 */
data class EnhancedChatMessage(
    val messageId: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val reactions: Map<String, List<String>> = emptyMap(), // emoji -> list of users who reacted
    val isEdited: Boolean = false,
    val editedAt: Long? = null,
    val isDeleted: Boolean = false,
    val readReceipts: Map<String, Long> = emptyMap() // userId -> timestamp when read
) {
    fun getReactionSummary(): String {
        return reactions.entries.joinToString("  ") { (emoji, users) ->
            "$emoji ${users.size}"
        }
    }

    fun hasReaction(emoji: String, userId: String): Boolean {
        return reactions[emoji]?.contains(userId) == true
    }

    fun toggleReaction(emoji: String, userId: String) {
        val userList = reactions[emoji]?.toMutableList() ?: mutableListOf()
        if (userList.contains(userId)) {
            userList.remove(userId)
        } else {
            userList.add(userId)
        }
    }
}

/**
 * Represents typing status of a user in chat
 */
data class TypingIndicator(
    val userId: String,
    val userName: String,
    val isTyping: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Data class for read receipts
 */
data class ReadReceipt(
    val userId: String,
    val messageId: String,
    val timestamp: Long = System.currentTimeMillis()
)

