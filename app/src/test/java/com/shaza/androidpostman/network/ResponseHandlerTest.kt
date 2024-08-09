package com.shaza.androidpostman.network

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.netowrk.ResponseHandler
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL

class ResponseHandlerTest {

    @Test
    fun `test handleResponse with successful response`() {
        // Arrange
        val headers = mapOf("Content-Type" to "application/json")
        val requestBody = "{\"key\":\"value\"}"
        val mockConnection = mockk<HttpURLConnection>()
        val inputStream = ByteArrayInputStream("response body".toByteArray())

        every { mockConnection.url } returns URL("http://example.com")
        every { mockConnection.requestMethod } returns "GET"
        every { mockConnection.responseCode } returns 200
        every { mockConnection.headerFields } returns mapOf("Content-Type" to listOf("application/json"))
        every { mockConnection.inputStream } returns inputStream
        every { mockConnection.disconnect() } just Runs // Add this line to handle disconnect()

        // Act
        val result = ResponseHandler.handleResponse(headers, requestBody, mockConnection)

        // Assert
        assertEquals("http://example.com", result.url)
        assertEquals(RequestType.GET, result.requestType)
        assertEquals(headers, result.requestHeaders)
        assertEquals(requestBody, result.body)
        assertEquals(200, result.responseCode)
        assertEquals(mapOf("Content-Type" to "[application/json]"), result.responseHeaders)
        assertEquals("response body", result.response)
        assertNull(result.error)
    }

    @Test
    fun `test handleResponse with exception`() {
        // Arrange
        val headers = mapOf("Content-Type" to "application/json")
        val requestBody = "{\"key\":\"value\"}"
        val mockConnection = mockk<HttpURLConnection>()

        every { mockConnection.url } returns URL("http://example.com")
        every { mockConnection.requestMethod } returns "POST"
        every { mockConnection.responseCode } throws RuntimeException("Connection failed")
        every { mockConnection.disconnect() } just Runs // Add this line to handle disconnect()

        // Act
        val result = ResponseHandler.handleResponse(headers, requestBody, mockConnection)

        // Assert
        assertEquals("http://example.com", result.url)
        assertEquals(RequestType.POST, result.requestType)
        assertEquals(headers, result.requestHeaders)
        assertEquals(requestBody, result.body)
        assertNull(result.response)
        assertEquals("Connection failed", result.error)
    }

    @Test
    fun `test handleResponse with error 500 response`() {
        // Arrange
        val headers = mapOf("Content-Type" to "application/json")
        val requestBody = "{\"key\":\"value\"}"
        val mockConnection = mockk<HttpURLConnection>()
        val errorStream = ByteArrayInputStream("error message".toByteArray())

        every { mockConnection.url } returns URL("http://example.com")
        every { mockConnection.requestMethod } returns "POST"
        every { mockConnection.responseCode } returns 500
        every { mockConnection.headerFields } returns mapOf("Content-Type" to listOf("application/json"))
        every { mockConnection.errorStream } returns errorStream
        every { mockConnection.disconnect() } just Runs // Add this line to handle disconnect()

        // Act
        val result = ResponseHandler.handleResponse(headers, requestBody, mockConnection)

        // Assert
        assertEquals("http://example.com", result.url)
        assertEquals(RequestType.POST, result.requestType)
        assertEquals(headers, result.requestHeaders)
        assertEquals(requestBody, result.body)
        assertEquals(500, result.responseCode)
        assertEquals(mapOf("Content-Type" to "[application/json]"), result.responseHeaders)
        assertEquals("error message", result.error)
        assertNull(result.response)
    }
}
