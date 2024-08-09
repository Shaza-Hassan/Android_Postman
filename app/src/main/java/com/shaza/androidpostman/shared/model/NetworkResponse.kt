package com.shaza.androidpostman.shared.model

import android.os.Parcelable
import com.shaza.androidpostman.home.model.RequestType
import kotlinx.parcelize.Parcelize

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
@Parcelize
data class NetworkResponse(
    var url: String? = null,
    var requestType: RequestType? = null,
    var queryParameters: Map<String, String>? = null,
    var requestHeaders: Map<String, String>? = null,
    var responseHeaders: Map<String, String>? = null,
    var body: String? = null,
    var responseCode: Int? = null,
    var elapsedTime: Long? = null,
    var response: String? = null,
    var error: String? = null
) : Parcelable {

    fun isSuccessful(): Boolean {
        return responseCode in 200..299
    }
}