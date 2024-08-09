package com.shaza.androidpostman.shared.database

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

object DBQueryBuilder {
    private const val BASE_QUERY = "SELECT * FROM ${RequestInfoTableSchema.TABLE_NAME}"

    fun buildQuery(whereClauses: List<WhereClauses>, orderClauses: OrderClauses?): String {
        val whereClauses = whereClauses.toMutableList()
        whereClauses.removeAll(listOf(WhereClauses.GetAllRequest))
        val whereString = if (whereClauses.isNotEmpty()) {
            " WHERE ${whereClauses.joinToString(" AND ") { it.clauses }}"
        } else {
            ""
        }

        val orderString = orderClauses?.let { " ORDER BY ${it.entity} ${it.sortingCriteria}" } ?: ""

        return "$BASE_QUERY$whereString$orderString"
    }
}



