package com.thinkgas.heatapp.ui.dashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thinkgas.heatapp.data.remote.model.TpiCategory
import com.thinkgas.heatapp.databinding.DashboardListItemBinding

class DashboardAdapter(
    private val context: Context,
    private val categoryList: List<TpiCategory>,
    private val clickListener: (TpiCategory) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {
    inner class DashboardViewHolder(private val binding: DashboardListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, data: TpiCategory, clickListener: (TpiCategory) -> Unit) {
            binding.apply {
                ivService.setImageDrawable(ContextCompat.getDrawable(context, data.icon))
                tvName.text = data.category
                cvCategory.setOnClickListener {
                    clickListener(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val binding = DashboardListItemBinding.inflate(
            LayoutInflater.from(context),
            parent, false
        )
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(context, categoryList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}