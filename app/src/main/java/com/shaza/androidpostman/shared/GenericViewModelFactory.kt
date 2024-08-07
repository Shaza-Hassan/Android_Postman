package com.shaza.androidpostman.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */

class GenericViewModelFactory<T : ViewModel>(
    private val viewModelClass: Class<T>,
    private val creator: () -> T
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModelClass)) {
            @Suppress("UNCHECKED_CAST")
            return creator() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}