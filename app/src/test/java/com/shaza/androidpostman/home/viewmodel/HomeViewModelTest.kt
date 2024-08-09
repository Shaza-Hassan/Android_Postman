package com.shaza.androidpostman.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shaza.androidpostman.home.model.Header
import com.shaza.androidpostman.home.model.HomeGateway
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.Resource
import com.shaza.androidpostman.shared.model.ResourceStatus
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockHomeGateway: HomeGateway
    private val observer = mockk<Observer<Resource<NetworkResponse>>>(relaxed = true)

    @Before
    fun setUp() {
        mockHomeGateway = mockk()
        viewModel = HomeViewModel(mockHomeGateway)
    }

    @Test
    fun `test set url`() {
        val testUrl = "http://example.com"
        viewModel.setUrl(testUrl)
        assertEquals(testUrl, viewModel.url.value)
    }

    @Test
    fun `test set request type`() {
        val testType = RequestType.POST
        viewModel.setRequestType(testType)
        assertEquals(testType, viewModel.requestType.value)
    }

    @Test
    fun setBody_updatesLiveData() {
        val testBody = "test body"
        viewModel.setBody(testBody)
        assertEquals(testBody, viewModel.body.value)
    }

    @Test
    fun addHeader_addsHeaderToList() {
        viewModel.addHeader()
        assertEquals(1, viewModel.getHeaders().size)
    }

    @Test
    fun removeHeader_removesHeaderFromList() {
        viewModel.addHeader()
        viewModel.removeHeader(0)
        assertEquals(0, viewModel.getHeaders().size)
    }

    @Test
    fun `sendRequest updates LiveData with success`() {
        // Arrange
        val url = "http://example.com"
        val requestType = RequestType.GET
        val headers = listOf<Header>()
        val body = ""
        val networkResponse =
            NetworkResponse(url, requestType, requestHeaders = headers.associate { it.title!! to it.value!! }, body = body)

        viewModel.setBody(body)
        viewModel.response.observeForever(observer)

        every {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body
            )
        } returns networkResponse
        every { mockHomeGateway.addToDB(networkResponse) } just Runs

        // Set ViewModel state
        viewModel.setUrl(url)
        viewModel.setRequestType(requestType)

        // Act
        viewModel.sendRequest()

        val latch = CountDownLatch(1) // To wait for the background thread to finish

        every { observer.onChanged(any()) } answers {
            if (firstArg<Resource<NetworkResponse>>().status == ResourceStatus.Success) {
                latch.countDown() // Release the latch when success is posted
            }
        }

        // Assert
        verify { mockHomeGateway.makeRequest(url, requestType, headers.associate { it.title!! to it.value!! }, body) }
        verify { observer.onChanged(Resource.loading()) }
        latch.await(2, TimeUnit.SECONDS) // Wait for the background thread to complete
        verify { observer.onChanged(Resource.success(networkResponse)) }
        verify { mockHomeGateway.addToDB(networkResponse) }
    }

}