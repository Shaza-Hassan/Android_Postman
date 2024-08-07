package com.shaza.androidpostman.shared.netowrk

import android.net.Uri

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
object UrlUtils {
    fun extractQueryParams(urlString: String): Map<String, String> {
        val uri = Uri.parse(urlString)
        val queryParams = mutableMapOf<String, String>()

        uri.queryParameterNames.forEach { key ->
            queryParams[key] = uri.getQueryParameter(key).orEmpty()
        }

        return queryParams
    }
}