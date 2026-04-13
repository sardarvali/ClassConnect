package com.syed.classconnect.ui.onboarding

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> final
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
<<<<<<< HEAD
import android.content.Context
=======
>>>>>>> final
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentOnboardingBinding
import com.syed.classconnect.ui.auth.AuthActivity
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

<<<<<<< HEAD
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pages = listOf(
<<<<<<< HEAD
            OnboardingPage("Collaborate in Real-Time", "Class chat and announcements keep everyone connected.", com.syed.classconnect.R.drawable.ic_chat),
            OnboardingPage("Never Miss a Deadline", "Track assignments and quizzes with smart reminders.", com.syed.classconnect.R.drawable.ic_assignment),
            OnboardingPage("AI-Powered Learning", "Your personal AI Study Buddy powered by Gemini.", com.syed.classconnect.R.drawable.ic_ai)
=======
            OnboardingPage(
                "Collaborate in Real-Time",
                "Class chat and announcements keep everyone connected.",
                R.drawable.ic_chat
            ),
            OnboardingPage(
                "Never Miss a Deadline",
                "Track assignments and quizzes with smart reminders.",
                R.drawable.ic_assignment
            ),
            OnboardingPage(
                "AI-Powered Learning",
                "Your personal AI Study Buddy powered by Gemini.",
                R.drawable.ic_ai
            )
>>>>>>> final
        )

        val adapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = adapter

        // Depth + fade page transformer for premium swipe feel
        binding.viewPager.setPageTransformer { page, position ->
            val absPos = abs(position)
            page.alpha = 1f - absPos * 0.4f
            page.scaleX = 1f - absPos * 0.15f
            page.scaleY = 1f - absPos * 0.15f
            page.translationX = -position * page.width * 0.15f
        }

        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == pages.size - 1) {
                    binding.btnNext.text = "Get Started"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < pages.size - 1) {
                binding.viewPager.currentItem++
            } else {
                finishOnboarding()
            }
        }

        binding.tvSkip.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        requireContext().getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean(Constants.PREF_ONBOARDING_COMPLETE, true).apply()
        startActivity(Intent(requireContext(), AuthActivity::class.java))
        @Suppress("DEPRECATION")
        requireActivity().overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out)
        requireActivity().finish()
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
=======
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
>>>>>>> final
}

