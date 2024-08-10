package com.shaza.androidpostman.home.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaza.androidpostman.home.model.Header
import com.shaza.androidpostman.home.model.HomeGateway
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.Resource
import com.shaza.androidpostman.shared.utils.EspressoIdlingResource
import java.util.concurrent.Executors

class HomeViewModel(
    private val homeGateway: HomeGateway,
) : ViewModel() {

    private val _url = MutableLiveData<String>()
    val url: LiveData<String> = _url

    private val _requestType = MutableLiveData<RequestType>()
    val requestType: LiveData<RequestType> = _requestType

    private val _headers = MutableLiveData<MutableList<Header>>()
    val headers: LiveData<MutableList<Header>> = _headers

    private val _body = MutableLiveData<String>()
    val body: LiveData<String> = _body

    private val _response = MutableLiveData<Resource<NetworkResponse>>()
    val response: LiveData<Resource<NetworkResponse>> = _response

    private val _selectedFileUri = MutableLiveData<Uri?>()
    val selectedFileUri: LiveData<Uri?> = _selectedFileUri

    init {
        _response.value = Resource.idle()
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

    fun setHeaders(headers: List<Header>) {
        _headers.value = headers.toMutableList()
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

    fun setSelectedFileUri(uri: Uri?) {
        _selectedFileUri.value = uri
    }

    fun onSendRequestClicked(contentResolver: ContentResolver) {
        if (homeGateway.isConnected()) {
            sendRequest(contentResolver)
        } else {
            _response.postValue(Resource.error(Exception("No internet connection")))
        }
    }

    fun sendRequest(contentResolver: ContentResolver) {
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

        EspressoIdlingResource.increment()
        Executors.newSingleThreadExecutor().execute {
            _response.postValue(Resource.loading())
            val networkResponse = homeGateway.makeRequest(
                url,
                requestType,
                requestHeaders,
                body,
                _selectedFileUri.value,
                contentResolver
            )
            _response.postValue(Resource.success(networkResponse))
            homeGateway.addToDB(networkResponse)
            EspressoIdlingResource.decrement()
        }
    }
}