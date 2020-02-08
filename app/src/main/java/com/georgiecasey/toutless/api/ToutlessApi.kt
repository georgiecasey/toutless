package com.georgiecasey.toutless.api

import com.georgiecasey.toutless.api.dto.Events
import com.georgiecasey.toutless.api.dto.Posts
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface ToutlessApi {
    @GET("events/")
    fun getEvents(): Deferred<Response<Events>>

    @GET("event/{toutless_thread_id}")
    fun getEventPosts(@Path("toutless_thread_id") toutlessThreadId: String): Deferred<Response<Posts>>

    @FormUrlEncoded
    @POST("update/")
    fun postFcmToken(@Field("current_token") currentToken: String, @Field("token") token: String): Deferred<Response<Void>>

    @FormUrlEncoded
    @POST("update/")
    fun postEventFavourite(@Field("token") token: String, @Field("toutless_thread_id") toutlessThreadId: String, @Field("is_favourite") isFavourite: Boolean): Deferred<Response<Void>>

    @FormUrlEncoded
    @POST("update/")
    fun postEventBuyingOrSelling(@Field("token") token: String, @Field("toutless_thread_id") toutlessThreadId: String, @Field("buying_or_selling") buyingOrSelling: String): Deferred<Response<Void>>
}