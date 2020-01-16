package com.georgiecasey.toutless.api.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson


class TimeToMillisAdapter {
    @FromJson
    @TimeToMillis
    fun fromJson(dateTime: Long): Long = dateTime * 1000

    @ToJson
    fun toJson(@TimeToMillis dateTime: Long) = dateTime / 1000
}

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class TimeToMillis