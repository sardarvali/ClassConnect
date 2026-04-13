package com.syed.classconnect.ui.classes

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syed.classconnect.data.repository.ClassRepository
import com.syed.classconnect.databinding.BottomSheetClassSettingsBinding
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ClassSettingsBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var classRepository: ClassRepository

    private var _binding: BottomSheetClassSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetClassSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val classId = requireArguments().getString(ARG_CLASS_ID).orEmpty()
        val role = requireArguments().getString(ARG_ROLE).orEmpty()
        val canEdit = role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN

        if (!canEdit || classId.isBlank()) {
            Toast.makeText(requireContext(), "You cannot edit this class", Toast.LENGTH_SHORT).show()
            dismissAllowingStateLoss()
            return
        }

        binding.etClassName.setText(requireArguments().getString(ARG_NAME).orEmpty())
        binding.etSubject.setText(requireArguments().getString(ARG_SUBJECT).orEmpty())
        binding.etColor.setText(requireArguments().getString(ARG_COLOR).orEmpty())

        binding.btnSave.setOnClickListener {
            val name = binding.etClassName.text?.toString()?.trim().orEmpty()
            val subject = binding.etSubject.text?.toString()?.trim().orEmpty()
            val color = binding.etColor.text?.toString()?.trim().orEmpty()

            if (name.isBlank()) {
                binding.etClassName.error = "Class name is required"
                return@setOnClickListener
            }

            if (color.isNotBlank()) {
                val validHex = try {
                    Color.parseColor(color)
                    true
                } catch (_: IllegalArgumentException) {
                    false
                }
                if (!validHex) {
                    binding.etColor.error = "Use a valid hex color like #1E6FFF"
                    return@setOnClickListener
                }
            }

            binding.btnSave.isEnabled = false
            lifecycleScope.launch {
                val updates = mutableMapOf<String, Any>(
                    "name" to name,
                    "subject" to subject
                )
                if (color.isNotBlank()) updates["color"] = color

                val result = classRepository.updateClassFields(classId, updates)
                binding.btnSave.isEnabled = true
                if (result.isSuccess) {
                    parentFragmentManager.setFragmentResult(
                        REQUEST_KEY,
                        bundleOf(RESULT_UPDATED to true)
                    )
                    dismissAllowingStateLoss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        result.exceptionOrNull()?.message ?: "Failed to update class",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "class_settings_result"
        const val RESULT_UPDATED = "updated"

        private const val ARG_CLASS_ID = "arg_class_id"
        private const val ARG_ROLE = "arg_role"
        private const val ARG_NAME = "arg_name"
        private const val ARG_SUBJECT = "arg_subject"
        private const val ARG_COLOR = "arg_color"

        fun newInstance(
            classId: String,
            role: String,
            name: String,
            subject: String,
            color: String
        ): ClassSettingsBottomSheet {
            return ClassSettingsBottomSheet().apply {
                arguments = bundleOf(
                    ARG_CLASS_ID to classId,
                    ARG_ROLE to role,
                    ARG_NAME to name,
                    ARG_SUBJECT to subject,
                    ARG_COLOR to color
                )
            }
        }
    }
}

