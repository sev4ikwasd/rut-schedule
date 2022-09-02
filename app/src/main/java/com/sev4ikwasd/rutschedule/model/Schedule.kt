package com.sev4ikwasd.rutschedule.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class Schedule(
    val group: String,
    val classes: List<Class>,
    val classNumberToTimePeriod: Map<Int, TimePeriod>,
    val dateFrom: LocalDate,
    val dateTo: LocalDate
)

data class Class(
    val type: String,
    val name: String,
    val teacher: String,
    val classroom: String,
    val week: Int,
    val dayOfWeek: DayOfWeek,
    val classNumber: Int
)

data class TimePeriod(
    val timeFrom: LocalTime,
    val timeTo: LocalTime
)