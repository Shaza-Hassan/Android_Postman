package com.shaza.androidpostman.utils

import androidx.lifecycle.LiveData
import androidx.test.espresso.IdlingResource

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

class DataIdlingResource(
    private val liveData: LiveData<*>
) : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = DataIdlingResource::class.java.name

    override fun isIdleNow(): Boolean {
        return liveData.value != null
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
        liveData.observeForever {
            if (isIdleNow) {
                callback?.onTransitionToIdle()
            }
        }
    }
}