package com.shaza.androidpostman.shared.netowrk

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
class APIClient : HTTPClient {

    override fun makeRequest(
        url: String,
        requestType: RequestType,
        headers: Map<String, String>,
        body: String?
    ): NetworkResponse {
        try {
            val connection = ConnectionManager.createConnection(url, requestType)
            RequestHandler.applyHeaders(connection, headers)
            RequestHandler.appendBodyIfPost(requestType, body, connection)
            val response = ResponseHandler.handleResponse(headers,body,connection)
            response.queryParameters = UrlUtils.extractQueryParams(url)
            return response
        } catch (e: Exception) {
            val networkResponse = NetworkResponse(url, requestType, requestHeaders = headers, body = body,error = e.message)
            networkResponse.queryParameters = UrlUtils.extractQueryParams(url)
            return networkResponse
        }
    }
}