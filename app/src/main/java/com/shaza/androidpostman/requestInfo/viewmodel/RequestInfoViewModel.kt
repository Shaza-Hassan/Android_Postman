package com.shaza.androidpostman.requestInfo.viewmodel

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.utils.SdkVersionProvider

class RequestInfoViewModel(private val sdkVersionProvider: SdkVersionProvider = SdkVersionProvider()) : ViewModel() {


    private var _networkResponse: MutableLiveData<NetworkResponse> = MutableLiveData()
    val networkResponse: LiveData<NetworkResponse> = _networkResponse
    val sdk = sdkVersionProvider.getSdkInt()

    fun extractData(bundle: Bundle) {

        if (sdk >= Build.VERSION_CODES.TIRAMISU) {
            extractDataFromBundle(bundle)
        } else {
            extractDataFromBundleOld(bundle)
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun extractDataFromBundle(bundle: Bundle) {
        _networkResponse.value = bundle.getParcelable("networkResponse",NetworkResponse::class.java)
    }

    @Suppress("DEPRECATION")
    private fun extractDataFromBundleOld(bundle: Bundle) {
        _networkResponse.value = bundle.getParcelable("networkResponse")
    }
}