package com.georgiecasey.toutless.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.georgiecasey.toutless.ui.EventPostsViewModel
import com.georgiecasey.toutless.ui.EventsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(EventsViewModel::class)
    abstract fun provideEventsViewModel(viewModel: EventsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EventPostsViewModel::class)
    abstract fun provideEventPostsViewModel(viewModel: EventPostsViewModel): ViewModel
}