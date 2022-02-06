package com.georgiecasey.toutless.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.Resource
import com.georgiecasey.toutless.api.ResponseHandler
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
    private val toutlessApi: ToutlessApi,
    private val responseHandler: ResponseHandler
) {
    suspend fun getEvents(): Resource<List<Event>> {
        val events = eventDao.fetchAll()
        if (events.count() == 0) {
            return getEventsRemote()
        }
        return responseHandler.handleSuccess(events)
    }

    suspend fun getEventsRemote(): Resource<List<Event>> {
        try {
            val events = toutlessApi.getEvents().await()
            val eventsEntities = events.body()?.events?.map {
                Event.fromDto(it)
            }
            eventDao.insertOrUpdateAll(eventsEntities)
            return responseHandler.handleSuccess(eventDao.fetchAll())
        } catch (e: Exception) {
            return responseHandler.handleException(e)
        }
    }

    fun toggleEventFavourite(toutlessThreadId: String, isFavourite: Boolean) {
        eventDao.updateFavourite(toutlessThreadId, isFavourite)
        val postWorkRequest = OneTimeWorkRequestBuilder<PostEventFavouriteWorker>()
            .setInputData(workDataOf(PostEventFavouriteWorker.ARG_TOUTLESS_THREAD_ID to toutlessThreadId, PostEventFavouriteWorker.ARG_IS_FAVOURITE to isFavourite))
            .build()
        WorkManager.getInstance(application).enqueue(postWorkRequest)
    }
}