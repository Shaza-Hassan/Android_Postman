package com.shaza.androidpostman.home.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import com.shaza.androidpostman.R
import com.shaza.androidpostman.databinding.FragmentHomeBinding
import com.shaza.androidpostman.home.model.HomeRepository
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.home.view.adapter.AddHeadersAdapter
import com.shaza.androidpostman.home.view.adapter.RemoveHeader
import com.shaza.androidpostman.home.viewmodel.HomeViewModel
import com.shaza.androidpostman.shared.GenericViewModelFactory
import com.shaza.androidpostman.shared.netowrk.APIClient

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val  apiClient = APIClient()
    private val homeRepository = HomeRepository(apiClient)

    private val viewModel: HomeViewModel by viewModels{
        GenericViewModelFactory(HomeViewModel::class.java) {
            HomeViewModel(homeRepository)
        }
    }
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
        onToggleButtonChange()

        addNewHeaderButtonClickListener()

        onSendRequestButtonClicked()

        onButtonAddFileClick()

        onUrlEditTextChange()

        onBodyEditTextChange()
    }

    private fun initObservers() {
        // Initialize Observers
    }

    private fun onToggleButtonChange(){
        binding.httpRequestType.httpTypeToggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
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