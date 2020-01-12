package com.georgiecasey.toutless.di

import com.georgiecasey.toutless.ToutlessApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ActivityBuilder::class,
    AndroidInjectionModule::class,
    ApiModule::class,
    DataModule::class,
    FragmentBuilder::class,
    ViewModelModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: ToutlessApplication): Builder

        fun build(): AppComponent
    }

    fun inject(application: ToutlessApplication)
}