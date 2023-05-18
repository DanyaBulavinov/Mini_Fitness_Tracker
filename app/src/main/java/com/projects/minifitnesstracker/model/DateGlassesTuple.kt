package com.projects.minifitnesstracker.model

import androidx.room.ColumnInfo
import java.sql.Date

data class DateGlassesTuple(
    @ColumnInfo(name = "first") val date: Date,
    @ColumnInfo(name = "second") val amountOfGlasses: Int
)

