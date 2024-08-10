package com.shaza.androidpostman.shared.netowrk

import android.content.ContentResolver
import android.net.Uri
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
        body: String? = null,
        uri: Uri? = null,
        contentResolver: ContentResolver
    ): NetworkResponse
}