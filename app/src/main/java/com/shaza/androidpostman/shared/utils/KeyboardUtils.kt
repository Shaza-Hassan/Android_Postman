package com.shaza.androidpostman.shared.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */


fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}