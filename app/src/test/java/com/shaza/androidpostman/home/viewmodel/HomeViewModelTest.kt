package com.shaza.androidpostman.home.viewmodel

import android.content.ContentResolver
import android.net.Uri
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
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
    private lateinit var contentResolver: ContentResolver
    private lateinit var uri: Uri

    @Before
    fun setUp() {
        mockHomeGateway = mockk()
        viewModel = spyk(HomeViewModel(mockHomeGateway))
        contentResolver = mockk()
        uri = mockk()
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
    fun `set body`() {
        val testBody = "test body"
        viewModel.setBody(testBody)
        assertEquals(testBody, viewModel.body.value)
    }

    @Test
    fun `add header to header list`() {
        viewModel.addHeader()
        assertEquals(1, viewModel.headers.value?.size)
    }

    @Test
    fun `remove header from header list`() {
        viewModel.addHeader()
        viewModel.removeHeader(0)
        assertEquals(0, viewModel.headers.value?.size)
    }

    @Test
    fun `sendRequest updates LiveData with success`() {
        // Arrange
        val url = "http://example.com"
        val requestType = RequestType.GET
        val headers = listOf<Header>()
        val body = ""
        val networkResponse =
            NetworkResponse(
                url,
                requestType,
                requestHeaders = headers.associate { it.title!! to it.value!! },
                body = body
            )

        viewModel.setBody(body)
        viewModel.response.observeForever(observer)

        every {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body, null, contentResolver
            )
        } returns networkResponse
        every { mockHomeGateway.addToDB(networkResponse) } just Runs

        // Set ViewModel state
        viewModel.setUrl(url)
        viewModel.setRequestType(requestType)

        // Act
        viewModel.sendRequest(contentResolver)

        val latch = CountDownLatch(1) // To wait for the background thread to finish

        every { observer.onChanged(any()) } answers {
            if (firstArg<Resource<NetworkResponse>>().status == ResourceStatus.Success) {
                latch.countDown() // Release the latch when success is posted
            }
        }

        // Assert
        verify {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body, null, contentResolver
            )
        }
        verify { observer.onChanged(Resource.loading()) }
        latch.await(2, TimeUnit.SECONDS) // Wait for the background thread to complete
        verify { observer.onChanged(Resource.success(networkResponse)) }
        verify { mockHomeGateway.addToDB(networkResponse) }
    }

    @Test
    fun `onSendRequestClicked calls sendRequest when connected`() {
        // Arrange
        every { mockHomeGateway.isConnected() } returns true
        val sendRequestSlot = slot<Unit>()
        every { viewModel.sendRequest(contentResolver) } answers {
            sendRequestSlot.captured = Unit
        }

        // Act
        viewModel.onSendRequestClicked(contentResolver)

        // Assert
        verify { mockHomeGateway.isConnected() }
        verify { viewModel.sendRequest(contentResolver) }
        assertEquals(Unit, sendRequestSlot.captured) // Ensure sendRequest was called
    }


    @Test
    fun `onSendRequestClicked posts error when not connected`() {
        every { mockHomeGateway.isConnected() } returns false

        viewModel.onSendRequestClicked(contentResolver)
        val actual = viewModel.response.value

        verify { mockHomeGateway.isConnected() }
        val expectedResource: Resource<NetworkResponse> =
            Resource.error(Exception("No internet connection"))

        assertEquals(
            expectedResource.status,
            viewModel.response.value?.status
        )

        assertEquals(
            expectedResource,
            actual
        )
    }

    @Test
    fun `test setSelectedFileUri`() {
        val testUri = Uri.parse("http://example.com")
        viewModel.setSelectedFileUri(testUri)
        assertEquals(testUri, viewModel.selectedFileUri.value)
    }

    @Test
    fun `test send uri when making calling api`() {
        // Arrange
        val url = "http://example.com"
        val requestType = RequestType.GET
        val headers = listOf<Header>()
        val body = ""
        val networkResponse =
            NetworkResponse(
                url,
                requestType,
                requestHeaders = headers.associate { it.title!! to it.value!! },
                body = body
            )

        viewModel.setBody(body)
        viewModel.response.observeForever(observer)

        every {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body, uri, contentResolver
            )
        } returns networkResponse
        every { mockHomeGateway.addToDB(networkResponse) } just Runs

        // Set ViewModel state
        viewModel.setUrl(url)
        viewModel.setRequestType(requestType)
        viewModel.setSelectedFileUri(uri)

        // Act
        viewModel.sendRequest(contentResolver)

        val latch = CountDownLatch(1) // To wait for the background thread to finish

        every { observer.onChanged(any()) } answers {
            if (firstArg<Resource<NetworkResponse>>().status == ResourceStatus.Success) {
                latch.countDown() // Release the latch when success is posted
            }
        }

        // Assert
        verify {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body, uri, contentResolver
            )
        }
        verify { observer.onChanged(Resource.loading()) }
        latch.await(2, TimeUnit.SECONDS) // Wait for the background thread to complete
        verify { observer.onChanged(Resource.success(networkResponse)) }
        verify { mockHomeGateway.addToDB(networkResponse) }
    }

    @Test
    fun `test send body when making calling api`() {
        // Arrange
        val url = "http://example.com"
        val requestType = RequestType.POST
        val headers = listOf<Header>()
        val body = "test body"
        val networkResponse =
            NetworkResponse(
                url,
                requestType,
                requestHeaders = headers.associate { it.title!! to it.value!! },
                body = body
            )

        viewModel.setBody(body)
        viewModel.response.observeForever(observer)

        every {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body, null, contentResolver
            )
        } returns networkResponse
        every { mockHomeGateway.addToDB(networkResponse) } just Runs

        // Set ViewModel state
        viewModel.setUrl(url)
        viewModel.setRequestType(requestType)

        // Act
        viewModel.sendRequest(contentResolver)

        val latch = CountDownLatch(1) // To wait for the background thread to finish

        every { observer.onChanged(any()) } answers {
            if (firstArg<Resource<NetworkResponse>>().status == ResourceStatus.Success) {
                latch.countDown() // Release the latch when success is posted
            }
        }

        // Assert
        verify {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body, null, contentResolver
            )
        }
        verify { observer.onChanged(Resource.loading()) }
        latch.await(2, TimeUnit.SECONDS) // Wait for the background thread to complete
        verify { observer.onChanged(Resource.success(networkResponse)) }
        verify { mockHomeGateway.addToDB(networkResponse) }
    }

    @Test
    fun `test set headers when calling api`() {
        // Arrange
        val url = "http://example.com"
        val requestType = RequestType.POST
        val headers = listOf(Header("key1", "value1"), Header("key2", "value2"))
        val body = "test body"
        val networkResponse =
            NetworkResponse(
                url,
                requestType,
                requestHeaders = headers.associate { it.title!! to it.value!! },
                body = body
            )

        viewModel.setBody(body)
        viewModel.response.observeForever(observer)

        every {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body, null, contentResolver
            )
        } returns networkResponse
        every { mockHomeGateway.addToDB(networkResponse) } just Runs

        // Set ViewModel state
        viewModel.setUrl(url)
        viewModel.setRequestType(requestType)
        viewModel.setHeaders(headers)

        // Act
        viewModel.sendRequest(contentResolver)

        val latch = CountDownLatch(1) // To wait for the background thread to finish

        every { observer.onChanged(any()) } answers {
            if (firstArg<Resource<NetworkResponse>>().status == ResourceStatus.Success) {
                latch.countDown() // Release the latch when success is posted
            }
        }

        // Assert
        verify {
            mockHomeGateway.makeRequest(
                url,
                requestType,
                headers.associate { it.title!! to it.value!! },
                body, null, contentResolver
            )
        }
        verify { observer.onChanged(Resource.loading()) }
        latch.await(2, TimeUnit.SECONDS) // Wait for the background thread to complete
        verify { observer.onChanged(Resource.success(networkResponse)) }
        verify { mockHomeGateway.addToDB(networkResponse) }
    }

}