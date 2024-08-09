package com.shaza.androidpostman.home.model

import com.shaza.androidpostman.shared.database.AddRequestInDB
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.netowrk.HTTPClient

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
class HomeRepository(
    private val httpClient: HTTPClient,
    private val addRequestInDB: AddRequestInDB
): HomeGateway {

    override fun makeRequest(
        url: String,
        requestType: RequestType,
        headers: Map<String, String>,
        body: String?
    ): NetworkResponse {
        return httpClient.makeRequest(url, requestType, headers, body)
    }

    override fun addToDB(networkResponse: NetworkResponse) {
        addRequestInDB.addRequestToDataBase(networkResponse)
    }
}