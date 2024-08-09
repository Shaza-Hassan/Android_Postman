package com.shaza.androidpostman.shared.database

import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
class SelectFromDB(private val dbHelper: NetworkRequestDBHelper) {

    fun getAllRequests(
        whereClause: List<WhereClauses>,
        sortedBy: OrderClauses?
    ): List<NetworkResponse> {
        val query = DBQueryBuilder.buildQuery(whereClause, sortedBy)
        val cursor = dbHelper.writableDatabase.rawQuery(query, null)
        val requests = cursor?.let { RequestMapper.fromCursor(it) }
        return requests ?: mutableListOf()
    }
}