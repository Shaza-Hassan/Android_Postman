package com.shaza.androidpostman.shared.database

import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_ERROR
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_REQUEST_TYPE
import com.shaza.androidpostman.shared.database.RequestInfoTableSchema.COLUMN_RESPONSE

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
enum class WhereClauses(val clauses: String) {
    GetAllRequest(""),
    GetAllGETRequest("$COLUMN_REQUEST_TYPE = '${RequestType.GET.name}'"),
    GetAllPOSTRequest("$COLUMN_REQUEST_TYPE = '${RequestType.POST.name}'"),
    GetAllSuccessRequest("$COLUMN_RESPONSE IS NOT NULL AND $COLUMN_ERROR IS NULL"),
    GetAllFailedRequest("$COLUMN_ERROR IS NOT NULL AND $COLUMN_RESPONSE IS NULL")
}