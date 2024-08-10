package com.shaza.androidpostman.history.view.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.shaza.androidpostman.R
import com.shaza.androidpostman.databinding.HistoryRequestItemLayoutBinding
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.shared.model.NetworkResponse

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */
class RequestHistoryAdapter(
    private var historyList: List<NetworkResponse>,
    private val listener: HistoryItemListener
) : RecyclerView.Adapter<RequestHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryRequestItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    fun setList(list: List<NetworkResponse>) {
        historyList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val networkResponse = historyList[position]
        holder.bind(networkResponse)

        holder.binding.historyCardItem.setOnClickListener {
            listener.onItemClicked(networkResponse)
        }
    }

    class HistoryViewHolder(val binding: HistoryRequestItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(networkResponse: NetworkResponse) {
            itemView.apply {
                binding.requestType.text = networkResponse.requestType?.name
                binding.requestType.backgroundTintList = when (networkResponse.requestType) {
                    RequestType.GET -> context.resources.getColorStateList(R.color.get_response_color)
                    RequestType.POST -> context.resources.getColorStateList(R.color.post_response_color)
                    null -> {
                        context.resources.getColorStateList(R.color.get_response_color)
                    }
                }

                setSpannableText(networkResponse.url ?: "", "URL: ", binding.requestUrlHistory)

                setSpannableText("${networkResponse.elapsedTime} ms", "Time: ", binding.requestTime)

                if (networkResponse.response != null) {
                    binding.requestStatus.text = context.getString(R.string.success)
                    binding.requestStatus.backgroundTintList =
                        context.resources.getColorStateList(R.color.green)
                } else if (networkResponse.error != null) {
                    binding.requestStatus.text = context.getString(R.string.failed)
                    binding.requestStatus.backgroundTintList =
                        context.resources.getColorStateList(R.color.red)
                }
            }

        }

        private fun setSpannableText(normalText: String, boldText: String, textView: TextView) {
            val s = SpannableStringBuilder()
                .bold { append(boldText) }
                .append(normalText)

            textView.text = s
        }
    }
}

interface HistoryItemListener {
    fun onItemClicked(networkResponse: NetworkResponse)
}