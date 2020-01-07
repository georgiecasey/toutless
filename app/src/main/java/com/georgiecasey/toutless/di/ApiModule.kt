package com.georgiecasey.toutless.di

import com.georgiecasey.toutless.api.ToutlessApi
import com.georgiecasey.toutless.utils.Config
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object ApiModule {
    @Provides
    fun provideOkHttp(): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .dispatcher(Dispatcher().apply {
                maxRequestsPerHost = 1
            })

    @Provides
    fun provideRetrofit(moshi: Moshi): Retrofit.Builder =
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient.Builder): ToutlessApi =
        retrofit
            .baseUrl(Config.API_BASE_URL)
            .client(okHttpClient
                .build())
            .build()
            .create(ToutlessApi::class.java)
}