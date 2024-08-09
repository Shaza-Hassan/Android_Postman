package com.shaza.androidpostman.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.NetworkRequestDBHelper
import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema
import com.shaza.androidpostman.shared.database.RequestMapper
import com.shaza.androidpostman.shared.database.SelectFromDB
import com.shaza.androidpostman.shared.model.NetworkResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

@RunWith(AndroidJUnit4::class)
class SelectFromDBTest {

    private lateinit var dbHelper: NetworkRequestDBHelper
    private lateinit var selectFromDB: SelectFromDB
    private lateinit var db: SQLiteDatabase
    private lateinit var cursor: Cursor

    @Before
    fun setUp() {
        dbHelper = mockk()
        db = mockk()
        cursor = mockk()

        // Mock the dbHelper to return the mock db
        every { dbHelper.writableDatabase } returns db

        // Mock the db to return the mock cursor
        every { db.rawQuery(any(), any()) } returns cursor

        // Mock the cursor

        every { cursor.moveToNext() } returns true andThen false // Only one row in the cursor
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


        selectFromDB = SelectFromDB(dbHelper)
    }

    @Test
    fun getAllRequestsShouldReturnAllRequestsWithNoWhereClause() {
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

        // Insert data into the database
        val query = "SELECT * FROM ${RequestInfoTableSchema.TABLE_NAME} ORDER BY ${OrderClauses.OrderById}"
        every { db.rawQuery(query, null) } returns cursor

        // Act
        val results = selectFromDB.getAllRequests(emptyList(), OrderClauses.OrderById)

        // Assert
        assertEquals(1, results.size)
        assertEquals("http://example.com", results[0].url)
    }
}

