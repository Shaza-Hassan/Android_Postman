package com.shaza.androidpostman.home.viewmodel

import androidx.lifecycle.LiveData
import com.shaza.androidpostman.home.model.Header
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.Resource

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */
interface HomeViewModelInterface {
    val url: LiveData<String>
    val requestType: LiveData<RequestType>
    val headers: LiveData<MutableList<Header>>
    val body: LiveData<String>
    val response: LiveData<Resource<NetworkResponse>>

    fun setUrl(url: String)
    fun setRequestType(type: RequestType)
    fun addHeader()
    fun removeHeader(index: Int)
    fun getHeaders(): List<Header>
    fun setBody(body: String)
    fun sendRequest()
}