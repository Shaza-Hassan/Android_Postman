package com.shaza.androidpostman.home.model

import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.netowrk.APIClient
import com.shaza.androidpostman.shared.netowrk.HTTPClient

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
class HomeRepository(
    private val httpClient: HTTPClient,
): HomeGateway {

    override fun makeRequest(
        url: String,
        requestType: RequestType,
        headers: Map<String, String>,
        body: String?
    ): NetworkResponse {
        return httpClient.makeRequest(url, requestType, headers, body)
    }

    companion object {
        fun newInstance(): HomeRepository {
            return HomeRepository(APIClient())
        }
    }
}