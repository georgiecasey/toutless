package com.georgiecasey.toutless.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.room.entities.Event
import com.georgiecasey.toutless.room.entities.EventDao
import com.georgiecasey.toutless.room.entities.Post
import com.georgiecasey.toutless.room.entities.PostDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EventPostsViewModel
@Inject
constructor(
    private val eventDao: EventDao,
    private val postDao: PostDao,
    private val application: ToutlessApplication,
    private val toutlessApi: ToutlessApi
) : ViewModel() {
    private val _eventPostsListLiveData = MutableLiveData<List<Post>>()
    val eventPostsListLiveData: LiveData<List<Post>>
        get() = _eventPostsListLiveData
    private val _currentEvent = MutableLiveData<Event>()
    val currentEvent: LiveData<Event>
        get() = _currentEvent

    fun getCurrentEvent(toutlessThreadId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val event = eventDao.fetchById(toutlessThreadId)
            _currentEvent.postValue(event)
        }
    }

    fun updateEventBuyingOrSelling(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField) {
        viewModelScope.launch(Dispatchers.Default) {
            eventDao.updateBuyingOrSelling(toutlessThreadId, buyingOrSelling)
        }
    }

    fun getEventPosts(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField) =
        viewModelScope.launch(Dispatchers.Default) {
            Timber.d("postDao.getEventPosts")
            val posts = postDao.fetchAllByThreadId(toutlessThreadId)
            if (posts.count() == 0) getEventPostsRemote(toutlessThreadId, buyingOrSelling)
            filterPosts(posts, buyingOrSelling)
        }

    fun getEventPostsRemote(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField) {
        viewModelScope.launch(Dispatchers.Default) {
            val posts = toutlessApi.getEventPosts(toutlessThreadId).await()
            if (posts.isSuccessful) {
                val postsEntities = posts.body()?.posts?.map {
                    Post.fromDto(it)
                }
                postDao.insertAll(postsEntities)
                postsEntities?.let {
                    filterPosts(it, buyingOrSelling)
                }
            }
        }
    }

    fun filterPosts(posts: List<Post>, buyingOrSelling: BuyingOrSellingField) {
        when (buyingOrSelling) {
            is BuyingOrSellingField.Buying ->
                _eventPostsListLiveData.postValue(posts.filter { it.postSmilies == "forsale"})
            is BuyingOrSellingField.Selling ->
                _eventPostsListLiveData.postValue(posts.filter { it.postSmilies == "wanted"})
        }
    }
}
