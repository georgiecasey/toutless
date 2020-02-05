package com.georgiecasey.toutless.repository

import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.room.entities.EventDao
import com.georgiecasey.toutless.room.entities.Post
import com.georgiecasey.toutless.room.entities.PostDao
import com.georgiecasey.toutless.ui.BuyingOrSellingField
import javax.inject.Inject

class EventPostsRepository
@Inject
constructor(
    private val eventDao: EventDao,
    private val postDao: PostDao,
    private val toutlessApi: ToutlessApi
) {

    fun getCurrentEvent(toutlessThreadId: String) =
        eventDao.fetchById(toutlessThreadId)

    fun updateEventBuyingOrSelling(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField) {
        eventDao.updateBuyingOrSelling(toutlessThreadId, buyingOrSelling)
    }

    suspend fun getEventPosts(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField): List<Post> {
        val posts = postDao.fetchAllByThreadId(toutlessThreadId)
        if (posts.count() == 0) {
            return getEventPostsRemote(toutlessThreadId, buyingOrSelling)
        }
        return filterPosts(posts, buyingOrSelling)
    }

    suspend fun getEventPostsRemote(toutlessThreadId: String, buyingOrSelling: BuyingOrSellingField): List<Post> {
        val posts = toutlessApi.getEventPosts(toutlessThreadId).await()
        if (posts.isSuccessful) {
            val postsEntities = posts.body()?.posts?.map {
                Post.fromDto(it)
            }
            postDao.insertAll(postsEntities)
            postsEntities?.let {
                return filterPosts(it, buyingOrSelling)
            }
        }
        return emptyList<Post>()
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