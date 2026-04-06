package com.syed.classconnect.ui.classes

import android.os.Build
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
        view.findViewById<TextView>(R.id.tvDialogTitle).text = "Set time for $day"

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        startPicker.hour = s.get(java.util.Calendar.HOUR_OF_DAY)
                        startPicker.minute = s.get(java.util.Calendar.MINUTE)
                        endPicker.hour = e.get(java.util.Calendar.HOUR_OF_DAY)
                        endPicker.minute = e.get(java.util.Calendar.MINUTE)
                    } else {
                        startPicker.currentHour = s.get(java.util.Calendar.HOUR_OF_DAY)
                        startPicker.currentMinute = s.get(java.util.Calendar.MINUTE)
                        endPicker.currentHour = e.get(java.util.Calendar.HOUR_OF_DAY)
                        endPicker.currentMinute = e.get(java.util.Calendar.MINUTE)
                    }
                }
            } catch (e: Exception) {
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
            btnConfirm.text = "Replace Session"
        }

        btnAddAppend.setOnClickListener {
            val startHour =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) startPicker.hour else startPicker.currentHour
            val startMin =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) startPicker.minute else startPicker.currentMinute
            val endHour =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) endPicker.hour else endPicker.currentHour
            val endMin =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) endPicker.minute else endPicker.currentMinute

            if ((endHour * 60 + endMin) <= (startHour * 60 + startMin)) {
                tvTimeError?.visibility = View.VISIBLE
                tvTimeError?.text = "End time must be after start time"
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
            val startHour =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) startPicker.hour else startPicker.currentHour
            val startMin =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) startPicker.minute else startPicker.currentMinute
            val endHour =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) endPicker.hour else endPicker.currentHour
            val endMin =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) endPicker.minute else endPicker.currentMinute

            if ((endHour * 60 + endMin) <= (startHour * 60 + startMin)) {
                tvTimeError?.visibility = View.VISIBLE
                tvTimeError?.text = "End time must be after start time"
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startPicker.hour = 9; startPicker.minute = 0
            endPicker.hour = 10; endPicker.minute = 0
        } else {
            startPicker.currentHour = 9; startPicker.currentMinute = 0
            endPicker.currentHour = 10; endPicker.currentMinute = 0
        }
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
