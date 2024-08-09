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
        val startTime = System.currentTimeMillis()
        var response: NetworkResponse? = null
        try {

            val connection = ConnectionManager.createConnection(url, requestType)
            RequestHandler.applyHeaders(connection, headers)
            RequestHandler.appendBodyIfPost(requestType, body, connection)
            response = ResponseHandler.handleResponse(headers,body,connection)
            response.queryParameters = UrlUtils.extractQueryParams(url)
        } catch (e: Exception) {
            response = NetworkResponse(url, requestType, requestHeaders = headers, body = body,error = e.message)
            response.queryParameters = UrlUtils.extractQueryParams(url)
        } finally {
            val elapsedTime = System.currentTimeMillis() - startTime
            response?.let {
                it.elapsedTime = elapsedTime
                return it
            } ?: run {
                val networkResponse = NetworkResponse(url, requestType, requestHeaders = headers, body = body,elapsedTime = elapsedTime)
                return networkResponse
            }
        }
    }
}