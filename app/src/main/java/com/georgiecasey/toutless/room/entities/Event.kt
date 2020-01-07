package com.georgiecasey.toutless.room.entities

import androidx.room.*

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
    val venue: String
)

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
}