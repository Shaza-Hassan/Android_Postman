package com.shaza.androidpostman.database

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.DBQueryBuilder
import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_ERROR
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_ID
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_REQUEST_TYPE
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_RESPONSE
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_TIME
import com.shaza.androidpostman.shared.database.WhereClauses
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
class DBQueryBuilderTest {

    @Test
    fun `buildQuery should return base query when no where clauses and no order clauses`() {
        // Arrange
        val whereClauses = emptyList<WhereClauses>()
        val orderClauses: OrderClauses? = null

        // Act
        val query = DBQueryBuilder.buildQuery(whereClauses, orderClauses)

        // Assert
        val expectedQuery = "SELECT * FROM ${RequestInfoTableSchema.TABLE_NAME}"
        assertEquals(expectedQuery, query)
    }

    @Test
    fun `buildQuery should include where clauses when provided`() {
        // Arrange
        val whereClauses = listOf(
            WhereClauses.GetAllSuccessRequest, WhereClauses.GetAllGETRequest
        )
        val orderClauses: OrderClauses? = null

        // Act
        val query = DBQueryBuilder.buildQuery(whereClauses, orderClauses)

        // Assert
        val expectedQuery =
            "SELECT * FROM ${RequestInfoTableSchema.TABLE_NAME}" +
                    " WHERE $COLUMN_RESPONSE IS NOT NULL AND $COLUMN_ERROR IS NULL " +
                    "AND $COLUMN_REQUEST_TYPE = '${RequestType.GET.name}'"
        assertEquals(expectedQuery, query)
    }

    @Test
    fun `buildQuery should include order clauses when provided`() {
        // Arrange
        val whereClauses = listOf(WhereClauses.GetAllRequest)
        val orderClauses = OrderClauses.OrderByTime

        // Act
        val query = DBQueryBuilder.buildQuery(whereClauses, orderClauses)

        // Assert
        val expectedQuery =
            "SELECT * FROM ${RequestInfoTableSchema.TABLE_NAME} ORDER BY ${COLUMN_TIME} ASC"
        assertEquals(expectedQuery, query)
    }

    @Test
    fun `buildQuery should include both where and order clauses when provided`() {
        // Arrange
        val whereClauses = listOf(
            WhereClauses.GetAllSuccessRequest, WhereClauses.GetAllPOSTRequest
        )
        val orderClauses = OrderClauses.OrderById

        // Act
        val query = DBQueryBuilder.buildQuery(whereClauses, orderClauses)

        // Assert
        val expectedQuery =
            "SELECT * FROM ${RequestInfoTableSchema.TABLE_NAME} " +
                    "WHERE $COLUMN_RESPONSE IS NOT NULL AND $COLUMN_ERROR IS NULL " +
                    "AND $COLUMN_REQUEST_TYPE = '${RequestType.POST.name}' ORDER BY $COLUMN_ID ASC"
        assertEquals(expectedQuery, query)
    }

    @Test
    fun `buildQuery should handle empty where clauses and null order clauses`() {
        // Arrange
        val whereClauses = emptyList<WhereClauses>()
        val orderClauses: OrderClauses? = null

        // Act
        val query = DBQueryBuilder.buildQuery(whereClauses, orderClauses)

        // Assert
        val expectedQuery = "SELECT * FROM ${RequestInfoTableSchema.TABLE_NAME}"
        assertEquals(expectedQuery, query)
    }

}