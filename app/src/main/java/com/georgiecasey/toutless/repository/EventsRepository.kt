package com.georgiecasey.toutless.repository

import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.room.entities.Event
import com.georgiecasey.toutless.room.entities.EventDao
import javax.inject.Inject

class EventsRepository
@Inject
constructor(
    private val eventDao: EventDao,
    private val toutlessApi: ToutlessApi
) {

    suspend fun getEvents(): List<Event> {
        val events = eventDao.fetchAll()
        if (events.count() == 0) {
            return getEventsRemote()
        }
        return events
    }

    suspend fun getEventsRemote(): List<Event> {
        val events = toutlessApi.getEvents().await()
        if (events.isSuccessful) {
            val eventsEntities = events.body()?.events?.map {
                Event.fromDto(it)
            }
            eventDao.insertOrUpdateAll(eventsEntities)
        }

        return eventDao.fetchAll()
    }

    fun toggleEventFavourite(toutlessThreadId: String, isFavourite: Boolean) {
        eventDao.updateFavourite(toutlessThreadId, isFavourite)
    }
}