package com.shaza.androidpostman.home.model

import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
interface HomeGateway {
    fun makeRequest(
        url: String,
        requestType: RequestType,
        headers: Map<String, String>,
        body: String? = null
    ): NetworkResponse

    fun addToDB(networkResponse: NetworkResponse)
}