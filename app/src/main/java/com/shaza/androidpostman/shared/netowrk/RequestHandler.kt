package com.shaza.androidpostman.shared.netowrk

import com.shaza.androidpostman.home.model.RequestType
import java.io.OutputStreamWriter
import java.net.HttpURLConnection

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
object RequestHandler {
    fun applyHeaders(connection: HttpURLConnection, headers: Map<String, String>) {
        headers.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }
    }

    fun appendBodyIfPost(requestType: RequestType, body: String?, connection: HttpURLConnection) {
        if (requestType == RequestType.POST && body != null) {
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            OutputStreamWriter(connection.outputStream).use {
                it.write(body)
                it.flush()
            }
        }
    }
}