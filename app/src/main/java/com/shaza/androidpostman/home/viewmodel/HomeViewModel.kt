package com.shaza.androidpostman.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaza.androidpostman.home.model.Header
import com.shaza.androidpostman.home.model.HomeGateway
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.Resource

class HomeViewModel(
    private val homeGateway: HomeGateway
) : HomeViewModelInterface, ViewModel() {

    private val _url = MutableLiveData<String>()
    override val url: LiveData<String> = _url

    private val _requestType = MutableLiveData<RequestType>()
    override val requestType: LiveData<RequestType> = _requestType

    private val _headers = MutableLiveData<MutableList<Header>>()
    override val headers: LiveData<MutableList<Header>> = _headers

    private val _body = MutableLiveData<String>()
    override val body: LiveData<String> = _body

    private val _response = MutableLiveData<Resource<NetworkResponse>>()
    override val response: LiveData<Resource<NetworkResponse>> = _response

    init {
        _response.value = Resource.idle()
        _headers.value = mutableListOf()
    }

    override fun setUrl(url: String) {
        _url.value = url
    }

    override fun setRequestType(type: RequestType) {
        _requestType.value = type
    }

    override fun addHeader() {
        val header = Header("", "")
        _headers.value?.add(header)
    }

    override fun removeHeader(index: Int) {
        _headers.value?.removeAt(index)
    }

    override fun getHeaders(): List<Header> {
        return _headers.value ?: emptyList()
    }

    override fun setBody(body: String) {
        _body.value = body
    }

    override fun sendRequest() {
        // Ensure that you're using the latest state values
        val url = _url.value ?: ""
        val requestType = _requestType.value ?: RequestType.GET
        val headers = _headers.value ?: emptyList()
        val body = _body.value


        val requestHeaders = mutableMapOf<String, String>()
        headers.map {
            if (it.title?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                requestHeaders[it.title!!] = it.value!!
            }
        }

        Thread {
            _response.postValue(Resource.loading())
            val networkResponse = homeGateway.makeRequest(url, requestType, requestHeaders, body)
            Log.v("HomeViewModel", "Response: $networkResponse")
            _response.postValue(Resource.success(networkResponse))
        }.start()
    }
}