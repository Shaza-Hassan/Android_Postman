package com.shaza.androidpostman.shared.netowrk

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
interface HTTPClient {
    fun makeRequest(
        url: String,
        requestType: RequestType,
        headers: Map<String, String>,
        body: String? = null
    ): NetworkResponse
}