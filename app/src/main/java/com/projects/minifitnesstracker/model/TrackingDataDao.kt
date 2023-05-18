package com.projects.minifitnesstracker.model

import androidx.room.*
import java.sql.Date
import java.time.LocalDateTime

@Dao
interface TrackingDataDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trackingData: TrackingData)

    @Update
    suspend fun update(trackingData: TrackingData)

    @Query("SELECT * FROM tracking_data WHERE date = :date")
    suspend fun getOneRecord(date: Date): TrackingData?

    @Query("SELECT beginning_of_sleep FROM tracking_data WHERE date = :date")
    suspend fun getBeginningTimeSleep(date: Date): LocalDateTime?

    @Query("SELECT ending_of_sleep FROM tracking_data WHERE date = :date")
    suspend fun getEndingTimeSleep(date: Date): LocalDateTime?

    @Query("SELECT date as first, amount_of_steps as second FROM tracking_data WHERE strftime('%Y-%m', date) = :month GROUP BY date ORDER BY date ASC")
    suspend fun getAllStepRecordsPerMonth(month: String): List<DateStepsTuple>

    @Query("SELECT date as first, amount_of_sleep as second FROM tracking_data WHERE strftime('%Y-%m', date) = :month GROUP BY date ORDER BY date ASC")
    suspend fun getAllSleepRecordsPerMonth(month: String): List<DateSleepTuple>

    @Query("SELECT date FROM tracking_data")
    suspend fun getAllDates(): List<Date>

    @Query("SELECT date as first, glasses_of_water as second FROM tracking_data WHERE strftime('%Y-%m', date) = :month GROUP BY date ORDER BY date ASC")
    suspend fun getAllGlassesOfWaterRecords(month: String): List<DateGlassesTuple>
}