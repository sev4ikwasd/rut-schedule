package com.sev4ikwasd.rutschedule.data.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.sev4ikwasd.rutschedule.model.Class
import com.sev4ikwasd.rutschedule.model.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Entity(indices = [Index(value = ["groupId"], unique = true)])
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Int,
    val group: String,
    val dateFrom: LocalDate,
    val dateTo: LocalDate
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = ScheduleEntity::class,
        parentColumns = ["id"],
        childColumns = ["scheduleId"],
        onDelete = CASCADE
    )]
)
data class ClassEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    var scheduleId: Long = 0,
    val type: String,
    val name: String,
    val teachers: List<String>,
    val classrooms: List<String>,
    val week: Int,
    val dayOfWeek: DayOfWeek,
    val classNumber: Int,
    val timeFrom: LocalTime,
    val timeTo: LocalTime
)

data class ScheduleWithClassesEntity(
    @Embedded
    val schedule: ScheduleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "scheduleId"
    )
    val classes: List<ClassEntity>
)

fun ClassEntity.toDomain(): Class {
    return Class(type, name, teachers, classrooms, week, dayOfWeek, classNumber, timeFrom, timeTo)
}

fun Class.fromDomain(): ClassEntity {
    return ClassEntity(
        type = type,
        name = name,
        teachers = teachers,
        classrooms = classrooms,
        week = week,
        dayOfWeek = dayOfWeek,
        classNumber = classNumber,
        timeFrom = timeFrom,
        timeTo = timeTo
    )
}

fun ScheduleWithClassesEntity.toDomain(): Schedule {
    return Schedule(
        schedule.groupId,
        schedule.group,
        classes.map(ClassEntity::toDomain),
        schedule.dateFrom,
        schedule.dateTo
    )
}

fun Schedule.fromDomain(): ScheduleWithClassesEntity {
    return ScheduleWithClassesEntity(
        ScheduleEntity(groupId = groupId, group = group, dateFrom = dateFrom, dateTo = dateTo),
        classes.map(Class::fromDomain)
    )
}