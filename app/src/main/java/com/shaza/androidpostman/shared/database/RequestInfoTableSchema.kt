package com.shaza.androidpostman.shared.database

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

object RequestInfoTableSchema {
    const val TABLE_NAME = "request_info"
    const val COLUMN_ID = "_id"
    const val COLUMN_URL = "url"
    const val COLUMN_REQUEST_TYPE = "type"
    const val COLUMN_STATUS_CODE = "status_code"
    const val COLUMN_TIME = "time"
    const val COLUMN_RESPONSE = "response"
    const val COLUMN_ERROR = "error"
    const val COLUMN_QUERY_PARAMS = "query_params"
    const val COLUMN_BODY_REQUEST = "body_request"
    const val COLUMN_REQUEST_HEADERS = "request_headers"
    const val COLUMN_RESPONSE_HEADERS = "response_headers"

    const val CREATE_TABLE_QUERY = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            $COLUMN_URL TEXT NULL,
            $COLUMN_REQUEST_TYPE TEXT  NULL,
            $COLUMN_STATUS_CODE INTEGER  NULL,
            $COLUMN_TIME INTEGER NULL,
            $COLUMN_RESPONSE TEXT,
            $COLUMN_ERROR TEXT,
            $COLUMN_QUERY_PARAMS TEXT,
            $COLUMN_BODY_REQUEST TEXT,
            $COLUMN_REQUEST_HEADERS TEXT,
            $COLUMN_RESPONSE_HEADERS TEXT
        )
    """

    const val DROP_TABLE_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
}
