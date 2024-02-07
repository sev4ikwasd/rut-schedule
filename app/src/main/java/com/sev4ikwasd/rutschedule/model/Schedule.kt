package com.sev4ikwasd.rutschedule.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Serializable
data class Institutes(
    val institutes: List<Institute>
)

@Serializable
data class Institute(
    val name: String, val abbreviation: String, val courses: List<Course>
)

@Serializable
data class Course(
    val course: Int, val specialties: List<Specialty>
)

@Serializable
data class Specialty(
    val name: String, val abbreviation: String, val groups: List<Group>
)

@Serializable
data class Group(
    val id: Int, val name: String
)

@Serializable
data class Timetables(
    val timetables: List<Timetable>
)

@Serializable
enum class TimetableType(val type: String) {
    SESSION("SESSION"), PERIODIC("PERIODIC"), NON_PERIODIC("NON_PERIODIC")
}

@Serializable
data class Timetable(
    val id: String,
    val type: TimetableType,
    val typeName: String,
    @Serializable(LocalDateSerializer::class) val startDate: LocalDate,
    @Serializable(LocalDateSerializer::class) val endDate: LocalDate,
)

data class GroupSchedules(
    val id: Int, val groupName: String, val schedules: List<GroupSchedule>
)


data class GroupSchedule(
    val timetable: Timetable,
    val periodicContent: PeriodicContent?,
    val nonPeriodicContent: NonPeriodicContent?
)

sealed class Content {
    abstract val events: List<Event>
}

data class PeriodicContent(
    override val events: List<PeriodicEvent>, val recurrence: FrequencyRuleFull
) : Content()

data class NonPeriodicContent(
    override val events: List<NonPeriodicEvent>
) : Content()

sealed class Event {
    abstract val name: String
    abstract val typeName: String
    abstract val startDatetime: LocalDateTime
    abstract val endDatetime: LocalDateTime
    abstract val lecturers: List<String>
    abstract val rooms: List<String>
    abstract val groups: List<String>
}

@Serializable
enum class Frequency(val frequency: String) {
    WEEKLY("WEEKLY")
}

@Serializable
data class FrequencyRule(
    val frequency: Frequency, val interval: Int
)

data class FrequencyRuleFull(
    val frequency: Frequency, val interval: Int, val firstInterval: Int
)

data class PeriodicEvent(
    override val name: String,
    override val typeName: String,
    override val lecturers: List<String>,
    override val rooms: List<String>,
    override val groups: List<String>,
    override val startDatetime: LocalDateTime,
    override val endDatetime: LocalDateTime,
    val timeSlotName: String,
    val periodNumber: Int,
    val recurrenceRule: FrequencyRule
) : Event()

data class NonPeriodicEvent(
    override val name: String,
    override val typeName: String,
    override val lecturers: List<String>,
    override val rooms: List<String>,
    override val groups: List<String>,
    override val startDatetime: LocalDateTime,
    override val endDatetime: LocalDateTime,
) : Event()

@Serializable
data class GroupScheduleSerialized(
    val timetable: Timetable,
    val periodicContent: PeriodicContentSerialized?,
    val nonPeriodicContent: NonPeriodicContentSerialized?
)

@Serializable
sealed class ContentSerialized {
    abstract val events: List<EventSerialized>
}

@Serializable
data class PeriodicContentSerialized(
    override val events: List<PeriodicEventSerialized>, val recurrence: FrequencyRuleFullSerialized
) : ContentSerialized()

@Serializable
data class NonPeriodicContentSerialized(
    override val events: List<NonPeriodicEventSerialized>
) : ContentSerialized()

@Serializable
sealed class EventSerialized {
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
data class FrequencyRuleFullSerialized(
    val frequency: Frequency, val interval: Int, val currentNumber: Int
)

@Serializable
data class PeriodicEventSerialized(
    override val name: String,
    override val typeName: String,
    @Serializable(LecturerListSerializer::class) override val lecturers: List<String>,
    @Serializable(RoomListSerializer::class) override val rooms: List<String>,
    @Serializable(GroupListSerializer::class) override val groups: List<String>,
    @Serializable(LocalDateTimeSerializer::class) override val startDatetime: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class) override val endDatetime: LocalDateTime,
    val timeSlotName: String,
    val periodNumber: Int,
    val recurrenceRule: FrequencyRule
) : EventSerialized()

@Serializable
data class NonPeriodicEventSerialized(
    override val name: String,
    override val typeName: String,
    @Serializable(LecturerListSerializer::class) override val lecturers: List<String>,
    @Serializable(RoomListSerializer::class) override val rooms: List<String>,
    @Serializable(GroupListSerializer::class) override val groups: List<String>,
    @Serializable(LocalDateTimeSerializer::class) override val startDatetime: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class) override val endDatetime: LocalDateTime,
) : EventSerialized()


fun GroupScheduleSerialized.toDomain(): GroupSchedule {
    return GroupSchedule(
        timetable, periodicContent?.toDomain(timetable.startDate), nonPeriodicContent?.toDomain()
    )
}

fun PeriodicContentSerialized.toDomain(startDate: LocalDate): PeriodicContent {
    return PeriodicContent(
        events.map(PeriodicEventSerialized::toDomain),
        recurrence.toDomain(startDate)
    )
}

fun FrequencyRuleFullSerialized.toDomain(startDate: LocalDate): FrequencyRuleFull {
    if (frequency == Frequency.WEEKLY) {
        val dateFromWeekBeginning = startDate.minusDays(startDate.dayOfWeek.ordinal.toLong())
        val startWeek = abs(
            ChronoUnit.WEEKS.between(
                dateFromWeekBeginning,
                LocalDate.now()
            ) - currentNumber + 1
        )
        val startInterval = (startWeek % interval).toInt()
        return FrequencyRuleFull(frequency, interval, startInterval)
    } else throw IllegalArgumentException("Non weekly intervals not yes supported")
}

fun PeriodicEventSerialized.toDomain(): PeriodicEvent {
    return PeriodicEvent(
        name,
        typeName,
        lecturers,
        rooms,
        groups,
        startDatetime,
        endDatetime,
        timeSlotName,
        periodNumber,
        recurrenceRule
    )
}

fun NonPeriodicContentSerialized.toDomain(): NonPeriodicContent {
    return NonPeriodicContent(
        events.map(NonPeriodicEventSerialized::toDomain)
    )
}

fun NonPeriodicEventSerialized.toDomain(): NonPeriodicEvent {
    return NonPeriodicEvent(name, typeName, lecturers, rooms, groups, startDatetime, endDatetime)
}
