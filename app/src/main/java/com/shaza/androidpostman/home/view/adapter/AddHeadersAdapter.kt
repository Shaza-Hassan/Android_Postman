package com.shaza.androidpostman.home.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.shaza.androidpostman.databinding.HeaderItemLayoutBinding
import com.shaza.androidpostman.home.model.Header

/**
 * Created by Shaza Hassan on 2024/Aug/07.
 */

class AddHeadersAdapter(val headers: List<Header>, val onRemoveHeader: RemoveHeader) :
    RecyclerView.Adapter<AddHeadersAdapter.AddHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddHeaderViewHolder {
        val binding =
            HeaderItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddHeaderViewHolder(binding)
    }

    override fun getItemCount(): Int = headers.size


    override fun onBindViewHolder(holder: AddHeaderViewHolder, position: Int) {
        val header = headers[position]
        holder.bind(header)
        holder.binding.removeHeader.setOnClickListener {
            onRemoveHeader.onRemoveHeader(position)
        }
    }

    class AddHeaderViewHolder(val binding: HeaderItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(header: Header) {
            itemView.apply {
                binding.headerTitleEditText.doOnTextChanged { text, _, _, _ ->
                    header.title = text.toString()
                }

                binding.headerValueEditText.doOnTextChanged { text, _, _, _ ->
                    header.value = text.toString()
                }
            }
        }
    }
}


interface RemoveHeader {
    fun onRemoveHeader(index: Int)
}