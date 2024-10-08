package com.shaza.androidpostman.shared.netowrk

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.model.NetworkResponse
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
object ResponseHandler {
    fun handleResponse(
        headers: Map<String, String>, requestBody: String?,
        connection: HttpURLConnection
    ): NetworkResponse {
        val networkResponse = NetworkResponse(
            url = connection.url.toString(),
            requestType = RequestType.valueOf(connection.requestMethod),
            requestHeaders = headers,
            body = requestBody
        )

        try {
            networkResponse.responseCode = connection.responseCode
            networkResponse.responseHeaders =
                connection.headerFields.mapValues { it.value.toString() }

            if (networkResponse.responseCode in 200..299) {
                networkResponse.response = connection.inputStream.readText()

            } else {
                networkResponse.error = connection.errorStream.readText()
            }
        } catch (e: Exception) {
            networkResponse.error = e.message
        } finally {
            connection.disconnect()
        }

        return networkResponse
    }
}

fun InputStream.readText(): String = BufferedReader(InputStreamReader(this)).use { it.readText() }
