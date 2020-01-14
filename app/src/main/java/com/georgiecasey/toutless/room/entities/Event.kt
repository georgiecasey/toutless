package com.georgiecasey.toutless.room.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import com.georgiecasey.toutless.ui.BuyingOrSellingField

@Entity(tableName = "event")
data class Event(
    @PrimaryKey
    @ColumnInfo(name = "toutless_thread_id")
    val toutlessThreadId: String,
    @ColumnInfo(name = "event_dates")
    val eventDates: String,
    @ColumnInfo(name = "event_name")
    val eventName: String,
    @ColumnInfo(name = "number_of_posts")
    val numberOfPosts: Int,
    @ColumnInfo(name = "venue")
    val venue: String,
    @ColumnInfo(name = "buying_or_selling")
    val buyingOrSelling: BuyingOrSellingField,
    @ColumnInfo(name = "is_favourite")
    val isFavourite: Boolean
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

        fun fromDto(eventDto: com.georgiecasey.toutless.api.dto.Events.Event): Event {
           return Event(
               toutlessThreadId = eventDto.toutlessThreadId,
               eventDates = eventDto.eventDates,
               eventName = eventDto.eventName,
               numberOfPosts = eventDto.numberOfPosts,
               venue = eventDto.venue,
               buyingOrSelling = BuyingOrSellingField.Buying,
               isFavourite = false
           )
        }
    }
}

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Event?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<Event>?)

    @Query("DELETE FROM event")
    fun removeAll()

    @Query("SELECT * FROM event WHERE toutless_thread_id = :toutlessThreadId LIMIT 1")
    fun fetchById(toutlessThreadId: String): Event?

    @Query("SELECT * FROM event")
    fun fetchAll(): List<Event>

    @Query("UPDATE event SET buying_or_selling = :buyingOrSelling WHERE toutless_thread_id = :toutlessThreadId")
    fun updateBuyingOrSelling(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField)
}