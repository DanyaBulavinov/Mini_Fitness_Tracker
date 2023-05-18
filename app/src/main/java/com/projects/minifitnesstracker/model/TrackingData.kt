package com.projects.minifitnesstracker.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
@Entity(tableName = "tracking_data")
data class TrackingData(

    var weight: Double? = null,
    var height: Double? = null,
    @ColumnInfo(name = "glasses_of_water", defaultValue = "0")
    var glassesOfWater: Int? = 0,
    @ColumnInfo(name = "amount_of_steps", defaultValue = "0")
    var amountOfSteps: Int? = 0,
    @Embedded
    var sleep: Sleep? = null,
    @ColumnInfo(name = "amount_of_sleep", defaultValue = "0")
    var amountOfSleep: Int? = 0,
    @PrimaryKey(autoGenerate = false)
    val date: Date
) : Parcelable
