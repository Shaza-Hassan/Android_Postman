package com.shaza.androidpostman.shared.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.extensions.toMap
import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
object RequestMapper {

    fun toContentValues(httpResponse: NetworkResponse): ContentValues {
        return ContentValues().apply {
            put(RequestInfoTableSchema.COLUMN_URL, httpResponse.url)
            put(RequestInfoTableSchema.COLUMN_REQUEST_TYPE, httpResponse.requestType?.name)
            put(RequestInfoTableSchema.COLUMN_STATUS_CODE, httpResponse.responseCode)
            put(RequestInfoTableSchema.COLUMN_TIME, httpResponse.elapsedTime)
            put(RequestInfoTableSchema.COLUMN_RESPONSE, httpResponse.response)
            put(RequestInfoTableSchema.COLUMN_ERROR, httpResponse.error)
            put(RequestInfoTableSchema.COLUMN_QUERY_PARAMS, httpResponse.queryParameters.toString())
            put(RequestInfoTableSchema.COLUMN_BODY_REQUEST, httpResponse.body)
            put(RequestInfoTableSchema.COLUMN_REQUEST_HEADERS, httpResponse.requestHeaders.toString())
            put(RequestInfoTableSchema.COLUMN_RESPONSE_HEADERS, httpResponse.responseHeaders.toString())
        }
    }

    @SuppressLint("Range")
    fun fromCursor(cursor: Cursor): List<NetworkResponse> {
        val requests = mutableListOf<NetworkResponse>()
        while (cursor.moveToNext()) {
            val httpResponse = NetworkResponse(
                url = cursor.getString(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_URL)),
                requestType = RequestType.valueOf(cursor.getString(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_REQUEST_TYPE))),
                responseCode = cursor.getInt(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_STATUS_CODE)),
                elapsedTime = cursor.getLong(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_TIME)),
                response = cursor.getString(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_RESPONSE)),
                error = cursor.getString(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_ERROR)),
                queryParameters = cursor.getString(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_QUERY_PARAMS)).toMap(),
                body = cursor.getString(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_BODY_REQUEST)),
                requestHeaders = cursor.getString(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_REQUEST_HEADERS)).toMap(),
                responseHeaders = cursor.getString(cursor.getColumnIndex(RequestInfoTableSchema.COLUMN_RESPONSE_HEADERS)).toMap()
            )
            requests.add(httpResponse)
        }
        return requests
    }
}
