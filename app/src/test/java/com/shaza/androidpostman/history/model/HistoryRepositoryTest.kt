package com.shaza.androidpostman.history.model

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.SelectFromDB
import com.shaza.androidpostman.shared.database.WhereClauses
import com.shaza.androidpostman.shared.model.NetworkResponse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

class HistoryRepositoryTest {

    @Mock
    lateinit var mockSelectFromDB: SelectFromDB

    @Mock
    lateinit var historyRepository: HistoryRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        historyRepository = HistoryRepository(mockSelectFromDB)
    }

    @Test
    fun `test list all request without any filters or order id`() {
        val whereClauses = listOf<WhereClauses>() // Provide specific WhereClauses instances as needed
        val orderClauses = OrderClauses.OrderById

        val expectedResponse = listOf(
            NetworkResponse(url = "http://example.com", requestType = RequestType.GET, requestHeaders = emptyMap(), body = "{}"),
            NetworkResponse(url = "http://example.com", requestType = RequestType.GET, requestHeaders = emptyMap(), body = "{}"),
        )

        `when`(mockSelectFromDB.getAllRequests(listOf(), orderClauses))
            .thenReturn(expectedResponse)

        val result = historyRepository.getHistory(listOf(), orderClauses)

        assertEquals(expectedResponse, result)
        verify(mockSelectFromDB).getAllRequests(whereClauses, orderClauses)
    }
}