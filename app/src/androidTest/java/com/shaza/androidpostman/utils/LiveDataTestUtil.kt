package com.shaza.androidpostman.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

object LiveDataTestUtil {
    fun <T> fromValue(value: T): LiveData<T> {
        return MutableLiveData<T>().apply { postValue(value) }
    }
}