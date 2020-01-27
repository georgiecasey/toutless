package com.georgiecasey.toutless.service.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.di.workmanager.WorkerKey
import com.georgiecasey.toutless.utils.extension.fcmToken
import com.georgiecasey.toutless.utils.extension.prefs
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class PostFcmTokenWorker
@Inject
constructor(private val application: ToutlessApplication,
            workerParams: WorkerParameters,
            private val toutlessApi: ToutlessApi) : CoroutineWorker(application, workerParams) {
    override suspend fun doWork(): Result {
        val token = inputData.getString(ARG_FCM_TOKEN)
        val currentToken = inputData.getString(ARG_CURRENT_FCM_TOKEN)
        if (token.isNullOrEmpty() || currentToken.isNullOrEmpty()) return Result.failure()

        val response = toutlessApi.postFcmToken(currentToken, token).await()

        if (response.isSuccessful) {
            applicationContext.prefs.fcmToken = token
            return Result.success()
        }

        return Result.failure()
    }

    @Module
    abstract class Builder {
        @Binds
        @IntoMap
        @WorkerKey(PostFcmTokenWorker::class)
        abstract fun bindPostFcmTokenWorker(worker: PostFcmTokenWorker): CoroutineWorker
    }

    companion object {
        const val ARG_FCM_TOKEN = "arg.fcm_token"
        const val ARG_CURRENT_FCM_TOKEN = "arg.current_fcm_token"
        fun start() {
            WorkManager
                .getInstance()
                .enqueue(OneTimeWorkRequest.from(PostFcmTokenWorker::class.java))
        }
    }
}