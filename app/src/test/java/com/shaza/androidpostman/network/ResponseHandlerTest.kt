package com.shaza.androidpostman.network

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.netowrk.ResponseHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.*
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL

class ResponseHandlerTest {

    @Test
    fun `test handleResponse with successful response`() {
        // Arrange
        val headers = mapOf("Content-Type" to "application/json")
        val requestBody = "{\"key\":\"value\"}"
        val mockConnection = mock(HttpURLConnection::class.java)
        val inputStream = ByteArrayInputStream("response body".toByteArray())


        `when`(mockConnection.url).thenReturn(java.net.URL("http://example.com"))
        `when`(mockConnection.requestMethod).thenReturn("GET")
        `when`(mockConnection.responseCode).thenReturn(200)
        `when`(mockConnection.headerFields).thenReturn(mapOf("Content-Type" to listOf("application/json")))
        `when`(mockConnection.inputStream).thenReturn(inputStream)

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
        val mockConnection = mock(HttpURLConnection::class.java)

        `when`(mockConnection.url).thenReturn(java.net.URL("http://example.com"))
        `when`(mockConnection.requestMethod).thenReturn("POST")
        `when`(mockConnection.responseCode).thenThrow(RuntimeException("Connection failed"))

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
        val mockConnection = mock(HttpURLConnection::class.java)
        val errorStream = ByteArrayInputStream("error message".toByteArray())

        `when`(mockConnection.url).thenReturn(URL("http://example.com"))
        `when`(mockConnection.requestMethod).thenReturn("POST")
        `when`(mockConnection.responseCode).thenReturn(500)
        `when`(mockConnection.headerFields).thenReturn(mapOf("Content-Type" to listOf("application/json")))
        `when`(mockConnection.errorStream).thenReturn(errorStream)

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
