package com.shaza.androidpostman.shared.model

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

data class Resource<T>(
    val status: ResourceStatus,
    val data: T? = null,
    val error: Throwable? = null
) {

    companion object {
        fun <T> idle(): Resource<T> = Resource(ResourceStatus.Idle)
        fun <T> loading(): Resource<T> = Resource(ResourceStatus.Loading)
        fun <T> success(data: T): Resource<T> = Resource(ResourceStatus.Success, data)
        fun <T> error(error: Throwable): Resource<T> = Resource(ResourceStatus.Error, error = error)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resource<*>

        return status == other.status
    }

    override fun hashCode(): Int {
        return status.hashCode()
    }
}
