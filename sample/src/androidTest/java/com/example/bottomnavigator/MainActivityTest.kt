package com.example.bottomnavigator

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clicktest() {
        onView(withId(R.id.title)).check(matches(withText(getTitle(2, 1))))
        onView(withId(R.id.btn)).perform(click())
        onView(withId(R.id.title)).check(matches(withText(getTitle(2, 2))))

        onView(withId(R.id.tab1)).perform(click())
        onView(withId(R.id.title)).check(matches(withText(getTitle(1, 1))))
        onView(withId(R.id.btn)).perform(click())
        onView(withId(R.id.title)).check(matches(withText(getTitle(1, 2))))

        onView(withId(R.id.tab3)).perform(click())
        onView(withId(R.id.title)).check(matches(withText(getTitle(3, 1))))

        onView(withId(R.id.tab2)).perform(click())
        onView(withId(R.id.title)).check(matches(withText(getTitle(2, 2))))

        rotateScreen()

        pressBack()
        onView(withId(R.id.title)).check(matches(withText(getTitle(2, 1))))
        pressBack()
        onView(withId(R.id.title)).check(matches(withText(getTitle(3, 1))))
        pressBack()
        onView(withId(R.id.title)).check(matches(withText(getTitle(1, 2))))
        pressBack()
        onView(withId(R.id.title)).check(matches(withText(getTitle(1, 1))))
    }

    private fun getTitle(tab: Int, depth: Int) = "Tab $tab Depth $depth"

    private fun rotateScreen() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val orientation = context.resources.configuration.orientation

        val activity = activityRule.activity
        activity.requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

}
