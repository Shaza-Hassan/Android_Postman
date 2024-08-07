package com.shaza.androidpostman.shared.netowrk

import com.shaza.androidpostman.home.model.RequestType
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
object ConnectionManager {
    fun createConnection(url: String, requestType: RequestType): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = requestType.name
        return connection
    }
}