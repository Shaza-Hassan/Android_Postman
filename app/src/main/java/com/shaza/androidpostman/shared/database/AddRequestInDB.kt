package com.shaza.androidpostman.shared.database

import android.database.sqlite.SQLiteDatabase
import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
class AddRequestInDB(private val dbHelper: NetworkRequestDBHelper) {

    fun addRequestToDataBase(networkResponse: NetworkResponse) {
        val values = RequestMapper.toContentValues(networkResponse)
        dbHelper.writableDatabase.insertWithOnConflict(
            RequestInfoTableSchema.TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

}