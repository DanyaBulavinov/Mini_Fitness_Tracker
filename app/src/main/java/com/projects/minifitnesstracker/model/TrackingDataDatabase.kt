package com.projects.minifitnesstracker.model

import android.content.Context
import androidx.room.*
import com.projects.minifitnesstracker.utils.Converters

@Database(entities = [TrackingData::class], version = 4, autoMigrations = [
    AutoMigration(
        from = 1,
        to = 2
    )
    ,
    AutoMigration(
        from = 2,
        to = 3
    )
    ,
    AutoMigration(
        from = 3,
        to = 4
    )
]
)
@TypeConverters(Converters::class)
abstract class TrackingDataDatabase : RoomDatabase() {
    abstract fun TrackingDataDao(): TrackingDataDao

    companion object {
        @Volatile
        private var instance: TrackingDataDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context)
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, TrackingDataDatabase::class.java, "tracking_data_database"
        ).build()
    }
}