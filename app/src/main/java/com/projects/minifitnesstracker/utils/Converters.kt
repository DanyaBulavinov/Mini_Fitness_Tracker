package com.projects.minifitnesstracker.utils

import androidx.room.TypeConverter
import java.sql.Date
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toDate(dateInSting: String?): Date? {
        return dateInSting?.let { Date.valueOf(it) }
    }
}