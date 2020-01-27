package com.georgiecasey.toutless.di

import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.di.workmanager.DaggerWorkerFactory
import com.georgiecasey.toutless.di.workmanager.WorkerSubComponent
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ActivityBuilder::class,
    AndroidInjectionModule::class,
    ApiModule::class,
    DataModule::class,
    FragmentBuilder::class,
    ViewModelModule::class])
interface AppComponent : AndroidInjector<ToutlessApplication> {
    fun daggerWorkerFactory(): DaggerWorkerFactory

    fun workerSubcomponentBuilder(): WorkerSubComponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: ToutlessApplication): Builder

        fun build(): AppComponent
    }
}