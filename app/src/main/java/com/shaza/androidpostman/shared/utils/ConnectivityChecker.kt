package com.shaza.androidpostman.shared.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Created by Shaza Hassan on 2024/Aug/10.
 */

class ConnectivityChecker(private val context: Context) {

    fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
