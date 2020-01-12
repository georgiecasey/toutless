package com.georgiecasey.toutless.di

import androidx.room.Room
import com.georgiecasey.toutless.BuildConfig
import com.georgiecasey.toutless.ToutlessApplication
import com.georgiecasey.toutless.api.models.dto.EventDao
import com.georgiecasey.toutless.room.ToutlessDatabase
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
    fun provideToutlessDao(database: ToutlessDatabase): EventDao =
        database.eventDao()

}