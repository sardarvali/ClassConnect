package com.syed.classconnect.ui.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.databinding.BottomSheetJoinClassBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JoinClassBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetJoinClassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetJoinClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etClassCode.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val code = s.toString().uppercase()
                if (s.toString() != code) { binding.etClassCode.setText(code); binding.etClassCode.setSelection(code.length) }
                if (code.length == 6) {
                    viewModel.getClassPreview(code) { classRoom ->
                        if (classRoom != null) {
                            binding.layoutPreview.show()
                            binding.tvPreviewName.text = classRoom.name
                            binding.tvPreviewTeacher.text = classRoom.teacherName
                            binding.tvPreviewSubject.text = classRoom.subject
                        } else binding.layoutPreview.hide()
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnJoin.setOnClickListener {
            val code = binding.etClassCode.text.toString().trim()
            if (code.length != 6) { binding.tilClassCode.error = "Enter a valid 6-character code"; return@setOnClickListener }
            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            viewModel.joinClass(code, uid)
        }

        viewModel.joinResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.btnJoin.isEnabled = false }
                is NetworkResult.Success -> { binding.progressBar.hide(); dismiss() }
                is NetworkResult.Error -> {
                    binding.progressBar.hide(); binding.btnJoin.isEnabled = true
                    showSnackbar(result.message)
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

