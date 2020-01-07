package com.georgiecasey.toutless.api

import com.georgiecasey.toutless.api.dto.Posts
import com.georgiecasey.toutless.api.models.dto.Events
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ToutlessApi {
    @GET("events/")
    fun getEvents(): Deferred<Response<Events>>

    @GET("event/{toutless_thread_id}")
    fun getEventPosts(@Path("toutless_thread_id") toutlessThreadId: String): Deferred<Response<Posts>>
}