package com.georgiecasey.toutless.di

import com.georgiecasey.toutless.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBuilder {
    @ContributesAndroidInjector
    fun bindSplash(): MainActivity
}