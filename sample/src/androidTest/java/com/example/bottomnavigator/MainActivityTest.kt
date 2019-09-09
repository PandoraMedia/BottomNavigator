/*
 * Copyright 2019 Pandora Media, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See accompanying LICENSE file or you may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bottomnavigator

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.allOf
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

    @Test fun clearAll() {
        // Given we're on the default tab with 2 fragments
        onView(withId(R.id.btn)).perform(click())
        onView(withId(R.id.title)).check(matches(withText(getTitle(2, 2))))
        // When we click on tab1
        onView(
            allOf(
                withText("Tab1"),
                isDescendantOfA(withId(R.id.bottomnav_view)),
                isDisplayed()))
            .perform(click())
        // Then tab1's root fragment is shown
        onView(withId(R.id.title)).check(matches(withText(getTitle(1, 1))))
        // When we click clear all
        onView(withId(R.id.btn_clear_all)).perform(click())
        // Then we're back to the default tab 2 and it's root fragment
        onView(withId(R.id.title)).check(matches(withText(getTitle(2, 1))))

    }

}
