package com.syed.classconnect.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.ImageView
import android.widget.ProgressBar
import android.view.View
import android.widget.FrameLayout

/**
 * Adapter for customizable dashboard cards with drag-and-drop support.
 * Allows users to reorder cards and saves layout to Firestore.
 */
class DashboardCardAdapter(
    private val cards: MutableList<DashboardCard>,
    private val onCardClick: (DashboardCard) -> Unit,
    private val onLayoutChanged: (List<DashboardCard>) -> Unit
) : RecyclerView.Adapter<DashboardCardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardTitle: TextView = itemView.findViewById(android.R.id.title)
        private val cardDescription: TextView? = itemView.findViewById(android.R.id.content)
        private val cardIcon: ImageView? = itemView.findViewById(android.R.id.icon)
        private val cardProgress: ProgressBar? = itemView.findViewById(android.R.id.progress)
        private val root: FrameLayout = itemView as FrameLayout

        fun bind(card: DashboardCard) {
            cardTitle.text = card.title
            cardDescription?.text = card.description
            cardIcon?.setImageResource(card.iconResId)
            cardProgress?.progress = card.progressPercentage

            // Hover effect: Card elevates slightly
            root.setOnHoverListener { _, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_HOVER_ENTER -> {
                        root.elevation = 16f
                        true
                    }
                    android.view.MotionEvent.ACTION_HOVER_EXIT -> {
                        root.elevation = 8f
                        true
                    }
                    else -> false
                }
            }

            // Long-press to snooze notification
            root.setOnLongClickListener {
                // Show snooze dialog
                true
            }

            // Click to view details
            root.setOnClickListener {
                onCardClick(card)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    /**
     * Updates card list and notifies adapter
     */
    fun updateCards(newCards: List<DashboardCard>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    /**
     * Moves card at fromPosition to toPosition (for drag-and-drop)
     */
    fun moveCard(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                cards[i] = cards[i + 1].also { cards[i + 1] = cards[i] }
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                cards[i] = cards[i - 1].also { cards[i - 1] = cards[i] }
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        onLayoutChanged(cards)
    }

    /**
     * Creates ItemTouchHelper for drag-and-drop
     */
    fun getItemTouchHelper(): ItemTouchHelper {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                moveCard(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Not used in this implementation
            }
        }
        return ItemTouchHelper(callback)
    }
}

/**
 * Model for dashboard card data
 */
data class DashboardCard(
    val id: String,
    val title: String,
    val description: String,
    val iconResId: Int,
    val progressPercentage: Int = 0,
    val cardType: CardType = CardType.DEFAULT,
    val order: Int = 0
)

enum class CardType {
    TODAY_CLASSES,
    DEADLINES,
    ANNOUNCEMENTS,
    NEWS,
    PENDING_GRADING,
    STUDENT_PROGRESS,
    DEFAULT
}

