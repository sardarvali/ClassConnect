package com.syed.classconnect.ui.classes.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.databinding.BottomSheetPostAnnouncementBinding
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostAnnouncementDialog : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPostAnnouncementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModels()
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var classId: String

    companion object {
        fun newInstance(classId: String) = PostAnnouncementDialog().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPostAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        val uid = auth.currentUser?.uid ?: return
        viewModel.loadUserRole(uid)

        binding.btnPost.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val body = binding.etBody.text.toString().trim()
            if (title.isEmpty() || body.isEmpty()) return@setOnClickListener
            val currentUser = viewModel.currentUserName
            viewModel.postAnnouncement(classId, title, body, uid, currentUser)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}

