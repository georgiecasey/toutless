package com.georgiecasey.toutless.utils.extension

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.georgiecasey.toutless.BuildConfig

val Context.prefs: SharedPreferences
    get() = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

private const val FCM_TOKEN = "fcm_token"

var SharedPreferences.fcmToken: String?
    get() = getString(FCM_TOKEN, null)
    set(value) {
        edit { putString(FCM_TOKEN, value) }
    }
