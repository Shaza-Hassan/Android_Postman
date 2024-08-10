package com.shaza.androidpostman.requestInfo.viewmodel

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.utils.SdkVersionProvider

class RequestInfoViewModel(private val sdkVersionProvider: SdkVersionProvider = SdkVersionProvider()) :
    ViewModel(), RequestInfoViewModelInterface {


    private var _networkResponse: MutableLiveData<NetworkResponse> = MutableLiveData()
    override val networkResponse: LiveData<NetworkResponse> = _networkResponse
    override val sdk = sdkVersionProvider.getSdkInt()

    override fun extractData(bundle: Bundle) {

        if (sdk >= Build.VERSION_CODES.TIRAMISU) {
            extractDataFromBundle(bundle)
        } else {
            extractDataFromBundleOld(bundle)
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun extractDataFromBundle(bundle: Bundle) {
        _networkResponse.value =
            bundle.getParcelable("networkResponse", NetworkResponse::class.java)
    }

    @Suppress("DEPRECATION")
    private fun extractDataFromBundleOld(bundle: Bundle) {
        _networkResponse.value = bundle.getParcelable("networkResponse")
    }
}

interface RequestInfoViewModelInterface {
    val networkResponse: LiveData<NetworkResponse>
    val sdk: Int

    fun extractData(bundle: Bundle)
}