package com.georgiecasey.toutless.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.georgiecasey.toutless.R
import com.georgiecasey.toutless.room.entities.Event
import kotlinx.android.synthetic.main.item_event.view.*
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide

class EventsRecyclerAdapter(private val listener: OnEventClickListener) :
    RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder>() {
    private var items = mutableListOf<Event>()

    interface OnEventClickListener {
        fun onEventClicked(toutlessThreadId: String)
        fun onEventFavouriteClicked(toutlessThreadId: String, isFavourite: Boolean)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position))
    }

    fun setItems(newItems: List<Event>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(this.items, newItems))
        result.dispatchUpdatesTo(CustomCallback(newItems))
        this.items.clear()
        this.items.addAll(newItems)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var event: Event? = null

        init {
            view.setOnClickListener(this)
            view.cbFavourite.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            event?.let { event ->
                when (v.id) {
                    R.id.cvEventRow -> {
                        listener.onEventClicked(event.toutlessThreadId)
                    }
                    R.id.cbFavourite-> {
                        (v as? CheckBox)?.let {
                            listener.onEventFavouriteClicked(event.toutlessThreadId, it.isChecked)
                        }
                    }
                    else -> throw IllegalStateException("OnClick event is not of row or checkbox")
                }
            }
        }

        fun bind(event: Event) {
            this.event = event
            view.tvEventName.text = event.eventName
            view.tvVenue.text = event.venue
            view.tvEventDates.text = event.eventDates
            event.isFavourite?.let { view.cbFavourite.isChecked = it }
            view.tvNumberOfPosts.text = event.numberOfPosts.toString()
            Glide
                .with(view.context)
                .load("http://www.georgiecasey.com/toutless_api/event_image.php?toutless_thread_id="+event.toutlessThreadId)
                .override(640, 640)
                .dontTransform()
                .into(view.ivSpotifyImage)
        }
    }

    inner class DiffUtilCallback(
        private var oldItems: List<Event>,
        private var newItems: List<Event>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].toutlessThreadId == newItems[newItemPosition].toutlessThreadId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].hashCode() == newItems[newItemPosition].hashCode()
        }
    }

    inner class CustomCallback(val newItems: List<Event>) : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count)
        }

        // you need this to ensure animation works when event favourited,
        // but no animation when unfavourited
        override fun onMoved(fromPosition: Int, toPosition: Int) {
            if (newItems[toPosition].isFavourite == true) {
                notifyItemMoved(fromPosition, toPosition)
            } else if (newItems[toPosition].isFavourite == false) {
                notifyDataSetChanged()
            }
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }
    }

    inner class GlidePreloadModelProvider(val context: Context): ListPreloader.PreloadModelProvider<Event> {
        override fun getPreloadItems(position: Int): List<Event> {
            return items.subList(position, position+1)
        }

        override fun getPreloadRequestBuilder(event: Event): RequestBuilder<*>? {
            return Glide
                .with(context)
                .load("http://www.georgiecasey.com/toutless_api/event_image.php?toutless_thread_id="+event.toutlessThreadId)
                .override(640, 640)
                .dontTransform()
        }
    }
}



