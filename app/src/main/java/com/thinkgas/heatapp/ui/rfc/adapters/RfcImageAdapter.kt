package com.thinkgas.heatapp.ui.rfc.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.remote.model.Attachment
import com.thinkgas.heatapp.databinding.PhotoListItemBinding

class RfcImageAdapter(
    private val context: Context,
    var list: List<Attachment>,
    private val clickListener: (Attachment) -> Unit
): RecyclerView.Adapter<RfcImageAdapter.AttachmentViewHolder>(){

    inner class AttachmentViewHolder(private val binding:PhotoListItemBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(context: Context,
                 data:Attachment,
                 clickListener: (Attachment) -> Unit
        ){
            binding.apply {
                Glide.with(context)
                    .load(data.image)
                    .error(R.drawable.alert)
                    .listener(
                        object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.visibility =
                                    View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.visibility =
                                    View.GONE
                                return false
                            }
                        }
                    )
                    .into(img)
                clImage.setOnClickListener {
                    clickListener(data)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PhotoListItemBinding.inflate(layoutInflater, parent, false)
        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(context,list[position],clickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}