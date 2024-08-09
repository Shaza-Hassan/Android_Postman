package com.shaza.androidpostman.shared.model

import android.os.Parcelable
import com.shaza.androidpostman.home.model.RequestType
import kotlinx.parcelize.Parcelize

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
@Parcelize
data class NetworkResponse(
    override var url: String? = null,
    override var requestType: RequestType? = null,
    override var queryParameters: Map<String, String>? = null,
    override var requestHeaders: Map<String, String>? = null,
    override var responseHeaders: Map<String, String>? = null,
    override var body: String? = null,
    override var responseCode: Int? = null,
    override var elapsedTime: Long? = null,
    override var response: String? = null,
    override var error: String? = null
) : NetworkResponseInterface {

    override fun isSuccessful(): Boolean {
        return responseCode in 200..299
    }
}

// create interface for this data class
interface NetworkResponseInterface : Parcelable {
    val url: String?
    val requestType: RequestType?
    val queryParameters: Map<String, String>?
    val requestHeaders: Map<String, String>?
    val responseHeaders: Map<String, String>?
    val body: String?
    val responseCode: Int?
    val elapsedTime: Long?
    val response: String?
    val error: String?
    fun isSuccessful(): Boolean
}