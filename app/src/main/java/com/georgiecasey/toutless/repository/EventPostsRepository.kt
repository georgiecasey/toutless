package com.georgiecasey.toutless.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.Resource
import com.georgiecasey.toutless.api.ResponseHandler
import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.room.entities.EventDao
import com.georgiecasey.toutless.room.entities.Post
import com.georgiecasey.toutless.room.entities.PostDao
import com.georgiecasey.toutless.service.workers.PostEventBuyingOrSellingWorker
import com.georgiecasey.toutless.ui.BuyingOrSellingField
import javax.inject.Inject

class EventPostsRepository
@Inject
constructor(
    private val application: ToutlessApplication,
    private val eventDao: EventDao,
    private val postDao: PostDao,
    private val toutlessApi: ToutlessApi,
    private val responseHandler: ResponseHandler
) {

    fun getCurrentEvent(toutlessThreadId: String) =
        eventDao.fetchById(toutlessThreadId)

    fun updateEventBuyingOrSelling(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField) {
        eventDao.updateBuyingOrSelling(toutlessThreadId, buyingOrSelling)
        val postWorkRequest = OneTimeWorkRequestBuilder<PostEventBuyingOrSellingWorker>()
            .setInputData(workDataOf(PostEventBuyingOrSellingWorker.ARG_TOUTLESS_THREAD_ID to toutlessThreadId, PostEventBuyingOrSellingWorker.ARG_BUYING_OR_SELLING to buyingOrSelling?.roomString))
            .build()
        WorkManager.getInstance(application).enqueue(postWorkRequest)
    }

    suspend fun getEventPosts(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField): Resource<List<Post>> {
        val posts = postDao.fetchAllByThreadId(toutlessThreadId)
        if (posts.count() == 0) {
            return getEventPostsRemote(toutlessThreadId, buyingOrSelling)
        }
        return responseHandler.handleSuccess(filterPosts(posts, buyingOrSelling))
    }

    suspend fun getEventPostsRemote(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField): Resource<List<Post>> {
        return try {
            val posts = toutlessApi.getEventPosts(toutlessThreadId).await()
            val postsEntities = posts.body()?.posts?.map {
                Post.fromDto(it)
            }
            postDao.insertAll(postsEntities)
            responseHandler.handleSuccess(filterPosts(postsEntities!!, buyingOrSelling))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    fun filterPosts(posts: List<Post>, buyingOrSelling: BuyingOrSellingField): List<Post> {
        when (buyingOrSelling) {
            is BuyingOrSellingField.Buying ->
                return posts
                    .filter{ it.postSmilies == "forsale" }
                    .sortedByDescending { it.postTime }
            is BuyingOrSellingField.Selling ->
                return posts
                    .filter{ it.postSmilies == "wanted" }
                    .sortedByDescending { it.postTime }
        }
    }
}