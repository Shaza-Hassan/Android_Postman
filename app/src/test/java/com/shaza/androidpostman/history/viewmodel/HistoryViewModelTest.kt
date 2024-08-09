package com.shaza.androidpostman.history.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shaza.androidpostman.history.model.HistoryGateway
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.WhereClauses
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.Resource
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
class HistoryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var historyGateway: HistoryGateway
    private lateinit var historyViewModel: HistoryViewModel
    private val observer: Observer<Resource<List<NetworkResponse>>> = mock()

    @Before
    fun setUp() {
        historyGateway = mock(HistoryGateway::class.java)
        historyViewModel = HistoryViewModel(historyGateway)
        historyViewModel.requests.observeForever(observer)
    }

    @Test
    fun `test getAllRequests updates LiveData with success`() {
        // Arrange
        val whereClauses = listOf(WhereClauses.GetAllRequest)
        val orderClauses = OrderClauses.OrderById
        val expectedResponses = listOf(
            NetworkResponse(url = "http://example.com", requestType = RequestType.GET, requestHeaders = emptyMap(), body = "{}")
        )
        `when`(historyGateway.getHistory(whereClauses, orderClauses))
            .thenReturn(expectedResponses)
        val latch = CountDownLatch(2) // One for loading and one for success

        // Act
        historyViewModel.getAllRequests()

        // Assert
        verify(observer).onChanged(Resource.loading())
        latch.await(2, TimeUnit.SECONDS) // Wait for up to 2 seconds
        verify(observer).onChanged(Resource.success(expectedResponses))
    }

    @Test
    fun `update where clause list with return only all requests type`() {
        // Arrange
        val whereClauses = WhereClauses.GetAllRequest
        val expectedWhereClauses = listOf(WhereClauses.GetAllRequest)
        // Act
        historyViewModel.updateRequestType(whereClauses)

        // Assert
        assertTrue(historyViewModel.whereClause.containsAll(expectedWhereClauses))
    }

    @Test
    fun `update where clause list with return only get requests`(){
        // Arrange
        val whereClauses = WhereClauses.GetAllGETRequest
        val expectedWhereClauses = listOf(WhereClauses.GetAllGETRequest)
        // Act
        historyViewModel.updateRequestType(whereClauses)

        // Assert
        assertTrue(historyViewModel.whereClause.containsAll(expectedWhereClauses))
    }

    @Test
    fun `update where clause list with return only post requests`(){
        // Arrange
        val whereClauses = WhereClauses.GetAllPOSTRequest
        val expectedWhereClauses = listOf(WhereClauses.GetAllPOSTRequest)
        // Act
        historyViewModel.updateRequestType(whereClauses)

        // Assert
        assertTrue(historyViewModel.whereClause.containsAll(expectedWhereClauses))
    }

    @Test
    fun `update where clause list with return only success requests`(){
        // Arrange
        val whereClauses = WhereClauses.GetAllSuccessRequest
        val expectedWhereClauses = listOf(WhereClauses.GetAllSuccessRequest)
        // Act
        historyViewModel.updateRequestStatus(whereClauses)

        // Assert
        assertTrue(historyViewModel.whereClause.containsAll(expectedWhereClauses))
    }

    @Test
    fun `update where clause list with return only failed requests`(){
        // Arrange
        val whereClauses = WhereClauses.GetAllFailedRequest
        val expectedWhereClauses = listOf(WhereClauses.GetAllFailedRequest)
        // Act
        historyViewModel.updateRequestStatus(whereClauses)

        // Assert
        assertTrue(historyViewModel.whereClause.containsAll(expectedWhereClauses))
    }

    @Test
    fun `where for all GET success requests`(){
        // Arrange
        val expectedWhereClauses = listOf(WhereClauses.GetAllSuccessRequest,WhereClauses.GetAllSuccessRequest)
        // Act
        historyViewModel.updateRequestType(WhereClauses.GetAllGETRequest)
        historyViewModel.updateRequestStatus(WhereClauses.GetAllSuccessRequest)

        // Assert
        assertTrue(historyViewModel.whereClause.containsAll(expectedWhereClauses))
    }

    @Test
    fun `where for all POST failed requests`(){
        // Arrange
        val expectedWhereClauses = listOf(WhereClauses.GetAllFailedRequest,WhereClauses.GetAllFailedRequest)

        // Act
        historyViewModel.updateRequestType(WhereClauses.GetAllPOSTRequest)
        historyViewModel.updateRequestStatus(WhereClauses.GetAllFailedRequest)

        // Assert
        assertTrue(historyViewModel.whereClause.containsAll(expectedWhereClauses))
    }

    @Test
    fun `where for all requests`(){
        // Arrange
        val expectedWhereClauses = listOf(WhereClauses.GetAllRequest)

        // Act
        historyViewModel.updateRequestType(WhereClauses.GetAllRequest)
        historyViewModel.updateRequestStatus(WhereClauses.GetAllRequest)

        // Assert
        assertTrue(historyViewModel.whereClause.containsAll(expectedWhereClauses))
        assertTrue(historyViewModel.whereClause.size == 1)
    }

    @Test
    fun `set order clause to order by id`(){
        // Arrange
        val orderClauses = OrderClauses.OrderById
        val expectedOrderClauses = OrderClauses.OrderById
        // Act
        historyViewModel.setOrderClauses(orderClauses)

        // Assert
        assertTrue(historyViewModel.orderClauses == expectedOrderClauses)
    }
}