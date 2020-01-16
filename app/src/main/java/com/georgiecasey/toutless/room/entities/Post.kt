package com.georgiecasey.toutless.room.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.*

@Entity(tableName = "post")
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "toutless_post_id")
    val toutlessPostId: String,
    @ColumnInfo(name = "toutless_thread_id")
    val toutlessThreadId: String,
    @ColumnInfo(name = "author_id")
    val authorId: String,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "post_text")
    val postText: String,
    @ColumnInfo(name = "post_time")
    val postTime: Long
) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.toutlessPostId == newItem.toutlessPostId
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }

        fun fromDto(postDto: com.georgiecasey.toutless.api.dto.Posts.Post): Post {
            return Post(
                toutlessPostId = postDto.toutlessPostId,
                toutlessThreadId = postDto.toutlessThreadId,
                authorId = postDto.authorId,
                icon = postDto.icon,
                postText = postDto.postText,
                postTime = postDto.postTime
            )
        }
    }
}

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Post?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Post>?)

    @Query("DELETE FROM post")
    fun removeAll()

    @Query("SELECT * FROM post WHERE toutless_post_id = :toutlessPostId LIMIT 1")
    fun fetchByPostId(toutlessPostId: String): Post?

    @Query("SELECT * FROM post WHERE toutless_thread_id = :toutlessThreadId")
    fun fetchAllByThreadId(toutlessThreadId: String): List<Post>
}