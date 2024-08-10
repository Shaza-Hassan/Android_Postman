package com.shaza.androidpostman.home.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
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
import com.shaza.androidpostman.requestInfo.view.RequestInfoFragment
import com.shaza.androidpostman.shared.GenericViewModelFactory
import com.shaza.androidpostman.shared.database.AddRequestInDB
import com.shaza.androidpostman.shared.database.NetworkRequestDBHelper
import com.shaza.androidpostman.shared.model.ResourceStatus
import com.shaza.androidpostman.shared.netowrk.APIClient
import com.shaza.androidpostman.shared.netowrk.HTTPClient
import com.shaza.androidpostman.shared.utils.ConnectivityChecker
import com.shaza.androidpostman.shared.utils.hideKeyboard

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()

        const val STORAGE_PERMISSION_CODE = 101
        const val FILE_PICKER_REQUEST_CODE = 102
    }

    private lateinit var apiClient: HTTPClient

    private lateinit var homeRepository: HomeGateway

    lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: AddHeadersAdapter

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
        homeRepository = HomeRepository(
            apiClient,
            AddRequestInDB(NetworkRequestDBHelper.getInstance(requireContext())),
            ConnectivityChecker(requireContext())
        )
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

        onRemoveFileButtonClicked()

        onUrlEditTextChange()

        onBodyEditTextChange()
    }

    private fun initObservers() {
        // Initialize Observers
        observeOnResponseResource()
    }

    private fun observeOnResponseResource() {
        observeOnResponse()

        observeOnSelectedFileUri()
    }

    private fun observeOnResponse() {
        viewModel.response.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                is ResourceStatus.Error -> {
                    binding.progressBar.visibility = GONE
                    Toast.makeText(
                        requireContext(),
                        resource.error?.message ?: "",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun observeOnSelectedFileUri() {
        viewModel.selectedFileUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                handleSelectedFile(it)
            } ?: run {
                binding.selectedFileLayout.visibility = GONE
            }
        }
    }

    private fun onHistoryIconClicked() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
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

    private fun onToggleButtonChange() {
        binding.httpRequestType.httpTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
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

    private fun showHideBodyRequest(isShow: Boolean) {
        if (isShow) {
            binding.bodyOfPostRequestLayout.visibility = VISIBLE
        } else {
            binding.bodyOfPostRequestLayout.visibility = GONE
        }
    }

    private fun addNewHeaderButtonClickListener() {
        binding.headers.addNewHeader.setOnClickListener {
            viewModel.addHeader()
            adapter.notifyDataSetChanged()
        }
    }

    private fun onSendRequestButtonClicked() {
        binding.sendRequest.setOnClickListener {
            // Send Request
            hideKeyboard(requireActivity())
            binding.progressBar.visibility = VISIBLE
            viewModel.onSendRequestClicked(requireActivity().contentResolver)
        }
    }

    private fun onButtonAddFileClick() {
        binding.fileToUpload.setOnClickListener {
            // check if android version is less than or equal to 32 API
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S_V2) {
                requestStoragePermission()
            } else {
                openFilePicker()
            }
        }
    }

    private fun onRemoveFileButtonClicked() {
        binding.removeFile.setOnClickListener {
            viewModel.setSelectedFileUri(null)
        }
    }

    private fun onUrlEditTextChange() {
        binding.urlEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.setUrl(text.toString())
        }
    }

    private fun onBodyEditTextChange() {
        binding.bodyEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.setBody(text.toString())
        }
    }

    private fun requestStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openFilePicker()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // Explain why you need the permission, then request it
                // E.g., show a dialog and then request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            else -> {
                // Directly request the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openFilePicker() {
        getContentLauncher.launch("*/*") // Set MIME type (e.g., "image/*" for images)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openFilePicker()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val getContentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleSelectedFile(it)
            } ?: run {
                binding.selectedFileLayout.visibility = GONE
            }
        }

    private fun handleSelectedFile(uri: Uri) {
        binding.selectedFileLayout.visibility = VISIBLE
        binding.selectedFileName.text = getFileNameFromUri(uri)
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        val contentResolver = requireContext().contentResolver

        // Check if the Uri is pointing to a content provider
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    // Try to get the display name from the column
                    fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }

        // If the Uri is a file Uri, use the path to get the file name
        if (fileName == null) {
            fileName = uri.path
            fileName = fileName?.substring(fileName?.lastIndexOf('/')?.plus(1) ?: 0)
        }

        return fileName
    }

}