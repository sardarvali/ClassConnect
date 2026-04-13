package com.syed.classconnect.ui.classes.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.databinding.FragmentFeedBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModels()
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth

    private lateinit var adapter: FeedAdapter
    private lateinit var classId: String
=======
    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var adapter: FeedAdapter
    private lateinit var classId: String
    private var isTeacherOrAdmin = false
>>>>>>> final

    companion object {
        fun newInstance(classId: String) = FeedFragment().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

<<<<<<< HEAD
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return

        val uid = auth.currentUser?.uid ?: return
        viewModel.loadUserRole(uid)

        adapter = FeedAdapter(
<<<<<<< HEAD
            onPinClick = { ann -> viewModel.togglePin(classId, ann.id, !ann.isPinned) }
=======
            onPinClick = { ann -> viewModel.togglePin(classId, ann.id, !ann.isPinned) },
            onDeleteClick = { ann -> viewModel.deleteAnnouncement(classId, ann.id) },
            currentUserId = uid,
            isTeacherOrAdmin = false // default
>>>>>>> final
        )
        binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeed.adapter = adapter

        viewModel.loadFeed(classId)

        viewModel.feedItems.observe(viewLifecycleOwner) { items ->
<<<<<<< HEAD
            if (items.isEmpty()) { binding.layoutEmpty.show(); binding.rvFeed.hide() }
            else { binding.layoutEmpty.hide(); binding.rvFeed.show(); adapter.submitList(items) }
        }

        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
=======
            if (items.isEmpty()) {
                binding.layoutEmpty.show(); binding.rvFeed.hide()
            } else {
                binding.layoutEmpty.hide(); binding.rvFeed.show(); adapter.submitList(items)
            }
        }

        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            isTeacherOrAdmin = (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN)
            // Re-create adapter with known role if needed
            adapter = FeedAdapter(
                onPinClick = { ann -> viewModel.togglePin(classId, ann.id, !ann.isPinned) },
                onDeleteClick = { ann -> viewModel.deleteAnnouncement(classId, ann.id) },
                currentUserId = uid,
                isTeacherOrAdmin = isTeacherOrAdmin
            )
            binding.rvFeed.adapter = adapter
            adapter.submitList(viewModel.feedItems.value)

            if (isTeacherOrAdmin) {
>>>>>>> final
                binding.fab.show()
            } else {
                binding.fab.hide()
            }
        }

        binding.fab.setOnClickListener {
            PostAnnouncementDialog.newInstance(classId)
                .show(parentFragmentManager, "post_announcement")
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFeed(classId)
            binding.swipeRefresh.isRefreshing = false
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

