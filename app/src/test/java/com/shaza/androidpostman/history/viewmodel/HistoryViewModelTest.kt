package com.shaza.androidpostman.history.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shaza.androidpostman.history.model.HistoryGateway
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.WhereClauses
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.Resource
import com.shaza.androidpostman.shared.model.ResourceStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
    private val observer = mockk<Observer<Resource<List<NetworkResponse>>>>(relaxed = true)

    @Before
    fun setUp() {
        historyGateway = mockk()
        historyViewModel = HistoryViewModel(historyGateway)
        historyViewModel.requests.observeForever(observer)
    }

    @Test
    fun `test getAllRequests updates LiveData with success`() {
        // Arrange
        val expectedResponses = listOf(
            NetworkResponse(url = "http://example.com", requestType = RequestType.GET, requestHeaders = emptyMap(), body = "{}")
        )
        every { historyGateway.getHistory(any(), eq(OrderClauses.OrderById)) } returns expectedResponses
        historyViewModel.getAllRequests()

        val latch = CountDownLatch(1) // Waiting for success callback

        every { observer.onChanged(any()) } answers {
            if (firstArg<Resource<List<NetworkResponse>>>().status == ResourceStatus.Success) {
                latch.countDown() // Release the latch when success is posted
            }
        }

        // Act
        historyViewModel.getAllRequests()

        // Assert
        verify { historyGateway.getHistory(any(), eq(OrderClauses.OrderById)) }
        verify { observer.onChanged(Resource.loading()) }
        latch.await(2, TimeUnit.SECONDS) // Wait for up to 2 seconds
        verify { observer.onChanged(Resource.success(expectedResponses)) }

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
        val orderClauses = OrderClauses.OrderByTime
        val expectedOrderClauses = OrderClauses.OrderByTime
        // Act
        historyViewModel.setOrderClauses(orderClauses)

        // Assert
        assertEquals(historyViewModel.getOrderClauses(),expectedOrderClauses)
    }
}