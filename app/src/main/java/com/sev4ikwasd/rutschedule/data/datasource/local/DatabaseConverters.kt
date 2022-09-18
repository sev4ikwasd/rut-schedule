package com.sev4ikwasd.rutschedule.data.datasource.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

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
    fun secondsToDate(value: Int?): LocalTime? {
        return value?.let { LocalTime.ofSecondOfDay(it.toLong()) }
    }

    @TypeConverter
    fun dateToSeconds(date: LocalTime?): Int? {
        return date?.toSecondOfDay()
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