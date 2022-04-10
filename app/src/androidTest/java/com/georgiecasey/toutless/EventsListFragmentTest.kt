package com.georgiecasey.toutless

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Rule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class EventsListFragmentTest {
    @get: Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_isListFragmentVisible_onAppLaunch() {
        onView(withId(R.id.rvEvents)).check(matches(isDisplayed()))
    }

    @Test
    fun scroll25RowsDownTheRecyclerViewForGlideCacheVideo() {
        // scroll to position 2 to get preloader working? Then wait 15 seconds for preloader to, eh, preload and
        // scroll through the list. I recorded this test with adb shell screenrecord. Needs to be run on a throttled
        // device or test is pointless
        onView(withId(R.id.rvEvents)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                2
            )
        )
        Thread.sleep(15000)
        for (i in 1..10) {
            onView(withId(R.id.rvEvents)).perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                    i * 3
                )
            )
            Thread.sleep(1500)
        }
        // Sleep 5 seconds at the end to see if non-preloader catches up at all.
        Thread.sleep(5000)
    }
}