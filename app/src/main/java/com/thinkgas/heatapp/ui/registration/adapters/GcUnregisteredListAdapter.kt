package com.thinkgas.heatapp.ui.registration.adapters

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.databinding.GcUnregListItemBinding
import com.thinkgas.heatapp.utils.AppUtils


class GcUnregisteredListAdapter(var context: Context,
                     var clickListener: (Agent) -> Unit,
                                private var navigateListener:(String,String) ->Unit,
                                private var callListener:(String) -> Unit,) :
    PagingDataAdapter<Agent, GcUnregisteredListAdapter.AgentViewHolder>(AgentDiffCallBack()) {
    inner class AgentViewHolder(
        private var binding: GcUnregListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: Agent?, clickListener: (Agent) -> Unit, navigateListener: (String, String) -> Unit, callListener: (String) -> Unit) {
            binding.apply {
                tvName.text = item!!.customerName
                tvAddress.text = item.landmark
                tvMobile.text = item.mobileNo
                tvGcNo.text = item.gcUnregNumber



                val distance= AppUtils.distanceInKms(AppCache.latitude!!,
                    AppCache.longitude!!,
                    item.latitude.toDouble(),
                    item.longitude.toDouble()
                )

                val content = SpannableString("${distance.toInt()} kms away")
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                tvNearby.text = content
                tvNearby.setOnClickListener {
                    navigateListener(item.latitude,item.longitude)
                }

                tvMobile.setOnClickListener {
                    callListener(item.mobileNo)
                }
                gcUnregCard.setOnClickListener {
                    clickListener(item)
                }


            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentViewHolder {
        return AgentViewHolder(
            GcUnregListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AgentViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(context, it, clickListener,navigateListener,callListener) }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class AgentDiffCallBack : DiffUtil.ItemCallback<Agent>() {
    override fun areItemsTheSame(oldItem: Agent, newItem: Agent): Boolean {
        return oldItem.mobileNo == newItem.mobileNo
    }

    override fun areContentsTheSame(oldItem: Agent, newItem: Agent): Boolean {
        return oldItem == newItem
    }
}