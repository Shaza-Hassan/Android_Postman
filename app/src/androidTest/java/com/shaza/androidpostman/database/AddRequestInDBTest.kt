package com.shaza.androidpostman.database

import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.AddRequestInDB
import com.shaza.androidpostman.shared.database.NetworkRequestDBHelper
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema
import com.shaza.androidpostman.shared.database.RequestMapper
import com.shaza.androidpostman.shared.model.NetworkResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

@RunWith(AndroidJUnit4::class)
class AddRequestInDBTest {

    @Test
    fun addRequestToDataBaseShouldCallInsertWithOnConflictWithCorrectParameters() {
        // Arrange
        val dbHelper = mockk<NetworkRequestDBHelper>()
        val db = mockk<SQLiteDatabase>()
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
        val values = RequestMapper.toContentValues(networkResponse)

        every { dbHelper.writableDatabase } returns db
        every { db.insertWithOnConflict(any(), any(), any(), any()) } returns 1L

        val addRequestInDB = AddRequestInDB(dbHelper)

        // Act
        addRequestInDB.addRequestToDataBase(networkResponse)

        // Assert
        verify {
            db.insertWithOnConflict(
                RequestInfoTableSchema.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
    }
}
