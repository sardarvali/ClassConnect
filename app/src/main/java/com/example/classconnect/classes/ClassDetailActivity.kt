package com.syed.classconnect.ui.classes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.tabs.TabLayoutMediator
import com.syed.classconnect.R
import com.syed.classconnect.databinding.ActivityClassDetailBinding
import com.syed.classconnect.ui.assignments.AssignmentsFragment
import com.syed.classconnect.ui.attendance.AttendanceFragment
import com.syed.classconnect.ui.chat.ChatFragment
import com.syed.classconnect.ui.classes.feed.FeedFragment
import com.syed.classconnect.ui.quiz.QuizListFragment
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.toColorInt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class ClassDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classId = intent.getStringExtra(Constants.EXTRA_CLASS_ID) ?: run { finish(); return }
        val className = intent.getStringExtra(Constants.EXTRA_CLASS_NAME) ?: ""
        val classColor = intent.getStringExtra(Constants.EXTRA_CLASS_COLOR) ?: "#1E6FFF"
        val classCode = intent.getStringExtra("classCode") ?: ""

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = className
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Show class code in pill
        if (classCode.isNotEmpty()) {
            binding.classCodeText.text = classCode
            binding.classCodePill.visibility = View.VISIBLE

            // Tap to copy
            binding.classCodePill.setOnClickListener {
                val clipboard = getSystemService(ClipboardManager::class.java)
                clipboard.setPrimaryClip(ClipData.newPlainText("Class Code", classCode))
                Toast.makeText(this, getString(R.string.class_code_copied), Toast.LENGTH_SHORT).show()
            }

            // Long-press to share
            binding.classCodePill.setOnLongClickListener {
                startActivity(Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, getString(R.string.class_code_share_text, classCode))
                    }, "Share class code"
                ))
                true
            }
        } else {
            binding.classCodePill.visibility = View.GONE
        }

        // Tap on toolbar subtitle (class code) copies it to clipboard
        binding.toolbar.setOnClickListener {
            if (classCode.isNotEmpty()) {
                val clipboard = getSystemService(ClipboardManager::class.java)
                clipboard.setPrimaryClip(ClipData.newPlainText("Class Code", classCode))
                Toast.makeText(this, getString(R.string.class_code_copied), Toast.LENGTH_SHORT).show()
            }
        }

        // Apply gradient header using the class color as start, ending at a darker tone
        val startColor = classColor.toColorInt()
        val endColor = ContextCompat.getColor(this, R.color.bg_surface)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(startColor, endColor)
        )
        binding.headerBackground.background = gradient

        // Rounded pill tab indicator
        binding.tabLayout.setSelectedTabIndicator(R.drawable.bg_tab_indicator)

        // Load user role then set up tabs
        val userRole = intent.getStringExtra(Constants.EXTRA_USER_ROLE)
        if (userRole != null) {
            setupTabs(classId, userRole)
        } else {
            // Fetch role from Firestore
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val doc = FirebaseFirestore.getInstance()
                            .collection("users").document(uid).get().await()
                        val role = doc.getString("role") ?: Constants.ROLE_STUDENT
                        setupTabs(classId, role)
                    } catch (e: Exception) {
                        setupTabs(classId, Constants.ROLE_STUDENT)
                    }
                }
            } else {
                setupTabs(classId, Constants.ROLE_STUDENT)
            }
        }
    }

    private fun setupTabs(classId: String, userRole: String) {
        val fragments = mutableListOf(
            FeedFragment.newInstance(classId),
            AssignmentsFragment.newInstance(classId),
            AttendanceFragment.newInstance(classId),
            QuizListFragment.newInstance(classId),
            ChatFragment.newInstance(classId)
        )
        val tabTitles = mutableListOf(
            getString(R.string.tab_feed),
            getString(R.string.tab_assignments),
            getString(R.string.tab_attendance),
            getString(R.string.tab_quiz),
            getString(R.string.tab_chat)
        )

        // Only add Students tab for teachers and admins
        if (userRole == Constants.ROLE_TEACHER || userRole == Constants.ROLE_ADMIN) {
            fragments.add(StudentsFragment.newInstance(classId))
            tabTitles.add(getString(R.string.tab_students))
        }

        binding.viewPager.adapter = ClassTabAdapter(this, fragments)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = tabTitles[pos]
        }.attach()

        // Handle deep-link: open specific tab if requested
        val openTab = intent.getIntExtra(Constants.EXTRA_TAB_INDEX, -1)
        if (openTab in 0 until fragments.size) {
            binding.viewPager.post {
                binding.viewPager.currentItem = openTab
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    @Suppress("DEPRECATION")
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_down_exit)
    }

    companion object {
        fun start(context: Context, classId: String, className: String, color: String, classCode: String = "") {
            context.startActivity(Intent(context, ClassDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_CLASS_ID, classId)
                putExtra(Constants.EXTRA_CLASS_NAME, className)
                putExtra(Constants.EXTRA_CLASS_COLOR, color)
                putExtra("classCode", classCode)
            })
            // Smooth slide-up entrance from the card
            if (context is android.app.Activity) {
                @Suppress("DEPRECATION")
                context.overridePendingTransition(R.anim.slide_up_enter, R.anim.fade_out)
            }
        }
    }
}
