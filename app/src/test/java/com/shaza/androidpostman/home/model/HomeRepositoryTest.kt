package com.shaza.androidpostman.home.model

import com.shaza.androidpostman.shared.database.AddRequestInDB
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.netowrk.HTTPClient
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */
class HomeRepositoryTest {

    private lateinit var homeRepository: HomeRepository
    private lateinit var mockHttpClient: HTTPClient
    private lateinit var addRequestInDB: AddRequestInDB

    @Before
    fun setUp() {
        mockHttpClient = mockk()
        addRequestInDB = mockk()
        homeRepository = HomeRepository(mockHttpClient, addRequestInDB)
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

        every { mockHttpClient.makeRequest(url,requestType,headers,body) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body)

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
//            NetworkResponse.Success("POST response body")
        every { mockHttpClient.makeRequest(url,requestType,headers,body) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body)

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
        every { mockHttpClient.makeRequest(url,requestType,headers,body) } throws Exception("Network error")

        // Act
        homeRepository.makeRequest(url, requestType, headers, body)
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
        every { mockHttpClient.makeRequest(url,requestType,headers,body) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body)

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
        every { mockHttpClient.makeRequest(url,requestType,headers,body) } returns expectedResponse

        // Act
        val actualResponse = homeRepository.makeRequest(url, requestType, headers, body)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }


}