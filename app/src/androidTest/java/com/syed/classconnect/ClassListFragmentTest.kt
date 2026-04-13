package com.syed.classconnect

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.ui.main.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for ClassListFragment.
 *
 * These tests verify the structural elements of the class list screen.
 * They run only when a test user is already signed in to Firebase; otherwise
 * they are skipped gracefully with assumeTrue so CI is not broken.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ClassListFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        // Skip this test suite if no authenticated user is present (e.g., fresh CI environment)
        assumeTrue(
            "Skipping ClassListFragmentTest: no authenticated Firebase user",
            FirebaseAuth.getInstance().currentUser != null
        )
    }

    @Test
    fun classListScreen_recyclerViewIsDisplayed() {
        onView(withId(R.id.rv_classes)).check(matches(isDisplayed()))
    }

    @Test
    fun classListScreen_fabIsVisible() {
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
    }

    @Test
    fun classListScreen_hasBottomNavigation() {
        onView(withId(R.id.bottom_nav)).check(matches(isDisplayed()))
    }

    @Test
    fun classListScreen_emptyStateLayoutExists() {
        // The empty state layout is present in the view hierarchy (may be GONE if classes exist)
        onView(withId(R.id.layout_empty)).check(matches(withEffectiveVisibility(Visibility.GONE)
            .or(withEffectiveVisibility(Visibility.VISIBLE))))
    }
}

// Simple OR matcher helper used above
private fun <T> org.hamcrest.Matcher<T>.or(other: org.hamcrest.Matcher<T>) =
    org.hamcrest.CoreMatchers.anyOf(this, other)

