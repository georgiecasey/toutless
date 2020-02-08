package com.georgiecasey.toutless.di.workmanager

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.georgiecasey.toutless.service.workers.PostEventBuyingOrSellingWorker
import com.georgiecasey.toutless.service.workers.PostEventFavouriteWorker
import com.georgiecasey.toutless.service.workers.PostFcmTokenWorker
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Provider

@Subcomponent(modules = [
    PostFcmTokenWorker.Builder::class,
    PostEventFavouriteWorker.Builder::class,
    PostEventBuyingOrSellingWorker.Builder::class
])
interface WorkerSubComponent {
    fun workers(): Map<Class<out CoroutineWorker>, Provider<CoroutineWorker>>

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun workerParameters(param: WorkerParameters): Builder

        fun build(): WorkerSubComponent
    }
}