package com.shaza.androidpostman.history.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.shaza.androidpostman.R
import com.shaza.androidpostman.databinding.FragmentHistoryBinding
import com.shaza.androidpostman.history.model.HistoryGateway
import com.shaza.androidpostman.history.model.HistoryRepository
import com.shaza.androidpostman.history.view.adapter.HistoryItemListener
import com.shaza.androidpostman.history.view.adapter.RequestHistoryAdapter
import com.shaza.androidpostman.history.viewmodel.HistoryViewModel
import com.shaza.androidpostman.requestInfo.view.RequestInfoFragment
import com.shaza.androidpostman.shared.GenericViewModelFactory
import com.shaza.androidpostman.shared.database.NetworkRequestDBHelper
import com.shaza.androidpostman.shared.database.OrderClauses
import com.shaza.androidpostman.shared.database.SelectFromDB
import com.shaza.androidpostman.shared.database.WhereClauses
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.ResourceStatus
import com.shaza.androidpostman.shared.utils.hideKeyboard

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var historyGateway: HistoryGateway
    lateinit var viewModel: HistoryViewModel
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: RequestHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyGateway =
            HistoryRepository(SelectFromDB(NetworkRequestDBHelper.getInstance(requireContext())))
        val viewModelFactory = GenericViewModelFactory(HistoryViewModel::class.java) {
            HistoryViewModel(historyGateway)
        }
        viewModel = ViewModelProvider(this, viewModelFactory)[HistoryViewModel::class.java]
        viewModel.getAllRequests()
        initRecyclerView()
        initObservers()
        initClickListener()
    }

    private fun initObservers() {
        viewModel.requests.observe(viewLifecycleOwner) { requestResource ->
            when (requestResource.status) {
                is ResourceStatus.Success -> {
                    binding.historyProgressBar.visibility = View.GONE
                    requestResource.data?.let {
                        adapter.setList(it)
                    }
                }

                is ResourceStatus.Error -> {
                    binding.historyProgressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        requestResource.error?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ResourceStatus.Idle -> {
                    binding.historyProgressBar.visibility = View.GONE
                }

                is ResourceStatus.Loading -> {
                    binding.historyProgressBar.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun initClickListener() {
        onRequestTypeRadioButtonsClicked()
        onRequestStatusTypeRadioButtonsClicked()
        sortByTimeCheckBox()
        onNavigationButtonClicked()
    }

    private fun onNavigationButtonClicked() {
        binding.historyToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun onRequestTypeRadioButtonsClicked() {
        binding.httpTypeToggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.all_request_type -> {
                    viewModel.updateRequestType(WhereClauses.GetAllRequest)
                }

                R.id.get_type -> {
                    viewModel.updateRequestType(WhereClauses.GetAllGETRequest)
                }

                R.id.post_type -> {
                    viewModel.updateRequestType(WhereClauses.GetAllPOSTRequest)
                }
            }
        }
    }

    private fun onRequestStatusTypeRadioButtonsClicked() {
        binding.httpStatusToggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.all_request -> {
                    viewModel.updateRequestStatus(WhereClauses.GetAllRequest)
                }

                R.id.success_requests -> {
                    viewModel.updateRequestStatus(WhereClauses.GetAllSuccessRequest)
                }

                R.id.failed_requests -> {
                    viewModel.updateRequestStatus(WhereClauses.GetAllFailedRequest)
                }
            }
        }
    }

    private fun sortByTimeCheckBox() {
        binding.sortByTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.setOrderClauses(OrderClauses.OrderByTime)
            } else {
                viewModel.setOrderClauses(OrderClauses.OrderById)
            }
        }
    }

    private fun initRecyclerView() {
        adapter = RequestHistoryAdapter(listOf(), historyItemListener)
        binding.requestHistoryRecyclerView.adapter = adapter
    }

    private val historyItemListener: HistoryItemListener = object : HistoryItemListener {
        override fun onItemClicked(networkResponse: NetworkResponse) {
            val requestInfoFragment = RequestInfoFragment.newInstance(networkResponse)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main, requestInfoFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}