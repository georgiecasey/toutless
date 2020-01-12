package com.georgiecasey.toutless.api.models.dto

import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Events(
    @Json(name = "events")
    val events: List<Event>,
    @Json(name = "last_checked")
    val lastChecked: String
) {
    @JsonClass(generateAdapter = true)
    @Entity(tableName = "event")
    data class Event(
        @PrimaryKey
        @ColumnInfo(name = "toutless_thread_id")
        @Json(name = "toutless_thread_id")
        val toutlessThreadId: String,
        @ColumnInfo(name = "event_dates")
        @Json(name = "event_dates")
        val eventDates: String,
        @ColumnInfo(name = "event_name")
        @Json(name = "event_name")
        val eventName: String,
        @ColumnInfo(name = "number_of_posts")
        @Json(name = "number_of_posts")
        val numberOfPosts: Int,
        @ColumnInfo(name = "venue")
        @Json(name = "venue")
        val venue: String
    ) {
        companion object {
            val diffUtil = object : DiffUtil.ItemCallback<Event>() {
                override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                    return oldItem.toutlessThreadId == newItem.toutlessThreadId
                }

                override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                    return oldItem.hashCode() == newItem.hashCode()
                }
            }
        }
    }
}

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Events.Event?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Events.Event>?)

    @Query("DELETE FROM event")
    fun removeAll()

    @Query("SELECT * FROM event WHERE toutless_thread_id = :toutlessThreadId LIMIT 1")
    fun fetchById(toutlessThreadId: String): Events.Event?

    @Query("SELECT * FROM event")
    fun fetchAll(): List<Events.Event>
}