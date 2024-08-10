package com.shaza.androidpostman.home.model

import android.content.ContentResolver
import android.net.Uri
import com.shaza.androidpostman.shared.database.AddRequestInDB
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.netowrk.HTTPClient
import com.shaza.androidpostman.shared.utils.ConnectivityChecker

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */
class HomeRepository(
    private val httpClient: HTTPClient,
    private val addRequestInDB: AddRequestInDB,
    private val connectivityChecker: ConnectivityChecker,
): HomeGateway {

    override fun makeRequest(
        url: String,
        requestType: RequestType,
        headers: Map<String, String>,
        body: String?,
        uri: Uri?,
        contentResolver: ContentResolver
    ): NetworkResponse {
        return httpClient.makeRequest(url, requestType, headers, body,uri,contentResolver)
    }

    override fun addToDB(networkResponse: NetworkResponse) {
        addRequestInDB.addRequestToDataBase(networkResponse)
    }

    override fun isConnected(): Boolean {
        return connectivityChecker.isConnected()
    }
}