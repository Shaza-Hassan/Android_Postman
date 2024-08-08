package com.shaza.androidpostman.viewactionutils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Matcher

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */


fun typeTextInChildViewWithId(id: Int, text: String): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isDisplayed()
        }

        override fun getDescription(): String {
            return "Type text into child view with specified ID"
        }

        override fun perform(uiController: UiController?, view: View?) {
            val childView = view?.findViewById<View>(id)
            childView?.let {
                click().perform(uiController, it)
                typeText(text).perform(uiController, it)

            }
        }
    }
}

fun performClickOnViewWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isDisplayed()
        }

        override fun getDescription(): String {
            return "Perform click on view with specified ID"
        }

        override fun perform(uiController: UiController?, view: View?) {
            val childView = view?.findViewById<View>(id)
            childView?.let {
                click().perform(uiController, it)
            }
        }
    }
}

fun isKeyboardHidden(): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "Check if the keyboard is hidden"
        }

        override fun perform(uiController: UiController?, view: View?) {
            val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val isKeyboardHidden = !imm.isAcceptingText
            if (!isKeyboardHidden) {
                throw AssertionError("Keyboard is not hidden")
            }
        }
    }
}