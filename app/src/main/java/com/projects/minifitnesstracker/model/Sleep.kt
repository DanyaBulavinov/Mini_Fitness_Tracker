package com.projects.minifitnesstracker.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Sleep(
    @ColumnInfo(name = "beginning_of_sleep")
    val beginningOfSleep: LocalDateTime,
    @ColumnInfo(name = "ending_of_sleep")
    val endingOfSleep: LocalDateTime
) : Parcelable
