package com.georgiecasey.toutless.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.georgiecasey.toutless.R
import com.georgiecasey.toutless.room.entities.Event
import kotlinx.android.synthetic.main.item_event.view.*
import kotlinx.android.synthetic.main.list_item_event.view.tvEventName
import timber.log.Timber

class EventsRecyclerAdapter(private val listener: OnEventClickListener) :
    ListAdapter<Events.Event, EventsRecyclerAdapter.ViewHolder>(Events.Event.diffUtil) {

    interface OnEventClickListener {
        fun onEventClicked(messageId: String)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var event: Event? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            event?.let {
                listener.onEventClicked(it.toutlessThreadId)
            }
        }

        fun bind(event: Event) {
            Timber.d("bind")
            this.event = event
            view.tvEventName.text = event.eventName
            view.tvVenue.text = event.venue
            view.tvEventDates.text = event.eventDates
            view.tvNumberOfPosts.text = event.numberOfPosts.toString()
        }
    }
}
