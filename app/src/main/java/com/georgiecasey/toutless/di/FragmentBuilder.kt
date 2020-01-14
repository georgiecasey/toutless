package com.georgiecasey.toutless.di

import com.georgiecasey.toutless.ui.EventPostsListFragment
import com.georgiecasey.toutless.ui.EventsListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentBuilder {
    @ContributesAndroidInjector
    fun bindEventsListFragment(): EventsListFragment

    @ContributesAndroidInjector
    fun bindEventPostsListFragment(): EventPostsListFragment
}