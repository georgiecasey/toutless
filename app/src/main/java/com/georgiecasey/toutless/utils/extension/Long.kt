package com.georgiecasey.toutless.utils.extension

import android.text.format.DateUtils

fun Long.toRelativeTime(): String {
    return DateUtils.getRelativeTimeSpanString(this, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()
}
