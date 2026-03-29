package com.example.campuslife.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslife.R
import com.example.campuslife.model.Event

class EventAdapter(
    private val events: MutableList<Event>,
    private val listener: OnEventClickListener
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    interface OnEventClickListener {
        fun onEventClick(event: Event, position: Int)
        fun onEventLongClick(event: Event, position: Int)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txtEventTitle)
        val txtDateTime: TextView = itemView.findViewById(R.id.txtEventDateTime)
        val txtLocation: TextView = itemView.findViewById(R.id.txtEventLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.txtTitle.text = event.title
        holder.txtDateTime.text = "${event.date} • ${event.time}"
        holder.txtLocation.text = event.location

        holder.itemView.setOnClickListener { listener.onEventClick(event, position) }
        holder.itemView.setOnLongClickListener {
            listener.onEventLongClick(event, position)
            true
        }
    }

    override fun getItemCount(): Int = events.size

    fun addEvent(event: Event) { events.add(0, event); notifyItemInserted(0) }
    fun updateEvent(event: Event, position: Int) { events[position] = event; notifyItemChanged(position) }
    fun removeEvent(position: Int) { events.removeAt(position); notifyItemRemoved(position) }
}