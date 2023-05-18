package com.projects.minifitnesstracker.model

import androidx.room.ColumnInfo
import java.sql.Date

data class DateStepsTuple(
    @ColumnInfo(name = "first") val date: Date,
    @ColumnInfo(name = "second") val amountOfSteps: Int
)
