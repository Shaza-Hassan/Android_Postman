package com.shaza.androidpostman.home.model

import android.content.ContentResolver
import android.net.Uri
import com.shaza.androidpostman.shared.database.AddRequestInDB
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.netowrk.HTTPClient
import com.shaza.androidpostman.shared.utils.ConnectivityChecker
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */
class HomeRepositoryTest {

    private lateinit var homeRepository: HomeRepository
    private lateinit var mockHttpClient: HTTPClient
    private lateinit var addRequestInDB: AddRequestInDB
    private lateinit var connectivityChecker: ConnectivityChecker
    private lateinit var contentResolver: ContentResolver
    private lateinit var uri: Uri
    @Before
    fun setUp() {
        mockHttpClient = mockk()
        addRequestInDB = mockk()
        connectivityChecker = mockk()
        homeRepository = HomeRepository(mockHttpClient, addRequestInDB, connectivityChecker)
        contentResolver = mockk()
        uri = mockk()
    }

    @Test
    fun `makeRequest should handle GET request type`() {
        // Arrange
        val url = "https://example.com"
        val requestType = RequestType.GET
        val headers = mapOf("Authorization" to "Bearer token")
        val body = null
        val expectedResponse  = NetworkResponse(
            url = url,
            requestType = requestType,
            requestHeaders = headers,
            body = body,
            responseCode = 200,
            response = "response body"
        )

        every { mockHttpClient.makeRequest(url,requestType,headers,body,uri, contentResolver) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body,uri, contentResolver)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `makeRequest should handle POST request type`() {
        // Arrange
        val url = "https://example.com"
        val requestType = RequestType.POST
        val headers = mapOf("Content-Type" to "application/json")
        val body = "{\"key\":\"value\"}"
        val expectedResponse  = NetworkResponse(
            url = url,
            requestType = requestType,
            requestHeaders = headers,
            body = body,
            responseCode = 200,
            response = "POST response body"
        )
        every { mockHttpClient.makeRequest(url,requestType,headers,body,uri, contentResolver) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body,uri, contentResolver)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    @Test(expected = Exception::class)
    fun `makeRequest should throw NetworkException on HTTPClient exception`() {
        // Arrange
        val url = "https://example.com"
        val requestType = RequestType.GET
        val headers = mapOf("Authorization" to "Bearer token")
        val body = null

        // Mock the behavior of the HTTPClient to throw an exception
        every { mockHttpClient.makeRequest(url,requestType,headers,body,uri, contentResolver) } throws Exception("Network error")

        // Act
        homeRepository.makeRequest(url, requestType, headers, body,uri, contentResolver)
    }

    @Test
    fun `makeRequest should handle empty URL`() {
        // Arrange
        val url = ""
        val requestType = RequestType.GET
        val headers = mapOf("Authorization" to "Bearer token")
        val body = null
        val expectedResponse = NetworkResponse(
            url = url,
            requestType = requestType,
            requestHeaders = headers,
            body = body,
            responseCode = 400,
            error = "URL is empty"
        )

        // Mock the behavior of the HTTPClient to return an error response
        every { mockHttpClient.makeRequest(url,requestType,headers,body,uri, contentResolver) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body,uri, contentResolver)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `makeRequest should handle invalid headers`() {
        // Arrange
        val url = "https://example.com"
        val requestType = RequestType.GET
        val headers = mapOf("InvalidHeader" to "InvalidValue")
        val body = null
        val expectedResponse = NetworkResponse(
            url = url,
            requestType = requestType,
            requestHeaders = headers,
            body = body,
            responseCode = 400,
            response = "Invalid headers",
            error = "Invalid headers"
        )

        // Mock the behavior of the HTTPClient to return an error response
        every { mockHttpClient.makeRequest(url,requestType,headers,body,uri, contentResolver) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body,uri, contentResolver)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `isConnected returns true when ConnectivityChecker indicates connected`() {
        every { connectivityChecker.isConnected() } returns true

        val result = connectivityChecker.isConnected()

        assertTrue(result)
        verify { connectivityChecker.isConnected() }
    }

    @Test
    fun `isConnected returns false when ConnectivityChecker indicates not connected`() {
        every { connectivityChecker.isConnected() } returns false

        val result = connectivityChecker.isConnected()

        assertFalse(result)
        verify { connectivityChecker.isConnected() }
    }

    @Test
    fun `addRequest should call AddRequestInDB`() {
        // Arrange
        val request = NetworkResponse(
            url = "https://example.com",
            requestType = RequestType.GET,
            requestHeaders = mapOf("Authorization" to "Bearer token"),
            body = null
        )

        every { addRequestInDB.addRequestToDataBase(request) } returns Unit

        // Act
        homeRepository.addToDB(request)

        // Assert
        verify { addRequestInDB.addRequestToDataBase(request) }
    }

    @Test
    fun `make request should handle uri`() {
        // Arrange
        val url = "https://example.com"
        val requestType = RequestType.GET
        val headers = mapOf("Authorization" to "Bearer token")
        val body = null
        val expectedResponse = NetworkResponse(
            url = url,
            requestType = requestType,
            requestHeaders = headers,
            body = body,
            responseCode = 200,
            response = "response body"
        )

        every { mockHttpClient.makeRequest(url,requestType,headers,body,uri, contentResolver) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body,uri, contentResolver)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

}