package com.georgiecasey.toutless.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Events(
    @Json(name = "events")
    val events: List<Event>,
    @Json(name = "last_checked")
    val lastChecked: String
) {
    @JsonClass(generateAdapter = true)
    data class Event(
        @Json(name = "toutless_thread_id")
        val toutlessThreadId: String,
        @Json(name = "event_dates")
        val eventDates: String,
        @Json(name = "event_name")
        val eventName: String,
        @Json(name = "number_of_posts")
        val numberOfPosts: Int,
        @Json(name = "venue")
        val venue: String
    )
}