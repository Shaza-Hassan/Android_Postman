package com.shaza.androidpostman.shared.model

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

sealed class ResourceStatus {
    data object Idle : ResourceStatus()
    data object Loading : ResourceStatus()
    data object Success : ResourceStatus()
    data object Error : ResourceStatus()

}
