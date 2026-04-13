package com.syed.classconnect.ui.classes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.databinding.BottomSheetCreateClassBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
<<<<<<< HEAD
import com.syed.classconnect.util.showSnackbar
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
=======
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
>>>>>>> final
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateClassBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCreateClassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassViewModel by viewModels()
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth

    private var selectedColor = Constants.CLASS_COLORS[0]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    @Inject
    lateinit var auth: FirebaseAuth

    private var selectedColor = Constants.CLASS_COLORS[0]

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = BottomSheetCreateClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorPicker()

        binding.btnCreate.setOnClickListener {
            val name = binding.etClassName.text.toString().trim()
            val subject = binding.etSubject.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
<<<<<<< HEAD
            if (name.isEmpty()) { binding.tilClassName.error = "Required"; return@setOnClickListener }
            if (subject.isEmpty()) { binding.tilSubject.error = "Required"; return@setOnClickListener }
=======
            if (name.isEmpty()) {
                binding.tilClassName.error = "Required"; return@setOnClickListener
            }
            if (subject.isEmpty()) {
                binding.tilSubject.error = "Required"; return@setOnClickListener
            }
>>>>>>> final

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            viewModel.currentUser.observe(viewLifecycleOwner) { user ->
                user ?: return@observe
                val classRoom = ClassRoom(
                    name = name, subject = subject, description = description,
                    teacherId = uid, teacherName = user.name,
                    institutionId = user.institutionId, color = selectedColor
                )
                viewModel.createClass(classRoom)
            }
            viewModel.loadUser(uid)
        }

        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            when (result) {
<<<<<<< HEAD
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.btnCreate.isEnabled = false }
=======
                is NetworkResult.Loading -> {
                    binding.progressBar.show(); binding.btnCreate.isEnabled = false
                }

>>>>>>> final
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    val classCode = result.data.second
                    showClassCodeDialog(classCode)
                }
<<<<<<< HEAD
=======

>>>>>>> final
                is NetworkResult.Error -> {
                    binding.progressBar.hide(); binding.btnCreate.isEnabled = true
                    showSnackbar(result.message)
                }
            }
        }
    }

    private fun showClassCodeDialog(code: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🎉 Class Created!")
            .setMessage(
                "Your class has been created successfully.\n\n" +
<<<<<<< HEAD
                "Share this code with your students so they can join:\n\n" +
                "📋  $code\n\n" +
                "Students can join via My Classes → Join Class"
            )
            .setPositiveButton("Copy Code") { _, _ ->
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("class_code", code))
                Snackbar.make(binding.root, "Code \"$code\" copied to clipboard!", Snackbar.LENGTH_SHORT).show()
=======
                        "Share this code with your students so they can join:\n\n" +
                        "📋  $code\n\n" +
                        "Students can join via My Classes → Join Class\n\n" +
                        "You can add the weekly timetable from the class's Timetable tab."
            )
            .setPositiveButton("Copy Code") { _, _ ->
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("class_code", code))
                Snackbar.make(
                    binding.root,
                    "Code \"$code\" copied to clipboard!",
                    Snackbar.LENGTH_SHORT
                ).show()
>>>>>>> final
                dismiss()
            }
            .setNegativeButton("Done") { _, _ -> dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun setupColorPicker() {
        val colorViews = listOf(
            binding.color1, binding.color2, binding.color3, binding.color4,
            binding.color5, binding.color6, binding.color7, binding.color8
        )
        Constants.CLASS_COLORS.forEachIndexed { index, color ->
            colorViews[index].setBackgroundColor(android.graphics.Color.parseColor(color))
            colorViews[index].setOnClickListener {
                selectedColor = color
                colorViews.forEach { it.scaleX = 1f; it.scaleY = 1f }
                colorViews[index].scaleX = 1.3f
                colorViews[index].scaleY = 1.3f
            }
        }
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
=======
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
>>>>>>> final
}


