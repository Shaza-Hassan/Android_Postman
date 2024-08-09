package com.shaza.androidpostman.history.view

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaza.androidpostman.R
import com.shaza.androidpostman.history.viewmodel.HistoryViewModel
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

@RunWith(AndroidJUnit4::class)
class HistoryFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<HistoryFragment>
    private val mockViewModel: HistoryViewModel = mockk<HistoryViewModel>()

    @Before
    fun setUp() {
        fragmentScenario = launchFragmentInContainer<HistoryFragment>(themeResId = R.style.Theme_AndroidPostman) {
            HistoryFragment().apply {
                // Override the ViewModelFactory to inject the mock ViewModel
                viewModel = mockViewModel
            }
        }

        Thread.sleep(1000)
    }

    @Test
    fun testHistoryFragmentIsDisplayed(){
        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testUpdateRequestTypeSelector(){
        onView(withId(R.id.get_type)).perform(click())
        onView(withId(R.id.get_type)).check(matches(isChecked()))

        Thread.sleep(1000)

        onView(withId(R.id.post_type)).perform(click())
        onView(withId(R.id.post_type)).check(matches(isChecked()))

        Thread.sleep(1000)

        onView(withId(R.id.all_request_type)).perform(click())
        onView(withId(R.id.all_request_type)).check(matches(isChecked()))
    }

    @Test
    fun testUpdateRequestStatusSelector(){
        onView(withId(R.id.success_requests)).perform(click())
        onView(withId(R.id.success_requests)).check(matches(isChecked()))

        Thread.sleep(1000)

        onView(withId(R.id.failed_requests)).perform(click())
        onView(withId(R.id.failed_requests)).check(matches(isChecked()))

        Thread.sleep(1000)

        onView(withId(R.id.all_request)).perform(click())
        onView(withId(R.id.all_request)).check(matches(isChecked()))
    }

}