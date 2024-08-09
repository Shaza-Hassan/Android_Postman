package com.shaza.androidpostman.shared.utils

import android.os.Build

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
class SdkVersionProvider {
    fun getSdkInt(): Int {
        return Build.VERSION.SDK_INT
    }
}