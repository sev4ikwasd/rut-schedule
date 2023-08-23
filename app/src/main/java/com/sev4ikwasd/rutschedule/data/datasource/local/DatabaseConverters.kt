package com.sev4ikwasd.rutschedule.data.datasource.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class DatabaseConverters {
    @TypeConverter
    fun timestampToLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun timestampToLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(value) }
    }

    @TypeConverter
    fun localDateTimeToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun stringToStringList(value: String?): List<String> {
        return if (value?.contains(";") == true) value.split(";") else {
            if ((value != null) && (value.isNotBlank()))
                listOf(value)
            else
                listOf()
        }
    }

    @TypeConverter
    fun stringListToString(strings: List<String>?): String? {
        return strings?.joinToString(";")
    }
}