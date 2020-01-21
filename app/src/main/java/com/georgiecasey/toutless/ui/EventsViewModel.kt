package com.georgiecasey.toutless.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.room.entities.Event
import com.georgiecasey.toutless.room.entities.EventDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EventsViewModel
@Inject
constructor(
    private val eventDao: EventDao,
    private val application: ToutlessApplication,
    private val toutlessApi: ToutlessApi
) : ViewModel() {
    private val _eventsListLiveData = MutableLiveData<List<Event>>()
    val eventsListLiveData: LiveData<List<Event>>
        get() = _eventsListLiveData

    fun getEvents() =
        viewModelScope.launch(Dispatchers.Default) {
            Timber.d("eventDao.fetchAll")
            val events = eventDao.fetchAll()
            if (events.count() == 0) getEventsRemote()
            _eventsListLiveData.postValue(events)
        }

    fun getEventsRemote() {
        viewModelScope.launch(Dispatchers.Default) {
            val events = toutlessApi.getEvents().await()
            if (events.isSuccessful) {
                val eventsEntities = events.body()?.events?.map {
                    Event.fromDto(it)
                }
                eventDao.insertOrUpdateAll(eventsEntities)
                _eventsListLiveData.postValue(eventsEntities)
            }
        }
    }
}
