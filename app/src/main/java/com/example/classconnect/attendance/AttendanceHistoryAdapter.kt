package com.syed.classconnect.ui.attendance

import android.view.LayoutInflater
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

    inner class ViewHolder(private val b: ItemAttendanceHistoryBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: AttendanceRecord) {
            b.tvDate.text = item.date
            b.tvPresent.text = "✓ ${item.present.size} present"
            b.tvAbsent.text = "✗ ${item.absent.size} absent"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemAttendanceHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size
}

