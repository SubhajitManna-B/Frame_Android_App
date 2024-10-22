package com.ovaku.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ovaku.R
import com.ovaku.data.models.event.eventImage.EventImagePayload
import com.ovaku.databinding.EventImageItemViewBinding

@SuppressLint("NotifyDataSetChanged")
class EventImageAdapter(
    private var eventImageList: MutableList<EventImagePayload>,
    private val interaction: OnInteraction
) : RecyclerView.Adapter<EventImageAdapter.EventImageViewHolder>() {

    override fun getItemCount() = eventImageList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EventImageViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.event_image_item_view, parent, false
            )
        )

    override fun onBindViewHolder(holder: EventImageViewHolder, position: Int) {
        val evenImage = eventImageList[holder.adapterPosition]
        holder.onBind(evenImage)

        holder.binding.ivLike.setOnClickListener {
            interaction.onLike(evenImage, holder.adapterPosition)
        }
        holder.binding.ivDislike.setOnClickListener {
            interaction.onDislike(evenImage, holder.adapterPosition)
        }
        holder.binding.ivFavourite.setOnClickListener {
            interaction.onFavourite(evenImage, holder.adapterPosition)
        }
    }

    inner class EventImageViewHolder(val binding: EventImageItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(eventImage: EventImagePayload) {
            binding.eventImage = eventImage
            binding.executePendingBindings()
        }
    }

    interface OnInteraction {
        fun onLike(eventImagePayload: EventImagePayload, position: Int)
        fun onFavourite(eventImagePayload: EventImagePayload, position: Int)
        fun onDislike(eventImagePayload: EventImagePayload, position: Int)
    }

    fun updateData(updateEventImageList: MutableList<EventImagePayload>){
        eventImageList = updateEventImageList
        notifyDataSetChanged()
    }
}