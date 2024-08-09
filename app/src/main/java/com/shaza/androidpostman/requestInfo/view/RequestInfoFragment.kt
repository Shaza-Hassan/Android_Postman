package com.shaza.androidpostman.requestInfo.view

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import com.shaza.androidpostman.R
import com.shaza.androidpostman.databinding.FragmentRequestInfoBinding
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.requestInfo.viewmodel.RequestInfoViewModel
import com.shaza.androidpostman.requestInfo.viewmodel.RequestInfoViewModelInterface
import com.shaza.androidpostman.shared.model.NetworkResponse

class RequestInfoFragment : Fragment() {

    companion object {
        fun newInstance(
            networkResponse: NetworkResponse
        ) : RequestInfoFragment {
            val fragment = RequestInfoFragment()
            val bundle = Bundle()
            bundle.putParcelable("networkResponse", networkResponse)
            fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var viewModel : RequestInfoViewModelInterface
    private lateinit var binding: FragmentRequestInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = viewModels<RequestInfoViewModel>().value

        arguments?.let {
            viewModel.extractData(it)
        }
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.requestInfoToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initObservers(){
        viewModel.networkResponse.observe(viewLifecycleOwner) {
            response ->
            response?.let { response ->
                requestBasicInfo(response)
                setRequestTypeData(response)
                setSpannableText(response.url ?: "", "URL: ", binding.requestUrl)
                handleShowRequestQueryParams(response)
                handleShowRequestHeaders(response)
                handleShowRequestBody(response)
                handleShowResponse(response)
                handleShowResponseHeaders(response)
            }
        }
    }

    private fun requestBasicInfo(response: NetworkResponse) {
        setSpannableText(
            response.responseCode.toString(),
            "Status code: ",
            binding.requestResponseBasicInfo.requestStatusCode
        )
        setSpannableText(
            "${response.elapsedTime.toString()} ms",
            "Time: ",
            binding.requestResponseBasicInfo.requestTime
        )
    }

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun setRequestTypeData(response: NetworkResponse) {
        binding.requestResponseBasicInfo.requestType.text =
            response.requestType?.name
        binding.requestResponseBasicInfo.requestType.backgroundTintList =
            when (response.requestType) {
                RequestType.GET -> requireContext().resources.getColorStateList(R.color.get_response_color)
                RequestType.POST -> requireContext().resources.getColorStateList(R.color.post_response_color)
                null -> requireContext().resources.getColorStateList(R.color.black)
            }
    }

    private fun handleShowRequestQueryParams(response: NetworkResponse) {
        response.queryParameters?.let {
            queryParams ->
            if (queryParams.isNotEmpty()) {
                showView(binding.requestQueryParams)
                val responseQueryParams = fromMapToString(queryParams)

                setSpannableText(
                    responseQueryParams ?: "",
                    "Query params: ",
                    binding.requestQueryParams
                )
            } else {
                hideView(binding.requestQueryParams)
            }
        }
    }

    private fun handleShowRequestHeaders(response: NetworkResponse) {
        response.requestHeaders?.let {
            requestHeaders ->
            if (requestHeaders.isNotEmpty()) {
                showView(binding.requestHeaders)
                val responseHeaders = fromMapToString(requestHeaders)

                setSpannableText(
                    responseHeaders ?: "",
                    "Response header: ",
                    binding.requestHeaders
                )
            } else {
                hideView(binding.requestHeaders)
            }
        } ?: run {
            hideView(binding.requestHeaders)
        }
    }

    private fun handleShowRequestBody(response: NetworkResponse) {
        response.body?.let {
            showView(binding.bodyRequest)
            setSpannableText(
                response.body ?: "",
                "Body request: ",
                binding.bodyRequest
            )
        } ?: run {
            hideView(binding.bodyRequest)
        }
    }

    private fun handleShowResponse(response: NetworkResponse) {
        response.response?.let {
            showView(binding.responseOrError)
            setSpannableText(
                response.response ?: "",
                "Response: ",
                binding.responseOrError
            )
        } ?: run {
            response.error?.let {
                showView(binding.responseOrError)
                binding.responseOrError.setTextColor(requireContext().getColor(R.color.red))
                setSpannableText(response.error ?: "", "Error: ", binding.responseOrError)
            } ?: run {
                hideView(binding.responseOrError)
            }
        }
    }

    private fun handleShowResponseHeaders(response: NetworkResponse) {
        response.responseHeaders?.let {
            showView(binding.responseHeader)
            setSpannableText(
                response.responseHeaders.toString() ?: "",
                "Response headers: ",
                binding.responseHeader
            )
        } ?: run {
            hideView(binding.responseHeader)
        }
    }

    private fun setSpannableText(normalText: String, boldText: String, textView: TextView) {
        val s = SpannableStringBuilder()
            .bold { append(boldText) }
            .append(normalText)

        textView.text = s
    }

    private fun fromMapToString(map: Map<String, String>): String {
        return map.map { (key, value) ->
            "$key: $value"
        }.joinToString("\n")
    }

    private fun showView(view: View) {
        view.visibility = VISIBLE
    }

    private fun hideView(view: View) {
        view.visibility = GONE
    }
}