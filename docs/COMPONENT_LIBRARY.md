# ClassConnect - Component Library Documentation

> **Last Updated:** April 6, 2026  
> **Version:** 1.0 (UPDATED)  
> **Audience:** Designers, Frontend Developers, Product Managers

---

## Overview

This document serves as a reference for all UI components used in ClassConnect, their properties, usage patterns, and accessibility requirements. It bridges the gap between design (Figma) and implementation (Android XML/Kotlin).

---

## Table of Contents

1. [Typography](#typography)
2. [Colors & Themes](#colors--themes)
3. [Spacing System](#spacing-system)
4. [Buttons](#buttons)
5. [Cards](#cards)
6. [List Items](#list-items)
7. [Input Fields](#input-fields)
8. [Icons](#icons)
9. [Animations](#animations)
10. [Error States](#error-states)
11. [Loading States](#loading-states)
12. [Accessibility](#accessibility)

---

## Typography

### Text Styles (via Material 3 + Design Tokens)

All text styles are defined in `design_tokens.xml` and accessible via dimension resources:

| Style | Size | Line Height | Letter Spacing | Use Case |
|-------|------|-------------|-----------------|----------|
| `text_xs` | 11sp | 16sp | 0sp | Captions, badges, helper text |
| `text_sm` | 13sp | 18sp | 0.3sp | Secondary labels, timestamps |
| `text_base` | 15sp | 20sp | 0.3sp | Body text, default |
| `text_md` | 17sp | 24sp | 0.5sp | Subheadings, important labels |
| `text_lg` | 20sp | 28sp | 0.5sp | Heading level 3 |
| `text_xl` | 24sp | 32sp | 0sp | Heading level 2 |
| `text_2xl` | 28sp | 36sp | 0sp | Heading level 1 (screen titles) |
| `text_3xl` | 32sp | 40sp | 0sp | Hero heading (splash, modals) |

### Implementation

```xml
<!-- Default text (15sp) -->
<TextView
    android:textSize="@dimen/text_base"
    android:textColor="@color/text_primary"
    android:lineSpacingExtra="@dimen/text_base_line_height" />

<!-- Large heading -->
<TextView
    android:textSize="@dimen/text_xl"
    android:textStyle="bold"
    android:textColor="@color/text_primary" />
```

### Text Color Palette

| Token | Light Mode | Dark Mode | Contrast Ratio | Usage |
|-------|-----------|-----------|-----------------|--------|
| `text_primary` | #0F172A | #F0F4FF | 16:1 | Primary body text, headings |
| `text_secondary` | #475569 | #8A9DC0 | 7.8:1 | Secondary labels, descriptions |
| `text_hint` | #94A3B8 | #4A5A78 | 4.9:1 | Hints, disabled text, captions |
| `text_on_brand` | #FFFFFF | #FFFFFF | 12:1+ | Text on brand-colored backgrounds |

**WCAG AA Compliance:** All text colors meet minimum 4.5:1 contrast for normal text, 3:1 for large text.

---

## Colors & Themes

### Primary Palette

| Token | Color | Usage |
|-------|-------|-------|
| `brand_primary` | #1E6FFF | Primary actions, focus states |
| `brand_accent` | #00D4FF | Secondary actions, highlights |
| `semantic_success` | #00C896 | Success messages, checkmarks |
| `semantic_warning` | #FFB020 | Warnings, caution states |
| `semantic_error` | #FF4D6A | Errors, destructive actions |
| `semantic_info` | #1E6FFF | Informational messages |

### Role-Based Colors

| Role | Color | Usage |
|------|-------|-------|
| `role_admin` | #9D6FFF | Admin badges, admin-only UI |
| `role_teacher` | #00C896 | Teacher badges, teacher sections |
| `role_student` | #1E6FFF | Student badges, student sections |

### Assignment Status Colors (Deadline-based)

| Status | Color | Condition |
|--------|-------|-----------|
| `deadline_overdue` | #FF4D6A | Due date passed |
| `deadline_today` | #FFB020 | Due today |
| `deadline_soon` | #FFA500 | Due within 3 days |
| `deadline_upcoming` | #1E6FFF | Due within 1 week |
| `deadline_later` | #00C896 | Due later |

### Implementation: Color-Coded Assignment Cards

```xml
<!-- Assignment card with deadline-based left border -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <!-- Deadline indicator (left border) -->
    <View
        android:id="@+id/deadlineIndicator"
        android:layout_width="4dp"
        android:layout_height="match_parent"
        android:background="@color/deadline_overdue" />

    <!-- Card content -->
    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical"
        android:padding="@dimen/card_padding_default">

        <!-- Title + Status badge -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <TextView
                android:id="@+id/assignmentTitle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textSize="@dimen/text_base"
                android:textStyle="bold" />

            <!-- Status badge -->
            <com.google.android.material.chip.Chip
                android:id="@+id/statusBadge"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/submitted" />
        </LinearLayout>

        <!-- Deadline countdown -->
        <TextView
            android:id="@+id/deadlineText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="@dimen/text_sm"
            android:textColor="@color/text_secondary"
            android:text="Due in 2 days" />

        <!-- Progress indicator (if file required) -->
        <ProgressBar
            android:id="@+id/submissionProgress"
            android:layout_width="match_parent"
            android:layout_height="4dp"
            android:progress="75"
            style="?attr/progressBarStyle" />
    </LinearLayout>
</LinearLayout>
```

### Kotlin: Programmatic Color Assignment

```kotlin
// Set deadline indicator color based on status
val deadlineColor = when {
    daysUntilDeadline < 0 -> R.color.deadline_overdue
    daysUntilDeadline == 0 -> R.color.deadline_today
    daysUntilDeadline <= 3 -> R.color.deadline_soon
    daysUntilDeadline <= 7 -> R.color.deadline_upcoming
    else -> R.color.deadline_later
}
binding.deadlineIndicator.setBackgroundColor(context.getColor(deadlineColor))
```

### Dark Mode Overrides

Dark mode color adjustments are in `values-night/colors.xml`:

```xml
<!-- Dark mode text colors (higher brightness) -->
<color name="text_primary">#F0F4FF</color>
<color name="text_secondary">#8A9DC0</color>

<!-- Dark mode semantic colors (adjusted for contrast) -->
<color name="semantic_success_container">#1B5E3F</color>
<color name="semantic_warning_container">#663D00</color>
```

**OLED Optimization:** Pure black backgrounds (#000000) can be enabled in `values-night/colors.xml` for OLED displays (commented out by default).

---

## Spacing System

All spacing follows a 4dp base unit, defined in `design_tokens.xml`:

| Token | Size | Usage |
|-------|------|-------|
| `spacing_xs` | 4dp | Minimal gaps, icon padding |
| `spacing_sm` | 8dp | Small gaps, component internals |
| `spacing_md` | 12dp | Medium gaps between elements |
| `spacing_lg` | 16dp | Standard gap between sections |
| `spacing_xl` | 20dp | Large gap for visual separation |
| `spacing_2xl` | 24dp | Extra-large gap, section padding |
| `spacing_3xl` | 32dp | Major section separations |
| `spacing_4xl` | 40dp | Full screen content padding (tablet) |
| `spacing_5xl` | 48dp | Large content padding (tablet) |

### Responsive Padding

Padding adjusts based on screen size:

| Size | Default | Small Phone (320dp) | Tablet (600dp) |
|------|---------|-------------------|-----------------|
| **screen_padding_h** | 20dp | 16dp | 40dp |
| **screen_padding_v** | 16dp | 12dp | 24dp |
| **card_padding** | 16dp | 12dp | 20dp |

Implementation:

```xml
<!-- Activity/Fragment root layout -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingStart="@dimen/screen_padding_h_default"
    android:paddingEnd="@dimen/screen_padding_h_default"
    android:paddingTop="@dimen/screen_padding_v_default" />
```

---

## Buttons

### Button Hierarchy

#### Primary Button (CTA)
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="@dimen/button_height_default"
    android:text="@string/submit"
    style="@style/Widget.MaterialComponents.Button" />
```

#### Secondary Button
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="@dimen/button_height_default"
    android:text="@string/cancel"
    style="@style/Widget.MaterialComponents.Button.OutlinedButton" />
```

#### Icon Button (minimum 48dp touch target)
```xml
<com.google.android.material.button.MaterialButton
    android:id="@+id/submitButton"
    android:layout_width="@dimen/touch_target_min"
    android:layout_height="@dimen/touch_target_min"
    android:contentDescription="@string/cd_submit_assignment"
    app:icon="@drawable/ic_send"
    style="@style/Widget.MaterialComponents.Button.OutlinedButton.Icon" />
```

### Button Press Animation

Use `AnimationPresets.buttonPressEffect()` for consistent feedback:

```kotlin
binding.submitButton.setOnClickListener {
    AnimationPresets.buttonPressEffect(it)
    viewModel.submitAssignment()
}
```

---

## Cards

### Standard Card Layout

```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="@dimen/card_margin_h"
    app:cardCornerRadius="@dimen/radius_lg"
    app:cardElevation="@dimen/elevation_2"
    app:strokeWidth="@dimen/stroke_default"
    app:strokeColor="@color/border_subtle">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/card_padding_default">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="@dimen/text_base"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="@dimen/text_sm"
            android:textColor="@color/text_secondary" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### Class Card (with Gradient Background)

```xml
<!-- Gradient drawable (e.g., res/drawable/gradient_class_blue.xml) -->
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
        android:startColor="@color/class_1_start"
        android:endColor="@color/class_1_end"
        android:angle="135" />
</shape>

<!-- Card layout -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="200dp"
    app:cardCornerRadius="@dimen/radius_xl"
    app:cardElevation="@dimen/elevation_3">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@drawable/gradient_class_blue"
        android:orientation="vertical"
        android:padding="@dimen/spacing_2xl"
        android:gravity="bottom">

        <TextView
            android:text="Mathematics 101"
            android:textColor="@color/text_on_brand"
            android:textSize="@dimen/text_lg"
            android:textStyle="bold" />
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

---

## List Items

### Assignment List Item

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:paddingStart="@dimen/spacing_lg"
    android:paddingEnd="@dimen/spacing_lg"
    android:paddingTop="@dimen/spacing_md"
    android:paddingBottom="@dimen/spacing_md">

    <!-- Deadline indicator (left border) -->
    <View
        android:id="@+id/deadlineIndicator"
        android:layout_width="@dimen/stroke_medium"
        android:layout_height="60dp"
        android:background="@color/deadline_overdue"
        android:layout_marginEnd="@dimen/spacing_lg" />

    <!-- Content -->
    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <TextView
                android:id="@+id/title"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textSize="@dimen/text_base"
                android:textStyle="bold" />

            <com.google.android.material.chip.Chip
                android:id="@+id/statusChip"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content" />
        </LinearLayout>

        <TextView
            android:id="@+id/deadline"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="@dimen/text_sm"
            android:textColor="@color/text_secondary"
            android:layout_marginTop="@dimen/spacing_xs" />

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="match_parent"
            android:layout_height="4dp"
            android:layout_marginTop="@dimen/spacing_sm"
            style="?attr/progressBarStyle" />
    </LinearLayout>
</LinearLayout>
```

---

## Input Fields

### Text Input

```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="@string/full_name"
    app:counterEnabled="true"
    app:counterMaxLength="100">

    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="@dimen/input_height"
        android:inputType="text"
        android:paddingStart="@dimen/input_padding_h"
        android:paddingEnd="@dimen/input_padding_h" />
</com.google.android.material.textfield.TextInputLayout>
```

### Accessibility for Input Fields

```kotlin
// In Fragment/Activity
val editText = binding.nameInput.editText
AccessibilityHelper.setupEditTextAccessibility(
    editText!!,
    getString(R.string.full_name)
)
```

---

## Icons

### Icon Sizes

| Token | Size | Usage |
|-------|------|-------|
| `icon_xs` | 16dp | Inline icons within text |
| `icon_sm` | 20dp | List item icons |
| `icon_md` | 24dp | Standard button icons, badges |
| `icon_lg` | 32dp | Large action buttons |
| `icon_xl` | 48dp | Hero icons, large buttons |

### Icon Button (Touch Target)

Always wrap icons in buttons with minimum 48dp touch target:

```xml
<!-- ✓ Correct: Adequate touch target -->
<ImageButton
    android:id="@+id/submitBtn"
    android:layout_width="@dimen/touch_target_min"
    android:layout_height="@dimen/touch_target_min"
    android:src="@drawable/ic_send"
    android:contentDescription="@string/cd_submit_assignment"
    android:scaleType="centerInside"
    android:padding="@dimen/icon_padding" />

<!-- ✗ Incorrect: Inadequate touch target -->
<ImageButton
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:src="@drawable/ic_send" />
```

---

## Animations

### Predefined Animation Presets

All animations are centralized in `AnimationPresets.kt`:

| Animation | Duration | Easing | Usage |
|-----------|----------|--------|-------|
| `slideIn()` | 300ms | Decelerate | Screen/list item entry |
| `fadeIn()` | 300ms | Decelerate | Content fade-in |
| `scaleUp()` | 300ms | Decelerate | Modal entrance |
| `buttonPressEffect()` | 150ms | Accelerate + Decelerate | Button feedback |
| `shimmerPulse()` | 1500ms | Accelerate + Decelerate | Loading skeleton |
| `shake()` | 150ms | Accelerate + Decelerate | Error feedback |
| `checkmarkSuccess()` | 300ms | Decelerate | Success confirmation |
| `pulse()` | 1200ms | Continuous | Subtle attention draw |

### Usage Examples

```kotlin
// List item entry animation (staggered)
fun animateListItemEntry(itemView: View, index: Int) {
    AnimationPresets.slideIn(
        itemView,
        duration = AnimationPresets.DURATION_MEDIUM,
        delayMs = AnimationPresets.getStaggerDelay(index)
    )
}

// Button press feedback
binding.submitButton.setOnClickListener {
    AnimationPresets.buttonPressEffect(it)
    viewModel.submitData()
}

// Error animation (shake)
fun showErrorAnimation(errorView: View) {
    AnimationPresets.shake(errorView)
}

// Success animation
fun showSuccessAnimation(successIcon: View) {
    AnimationPresets.checkmarkSuccess(successIcon)
}

// Loading state (shimmer)
fun startLoadingAnimation(skeletonView: View) {
    AnimationPresets.shimmerPulse(skeletonView)
}
```

---

## Error States

### Global Error Handling Pattern

Use `UiState<T>` sealed class for consistent error management:

```kotlin
// In Fragment
viewModel.assignments.observe(viewLifecycleOwner) { state ->
    when (state) {
        is UiState.Loading -> showShimmer()
        is UiState.Success -> {
            showAssignments(state.data)
            state.message?.let { showSuccessToast(it) }
        }
        is UiState.Error -> {
            showErrorState(state.message, state.action)
            // Optional: Log error code and exception
            state.errorCode?.let { Log.e("Assignment", "Error code: $it") }
        }
        is UiState.Empty -> showEmptyState()
        is UiState.Offline -> showOfflineIndicator(state.cachedData)
    }
}
```

### Error UI Components

#### Error Banner (Top of screen)
```xml
<com.google.android.material.snackbar.Snackbar
    android:id="@+id/errorBanner"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="top"
    android:background="@color/semantic_error"
    android:text="No internet connection">

    <Button
        android:text="@string/retry"
        android:textColor="@color/text_on_brand" />
</com.google.android.material.snackbar.Snackbar>
```

#### Error State Fragment
```xml
<!-- Shows full-screen error with icon, message, and action -->
<com.syed.classconnect.ui.util.ErrorStateFragment
    android:id="@+id/errorStateFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

#### Offline Indicator (Persistent banner)
```xml
<LinearLayout
    android:id="@+id/offlineBanner"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/semantic_warning_surface"
    android:gravity="center">

    <ImageView
        android:src="@drawable/ic_signal_wifi_off"
        android:contentDescription="@string/youre_offline" />

    <TextView
        android:text="@string/youre_offline"
        android:textColor="@color/semantic_warning" />
</LinearLayout>
```

---

## Loading States

### Shimmer Skeleton Loader

```xml
<!-- Shimmer placeholder for assignment card -->
<com.facebook.shimmer.ShimmerFrameLayout
    android:id="@+id/shimmerLoader"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    app:shimmer_duration="1500"
    app:shimmer_direction="left_to_right">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:paddingStart="@dimen/spacing_lg"
        android:paddingEnd="@dimen/spacing_lg">

        <!-- Skeleton title -->
        <View
            android:layout_width="150dp"
            android:layout_height="@dimen/spacing_lg"
            android:layout_marginTop="@dimen/spacing_md"
            android:background="@color/bg_surface_raised" />

        <!-- Skeleton subtitle -->
        <View
            android:layout_width="250dp"
            android:layout_height="@dimen/spacing_md"
            android:layout_marginTop="@dimen/spacing_md"
            android:background="@color/bg_surface_raised" />

        <!-- Skeleton progress bar -->
        <View
            android:layout_width="match_parent"
            android:layout_height="2dp"
            android:layout_marginTop="@dimen/spacing_md"
            android:background="@color/bg_surface_raised" />
    </LinearLayout>
</com.facebook.shimmer.ShimmerFrameLayout>
```

### Progress Indicators

#### Linear Progress (file upload)
```xml
<com.google.android.material.progressindicator.LinearProgressIndicator
    android:id="@+id/uploadProgress"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:progress="65"
    android:indeterminate="false" />
```

#### Circular Progress (processing)
```xml
<com.google.android.material.progressindicator.CircularProgressIndicator
    android:id="@+id/processingSpinner"
    android:layout_width="@dimen/icon_lg"
    android:layout_height="@dimen/icon_lg"
    app:indicatorSize="@dimen/icon_lg" />
```

---

## Accessibility

### Content Descriptions (All Interactive Elements)

**Rule:** Every interactive element must have a content description.

```kotlin
// Icon buttons
AccessibilityHelper.setContentDescription(
    binding.submitButton,
    R.string.cd_submit_assignment
)

// Avatar images
AccessibilityHelper.setContentDescription(
    binding.studentAvatar,
    getString(R.string.cd_avatar_template, studentName)
)

// Status badges
AccessibilityHelper.setContentDescription(
    binding.submittedBadge,
    R.string.cd_submitted_badge
)

// Card items
AccessibilityHelper.setContentDescription(
    binding.assignmentCard,
    getString(R.string.cd_assignment_card, assignment.dueDate)
)
```

### Minimum Touch Target Size (48dp × 48dp)

```kotlin
// Ensure all buttons meet accessibility guidelines
AccessibilityHelper.ensureMinimumTouchTarget(binding.submitButton, minSizeDp = 48)
```

### Live Regions (Dynamic Content Updates)

```kotlin
// Announce errors immediately
AccessibilityHelper.setLiveRegionAssertive(binding.errorBanner)
AccessibilityHelper.announce(
    binding.errorBanner,
    "Error: ${error.message}"
)

// Announce status changes when convenient
AccessibilityHelper.setLiveRegionPolite(binding.statusText)
```

### Screen Reader Testing Checklist

- [ ] All buttons have content descriptions
- [ ] All images have meaningful descriptions (not decorative ones hidden)
- [ ] Form fields have associated labels
- [ ] Error messages are announced
- [ ] Loading states are announced
- [ ] Navigation landmarks are clear (roles)
- [ ] Color is not the only indicator of status
- [ ] Focus order is logical
- [ ] Text has sufficient contrast (4.5:1 minimum)

---

## Internationalization (i18n)

### RTL Layout Support

ClassConnect supports right-to-left (RTL) languages (Arabic, Hebrew):

```kotlin
// Check if current locale is RTL
if (I18nHelper.isRtl(context)) {
    // Adjust layout accordingly
}

// Use start/end instead of left/right
view.layoutParams = MarginLayoutParams().apply {
    marginStart = 16
    marginEnd = 16  // Use end, not right
}
```

### Pluralization

```kotlin
// Correct pluralization for different locales
val text = I18nHelper.getPlural(
    context,
    R.plurals.cd_assignment_count,
    count = 5,
    5
)
// Outputs: "5 assignments" (English), "٥ مهام" (Arabic), etc.
```

### Locale-Aware Date Formatting

```kotlin
val date = Date()
val formattedDate = I18nHelper.formatDate(date)
// Uses device locale: "Apr 6, 2026" (en), "٦ أبريل ٢٠٢٦" (ar), etc.

val relativeTime = I18nHelper.formatRelativeTime(date, context)
// Outputs: "2m ago", "منذ دقيقتين", etc.
```

---

## Best Practices

### DO ✓

- Use design tokens (dimens, colors) instead of hardcoded values
- Always add content descriptions to interactive elements
- Test on multiple screen sizes (320dp, 480dp, 600dp, 840dp)
- Use material animations with consistent durations
- Provide feedback for all user interactions
- Support dark mode by testing both light and night color sets
- Use responsive padding (design_tokens + size qualifiers)
- Implement error states and empty states
- Announce important changes to screen readers
- Test with a real device and screen reader

### DON'T ✗

- Hardcode dimensions, colors, or animations
- Create buttons smaller than 48dp × 48dp
- Use color as the only indicator of status
- Forget content descriptions on images
- Ignore contrast requirements (WCAG AA)
- Use instant transitions without animations
- Assume light mode only (always test dark mode)
- Leave network errors silent
- Use decorative icons without accessibility markup
- Assume fixed screen sizes

---

## Testing Checklist

Before submitting UI work:

- [ ] All text is readable (contrast, size)
- [ ] All buttons are 48dp+ in size
- [ ] All interactive elements have descriptions
- [ ] Dark mode looks correct
- [ ] RTL languages are supported
- [ ] Small screens (320dp) don't have overflow
- [ ] Tablets (600dp+) have adequate spacing
- [ ] Animations are smooth (60fps, <500ms)
- [ ] Error states are handled gracefully
- [ ] Loading states are visible
- [ ] Network errors show messages
- [ ] Tested with screen reader (TalkBack)
- [ ] No hardcoded colors/dimensions

---

## Resources

- **Material Design 3:** https://m3.material.io/
- **Android Accessibility Guide:** https://developer.android.com/guide/topics/ui/accessibility
- **Material Motion:** https://material.io/design/motion/
- **WCAG 2.1 Guidelines:** https://www.w3.org/WAI/WCAG21/quickref/
- **Color Contrast Checker:** https://www.tpgi.com/color-contrast-checker/

---

**Status:** COMPLETE  
*Last Updated: April 6, 2026*

