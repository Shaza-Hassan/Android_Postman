package com.shaza.androidpostman.requestinfo.viewmodel

import android.os.Build
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shaza.androidpostman.requestInfo.viewmodel.RequestInfoViewModel
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.utils.SdkVersionProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
@RunWith(MockitoJUnitRunner::class)
class RequestInfoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: RequestInfoViewModel
    lateinit var mockNetworkResponse: NetworkResponse
    lateinit var sdkVersionProvider: SdkVersionProvider

    private val mockBundle: Bundle = mock(Bundle::class.java)
    private val mockObserver: Observer<NetworkResponse> = mock(Observer::class.java) as Observer<NetworkResponse>


    @Before
    fun setUp() {
        sdkVersionProvider = mock(SdkVersionProvider::class.java)
        viewModel = RequestInfoViewModel(sdkVersionProvider)

        mockNetworkResponse = mock(NetworkResponse::class.java)
        `when`(mockBundle.getParcelable("networkResponse", NetworkResponse::class.java))
            .thenReturn(mockNetworkResponse)

        `when`(mockBundle.getParcelable<NetworkResponse>("networkResponse"))
            .thenReturn(mockNetworkResponse)

        viewModel.networkResponse.observeForever(mockObserver)

    }

    @Test
    fun `extractData should call extractDataFromBundle for API level TIRAMISU and above`() {
        val (viewModel, mockNetworkResponse) = setupForAPILeveTIRAMISUAndAbove()
        // When
        viewModel.extractData(mockBundle)
        // Then
        verify(mockBundle).getParcelable("networkResponse", NetworkResponse::class.java)
        assertEquals(mockNetworkResponse, viewModel.networkResponse.value)
        verify(mockObserver).onChanged(mockNetworkResponse)
    }

    private fun setupForAPILeveTIRAMISUAndAbove(): Pair<RequestInfoViewModel, NetworkResponse> {
        `when`(sdkVersionProvider.getSdkInt()).thenReturn(Build.VERSION_CODES.TIRAMISU)
        val viewModel = RequestInfoViewModel(sdkVersionProvider)
        val mockNetworkResponse = mock(NetworkResponse::class.java)
        viewModel.networkResponse.observeForever(mockObserver)

        `when`(mockBundle.getParcelable("networkResponse", NetworkResponse::class.java))
            .thenReturn(mockNetworkResponse)
        return Pair(viewModel, mockNetworkResponse)
    }

    @Test
    fun `extractData should call extractDataFromBundle for API level lower TIRAMISU`() {
        // When
        viewModel.extractData(mockBundle)
        // Then
        verify(mockBundle).getParcelable<NetworkResponse>("networkResponse")
        assertEquals(mockNetworkResponse, viewModel.networkResponse.value)
        verify(mockObserver).onChanged(mockNetworkResponse)
    }


}