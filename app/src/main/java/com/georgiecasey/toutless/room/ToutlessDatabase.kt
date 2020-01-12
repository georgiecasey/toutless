package com.georgiecasey.toutless.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.georgiecasey.toutless.api.models.dto.EventDao
import com.georgiecasey.toutless.api.models.dto.Events
import com.georgiecasey.toutless.room.entities.Post

@Database(
    entities = [
        Events.Event::class,
        Post::class
    ],
    version = 1
)
abstract class ToutlessDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: ToutlessDatabase? = null

        fun getDatabase(context: Context): ToutlessDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToutlessDatabase::class.java,
                    "toutless_db"
                )
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
