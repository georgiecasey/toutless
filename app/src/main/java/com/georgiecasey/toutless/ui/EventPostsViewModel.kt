package com.georgiecasey.toutless.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiecasey.toutless.api.Resource
import com.georgiecasey.toutless.repository.EventPostsRepository
import com.georgiecasey.toutless.room.entities.Event
import com.georgiecasey.toutless.room.entities.Post
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EventPostsViewModel
@Inject
constructor(
    private val postRepo: EventPostsRepository
) : ViewModel() {
    private val _eventPostsListLiveData = MutableLiveData<Resource<List<Post>>>()
    val eventPostsListLiveData: LiveData<Resource<List<Post>>>
        get() = _eventPostsListLiveData
    private val _currentEvent = MutableLiveData<Event>()
    val currentEvent: LiveData<Event>
        get() = _currentEvent
    // georgie: probably don't need this when we have Resource class and ResponseHandler
    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }

    fun getCurrentEvent(toutlessThreadId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentEvent.postValue(postRepo.getCurrentEvent(toutlessThreadId))
        }
    }

    fun updateEventBuyingOrSelling(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepo.updateEventBuyingOrSelling(toutlessThreadId, buyingOrSelling)
        }
    }

    fun getEventPosts(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _eventPostsListLiveData.postValue(postRepo.getEventPosts(toutlessThreadId, buyingOrSelling))
        }

    fun getEventPostsRemote(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _eventPostsListLiveData.postValue(postRepo.getEventPostsRemote(toutlessThreadId, buyingOrSelling))
        }
    }
}
