package com.georgiecasey.toutless.service.workers

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

class PostEventBuyingOrSellingWorker
@Inject
constructor(application: ToutlessApplication,
            workerParams: WorkerParameters,
            private val toutlessApi: ToutlessApi
) : CoroutineWorker(application, workerParams) {
    override suspend fun doWork(): Result {
        val token = applicationContext.prefs.fcmToken
        val toutlessThreadId = inputData.getString(ARG_TOUTLESS_THREAD_ID)
        val buyingOrSelling = inputData.getString(ARG_BUYING_OR_SELLING)
        if (buyingOrSelling.isNullOrEmpty() ||
            token.isNullOrEmpty() ||
            toutlessThreadId.isNullOrEmpty()) {
            return Result.failure()
        }

        val response = toutlessApi.postEventBuyingOrSelling(token, toutlessThreadId, buyingOrSelling).await()

        if (response.isSuccessful) {
            return Result.success()
        }

        return Result.failure()
    }

    @Module
    abstract class Builder {
        @Binds
        @IntoMap
        @WorkerKey(PostEventBuyingOrSellingWorker::class)
        abstract fun bindPostEventFavouriteWorker(worker: PostEventBuyingOrSellingWorker): CoroutineWorker
    }

    companion object {
        const val ARG_TOUTLESS_THREAD_ID = "arg.toutless_thread_id"
        const val ARG_BUYING_OR_SELLING = "arg.buying_or_selling"
    }
}