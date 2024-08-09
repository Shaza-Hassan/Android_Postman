package com.shaza.androidpostman.shared.database

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
class NetworkRequestDBHelper private constructor (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: android.database.sqlite.SQLiteDatabase) {
        db.execSQL(RequestInfoTableSchema.CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: android.database.sqlite.SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(RequestInfoTableSchema.DROP_TABLE_QUERY)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "network_responses.db"
        private const val DATABASE_VERSION = 1
        private var instance: NetworkRequestDBHelper? = null

        fun getInstance(context: Context): NetworkRequestDBHelper {
            instance?.let {
                return it
            } ?: run {
                instance = NetworkRequestDBHelper(context)
                return instance!!
            }
        }
    }
}