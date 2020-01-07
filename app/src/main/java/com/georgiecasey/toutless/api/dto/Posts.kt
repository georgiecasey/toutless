package com.georgiecasey.toutless.api.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Posts(
    @Json(name = "posts")
    val posts: List<Post>
) {
    @JsonClass(generateAdapter = true)
    data class Post(
        @Json(name = "author_id")
        val authorId: String,
        @Json(name = "icon")
        val icon: String,
        @Json(name = "post_text")
        val postText: String,
        @Json(name = "post_time")
        val postTime: String,
        @Json(name = "toutless_post_id")
        val toutlessPostId: String
    )
}