package com.sev4ikwasd.rutschedule.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class Group(val groupName: String, val groupId: Int)

data class Schedule(
    val groupId: Int,
    val group: String,
    val classes: List<Class>,
    val dateFrom: LocalDate,
    val dateTo: LocalDate
)

data class Class(
    val type: String,
    val name: String,
    val teachers: List<String>,
    val classrooms: List<Int>,
    val week: Int,
    val dayOfWeek: DayOfWeek,
    val classNumber: Int,
    val timeFrom: LocalTime,
    val timeTo: LocalTime
)