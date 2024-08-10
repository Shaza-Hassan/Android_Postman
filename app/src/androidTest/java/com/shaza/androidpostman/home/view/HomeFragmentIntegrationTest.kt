package com.shaza.androidpostman.home.view

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaza.androidpostman.MainActivity
import com.shaza.androidpostman.R
import com.shaza.androidpostman.history.view.HistoryFragment
import com.shaza.androidpostman.home.model.HomeGateway
import com.shaza.androidpostman.home.viewmodel.HomeViewModel
import com.shaza.androidpostman.shared.utils.ConnectivityChecker
import com.shaza.androidpostman.shared.utils.EspressoIdlingResource
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Shaza Hassan on 2024/Aug/10.
 */

@RunWith(AndroidJUnit4::class)
class HomeFragmentIntegrationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()

    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testFullFlowGetRequest() {

        onView(withId(R.id.get_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://jsonplaceholder.typicode.com/posts/1"))
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.history_menu_item)).perform(click())

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.request_history_recycler_view)) // Replace with your RecyclerView ID
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, // First item in the list
                    click()
                )
            )

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testPOSTFlow() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://jsonplaceholder.typicode.com/posts"))
        onView(withId(R.id.body_edit_text)).perform(
            typeText(
                "{\n" + "    \"title\": \"foo\",\n" + "    \"body\": \"bar\",\n" + "    \"userId\": 1\n" + "}"
            )
        )
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.history_menu_item)).perform(click())

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.request_history_recycler_view)) // Replace with your RecyclerView ID
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, // First item in the list
                    click()
                )
            )

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testGETAndPostFlow() {
        onView(withId(R.id.get_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://jsonplaceholder.typicode.com/posts/1"))
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(
            clearText(), typeText("https://jsonplaceholder.typicode.com/posts")
        )
        onView(withId(R.id.body_edit_text)).perform(
            typeText(
                "{\n" + "    \"title\": \"foo\",\n" + "    \"body\": \"bar\",\n" + "    \"userId\": 1\n" + "}"
            )
        )
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.get_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(clearText(), typeText(""))
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.history_menu_item)).perform(click())

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.get_type)).perform(click())

        onView(withId(R.id.success_requests)).perform(click())

        onView(withId(R.id.sort_by_time)).perform(click())

        onView(withId(R.id.all_request_type)).perform(click())

        onView(withId(R.id.failed_requests)).perform(scrollTo(), click())
    }

    @Test
    fun testPostWithUploadFile() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://httpbin.org/post"))

        val mockFileUri = Uri.parse("content://path/to/mock/file.text")
        val resultData = Intent().apply {
            data = mockFileUri
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        Intents.intending(
            allOf(
                hasAction(Intent.ACTION_GET_CONTENT), hasType("*/*")
            )
        ).respondWith(result)

        onView(withId(R.id.file_to_upload)).perform(scrollTo(), click())

        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.history_menu_item)).perform(click())

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.request_history_recycler_view)) // Replace with your RecyclerView ID
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, // First item in the list
                    click()
                )
            )

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulGETRequestWithQueryParameters() {

        onView(withId(R.id.get_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://dummyjson.com/posts?limit=10&skip=10&select=title,reactions,userId"))
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.history_menu_item)).perform(click())

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.request_history_recycler_view)) // Replace with your RecyclerView ID
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, // First item in the list
                    click()
                )
            )

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun failedGETRequestWith500ErrorCode() {
        onView(withId(R.id.get_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://httpbin.org/status/500"))
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        Thread.sleep(1000)

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulPOSTRequest() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://jsonplaceholder.typicode.com/posts"))
        onView(withId(R.id.body_edit_text)).perform(
            typeText(
                "{\n" + "    \"title\": \"foo\",\n" + "    \"body\": \"bar\",\n" + "    \"userId\": 1\n" + "}"
            )
        )
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.history_menu_item)).perform(click())

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        onView(withId(R.id.request_history_recycler_view)) // Replace with your RecyclerView ID
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, // First item in the list
                    click()
                )
            )

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun failedPOSTRequest404ErrorCode() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://httpbin.org/status/404"))
        onView(withId(R.id.body_edit_text)).perform(
            typeText(
                "{\n" + "    \"title\": \"foo\",\n" + "    \"body\": \"bar\",\n" + "    \"userId\": 1\n" + "}"
            )
        )
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        Thread.sleep(4000)

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

//    Failed POST request with UnknownHostException

    @Test
    fun failedPOSTRequestUnknownHostException() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://unknownhost.com"))
        onView(withId(R.id.body_edit_text)).perform(
            typeText(
                "{\n" + "    \"title\": \"foo\",\n" + "    \"body\": \"bar\",\n" + "    \"userId\": 1\n" + "}"
            )
        )
        onView(withId(R.id.send_request)).perform(click())

        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))

        Thread.sleep(4000)

        pressBack()

        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

    //    Requesting while the device is not connected to the internet
    @Test
    fun failedPOSTRequestNoInternetConnection() {

        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://jsonplaceholder.typicode.com/posts"))
        onView(withId(R.id.body_edit_text)).perform(
            typeText(
                "{\n" + "    \"title\": \"foo\",\n" + "    \"body\": \"bar\",\n" + "    \"userId\": 1\n" + "}"
            )
        )
        onView(withId(R.id.send_request)).perform(click())

    }
}
