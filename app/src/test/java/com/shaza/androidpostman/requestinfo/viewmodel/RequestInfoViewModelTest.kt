package com.shaza.androidpostman.requestinfo.viewmodel

import android.os.Build
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shaza.androidpostman.requestInfo.viewmodel.RequestInfoViewModel
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.utils.SdkVersionProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

class RequestInfoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: RequestInfoViewModel
    lateinit var mockNetworkResponse: NetworkResponse
    lateinit var sdkVersionProvider: SdkVersionProvider

    private val mockBundle: Bundle = mockk()
    private val observer = mockk<Observer<NetworkResponse>>(relaxed = true)

    @Before
    fun setUp() {
        sdkVersionProvider = mockk()

        every { sdkVersionProvider.getSdkInt() } returns Build.VERSION_CODES.TIRAMISU
        viewModel = RequestInfoViewModel(sdkVersionProvider)

        mockNetworkResponse = mockk()
        every {
            mockBundle.getParcelable(
                "networkResponse",
                NetworkResponse::class.java
            )
        } returns mockNetworkResponse

        every { mockBundle.getParcelable<NetworkResponse>("networkResponse") } returns mockNetworkResponse

        viewModel.networkResponse.observeForever(observer)

    }

    @Test
    fun `extractData should call extractDataFromBundle for API level TIRAMISU and above`() {
        // When
        viewModel.extractData(mockBundle)
        // Then
        verify { mockBundle.getParcelable("networkResponse", NetworkResponse::class.java) }
        assertEquals(mockNetworkResponse, viewModel.networkResponse.value)
        verify { observer.onChanged(mockNetworkResponse) }
    }

    @Test
    fun `extractData should call extractDataFromBundle for API level lower TIRAMISU`() {
        every { sdkVersionProvider.getSdkInt() } returns (Build.VERSION_CODES.R)
        val viewModel = RequestInfoViewModel(sdkVersionProvider)
        viewModel.networkResponse.observeForever(observer)

        // When
        viewModel.extractData(mockBundle)
        // Then
        verify { mockBundle.getParcelable<NetworkResponse>("networkResponse") }
        assertEquals(mockNetworkResponse, viewModel.networkResponse.value)
        verify { observer.onChanged(mockNetworkResponse) }
    }


}