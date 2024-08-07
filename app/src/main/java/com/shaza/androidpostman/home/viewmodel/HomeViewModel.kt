package com.shaza.androidpostman.home.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaza.androidpostman.home.model.Header
import com.shaza.androidpostman.home.model.HomeGateway
import com.shaza.androidpostman.home.model.RequestType

class HomeViewModel(
    private val homeGateway: HomeGateway
) : ViewModel() {


    private val _url = MutableLiveData<String>()
    private val _requestType = MutableLiveData<RequestType>()
    private val _headers = MutableLiveData<MutableList<Header>>()
    private val _body = MutableLiveData<String>()

    init {
        _headers.value = mutableListOf()
    }
    fun setUrl(url: String) {
        _url.value = url
    }

    fun setRequestType(type: RequestType) {
        _requestType.value = type
    }

    fun addHeader() {
        val header = Header("", "")
        _headers.value?.add(header)
    }

    fun removeHeader(index: Int) {
        _headers.value?.removeAt(index)
    }

    fun getHeaders(): List<Header> {
        return _headers.value ?: emptyList()
    }

    fun setBody(body: String) {
        _body.value = body
    }

    fun sendRequest() {
        // Ensure that you're using the latest state values
        val url = _url.value ?: ""
        val requestType = _requestType.value ?: RequestType.GET
        val headers = _headers.value ?: emptyList()
        val body = _body.value

// Log or handle request logic
        Log.v("HomeViewModel", "sendRequest")
        Log.v("HomeViewModel", "url: $url")
        Log.v("HomeViewModel", "requestType: $requestType")
        Log.v("HomeViewModel", "headers: $headers")
        Log.v("HomeViewModel", "body: $body")

        val requestHeaders = mutableMapOf<String, String>()
        headers.map {
            if (it.title?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                requestHeaders[it.title!!] = it.value!!
            }
        }
        Thread {
            val networkResponse = homeGateway.makeRequest(url, requestType, requestHeaders, body)
            Log.v("HomeViewModel", "NetworkResponse: $networkResponse")
        }.start()

    }
}