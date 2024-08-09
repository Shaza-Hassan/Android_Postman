package com.shaza.androidpostman.shared.database

import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_ID
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_TIME

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

enum class OrderClauses(val entity: String, val sortingCriteria: String) {
    OrderById(COLUMN_ID, "ASC"),
    OrderByTime(COLUMN_TIME, "ASC")
}