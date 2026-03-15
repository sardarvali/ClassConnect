package com.syed.classconnect.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.data.model.ChatMessage
import com.syed.classconnect.databinding.FragmentChatBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var adapter: ChatAdapter
    private lateinit var classId: String

    // Full message list for search filtering
    private var allMessages: List<ChatMessage> = emptyList()

    companion object {
        fun newInstance(classId: String) = ChatFragment().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        val uid = auth.currentUser?.uid ?: return

        viewModel.loadCurrentUser(uid)
        viewModel.loadClassDetail(classId)

        adapter = ChatAdapter(
            currentUserId = uid,
            onLongPress = { msg ->
                if (msg.senderId == uid) {
                    showMessageOptionsDialog(msg)
                }
            }
        )
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = adapter

        viewModel.loadMessages(classId)

        viewModel.messages.observe(viewLifecycleOwner) { result ->
            if (result is NetworkResult.Success) {
                allMessages = result.data
                adapter.submitList(result.data)
                if (result.data.isNotEmpty())
                    binding.rvMessages.scrollToPosition(result.data.size - 1)
            }
        }

        viewModel.clearResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> Snackbar.make(binding.root, getString(R.string.chat_cleared), Snackbar.LENGTH_SHORT).show()
                is NetworkResult.Error -> Snackbar.make(binding.root, result.message, Snackbar.LENGTH_SHORT).show()
                is NetworkResult.Loading -> { /* loading state, no UI action needed */ }
            }
        }

        binding.btnSend.setOnClickListener { sendMessage(uid) }

        binding.fabScrollDown.setOnClickListener {
            val count = adapter.itemCount
            if (count > 0) binding.rvMessages.scrollToPosition(count - 1)
            binding.fabScrollDown.hide()
        }

        binding.rvMessages.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1)) binding.fabScrollDown.hide()
                else if (dy < 0) binding.fabScrollDown.show()
            }
        })

        // Register options menu
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_chat, menu)

                // Wire up SearchView
                val searchItem = menu.findItem(R.id.action_chat_search)
                val searchView = searchItem?.actionView as? SearchView
                searchView?.queryHint = getString(R.string.chat_search_messages)
                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterMessages(newText.orEmpty())
                        return true
                    }
                })

                // Update mute title based on state
                viewModel.isMuted.observe(viewLifecycleOwner) { muted ->
                    menu.findItem(R.id.action_chat_mute)?.title =
                        if (muted) getString(R.string.chat_unmute_notifications)
                        else getString(R.string.chat_mute_notifications)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_chat_mute -> {
                        viewModel.toggleMute()
                        val muted = viewModel.isMuted.value ?: false
                        val msg = if (!muted) getString(R.string.chat_muted) else getString(R.string.chat_unmuted)
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_chat_class_code -> {
                        showClassCodeDialog()
                        true
                    }
                    R.id.action_chat_clear -> {
                        showClearChatConfirmDialog()
                        true
                    }
                    R.id.action_chat_members -> {
                        showMembersDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun filterMessages(query: String) {
        val filtered = if (query.isBlank()) allMessages
        else allMessages.filter { it.text.contains(query, ignoreCase = true) }
        adapter.submitList(filtered)
    }

    private fun showMessageOptionsDialog(msg: ChatMessage) {
        val options = arrayOf("Delete Message", "Copy Text")
        MaterialAlertDialogBuilder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.deleteMessage(classId, msg.id)
                    1 -> {
                        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("message", msg.text))
                        Snackbar.make(binding.root, "Message copied", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }

    private fun showClassCodeDialog() {
        val classRoom = viewModel.classDetail.value
        val code = classRoom?.classCode ?: "Loading…"
        val className = classRoom?.name ?: ""
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.class_code_dialog_title))
            .setMessage("Share this code with students to join $className:\n\n📋  $code")
            .setPositiveButton(getString(R.string.copy_to_clipboard)) { _, _ ->
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("class_code", code))
                Snackbar.make(binding.root, getString(R.string.class_code_copied), Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showClearChatConfirmDialog() {
        // Only teachers/admins should clear chat — check role
        val user = viewModel.currentUser.value
        if (user?.role == Constants.ROLE_STUDENT) {
            Snackbar.make(binding.root, "Only teachers can clear chat history", Snackbar.LENGTH_SHORT).show()
            return
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.chat_clear_confirm_title))
            .setMessage(getString(R.string.chat_clear_confirm_msg))
            .setPositiveButton("Clear") { _, _ -> viewModel.clearChat(classId) }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showMembersDialog() {
        val classRoom = viewModel.classDetail.value ?: run {
            Snackbar.make(binding.root, "Loading class info…", Snackbar.LENGTH_SHORT).show()
            return
        }
        val memberCount = classRoom.studentIds.size + 1 // +1 for teacher
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.chat_members))
            .setMessage(
                "👨‍🏫 Teacher: ${classRoom.teacherName}\n\n" +
                "👥 Students: ${classRoom.studentIds.size}\n\n" +
                "Total members: $memberCount"
            )
            .setPositiveButton(getString(R.string.cancel), null)
            .show()
    }

    private fun sendMessage(uid: String) {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return
        val user = viewModel.currentUser.value ?: return
        val message = ChatMessage(
            senderId = uid, senderName = user.name,
            senderPhotoUrl = user.photoUrl, text = text,
            timestamp = Timestamp.now()
        )
        viewModel.sendMessage(classId, message)
        binding.etMessage.setText("")
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
