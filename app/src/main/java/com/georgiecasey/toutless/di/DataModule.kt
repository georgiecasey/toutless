package com.georgiecasey.toutless.di

import androidx.room.Room
import com.georgiecasey.toutless.BuildConfig
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.moshi.TimeToMillisAdapter
import com.georgiecasey.toutless.room.ToutlessDatabase
import com.georgiecasey.toutless.room.entities.EventDao
import com.georgiecasey.toutless.room.entities.PostDao
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DataModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(TimeToMillisAdapter())
            .build()

    @Provides
    @Singleton
    fun provideDatabase(context: ToutlessApplication): ToutlessDatabase =
        Room.databaseBuilder(context.applicationContext, ToutlessDatabase::class.java, "toutless.db")
            .apply {
                if (BuildConfig.DEBUG) fallbackToDestructiveMigration()
            }
            .build()

    @Provides
    fun provideEventDao(database: ToutlessDatabase): EventDao =
        database.eventDao()

    @Provides
    fun providePostDao(database: ToutlessDatabase): PostDao =
        database.postDao()
}