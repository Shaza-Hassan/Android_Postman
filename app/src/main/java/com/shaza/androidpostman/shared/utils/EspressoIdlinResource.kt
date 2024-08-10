package com.shaza.androidpostman.shared.utils

import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Created by Shaza Hassan on 2024/Aug/10.
 */
object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)
    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}