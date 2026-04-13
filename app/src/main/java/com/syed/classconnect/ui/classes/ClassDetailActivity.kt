package com.syed.classconnect.ui.classes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
<<<<<<< HEAD
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.tabs.TabLayoutMediator
=======
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
>>>>>>> final
import com.syed.classconnect.R
import com.syed.classconnect.databinding.ActivityClassDetailBinding
import com.syed.classconnect.ui.assignments.AssignmentsFragment
import com.syed.classconnect.ui.attendance.AttendanceFragment
import com.syed.classconnect.ui.chat.ChatFragment
import com.syed.classconnect.ui.classes.feed.FeedFragment
import com.syed.classconnect.ui.quiz.QuizListFragment
import com.syed.classconnect.util.Constants
<<<<<<< HEAD
import com.syed.classconnect.util.toColorInt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
=======
import dagger.hilt.android.AndroidEntryPoint
>>>>>>> final
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class ClassDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassDetailBinding
<<<<<<< HEAD

    override fun onCreate(savedInstanceState: Bundle?) {
=======
    private val viewModel: ClassViewModel by viewModels()

    private var classId: String = ""
    private var userRole: String = Constants.ROLE_STUDENT
    private var currentClassColor: String = "#1E6FFF"
    private val launchedWithSharedElements: Boolean by lazy {
        !intent.getStringExtra(Constants.EXTRA_SHARED_BG_TRANSITION_NAME).isNullOrBlank() &&
            !intent.getStringExtra(Constants.EXTRA_SHARED_TITLE_TRANSITION_NAME).isNullOrBlank()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
>>>>>>> final
        super.onCreate(savedInstanceState)
        binding = ActivityClassDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
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
=======
        postponeEnterTransition()

        intent.getStringExtra(Constants.EXTRA_SHARED_BG_TRANSITION_NAME)?.let { transitionName ->
            ViewCompat.setTransitionName(binding.headerGradient, transitionName)
        }
        intent.getStringExtra(Constants.EXTRA_SHARED_TITLE_TRANSITION_NAME)?.let { transitionName ->
            ViewCompat.setTransitionName(binding.tvClassName, transitionName)
        }

        binding.root.post { startPostponedEnterTransition() }

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { v, insets ->
            val statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusInsets.top)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.viewPager) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(bottom = bars.bottom)
            insets
        }

        classId = intent.getStringExtra(Constants.EXTRA_CLASS_ID) ?: run { finish(); return }

        // Initial setup from intent extras
        val className = intent.getStringExtra(Constants.EXTRA_CLASS_NAME) ?: ""
        val classColor = intent.getStringExtra(Constants.EXTRA_CLASS_COLOR) ?: "#1E6FFF"
        currentClassColor = classColor
        val classCode = intent.getStringExtra("classCode") ?: ""
        val classSubject = intent.getStringExtra("classSubject") ?: ""
        val teacherName = intent.getStringExtra("teacherName") ?: ""
        val studentCount = intent.getIntExtra("studentCount", 0)

        // Pre-fill header
        binding.tvClassName.text = className
        binding.tvSubject.text = classSubject
        binding.tvTeacherName.text = teacherName
        binding.tvMemberCount.text =
            "$studentCount ${if (studentCount == 1) "member" else "members"}"

        if (classCode.isNotEmpty()) {
            binding.tvClassCode.text = classCode
            binding.classCodePill.visibility = View.VISIBLE
>>>>>>> final
        } else {
            binding.classCodePill.visibility = View.GONE
        }

<<<<<<< HEAD
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
=======
        binding.headerGradient.background = buildGradient(classColor)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitClassDetail()
            }
        })

        // Setup role and tabs
        val intentRole = intent.getStringExtra(Constants.EXTRA_USER_ROLE)
        if (intentRole != null) {
            userRole = intentRole
            setupHeader()
            setupTabs()
        } else {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                lifecycleScope.launch {
                    try {
                        val doc = FirebaseFirestore.getInstance()
                            .collection("users").document(uid).get().await()
                        userRole = doc.getString("role") ?: Constants.ROLE_STUDENT
                    } catch (_: Exception) {
                    }
                    setupHeader()
                    setupTabs()
                }
            } else {
                setupHeader()
                setupTabs()
            }
        }

        supportFragmentManager.setFragmentResultListener(
            ClassSettingsBottomSheet.REQUEST_KEY,
            this
        ) { _, result ->
            if (result.getBoolean(ClassSettingsBottomSheet.RESULT_UPDATED, false)) {
                Snackbar.make(binding.root, "Class updated", Snackbar.LENGTH_SHORT).show()
                viewModel.loadClassDetail(classId)
            }
        }

        observeClassData()

        // Fetch fresh data
        viewModel.loadClassDetail(classId)
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            exitClassDetail()
        }

        binding.classCodePill.setOnClickListener {
            val code = binding.tvClassCode.text.toString()
            if (code.isNotBlank()) {
                val clipboard = getSystemService(ClipboardManager::class.java)
                clipboard.setPrimaryClip(ClipData.newPlainText("Class Code", code))
                Snackbar.make(binding.root, "Class code $code copied!", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.classCodePill.setOnLongClickListener {
            val code = binding.tvClassCode.text.toString()
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Join my class on ClassConnect! Code: $code")
                    }, "Share class code"
                )
            )
            true
        }

        binding.btnSettings.setOnClickListener {
            ClassSettingsBottomSheet.newInstance(
                classId = classId,
                role = userRole,
                name = binding.tvClassName.text?.toString().orEmpty(),
                subject = binding.tvSubject.text?.toString().orEmpty(),
                color = currentClassColor
            ).show(supportFragmentManager, "classSettings")
        }

        binding.btnChat.setOnClickListener {
            val chatTabIndex = getTabIndex("Chat")
            if (chatTabIndex >= 0) binding.viewPager.setCurrentItem(chatTabIndex, true)
        }
    }

    private fun observeClassData() {
        viewModel.classDetail.observe(this) { classRoom ->
            classRoom ?: return@observe

            if (classRoom.classCode.isNotEmpty()) {
                binding.tvClassCode.text = classRoom.classCode
                binding.classCodePill.visibility = View.VISIBLE
            } else {
                binding.classCodePill.visibility = View.GONE
            }

            binding.tvClassName.text = classRoom.name
            binding.tvSubject.text = classRoom.subject
            binding.tvTeacherName.text = classRoom.teacherName
            val studentCount = classRoom.studentIds.size
            binding.tvMemberCount.text =
                "$studentCount ${if (studentCount == 1) "member" else "members"}"

            binding.headerGradient.background = buildGradient(classRoom.color)
            currentClassColor = classRoom.color
        }
    }

    private fun buildGradient(hexColor: String): GradientDrawable {
        val colorInt = try {
            Color.parseColor(hexColor)
        } catch (e: Exception) {
            Color.parseColor("#1E6FFF")
        }

        // Darken the color slightly for the start
        val hsv = FloatArray(3)
        Color.colorToHSV(colorInt, hsv)
        hsv[2] *= 0.6f // darken value
        val darkColorInt = Color.HSVToColor(hsv)

        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(darkColorInt, colorInt)
        )
    }

    private fun setupTabs() {
        val fragments = mutableListOf<androidx.fragment.app.Fragment>(
            FeedFragment.newInstance(classId),
            TimetableFragment.newInstance(classId, userRole),
>>>>>>> final
            AssignmentsFragment.newInstance(classId),
            AttendanceFragment.newInstance(classId),
            QuizListFragment.newInstance(classId),
            ChatFragment.newInstance(classId)
        )
<<<<<<< HEAD
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
=======

        val tabTitles = mutableListOf(
            "Feed",
            "Timetable",
            "Assignments",
            "Attendance",
            "Quiz",
            "Chat"
        )

        if (userRole == Constants.ROLE_TEACHER || userRole == Constants.ROLE_ADMIN) {
            fragments.add(StudentsFragment.newInstance(classId))
            tabTitles.add("Students")
        }

        binding.viewPager.adapter = ClassTabAdapter(this, fragments)
        binding.viewPager.offscreenPageLimit = 2

>>>>>>> final
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = tabTitles[pos]
        }.attach()

<<<<<<< HEAD
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
=======
        // Shrink/extend chat button based on active tab
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (tabTitles.getOrNull(position) == "Chat") {
                    binding.btnChat.visibility = View.GONE
                } else {
                    binding.btnChat.visibility = View.VISIBLE
                }
            }
        })

        val openTab = intent.getIntExtra(Constants.EXTRA_TAB_INDEX, -1)
        if (openTab in 0 until fragments.size) {
            binding.viewPager.post { binding.viewPager.currentItem = openTab }
        }
    }

    private fun getTabIndex(tabName: String): Int {
        for (i in 0 until binding.tabLayout.tabCount) {
            if (binding.tabLayout.getTabAt(i)?.text?.toString()
                    ?.equals(tabName, ignoreCase = true) == true
            ) {
                return i
            }
        }
        return -1
    }

    private fun exitClassDetail() {
        if (launchedWithSharedElements) {
            finishAfterTransition()
        } else {
            @Suppress("DEPRECATION")
            super.finish()
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.fade_in, R.anim.slide_down_exit)
        }
    }

    companion object {
        fun start(
            context: Context,
            classId: String,
            className: String,
            color: String,
            classCode: String = "",
            subject: String = "",
            teacherName: String = "",
            studentCount: Int = 0,
            sharedBackgroundView: View? = null,
            sharedTitleView: View? = null
        ) {
            val intent = Intent(context, ClassDetailActivity::class.java).apply {
>>>>>>> final
                putExtra(Constants.EXTRA_CLASS_ID, classId)
                putExtra(Constants.EXTRA_CLASS_NAME, className)
                putExtra(Constants.EXTRA_CLASS_COLOR, color)
                putExtra("classCode", classCode)
<<<<<<< HEAD
            })
            // Smooth slide-up entrance from the card
=======
                putExtra("classSubject", subject)
                putExtra("teacherName", teacherName)
                putExtra("studentCount", studentCount)
                sharedBackgroundView?.let { bgView ->
                    ViewCompat.getTransitionName(bgView)?.let {
                        putExtra(Constants.EXTRA_SHARED_BG_TRANSITION_NAME, it)
                    }
                }
                sharedTitleView?.let { titleView ->
                    ViewCompat.getTransitionName(titleView)?.let {
                        putExtra(Constants.EXTRA_SHARED_TITLE_TRANSITION_NAME, it)
                    }
                }
            }

            if (context is android.app.Activity && sharedBackgroundView != null && sharedTitleView != null) {
                val bgName = ViewCompat.getTransitionName(sharedBackgroundView)
                val titleName = ViewCompat.getTransitionName(sharedTitleView)
                if (!bgName.isNullOrBlank() && !titleName.isNullOrBlank()) {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context,
                        Pair(sharedBackgroundView, bgName),
                        Pair(sharedTitleView, titleName)
                    )
                    ActivityCompat.startActivity(context, intent, options.toBundle())
                    return
                }
            }

            context.startActivity(intent)
>>>>>>> final
            if (context is android.app.Activity) {
                @Suppress("DEPRECATION")
                context.overridePendingTransition(R.anim.slide_up_enter, R.anim.fade_out)
            }
        }
    }
}
