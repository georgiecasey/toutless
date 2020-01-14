package com.georgiecasey.toutless.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.georgiecasey.toutless.room.entities.Event
import com.georgiecasey.toutless.room.entities.EventDao
import com.georgiecasey.toutless.room.entities.Post
import com.georgiecasey.toutless.room.entities.PostDao
import com.georgiecasey.toutless.room.typeconverters.BuyingOrSellingSealedClassTypeConverter

@Database(
    entities = [
        Event::class,
        Post::class
    ],
    version = 1
)
@TypeConverters(
    BuyingOrSellingSealedClassTypeConverter::class
)
abstract class ToutlessDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun postDao(): PostDao

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
