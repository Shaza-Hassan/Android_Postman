package com.shaza.androidpostman.home.model

import android.content.ContentResolver
import android.net.Uri
import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
interface HomeGateway {
    fun makeRequest(
        url: String,
        requestType: RequestType,
        headers: Map<String, String>,
        body: String? = null,
        uri: Uri? = null,
        contentResolver: ContentResolver
    ): NetworkResponse

    fun addToDB(networkResponse: NetworkResponse)

    fun isConnected(): Boolean
}