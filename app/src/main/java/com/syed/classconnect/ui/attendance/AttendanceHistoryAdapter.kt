package com.syed.classconnect.ui.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.AttendanceRecord
import com.syed.classconnect.databinding.ItemAttendanceHistoryBinding

class AttendanceHistoryAdapter : RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder>() {

    private val items = mutableListOf<AttendanceRecord>()

    fun submitList(list: List<AttendanceRecord>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(private val b: ItemAttendanceHistoryBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: AttendanceRecord) {
            b.tvDate.text = item.date
            b.tvPresent.text = "✓ ${item.present.size} present"
            b.tvAbsent.text = "✗ ${item.absent.size} absent"
            val totalMarked = item.present.size + item.absent.size
            val percentage = if (totalMarked > 0) item.present.size * 100 / totalMarked else 0
            b.tvPct.text = "$percentage%"
            b.tvPct.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemAttendanceHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size
}

