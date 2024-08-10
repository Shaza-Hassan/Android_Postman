package com.shaza.androidpostman.home.view

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaza.androidpostman.MainActivity
import com.shaza.androidpostman.R
import com.shaza.androidpostman.utils.typeTextInChildViewWithId
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

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

    @Test
    fun testOnHistoryButtonClicked() {
        onView(withId(R.id.history_menu_item)).perform(click())
        onView(withId(R.id.history_fragment)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testOnPostButtonPressed() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.body_of_post_request_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testOnWriteUrlInEditText() {
        onView(withId(R.id.url_edit_text)).perform(typeText("https://jsonplaceholder.typicode.com/posts"))
        onView(withId(R.id.url_edit_text)).check(matches(withText("https://jsonplaceholder.typicode.com/posts")))
    }

    @Test
    fun testOnWriteBodyInEditTextWhenPostButtonClicked() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.body_edit_text)).perform(typeText("This is a body"))
        onView(withId(R.id.body_edit_text)).check(matches(withText("This is a body")))
    }

    @Test
    fun testOnGetButtonClicked() {
        onView(withId(R.id.get_button)).perform(click())
        onView(withId(R.id.body_of_post_request_layout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testAddNewHeaderButtonClicked() {
        onView(withId(R.id.add_new_header)).perform(click())
        onView(withId(R.id.headers_list)).check(matches(hasDescendant(withId(R.id.header_title_edit_text))))
    }

    @Test
    fun testAddMultipleHeadersAndWriteInFirstItem() {
        onView(withId(R.id.add_new_header))
            .perform(click())
            .perform(click())
            .perform(click())

        onView(withId(R.id.headers_list)) // Replace with your RecyclerView ID
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, // First item in the list
                    typeTextInChildViewWithId(
                        R.id.header_title_edit_text,
                        "Header 1 Text"
                    ) // Replace with your actual EditText ID within the item layout
                )
            )
    }

    @Test
    fun fillScreenWithDataForGetRequest() {
        onView(withId(R.id.url_edit_text)).perform(typeText("https://dummyjson.com/posts/1"))
        onView(withId(R.id.add_new_header)).perform(click())

        onView(withId(R.id.headers_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                typeTextInChildViewWithId(R.id.header_title_edit_text, "Header 1 Text"),
            )
        )
        onView(withId(R.id.headers_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                typeTextInChildViewWithId(R.id.header_value_edit_text, "Header 1 Text"),
            )
        )
        onView(withId(R.id.send_request)).perform(click())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun fillScreenWithDataForGetWithoutHeaderRequest() {
        onView(withId(R.id.url_edit_text)).perform(typeText("https://dummyjson.com/posts/1"))
        onView(withId(R.id.send_request)).perform(click())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun fillScreenWithDataForPostRequestWithoutHeader() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://dummyjson.com/posts/add"))
        onView(withId(R.id.body_edit_text)).perform(typeText("{'title':'foo','body':'bar','userId':1}"))
        onView(withId(R.id.send_request)).perform(click())
    }

    @Test
    fun fillScreenWithDataForPostRequestWithHeader() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://dummyjson.com/posts/add"))
        onView(withId(R.id.add_new_header)).perform(click())
        onView(withId(R.id.headers_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                typeTextInChildViewWithId(R.id.header_title_edit_text, "Header 1 Text"),
            )
        )


        onView(withId(R.id.headers_list)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                typeTextInChildViewWithId(R.id.header_value_edit_text, "Header 1 Text"),
            )
        )

        onView(withId(R.id.body_edit_text)).perform(
            scrollTo(),
            typeText("{'title':'foo','body':'bar','userId':1}")
        )
        onView(withId(R.id.send_request)).perform(click(), closeSoftKeyboard())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))

    }

    @Test
    fun handlePostRequestWithSelectFile() {
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(
            typeText("https://dummyjson.com/posts/add"),
            closeSoftKeyboard()
        )
        val mockFileUri = Uri.parse("content://path/to/mock/file.text")
        val resultData = Intent().apply {
            data = mockFileUri
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        Intents.intending(
            allOf(
                hasAction(Intent.ACTION_GET_CONTENT),
                hasType("*/*")
            )
        ).respondWith(result)

        onView(withId(R.id.file_to_upload)).perform(scrollTo(), click())
    }

    @Test
    fun handlePostRequestWithSelectFileAndRemoveIt() {
        onView(withId(R.id.post_button)).perform(scrollTo(), click())
        onView(withId(R.id.url_edit_text)).perform(
            scrollTo(),
            typeText("https://dummyjson.com/posts/add"),
            closeSoftKeyboard()
        )
        val mockFileUri = Uri.parse("content://path/to/mock/file.text")
        val resultData = Intent().apply {
            data = mockFileUri
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        Intents.intending(
            allOf(
                hasAction(Intent.ACTION_GET_CONTENT),
                hasType("*/*")
            )
        ).respondWith(result)

        onView(withId(R.id.file_to_upload)).perform(scrollTo(), click())
        onView(withId(R.id.remove_file)).perform(scrollTo(), click())
        onView(withId(R.id.selected_file_layout)).check(matches(not(isDisplayed())))
    }
}

