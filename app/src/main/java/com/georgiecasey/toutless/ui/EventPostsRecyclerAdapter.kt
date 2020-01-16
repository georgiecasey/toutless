package com.georgiecasey.toutless.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.georgiecasey.toutless.R
import com.georgiecasey.toutless.room.entities.Post
import com.georgiecasey.toutless.utils.extension.toRelativeTime
import kotlinx.android.synthetic.main.item_post.view.*
import timber.log.Timber

class EventPostsRecyclerAdapter(private val listener: OnEventPostClickListener) :
    ListAdapter<Post, EventPostsRecyclerAdapter.ViewHolder>(Post.diffUtil) {

    interface OnEventPostClickListener {
        fun onEventPostClicked(toutlessPostId: String)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var post: Post? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            post?.let {
                listener.onEventPostClicked(it.toutlessPostId)
            }
        }

        fun bind(post: Post) {
            Timber.d("bind")
            this.post = post
            view.tvPostDates.text = post.postTime.toRelativeTime()
            view.tvPostText.text = post.postText
        }
    }
}
