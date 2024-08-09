package com.shaza.androidpostman.database

import android.database.Cursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema
import com.shaza.androidpostman.shared.database.RequestMapper
import com.shaza.androidpostman.shared.model.NetworkResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */


@RunWith(AndroidJUnit4::class)
class RequestMapperTest {

    @Test
    fun testToContentValuesWithRealContentValues() {
        // Arrange
        val networkResponse = NetworkResponse(
            url = "http://example.com",
            requestType = RequestType.GET,
            responseCode = 200,
            elapsedTime = 123L,
            response = "response body",
            error = "error message",
            queryParameters = mapOf("param1" to "value1"),
            body = "request body",
            requestHeaders = mapOf("header1" to "value1"),
            responseHeaders = mapOf("responseHeader1" to "value1")
        )

        // Act
        val contentValues = RequestMapper.toContentValues(networkResponse)

        // Assert
        assertEquals(
            "http://example.com",
            contentValues.getAsString(RequestInfoTableSchema.COLUMN_URL)
        )
        assertEquals("GET", contentValues.getAsString(RequestInfoTableSchema.COLUMN_REQUEST_TYPE))
        assertEquals(200, contentValues.getAsInteger(RequestInfoTableSchema.COLUMN_STATUS_CODE))
        assertEquals(123L, contentValues.getAsLong(RequestInfoTableSchema.COLUMN_TIME))
        assertEquals(
            "response body",
            contentValues.getAsString(RequestInfoTableSchema.COLUMN_RESPONSE)
        )
        assertEquals(
            "error message",
            contentValues.getAsString(RequestInfoTableSchema.COLUMN_ERROR)
        )
        assertEquals(
            "{param1=value1}",
            contentValues.getAsString(RequestInfoTableSchema.COLUMN_QUERY_PARAMS)
        )
        assertEquals(
            "request body",
            contentValues.getAsString(RequestInfoTableSchema.COLUMN_BODY_REQUEST)
        )
        assertEquals(
            "{header1=value1}",
            contentValues.getAsString(RequestInfoTableSchema.COLUMN_REQUEST_HEADERS)
        )
        assertEquals(
            "{responseHeader1=value1}",
            contentValues.getAsString(RequestInfoTableSchema.COLUMN_RESPONSE_HEADERS)
        )
    }

    @Test
    fun fromCursorShouldMapCursorToNetworkResponseListCorrectly() {
        // Arrange
        val cursor = mockk<Cursor>(relaxed = true)

        // Define column indices
        val urlIndex = 0
        val requestTypeIndex = 1
        val statusCodeIndex = 2
        val timeIndex = 3
        val responseIndex = 4
        val errorIndex = 5
        val queryParamsIndex = 6
        val bodyIndex = 7
        val requestHeadersIndex = 8
        val responseHeadersIndex = 9

        // Mock getColumnIndex to return indices
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_URL) } returns urlIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_REQUEST_TYPE) } returns requestTypeIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_STATUS_CODE) } returns statusCodeIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_TIME) } returns timeIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_RESPONSE) } returns responseIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_ERROR) } returns errorIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_QUERY_PARAMS) } returns queryParamsIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_BODY_REQUEST) } returns bodyIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_REQUEST_HEADERS) } returns requestHeadersIndex
        every { cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_RESPONSE_HEADERS) } returns responseHeadersIndex

        // Mock get methods for each column
        every { cursor.getString(urlIndex) } returns "http://example.com"
        every { cursor.getString(requestTypeIndex) } returns "GET"
        every { cursor.getInt(statusCodeIndex) } returns 200
        every { cursor.getLong(timeIndex) } returns 123L
        every { cursor.getString(responseIndex) } returns "response body"
        every { cursor.getString(errorIndex) } returns "error message"
        every { cursor.getString(queryParamsIndex) } returns "{param1=value1}"
        every { cursor.getString(bodyIndex) } returns "request body"
        every { cursor.getString(requestHeadersIndex) } returns "{header1=value1}"
        every { cursor.getString(responseHeadersIndex) } returns "{responseHeader1=value1}"

        // Mock moveToNext to return true once for the test
        every { cursor.moveToNext() } returns true andThen false

        // Act
        val responses = RequestMapper.fromCursor(cursor)

        // Assert
        assertEquals(1, responses.size)
        val response = responses[0]
        assertEquals("http://example.com", response.url)
        assertEquals(RequestType.GET, response.requestType)
        assertEquals(200, response.responseCode)
        assertEquals(123L, response.elapsedTime)
        assertEquals("response body", response.response)
        assertEquals("error message", response.error)
        assertEquals(mapOf("param1" to "value1"), response.queryParameters)
        assertEquals("request body", response.body)
        assertEquals(mapOf("header1" to "value1"), response.requestHeaders)
        assertEquals(mapOf("responseHeader1" to "value1"), response.responseHeaders)

    }
}
