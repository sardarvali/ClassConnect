package com.syed.classconnect.ui.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentTimetableBinding
import com.syed.classconnect.databinding.ItemTimetableDayBinding
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TimetableFragment : Fragment(R.layout.fragment_timetable) {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassViewModel by activityViewModels()

    private val classId by lazy { requireArguments().getString(Constants.EXTRA_CLASS_ID) ?: "" }
    private val userRole by lazy {
        requireArguments().getString(Constants.EXTRA_USER_ROLE) ?: Constants.ROLE_STUDENT
    }

    private val dayRows = linkedMapOf<String, ItemTimetableDayBinding>()

    companion object {
        fun newInstance(classId: String, userRole: String) = TimetableFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.EXTRA_CLASS_ID, classId)
                putString(Constants.EXTRA_USER_ROLE, userRole)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // With ViewBinding, <include> tags generate nested binding properties 
        // if the included layout has its own binding generated.
        dayRows["Monday"] = binding.rowMonday
        dayRows["Tuesday"] = binding.rowTuesday
        dayRows["Wednesday"] = binding.rowWednesday
        dayRows["Thursday"] = binding.rowThursday
        dayRows["Friday"] = binding.rowFriday
        dayRows["Saturday"] = binding.rowSaturday
        dayRows["Sunday"] = binding.rowSunday

        dayRows.forEach { (day, rowBinding) ->
            rowBinding.tvDayName.text = day
        }

        val isTeacher = userRole == Constants.ROLE_TEACHER || userRole == Constants.ROLE_ADMIN

        viewModel.classDetail.observe(viewLifecycleOwner) { classRoom ->
            classRoom ?: return@observe

            val schedule = classRoom.schedule ?: emptyMap()
            val today = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date())

            dayRows.forEach { (day, rowBinding) ->
                val timeValue = schedule[day]
                bindDayRow(rowBinding, day, timeValue, isTeacher, day == today)
            }

            val todayTime = schedule[today]
            binding.todayCard.isVisible = true
            binding.tvTodaySchedule.text = if (!todayTime.isNullOrBlank()) {
                "Today ($today): $todayTime"
            } else {
                "No class scheduled for today ($today)"
            }
            binding.tvTodaySchedule.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (!todayTime.isNullOrBlank()) R.color.brand_primary else R.color.text_secondary
                )
            )
        }
    }

    private fun bindDayRow(
        rowBinding: ItemTimetableDayBinding,
        day: String,
        timeValue: String?,
        isTeacher: Boolean,
        isToday: Boolean
    ) {
        val rootCard = rowBinding.root as MaterialCardView
        if (isToday) {
            rootCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.brand_primary)
            rootCard.strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_focus)
        } else {
            rootCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.border_subtle)
            rootCard.strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_default)
        }

        if (timeValue.isNullOrBlank()) {
            rowBinding.tvTimeDisplay.text = "Not scheduled"
            rowBinding.tvTimeDisplay.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_secondary
                )
            )
            rowBinding.btnClearDay.isVisible = false
        } else {
            rowBinding.tvTimeDisplay.text = normalizeTime(timeValue)
            rowBinding.tvTimeDisplay.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_primary
                )
            )
            rowBinding.btnClearDay.isVisible = isTeacher
        }

        rowBinding.btnEditDay.isVisible = isTeacher

        if (isTeacher) {
            val clickListener = View.OnClickListener { showTimePickerFor(day) }
            rootCard.setOnClickListener(clickListener)
            rowBinding.btnEditDay.setOnClickListener(clickListener)
            rowBinding.btnClearDay.setOnClickListener {
                viewModel.updateScheduleDay(classId, day, "")
            }
        }
    }

    private fun normalizeTime(raw: String): String {
        if (raw.contains("AM", ignoreCase = true) || raw.contains(
                "PM",
                ignoreCase = true
            )
        ) return raw
        return try {
            val parts = raw.replace(" to ", " – ").replace("-", " – ").split("–").map { it.trim() }
            if (parts.size == 2) {
                fun toAmPm(t: String): String {
                    val h = t.split(":")[0].toIntOrNull() ?: return t
                    val m = if (t.contains(":")) t.split(":")[1].toIntOrNull() ?: 0 else 0
                    val amPm = if (h < 12) "AM" else "PM"
                    val h12 = if (h > 12) h - 12 else if (h == 0) 12 else h
                    return "$h12:${m.toString().padStart(2, '0')} $amPm"
                }
                "${toAmPm(parts[0])} – ${toAmPm(parts[1])}"
            } else raw
        } catch (e: Exception) {
            raw
        }
    }

    private fun showTimePickerFor(day: String) {
        val existing = viewModel.classDetail.value?.schedule?.get(day) ?: ""
        TimetableEditDialog.newInstance(day, classId, existing)
            .show(childFragmentManager, "timetableEdit")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
