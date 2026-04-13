package com.syed.classconnect.ui.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syed.classconnect.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimetableEditDialog : BottomSheetDialogFragment() {

    private val day by lazy { requireArguments().getString("day") ?: "" }
    private val classId by lazy { requireArguments().getString("classId") ?: "" }
    private val viewModel: ClassViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_timetable_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvDialogTitle).text =
            getString(R.string.timetable_edit_title, day)

        val startPicker = view.findViewById<android.widget.TimePicker>(R.id.startTimePicker)
        val endPicker = view.findViewById<android.widget.TimePicker>(R.id.endTimePicker)
        startPicker.setIs24HourView(false)
        endPicker.setIs24HourView(false)

        val existing = arguments?.getString("existingTime") ?: ""
        if (existing.isNotBlank()) {
            try {
                val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.ENGLISH)
                val parts = existing.split("–", "-").map { it.trim() }
                if (parts.size >= 2) {
                    val s = java.util.Calendar.getInstance().apply { time = sdf.parse(parts[0])!! }
                    val e = java.util.Calendar.getInstance().apply { time = sdf.parse(parts[1])!! }
                    startPicker.hour = s[java.util.Calendar.HOUR_OF_DAY]
                    startPicker.minute = s[java.util.Calendar.MINUTE]
                    endPicker.hour = e[java.util.Calendar.HOUR_OF_DAY]
                    endPicker.minute = e[java.util.Calendar.MINUTE]
                }
            } catch (_: Exception) {
                setDefaultTimes(startPicker, endPicker)
            }
        } else {
            setDefaultTimes(startPicker, endPicker)
        }

        val tvTimeError = view.findViewById<TextView>(R.id.tvTimeError)
        val btnAddAppend = view.findViewById<View>(R.id.btnAddAppend)
        val btnConfirm = view.findViewById<TextView>(R.id.btnConfirm)

        if (existing.isNotBlank()) {
            btnAddAppend.visibility = View.VISIBLE
            btnConfirm.text = getString(R.string.timetable_replace_session)
        }

        btnAddAppend.setOnClickListener {
            val startHour = startPicker.hour
            val startMin = startPicker.minute
            val endHour = endPicker.hour
            val endMin = endPicker.minute

            if ((endHour * 60 + endMin) <= (startHour * 60 + startMin)) {
                tvTimeError?.visibility = View.VISIBLE
                tvTimeError?.text = getString(R.string.timetable_end_after_start)
                return@setOnClickListener
            }

            val startStr = formatSingleTime(startHour, startMin)
            val endStr = formatSingleTime(endHour, endMin)
            val finalTime =
                if (existing.isNotBlank()) "$existing\n$startStr - $endStr" else "$startStr - $endStr"

            viewModel.updateScheduleDay(classId, day, finalTime)
            dismiss()
        }

        view.findViewById<View>(R.id.btnConfirm).setOnClickListener {
            val startHour = startPicker.hour
            val startMin = startPicker.minute
            val endHour = endPicker.hour
            val endMin = endPicker.minute

            if ((endHour * 60 + endMin) <= (startHour * 60 + startMin)) {
                tvTimeError?.visibility = View.VISIBLE
                tvTimeError?.text = getString(R.string.timetable_end_after_start)
                return@setOnClickListener
            }
            tvTimeError?.visibility = View.GONE

            val formatted = formatTime(startHour, startMin, endHour, endMin)
            viewModel.updateScheduleDay(classId, day, formatted)
            dismiss()
        }

        view.findViewById<View>(R.id.btnCancel).setOnClickListener { dismiss() }
    }

    private fun setDefaultTimes(
        startPicker: android.widget.TimePicker,
        endPicker: android.widget.TimePicker
    ) {
        startPicker.hour = 9; startPicker.minute = 0
        endPicker.hour = 10; endPicker.minute = 0
    }

    private fun formatSingleTime(h: Int, m: Int): String {
        val amPm = if (h < 12) "AM" else "PM"
        val h12 = when {
            h == 0 -> 12; h > 12 -> h - 12; else -> h
        }
        return "$h12:${m.toString().padStart(2, '0')} $amPm"
    }

    private fun formatTime(sH: Int, sM: Int, eH: Int, eM: Int): String {
        return "${formatSingleTime(sH, sM)} - ${formatSingleTime(eH, eM)}"
    }

    companion object {
        fun newInstance(
            day: String,
            classId: String,
            existingTime: String = ""
        ): TimetableEditDialog {
            val fragment = TimetableEditDialog()
            fragment.arguments = Bundle().apply {
                putString("day", day)
                putString("classId", classId)
                putString("existingTime", existingTime)
            }
            return fragment
        }
    }
}
