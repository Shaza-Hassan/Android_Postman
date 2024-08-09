package com.shaza.androidpostman.home.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.shaza.androidpostman.R
import com.shaza.androidpostman.databinding.FragmentHomeBinding
import com.shaza.androidpostman.history.view.HistoryFragment
import com.shaza.androidpostman.home.model.HomeGateway
import com.shaza.androidpostman.home.model.HomeRepository
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.home.view.adapter.AddHeadersAdapter
import com.shaza.androidpostman.home.view.adapter.RemoveHeader
import com.shaza.androidpostman.home.viewmodel.HomeViewModel
import com.shaza.androidpostman.home.viewmodel.HomeViewModelInterface
import com.shaza.androidpostman.requestInfo.view.RequestInfoFragment
import com.shaza.androidpostman.shared.GenericViewModelFactory
import com.shaza.androidpostman.shared.database.AddRequestInDB
import com.shaza.androidpostman.shared.database.NetworkRequestDBHelper
import com.shaza.androidpostman.shared.model.ResourceStatus
import com.shaza.androidpostman.shared.netowrk.APIClient
import com.shaza.androidpostman.shared.netowrk.HTTPClient
import com.shaza.androidpostman.shared.utils.hideKeyboard

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var  apiClient: HTTPClient

    private lateinit var homeRepository : HomeGateway

    lateinit var viewModel: HomeViewModelInterface
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: AddHeadersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiClient = APIClient()
        homeRepository = HomeRepository(apiClient, AddRequestInDB(NetworkRequestDBHelper.getInstance(requireContext())))
        val viewModelFactory = GenericViewModelFactory(HomeViewModel::class.java) {
            HomeViewModel(homeRepository)
        }
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        initRecyclerView()
        initListener()
        initObservers()
    }

    private fun initRecyclerView() {
        // Initialize RecyclerView
        adapter = AddHeadersAdapter(viewModel.getHeaders(), object : RemoveHeader {
            override fun onRemoveHeader(index: Int) {
                viewModel.removeHeader(index)
                adapter.notifyDataSetChanged()
            }
        })

        binding.headers.headersList.adapter = adapter
    }

    private fun initListener() {
        // Initialize Listener
        onHistoryIconClicked()

        onToggleButtonChange()

        addNewHeaderButtonClickListener()

        onSendRequestButtonClicked()

        onButtonAddFileClick()

        onUrlEditTextChange()

        onBodyEditTextChange()
    }

    private fun initObservers() {
        // Initialize Observers
        observeOnResponseResource()
    }

    private fun observeOnResponseResource(){
        viewModel.response.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                is ResourceStatus.Error -> {
                    binding.progressBar.visibility = GONE
                    Toast.makeText(requireContext(), resource.error?.message ?: "", Toast.LENGTH_SHORT).show()
                }
                ResourceStatus.Idle -> {
                    binding.progressBar.visibility = GONE
                }
                ResourceStatus.Loading -> {
                    binding.progressBar.visibility = VISIBLE
                }
                ResourceStatus.Success -> {
                    binding.progressBar.visibility = GONE
                    resource.data?.let {
                        // Open Request Info Fragment
                         val requestInfoFragment = RequestInfoFragment.newInstance(it)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .add(R.id.main, requestInfoFragment)
                                .addToBackStack(null)
                                .commit()
                    } ?: run {
                        Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    private fun onHistoryIconClicked(){
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.history_menu_item -> {
                    // Open History Fragment
                    val historyFragment = HistoryFragment.newInstance()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.main, historyFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            true
        }
    }

    private fun onToggleButtonChange(){
        binding.httpRequestType.httpTypeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.get_button -> {
                    viewModel.setRequestType(RequestType.GET)
                    showHideBodyRequest(isShow = false)
                }

                R.id.post_button -> {
                    viewModel.setRequestType(RequestType.POST)
                    showHideBodyRequest(isShow = true)
                }
            }
        }
    }

    private fun showHideBodyRequest(isShow: Boolean){
        if (isShow){
            binding.bodyOfPostRequestLayout.visibility = VISIBLE
        } else {
            binding.bodyOfPostRequestLayout.visibility = GONE
        }
    }

    private fun addNewHeaderButtonClickListener(){
        binding.headers.addNewHeader.setOnClickListener {
            viewModel.addHeader()
            adapter.notifyDataSetChanged()
        }
    }

    private fun onSendRequestButtonClicked(){
        binding.sendRequest.setOnClickListener {
            // Send Request
            hideKeyboard(requireActivity())
            binding.progressBar.visibility = VISIBLE
            viewModel.sendRequest()
        }
    }

    private fun onButtonAddFileClick(){

    }

    private fun onUrlEditTextChange(){
        binding.urlEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.setUrl(text.toString())
        }
    }

    private fun onBodyEditTextChange(){
        binding.bodyEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.setBody(text.toString())
        }
    }
}