package com.syed.classconnect

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.syed.classconnect.ui.auth.AuthActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(AuthActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun loginScreen_displaysEmailAndPasswordFields() {
        onView(withId(R.id.et_email)).check(matches(isDisplayed()))
        onView(withId(R.id.et_password)).check(matches(isDisplayed()))
    }

    @Test
    fun loginScreen_displaysLoginButton() {
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }

    @Test
    fun loginScreen_displaysGoogleSignInButton() {
        onView(withId(R.id.btn_google)).check(matches(isDisplayed()))
    }

    @Test
    fun emptyEmail_showsValidationError() {
        onView(withId(R.id.et_password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.til_email)).check(matches(hasDescendant(withText(R.string.error_invalid_email))))
    }

    @Test
    fun invalidEmail_showsValidationError() {
        onView(withId(R.id.et_email)).perform(typeText("notanemail"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.til_email)).check(matches(hasDescendant(withText(R.string.error_invalid_email))))
    }

    @Test
    fun emptyPassword_showsValidationError() {
        onView(withId(R.id.et_email)).perform(typeText("user@test.com"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.til_password)).check(matches(hasDescendant(withText(R.string.error_empty_field))))
    }

    @Test
    fun forgotPasswordLink_isClickable() {
        onView(withId(R.id.tv_forgot_password)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_forgot_password)).check(matches(isClickable()))
    }

    @Test
    fun registerLink_isClickable() {
        onView(withId(R.id.tv_register)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_register)).perform(click())
        // After click we should see the register screen
        onView(withId(R.id.et_full_name)).check(matches(isDisplayed()))
    }
}

