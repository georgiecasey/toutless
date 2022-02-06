package com.georgiecasey.toutless.ui

import androidx.lifecycle.*
import com.georgiecasey.toutless.api.Resource
import com.georgiecasey.toutless.repository.EventsRepository
import com.georgiecasey.toutless.room.entities.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EventsViewModel
@Inject
constructor(
    private val eventRepo: EventsRepository
) : ViewModel() {
    private val _eventsListLiveData = MutableLiveData<Resource<List<Event>>>()
    val eventsListLiveData: LiveData<Resource<List<Event>>>
        get() = _eventsListLiveData

    fun getEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            _eventsListLiveData.postValue(eventRepo.getEvents())
        }
    }

    fun getEventsRemote() {
        viewModelScope.launch(Dispatchers.IO) {
            _eventsListLiveData.postValue(eventRepo.getEventsRemote())
        }
    }

    fun toggleEventFavourite(toutlessThreadId: String, isFavourite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            eventRepo.toggleEventFavourite(toutlessThreadId, isFavourite)
            getEvents()
        }
    }
}
