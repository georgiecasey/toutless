package com.georgiecasey.toutless.service.workers

import android.content.Context
import androidx.work.*
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.di.workmanager.WorkerKey
import com.georgiecasey.toutless.utils.extension.fcmToken
import com.georgiecasey.toutless.utils.extension.prefs
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class PostEventFavouriteWorker
@Inject
constructor(application: ToutlessApplication,
            workerParams: WorkerParameters,
            private val toutlessApi: ToutlessApi) : CoroutineWorker(application, workerParams) {
    override suspend fun doWork(): Result {
        val token = applicationContext.prefs.fcmToken
        val toutlessThreadId = inputData.getString(ARG_TOUTLESS_THREAD_ID)
        if (inputData.hasKeyWithValueOfType<Boolean>(ARG_IS_FAVOURITE) == false ||
            token.isNullOrEmpty() ||
            toutlessThreadId.isNullOrEmpty()) {
            return Result.failure()
        }

        val isFavourite = inputData.getBoolean(ARG_IS_FAVOURITE, false)

        val response = toutlessApi.postEventFavourite(token, toutlessThreadId, isFavourite).await()

        if (response.isSuccessful) {
            return Result.success()
        }

        return Result.failure()
    }

    @Module
    abstract class Builder {
        @Binds
        @IntoMap
        @WorkerKey(PostEventFavouriteWorker::class)
        abstract fun bindPostEventFavouriteWorker(worker: PostEventFavouriteWorker): CoroutineWorker
    }

    companion object {
        const val ARG_TOUTLESS_THREAD_ID = "arg.toutless_thread_id"
        const val ARG_IS_FAVOURITE = "arg.is_favourite"
    }
}