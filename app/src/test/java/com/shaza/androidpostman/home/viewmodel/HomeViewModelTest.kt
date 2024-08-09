package com.shaza.androidpostman.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shaza.androidpostman.home.model.Header
import com.shaza.androidpostman.home.model.HomeGateway
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.AddRequestInDB
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.Resource
import com.shaza.androidpostman.shared.model.ResourceStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.eq
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockHomeGateway: HomeGateway
    private lateinit var addRequestInDB: AddRequestInDB

    @Before
    fun setUp() {
        mockHomeGateway = mock(HomeGateway::class.java)
        addRequestInDB = mock()
        viewModel = HomeViewModel(mockHomeGateway, addRequestInDB)
    }

    @Test
    fun `test set url`(){
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
    fun sendRequest_updatesResponseLiveData() {
        val testUrl = "http://example.com"
        val testType = RequestType.GET
        val testHeaders = listOf(Header("key", "value"))
        val headers = testHeaders.map { it.title to it.value }.toMap() as Map<String, String>
        val testBody = "test body"
        val testResponse = NetworkResponse(
            url = testUrl,
            requestType = testType,
            responseHeaders = headers,
            response = testBody,
        )

        `when`(mockHomeGateway.makeRequest(
            eq(testUrl),
            eq(testType),
            anyMap(),
            eq(testBody)
        )).thenReturn(testResponse)


        viewModel.setUrl(testUrl)
        viewModel.setRequestType(testType)
        testHeaders.forEach { viewModel.addHeader() }
        viewModel.setBody(testBody)

        val latch = CountDownLatch(1)
        val observer = Observer<Resource<NetworkResponse>> { resource ->
            if (resource.status == ResourceStatus.Success) {
                latch.countDown()
            }
        }
        viewModel.response.observeForever(observer)

        viewModel.sendRequest()

        latch.await(5, TimeUnit.SECONDS)
        verify(mockHomeGateway).makeRequest(
            eq(testUrl),
            eq(testType),
            anyMap(), // Allows any map for headers
            eq(testBody)
        )
        viewModel.response.removeObserver(observer)
    }
}