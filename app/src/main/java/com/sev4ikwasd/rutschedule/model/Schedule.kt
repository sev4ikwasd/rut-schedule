package com.sev4ikwasd.rutschedule.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Institutes(
    val institutes: List<Institute>
)

@Serializable
data class Institute(
    val name: String,
    val abbreviation: String,
    val courses: List<Course>
)

@Serializable
data class Course(
    val course: Int,
    val specialties: List<Specialty>
)

@Serializable
data class Specialty(
    val name: String,
    val abbreviation: String,
    val groups: List<Group>
)

@Serializable
data class Group(
    val id: Int,
    val name: String
)

@Serializable
data class Timetables(
    val timetables: List<Timetable>
)

@Serializable
data class GroupSchedules(
    val id: Int,
    val groupName: String,
    val schedules: List<GroupSchedule>
)

@Serializable
data class GroupSchedule(
    val timetable: Timetable,
    val periodicContent: PeriodicContent?,
    val nonPeriodicContent: NonPeriodicContent?
)

@Serializable
enum class TimetableType(val type: String) {
    SESSION("SESSION"),
    PERIODIC("PERIODIC"),
    NON_PERIODIC("NON_PERIODIC")
}

@Serializable
data class Timetable(
    val id: String,
    val type: TimetableType,
    val typeName: String,
    @Serializable(LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(LocalDateSerializer::class)
    val endDate: LocalDate,
)

@Serializable
sealed class Content {
    abstract val events: List<Event>
}

@Serializable
data class PeriodicContent(
    override val events: List<PeriodicEvent>,
    val recurrence: FrequencyRule
) : Content()

@Serializable
data class NonPeriodicContent(
    override val events: List<NonPeriodicEvent>
) : Content()

@Serializable
sealed class Event {
    abstract val name: String
    abstract val typeName: String

    @Serializable(LocalDateTimeSerializer::class)
    abstract val startDatetime: LocalDateTime

    @Serializable(LocalDateTimeSerializer::class)
    abstract val endDatetime: LocalDateTime

    @Serializable(LecturerListSerializer::class)
    abstract val lecturers: List<String>

    @Serializable(RoomListSerializer::class)
    abstract val rooms: List<String>

    @Serializable(GroupListSerializer::class)
    abstract val groups: List<String>
}

@Serializable
enum class Frequency(val frequency: String) {
    WEEKLY("WEEKLY")
}

@Serializable
data class FrequencyRule(
    val frequency: Frequency,
    val interval: Int
)

@Serializable
data class PeriodicEvent(
    override val name: String,
    override val typeName: String,
    @Serializable(LecturerListSerializer::class)
    override val lecturers: List<String>,
    @Serializable(RoomListSerializer::class)
    override val rooms: List<String>,
    @Serializable(GroupListSerializer::class)
    override val groups: List<String>,
    @Serializable(LocalDateTimeSerializer::class)
    override val startDatetime: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class)
    override val endDatetime: LocalDateTime,
    val timeSlotName: String,
    val periodNumber: Int,
    val recurrenceRule: FrequencyRule
) : Event()

@Serializable
data class NonPeriodicEvent(
    override val name: String,
    override val typeName: String,
    @Serializable(LecturerListSerializer::class)
    override val lecturers: List<String>,
    @Serializable(RoomListSerializer::class)
    override val rooms: List<String>,
    @Serializable(GroupListSerializer::class)
    override val groups: List<String>,
    @Serializable(LocalDateTimeSerializer::class)
    override val startDatetime: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class)
    override val endDatetime: LocalDateTime,
) : Event()
