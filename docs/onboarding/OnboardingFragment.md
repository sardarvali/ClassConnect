# OnboardingFragment.kt — First-launch tutorial with ViewPager2 swipe pages, page transformer animation, and navigation to AuthActivity

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/onboarding/OnboardingFragment.kt`

---

## 🎯 What This File Does
`OnboardingFragment` shows new users a 3-page swipeable introduction to ClassConnect's key features: real-time collaboration, deadline tracking, and AI-powered learning. Each page has an illustration, title, and description. A depth+fade page transformer makes swipes feel premium. TabLayout dots indicate the current page. A "Next" button advances pages and changes to "Get Started" on the last page. Tapping "Get Started" or "Skip" marks onboarding complete in SharedPreferences and navigates to `AuthActivity`. `SplashActivity` or `MainActivity` checks the `PREF_ONBOARDING_COMPLETE` flag to decide whether to show this screen. Without this fragment, new users see no introduction.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.content.Intent` | Android SDK | Activity navigation | Start `AuthActivity` |
| `android.os.Bundle` | Android SDK | State map | Fragment lifecycle |
| `android.view.LayoutInflater` | Android SDK | XML → View | `fragment_onboarding.xml` |
| `android.view.View` | Android SDK | Base view | `onViewCreated` param |
| `android.view.ViewGroup` | Android SDK | Container | `onCreateView` |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | OnboardingFragment extends it |
| `android.content.Context` | Android SDK | App context | `getSharedPreferences()` |
| `androidx.viewpager2.widget.ViewPager2` | ViewPager2 | Swipeable pages | Page change callback |
| `com.google.android.material.tabs.TabLayoutMediator` | Material | Connects TabLayout to ViewPager2 | Page indicator dots |
| `com.syed.classconnect.R` | Project | Resource IDs | Drawable and string resources |
| `com.syed.classconnect.databinding.FragmentOnboardingBinding` | ViewBinding | `fragment_onboarding.xml` | `viewPager`, `tabIndicator`, `btnNext`, `tvSkip` |
| `com.syed.classconnect.ui.auth.AuthActivity` | Project | Auth screen | Navigation target after onboarding |
| `com.syed.classconnect.util.Constants` | Project | `PREF_ONBOARDING_COMPLETE` key | SharedPreferences persistence |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables DI | Required for Hilt infrastructure |
| `kotlin.math.abs` | Kotlin Math | Absolute value | Page transformer animation |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `data class OnboardingPage(title, description, imageRes)` *(defined in OnboardingAdapter.kt)*
Simple data holder used to populate each onboarding page.

### Page transformer — depth + fade effect
```kotlin
binding.viewPager.setPageTransformer { page, position ->
    val absPos = abs(position)
    page.alpha = 1f - absPos * 0.4f    // Fade out as page slides away
    page.scaleX = 1f - absPos * 0.15f  // Scale down as page slides away
    page.scaleY = 1f - absPos * 0.15f
    page.translationX = -position * page.width * 0.15f  // Parallax shift
}
```
`position`: 0.0 = current page, 1.0 = next page, -1.0 = previous. The transformer is called for every visible page during a swipe gesture.

### `TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()`
Connects the `TabLayout` to the `ViewPager2`. The lambda `{ _, _ -> }` is called to configure each tab — we pass empty lambda because we only want the default dot indicators. `.attach()` starts the synchronization.

### `ViewPager2.OnPageChangeCallback`
Used to detect when the last page is reached and change the "Next" button text to "Get Started".

### `finishOnboarding()`
1. Saves `PREF_ONBOARDING_COMPLETE = true` to SharedPreferences.
2. Starts `AuthActivity`.
3. Calls `overridePendingTransition(fade_scale_in, fade_scale_out)` for a fade transition animation.
4. Calls `requireActivity().finish()` to close `MainActivity` (the host) so back-pressing from `AuthActivity` doesn't return to onboarding.

### `@Suppress("DEPRECATION")` on `overridePendingTransition`
`overridePendingTransition` is deprecated in API 34 in favor of `overrideActivityTransition`. Suppressed for backward compatibility.

---

## 🏗️ Class Structure
`@AndroidEntryPoint class OnboardingFragment : Fragment()` — no ViewModel.

---

## ⚙️ Functions

### `onViewCreated(view, savedInstanceState)`
**Step by step:**
1. Creates 3 `OnboardingPage` objects with titles, descriptions, and drawable resources.
2. Creates `OnboardingAdapter(pages)`, sets on `viewPager`.
3. Sets page transformer for depth+fade animation.
4. Attaches `TabLayoutMediator` for dot indicators.
5. Registers `OnPageChangeCallback` to update "Next"/"Get Started" button text.
6. "Next" click: if not last page → increment page; else → `finishOnboarding()`.
7. "Skip" click → `finishOnboarding()`.

### `finishOnboarding()`
Saves completion flag, starts AuthActivity with fade animation, finishes host Activity.

---

## ⚠️ Important Notes & Gotchas
- `requireActivity().finish()` closes the Activity that hosts this Fragment (MainActivity/SplashActivity), NOT AuthActivity. This prevents the user from back-pressing back into onboarding.
- `PREF_ONBOARDING_COMPLETE` is checked in SplashActivity to decide whether to show onboarding or go directly to login.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.onboarding

// (imports as listed above)

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pages = listOf(
            OnboardingPage("Collaborate in Real-Time", "Class chat and announcements...", R.drawable.ic_chat),
            OnboardingPage("Never Miss a Deadline", "Track assignments...", R.drawable.ic_assignment),
            OnboardingPage("AI-Powered Learning", "Your personal AI Study Buddy...", R.drawable.ic_ai)
        )

        val adapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = adapter

        binding.viewPager.setPageTransformer { page, position ->
            val absPos = abs(position)
            page.alpha = 1f - absPos * 0.4f
            page.scaleX = 1f - absPos * 0.15f
            page.scaleY = 1f - absPos * 0.15f
            page.translationX = -position * page.width * 0.15f
            // Depth: pages scale down as they slide away. Parallax: slower translation.
        }

        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()
        // Dots that indicate which page is current.

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == pages.size - 1) {
                    binding.btnNext.text = "Get Started"
                    // Last page: button becomes the final CTA.
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < pages.size - 1) {
                binding.viewPager.currentItem++  // Advance to next page.
            } else {
                finishOnboarding()
            }
        }

        binding.tvSkip.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        requireContext().getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean(Constants.PREF_ONBOARDING_COMPLETE, true).apply()
        // Mark onboarding complete — SplashActivity checks this on next launch.

        startActivity(Intent(requireContext(), AuthActivity::class.java))
        @Suppress("DEPRECATION")
        requireActivity().overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out)
        requireActivity().finish()
        // Close the host activity so back from Auth doesn't return here.
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
```

