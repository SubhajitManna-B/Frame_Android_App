package com.ovaku.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ovaku.R
import com.ovaku.data.models.event.EventPayload
import com.ovaku.databinding.EventItemViewBinding

@SuppressLint("NotifyDataSetChanged")
class EventAdapter(
    private var eventList: MutableList<EventPayload>,
    private val interaction: OnInteraction
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    override fun getItemCount() = eventList.size

    private var isSelected = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EventViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.event_item_view, parent, false
            )
        )

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val eventPayload = eventList[holder.adapterPosition]
        holder.onBind(eventPayload)

        holder.binding.root.setOnClickListener {
            isSelected = holder.adapterPosition
            interaction.onItem(eventPayload, position)
            notifyDataSetChanged()
        }
    }

    inner class EventViewHolder(val binding: EventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(eventPayload: EventPayload) {
            eventPayload.isSelected = isSelected == adapterPosition
            binding.eventPayload = eventPayload
            binding.executePendingBindings()
        }
    }

    interface OnInteraction {
        fun onItem(eventPayload: EventPayload, position: Int)
    }

    fun updateData(updateEventPayloadList: MutableList<EventPayload>){
        eventList = updateEventPayloadList
        notifyDataSetChanged()
    }
}