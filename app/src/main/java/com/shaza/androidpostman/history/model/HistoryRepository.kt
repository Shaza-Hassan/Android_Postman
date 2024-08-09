package com.shaza.androidpostman.history.model

import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.SelectFromDB
import com.shaza.androidpostman.shared.database.WhereClauses
import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
class HistoryRepository(private val selectFromDB: SelectFromDB): HistoryGateway {
    override fun getHistory(whereClause: List<WhereClauses>, sortedBy: OrderClauses): List<NetworkResponse> {
        return selectFromDB.getAllRequests(whereClause, sortedBy)
    }
}