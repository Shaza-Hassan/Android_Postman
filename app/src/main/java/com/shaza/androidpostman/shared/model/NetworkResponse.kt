package com.shaza.androidpostman.shared.model

import com.shaza.androidpostman.home.model.RequestType

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
data class NetworkResponse(
    var url: String? = null,
    var requestType: RequestType? = null,
    var queryParameters: Map<String, String>? = null,
    var requestHeaders: Map<String, String>? = null,
    var responseHeaders: Map<String, String>? = null,
    var body: String? = null,
    var responseCode: Int? = null,
    var response: String? = null,
    var error: String? = null
)   {

    fun isSuccessful(): Boolean {
        return responseCode in 200..299
    }
}