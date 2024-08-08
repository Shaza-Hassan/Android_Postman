package com.shaza.androidpostman.home.view

import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import org.junit.Test
import org.junit.runner.RunWith
import com.shaza.androidpostman.R
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.shaza.androidpostman.MainActivity
import com.shaza.androidpostman.viewactionutils.typeTextInChildViewWithId
import org.junit.Rule
import org.hamcrest.CoreMatchers.not

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java) // Replace with your activity that contains HomeFragment


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
    fun testAddMultipleHeadersAndWriteInFirstItem(){
        onView(withId(R.id.add_new_header))
            .perform(click())
            .perform(click())
            .perform(click())

        onView(withId(R.id.headers_list)) // Replace with your RecyclerView ID
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0, // First item in the list
                    typeTextInChildViewWithId(R.id.header_title_edit_text, "Header 1 Text") // Replace with your actual EditText ID within the item layout
                )
            )
    }

    @Test
    fun fillScreenWithDataForGetRequest(){
        onView(withId(R.id.url_edit_text)).perform(typeText("'https://dummyjson.com/posts/1'"))
        onView(withId(R.id.add_new_header)).perform(click())
        onView(withId(R.id.headers_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                typeTextInChildViewWithId(R.id.header_title_edit_text, "Header 1 Text"),
            )
        )
        onView(withId(R.id.headers_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                typeTextInChildViewWithId(R.id.header_value_edit_text, "Header 1 Text"),
            )
        )
        onView(withId(R.id.send_request)).perform(click())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }
    @Test
    fun fillScreenWithDataForGetWithoutHeaderRequest(){
        onView(withId(R.id.url_edit_text)).perform(typeText("https://dummyjson.com/posts/1"))
        onView(withId(R.id.send_request)).perform(click())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun fillScreenWithDataForPostRequestWithoutHeader(){
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://dummyjson.com/posts/add"))
        onView(withId(R.id.body_edit_text)).perform(typeText("{'title':'foo','body':'bar','userId':1}"))
        onView(withId(R.id.send_request)).perform(click())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun fillScreenWithDataForPostRequestWithHeader(){
        onView(withId(R.id.post_button)).perform(click())
        onView(withId(R.id.url_edit_text)).perform(typeText("https://dummyjson.com/posts/add"))
        onView(withId(R.id.add_new_header)).perform(click())
        onView(withId(R.id.headers_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                typeTextInChildViewWithId(R.id.header_title_edit_text, "Header 1 Text"),
            )
        )
        onView(withId(R.id.headers_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                typeTextInChildViewWithId(R.id.header_value_edit_text, "Header 1 Text"),
            )
        )
        onView(withId(R.id.body_edit_text)).perform(typeText("{'title':'foo','body':'bar','userId':1}"))
        onView(withId(R.id.send_request)).perform(click())
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))

    }
}

