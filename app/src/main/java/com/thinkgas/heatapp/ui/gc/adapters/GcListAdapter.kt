package com.thinkgas.heatapp.ui.gc.adapters

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.databinding.ServiceDetailListItemBinding
import com.thinkgas.heatapp.utils.AppUtils


class GcListAdapter(var context: Context,
                             var isClaimable: Boolean,
                             var status:String,
                             private var clickListener: (Agent) -> Unit,
                             private var claimListener:(Agent) -> Unit,
                             private var navigateListener:(String,String) ->Unit,
                             private var callListener:(String) -> Unit,
                             private var cancelListener:(Agent) -> Unit,
                             private var supervisorListener:(Agent) -> Unit,
                             private var infoListener: (Agent)->Unit) :
    PagingDataAdapter<Agent, GcListAdapter.AgentViewHolder>(AgentDiffCallBack()) {

    inner class AgentViewHolder(private var binding: ServiceDetailListItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(
            item: Agent?, clickListener: (Agent) -> Unit, claimListener: (Agent) -> Unit,
            navigateListener: (String, String) -> Unit,
            callListener: (String) -> Unit,
            cancelListener: (Agent) -> Unit,
            supervisorListener: (Agent) -> Unit,
            infoListener: (Agent) -> Unit
        ) {

            binding.apply {
                if (item != null) {
                    tvName.text = item.customerName
                    tvAddress.text = item.address
                    tvMobile.text = item.mobileNo
//                  tvAssignDate.text = item.claimedDate
                    tvAppNo.text = item.applicationNumber
                    tvBpNo.text = item.bpnumber
                    tvAssignDate.text = "Assign Date : ${item.claimedDate}"
                    tvGcNo.text = item.gcNumber

                    tvMobile.setOnClickListener {
                        callListener(item.mobileNo)
                    }

                    val distance = AppUtils.distanceInKms(
                        AppCache.latitude!!,
                        AppCache.longitude!!,
                        item.latitude.toDouble(),
                        item.longitude.toDouble()
                    )
                    val content = SpannableString("${distance.toInt()} kms away")
                    content.setSpan(UnderlineSpan(), 0, content.length, 0)
                    tvNearby.text = content
                    tvNearby.setOnClickListener {
                        navigateListener(item.latitude, item.longitude)
                    }
                    cvPending.setOnClickListener {
                        if (status == "approved" || status == "declined") {
                            return@setOnClickListener
                        }
                        if (status == "pending") {
                            if (AppCache.isTpi && (item.tpiApprovalStatus == "Unclaim" || item.tpiApprovalStatus == null)) {
                                return@setOnClickListener
                            }
                        }
                        if (!isClaimable) {
                            clickListener(item)
                        }
                    }

                    if (isClaimable && !AppCache.isTpi) {
                        btnTpiInfo.visibility = View.VISIBLE
                        btnCancel.visibility = View.VISIBLE
                    } else {
                        btnTpiInfo.visibility = View.VISIBLE
                        btnTpiInfo.text = "TPI Info"
                    }

                    if (AppCache.isTpi) {
                        ivTpi.visibility = View.VISIBLE
                        if (status == "pending") {
                            btnTpiInfo.visibility = View.VISIBLE
                            if (item.tpiApprovalStatus == "Claim") {
                                btnTpiInfo.text = "Unclaim"
                            } else {
                                btnTpiInfo.text = "Claim"
                            }
                        } else {
                            btnTpiInfo.visibility = View.GONE
                        }
                    }

                    ivTpi.setOnClickListener {
                        supervisorListener(item)
                    }

                    btnTpiInfo.setOnClickListener {
                        if (AppCache.isTpi) {
                            claimListener(item)
                            return@setOnClickListener
                        }
                        if (isClaimable && !AppCache.isTpi) {
                            claimListener(item)
                        } else {
                            infoListener(item)
                        }
                    }

                    btnCancel.setOnClickListener {
                        cancelListener(item)
                    }

                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentViewHolder {
        return AgentViewHolder(
            ServiceDetailListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AgentViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(
            it, clickListener, claimListener, navigateListener, callListener,
            cancelListener, supervisorListener, infoListener
        ) }
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