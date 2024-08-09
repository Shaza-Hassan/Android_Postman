package com.shaza.androidpostman.utills

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.shaza.androidpostman.shared.utils.ConnectivityChecker
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by Shaza Hassan on 2024/Aug/10.
 */

class ConnectivityCheckerTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCapabilities: NetworkCapabilities
    private lateinit var connectivityChecker: ConnectivityChecker

    @Before
    fun setup() {
        context = mockk()
        connectivityManager = mockk()
        networkCapabilities = mockk()

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        connectivityChecker = ConnectivityChecker(context)
    }

    @Test
    fun `isConnected returns true when network capabilities indicate internet`() {
        val network = mockk<android.net.Network>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        val result = connectivityChecker.isConnected()

        assertTrue(result)
        verify { connectivityManager.activeNetwork }
        verify { connectivityManager.getNetworkCapabilities(network) }
        verify { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) }
    }

    @Test
    fun `isConnected returns false when no active network`() {
        every { connectivityManager.activeNetwork } returns null

        val result = connectivityChecker.isConnected()

        assertFalse(result)
        verify { connectivityManager.activeNetwork }
        verify(exactly = 0) { connectivityManager.getNetworkCapabilities(any()) }
    }

    @Test
    fun `isConnected returns false when network capabilities do not indicate internet`() {
        val network = mockk<android.net.Network>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false

        val result = connectivityChecker.isConnected()

        assertFalse(result)
        verify { connectivityManager.activeNetwork }
        verify { connectivityManager.getNetworkCapabilities(network) }
        verify { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) }
    }
}
