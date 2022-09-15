package com.sev4ikwasd.rutschedule.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class Group(val groupName: String, val groupId: Int)

data class Schedule(
    val group: String,
    val classes: List<Class>,
    val dateFrom: LocalDate,
    val dateTo: LocalDate
) {
    fun isEmpty(): Boolean {
        return (group == "") && (classes.isEmpty())
    }

    companion object {
        fun empty(): Schedule {
            return Schedule("", emptyList(), LocalDate.now(), LocalDate.now())
        }
    }
}

data class Class(
    val type: String,
    val name: String,
    val teachers: List<String>,
    val classroom: String,
    val week: Int,
    val dayOfWeek: DayOfWeek,
    val classNumber: Int,
    val timeFrom: LocalTime,
    val timeTo: LocalTime
)