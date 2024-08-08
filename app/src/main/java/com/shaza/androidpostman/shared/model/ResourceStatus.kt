package com.shaza.androidpostman.shared.model

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

sealed class ResourceStatus {
    object Idle : ResourceStatus()
    object Loading : ResourceStatus()
    object Success : ResourceStatus()
    data class Error(val error: Throwable) : ResourceStatus()
}
