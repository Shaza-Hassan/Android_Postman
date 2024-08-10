package com.shaza.androidpostman.history.model

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.SelectFromDB
import com.shaza.androidpostman.shared.database.WhereClauses
import com.shaza.androidpostman.shared.model.NetworkResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

class HistoryRepositoryTest {


    lateinit var mockSelectFromDB: SelectFromDB

    lateinit var historyRepository: HistoryRepository

    @Before
    fun setUp() {
        mockSelectFromDB = mockk()
        historyRepository = HistoryRepository(mockSelectFromDB)
    }

    @Test
    fun `test list all request without any filters or order id`() {
        val whereClauses =
            listOf<WhereClauses>()
        val orderClauses = OrderClauses.OrderById

        val expectedResponse = listOf(
            NetworkResponse(
                url = "http://example.com",
                requestType = RequestType.GET,
                requestHeaders = emptyMap(),
                body = "{}"
            ),
            NetworkResponse(
                url = "http://example.com",
                requestType = RequestType.GET,
                requestHeaders = emptyMap(),
                body = "{}"
            ),
        )

        every { mockSelectFromDB.getAllRequests(listOf(), orderClauses) } returns expectedResponse


        val result = historyRepository.getHistory(listOf(), orderClauses)

        assertEquals(expectedResponse, result)

        verify { mockSelectFromDB.getAllRequests(whereClauses, orderClauses) }
    }
}