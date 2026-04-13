package com.syed.classconnect.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Enhanced role-based onboarding implementation
 * Creates role-specific ViewPager screens with animations
 */
class RoleBasedOnboardingAdapter(
    fragment: Fragment,
    private val userRole: String
) : FragmentStateAdapter(fragment) {

    private val pages = mutableListOf<OnboardingStep>()

    init {
        setupPagesForRole(userRole)
    }

    private fun setupPagesForRole(role: String) {
        when (role) {
            "STUDENT" -> {
                pages.addAll(listOf(
                    OnboardingStep(
                        title = "Join a Class",
                        description = "Find and join classes using class codes or QR codes",
                        iconResId = android.R.drawable.ic_menu_view
                    ),
                    OnboardingStep(
                        title = "Submit Assignments",
                        description = "Complete and submit assignments directly from the app",
                        iconResId = android.R.drawable.ic_menu_agenda
                    ),
                    OnboardingStep(
                        title = "Take Quizzes",
                        description = "Test your knowledge with interactive quizzes",
                        iconResId = android.R.drawable.ic_menu_edit
                    ),
                    OnboardingStep(
                        title = "Mark Attendance",
                        description = "Quick attendance marking with QR codes",
                        iconResId = android.R.drawable.ic_menu_today
                    ),
                    OnboardingStep(
                        title = "AI Study Buddy",
                        description = "Get instant help with your studies from AI",
                        iconResId = android.R.drawable.ic_menu_help
                    )
                ))
            }
            "TEACHER" -> {
                pages.addAll(listOf(
                    OnboardingStep(
                        title = "Create a Class",
                        description = "Set up your class and share the code with students",
                        iconResId = android.R.drawable.ic_menu_add
                    ),
                    OnboardingStep(
                        title = "Post Assignments",
                        description = "Create and manage assignments easily",
                        iconResId = android.R.drawable.ic_menu_agenda
                    ),
                    OnboardingStep(
                        title = "Generate QR Codes",
                        description = "Quick attendance marking with QR codes",
                        iconResId = android.R.drawable.ic_menu_camera
                    ),
                    OnboardingStep(
                        title = "Grade Submissions",
                        description = "Review and grade student submissions",
                        iconResId = android.R.drawable.ic_menu_edit
                    ),
                    OnboardingStep(
                        title = "Track Progress",
                        description = "Monitor student performance with analytics",
                        iconResId = android.R.drawable.ic_menu_view
                    )
                ))
            }
            "ADMIN" -> {
                pages.addAll(listOf(
                    OnboardingStep(
                        title = "User Approval",
                        description = "Manage user registration and approvals",
                        iconResId = android.R.drawable.ic_menu_edit
                    ),
                    OnboardingStep(
                        title = "Role Management",
                        description = "Assign and manage user roles",
                        iconResId = android.R.drawable.ic_menu_view
                    ),
                    OnboardingStep(
                        title = "System Monitoring",
                        description = "Monitor app usage and system health",
                        iconResId = android.R.drawable.ic_menu_info_details
                    ),
                    OnboardingStep(
                        title = "Manage Classes",
                        description = "Oversee all classes and institution settings",
                        iconResId = android.R.drawable.ic_menu_agenda
                    ),
                    OnboardingStep(
                        title = "Analytics",
                        description = "View institution-wide analytics and reports",
                        iconResId = android.R.drawable.ic_menu_compass
                    )
                ))
            }
        }
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardingStepFragment.newInstance(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    /**
     * Gets the current page
     */
    fun getCurrentPage(position: Int): OnboardingStep? {
        return if (position < pages.size) pages[position] else null
    }
}

/**
 * Data class for onboarding steps
 */
data class OnboardingStep(
    val title: String,
    val description: String,
    val iconResId: Int,
    val animationResId: Int? = null
)

/**
 * Fragment for individual onboarding step
 */
class OnboardingStepFragment : Fragment() {
    companion object {
        private const val ARG_STEP = "step"

        fun newInstance(step: OnboardingStep): OnboardingStepFragment {
            return OnboardingStepFragment().apply {
                arguments = android.os.Bundle().apply {
                    // Serialize step data
                }
            }
        }
    }
}

