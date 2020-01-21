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

        fun toUpdateEntity(event: Event): EventUpdate {
            return EventUpdate(
                toutlessThreadId = event.toutlessThreadId,
                eventDates = event.eventDates,
                eventName = event.eventName,
                numberOfPosts = event.numberOfPosts,
                venue = event.venue
            )
        }
    }
}

data class EventUpdate(
    @ColumnInfo(name = "toutless_thread_id")
    val toutlessThreadId: String,
    @ColumnInfo(name = "event_dates")
    val eventDates: String,
    @ColumnInfo(name = "event_name")
    val eventName: String,
    @ColumnInfo(name = "number_of_posts")
    val numberOfPosts: Int,
    @ColumnInfo(name = "venue")
    val venue: String
)

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: Event?): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: List<Event>?): List<Long>

    @Update(entity = Event::class)
    fun update(obj: EventUpdate?)

    @Update(entity = Event::class)
    fun update(obj: List<EventUpdate>?)

    @Transaction
    fun insertOrUpdate(data: Event?) {
        val id = insert(data)
        if (id == -1L) {
            data?.let {
                val eventUpdate = Event.toUpdateEntity(it)
                update(eventUpdate)
            }
        }
    }

    @Transaction
    fun insertOrUpdateAll(data: List<Event>?) {
        val insertResult = insert(data)
        val updateList = mutableListOf<EventUpdate>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) data?.let {
                updateList.add(Event.toUpdateEntity(it[i])) }
        }

        if (!updateList.isEmpty()) update(updateList)
    }

    @Query("DELETE FROM event")
    fun removeAll()

    @Query("SELECT * FROM event WHERE toutless_thread_id = :toutlessThreadId LIMIT 1")
    fun fetchById(toutlessThreadId: String): Event?

    @Query("SELECT * FROM event ORDER BY is_favourite DESC")
    fun fetchAll(): List<Event>

    @Query("UPDATE event SET buying_or_selling = :buyingOrSelling WHERE toutless_thread_id = :toutlessThreadId")
    fun updateBuyingOrSelling(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField)

    @Query("UPDATE event SET is_favourite = :isFavourite WHERE toutless_thread_id = :toutlessThreadId")
    fun updateFavourite(toutlessThreadId: String, isFavourite: Boolean)
}