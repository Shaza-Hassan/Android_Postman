package com.shaza.androidpostman.history.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaza.androidpostman.history.model.HistoryGateway
import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.WhereClauses
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.Resource
import com.shaza.androidpostman.shared.utils.EspressoIdlingResource
import java.util.concurrent.Executors

class HistoryViewModel(private val historyGateway: HistoryGateway) : ViewModel() {

    private val _whereClause: MutableList<WhereClauses> = mutableListOf()
    val whereClause: List<WhereClauses> = _whereClause

    private var _orderClauses: OrderClauses = OrderClauses.OrderById

    private val _requests = MutableLiveData<Resource<List<NetworkResponse>>>()

    val requests: LiveData<Resource<List<NetworkResponse>>>
        get() = _requests


    fun updateRequestType(whereClauses: WhereClauses) {
        _whereClause.removeAll {
            it == WhereClauses.GetAllPOSTRequest || it == WhereClauses.GetAllGETRequest
        }
        if (!_whereClause.contains(whereClauses)) {
            _whereClause.add(whereClauses)
        }
        getAllRequests()
    }

    fun updateRequestStatus(whereClauses: WhereClauses) {
        _whereClause.removeAll {
            it == WhereClauses.GetAllSuccessRequest || it == WhereClauses.GetAllFailedRequest
        }
        if (!_whereClause.contains(whereClauses)) {
            _whereClause.add(whereClauses)
        }

        getAllRequests()
    }

    fun setOrderClauses(orderClauses: OrderClauses) {
        _orderClauses = orderClauses
        getAllRequests()
    }

    fun getOrderClauses(): OrderClauses {
        return _orderClauses
    }

    fun getAllRequests() {
        EspressoIdlingResource.increment()
        Executors.newSingleThreadExecutor().execute {
            _requests.postValue(Resource.loading())

            val requests = historyGateway.getHistory(whereClause, getOrderClauses())

            _requests.postValue(Resource.success(requests))

            EspressoIdlingResource.decrement()
        }
    }
}