package com.georgiecasey.toutless.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.room.entities.Event
import com.georgiecasey.toutless.room.entities.EventDao
import com.georgiecasey.toutless.service.workers.PostEventFavouriteWorker
import javax.inject.Inject

class EventsRepository
@Inject
constructor(
    private val application: ToutlessApplication,
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
        val postWorkRequest = OneTimeWorkRequestBuilder<PostEventFavouriteWorker>()
            .setInputData(workDataOf(PostEventFavouriteWorker.ARG_TOUTLESS_THREAD_ID to toutlessThreadId, PostEventFavouriteWorker.ARG_IS_FAVOURITE to isFavourite))
            .build()
        WorkManager.getInstance(application).enqueue(postWorkRequest)
    }
}