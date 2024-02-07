package com.sev4ikwasd.rutschedule.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.sev4ikwasd.rutschedule.model.Frequency
import com.sev4ikwasd.rutschedule.model.FrequencyRule
import com.sev4ikwasd.rutschedule.model.FrequencyRuleFull
import com.sev4ikwasd.rutschedule.model.GroupSchedule
import com.sev4ikwasd.rutschedule.model.GroupSchedules
import com.sev4ikwasd.rutschedule.model.NonPeriodicContent
import com.sev4ikwasd.rutschedule.model.NonPeriodicEvent
import com.sev4ikwasd.rutschedule.model.PeriodicContent
import com.sev4ikwasd.rutschedule.model.PeriodicEvent
import com.sev4ikwasd.rutschedule.model.Timetable
import com.sev4ikwasd.rutschedule.model.TimetableType
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class GroupSchedulesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val apiId: Int,
    val groupName: String,
)

data class GroupSchedulesWithSchedulesEntity(
    @Embedded val groupSchedulesEntity: GroupSchedulesEntity, @Relation(
        parentColumn = "id", entityColumn = "groupSchedulesId", entity = GroupScheduleEntity::class
    ) val schedules: List<GroupScheduleWithEmbeddingsEntity>
)

fun GroupSchedulesWithSchedulesEntity.toDomain(): GroupSchedules {
    return GroupSchedules(
        groupSchedulesEntity.apiId,
        groupSchedulesEntity.groupName,
        schedules.map(GroupScheduleWithEmbeddingsEntity::toDomain)
    )
}

fun GroupSchedules.toEntity(): GroupSchedulesWithSchedulesEntity {
    return GroupSchedulesWithSchedulesEntity(
        GroupSchedulesEntity(0, id, groupName), schedules.map(GroupSchedule::toEntity)
    )
}

@Entity(
    foreignKeys = [ForeignKey(
        entity = GroupSchedulesEntity::class,
        parentColumns = ["id"],
        childColumns = ["groupSchedulesId"],
        onDelete = CASCADE
    ), ForeignKey(
        entity = TimetableEntity::class,
        parentColumns = ["id"],
        childColumns = ["timetableId"],
        onDelete = CASCADE
    ), ForeignKey(
        entity = PeriodicContentEntity::class,
        parentColumns = ["id"],
        childColumns = ["periodicContentId"],
        onDelete = CASCADE
    ), ForeignKey(
        entity = NonPeriodicContentEntity::class,
        parentColumns = ["id"],
        childColumns = ["nonPeriodicContentId"],
        onDelete = CASCADE
    )]
)
data class GroupScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) var groupSchedulesId: Long,
    @ColumnInfo(index = true) var timetableId: Long,
    @ColumnInfo(index = true) var periodicContentId: Long?,
    @ColumnInfo(index = true) var nonPeriodicContentId: Long?
)

data class GroupScheduleWithEmbeddingsEntity(
    @Embedded val groupScheduleEntity: GroupScheduleEntity,
    @Relation(
        parentColumn = "timetableId", entityColumn = "id"
    ) val timetableEntity: TimetableEntity, @Relation(
        parentColumn = "periodicContentId",
        entityColumn = "id",
        entity = PeriodicContentEntity::class
    ) val periodicContentEntity: PeriodicContentWithEventsEntity?, @Relation(
        parentColumn = "nonPeriodicContentId",
        entityColumn = "id",
        entity = NonPeriodicContentEntity::class
    ) val nonPeriodicContentEntity: NonPeriodicContentWithEventsEntity?
)

fun GroupScheduleWithEmbeddingsEntity.toDomain(): GroupSchedule {
    return GroupSchedule(
        timetableEntity.toDomain(),
        periodicContentEntity?.toDomain(),
        nonPeriodicContentEntity?.toDomain()
    )
}

fun GroupSchedule.toEntity(): GroupScheduleWithEmbeddingsEntity {
    return GroupScheduleWithEmbeddingsEntity(
        GroupScheduleEntity(0, 0, 0, null, null),
        timetable.toEntity(),
        periodicContent?.toEntity(),
        nonPeriodicContent?.toEntity()
    )
}

@Entity
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val apiId: String,
    val type: TimetableType,
    val typeName: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)

fun TimetableEntity.toDomain(): Timetable {
    return Timetable(apiId, type, typeName, startDate, endDate)
}

fun Timetable.toEntity(): TimetableEntity {
    return TimetableEntity(0, id, type, typeName, startDate, endDate)
}

data class FrequencyRuleEntity(
    val frequency: Frequency, val interval: Int
)

fun FrequencyRuleEntity.toDomain(): FrequencyRule {
    return FrequencyRule(frequency, interval)
}

fun FrequencyRule.toEntity(): FrequencyRuleEntity {
    return FrequencyRuleEntity(frequency, interval)
}

data class FrequencyRuleFullEntity(
    val frequency: Frequency,
    val interval: Int,
    val firstInterval: Int
)

fun FrequencyRuleFullEntity.toDomain(): FrequencyRuleFull {
    return FrequencyRuleFull(frequency, interval, firstInterval)
}

fun FrequencyRuleFull.toEntity(): FrequencyRuleFullEntity {
    return FrequencyRuleFullEntity(frequency, interval, firstInterval)
}

@Entity
data class PeriodicContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long, @Embedded val recurrence: FrequencyRuleFullEntity
)

data class PeriodicContentWithEventsEntity(
    @Embedded val periodicContentEntity: PeriodicContentEntity, @Relation(
        parentColumn = "id", entityColumn = "periodicContentId"
    ) val events: List<PeriodicEventEntity>
)

fun PeriodicContentWithEventsEntity.toDomain(): PeriodicContent {
    return PeriodicContent(
        events.map(PeriodicEventEntity::toDomain), periodicContentEntity.recurrence.toDomain()
    )
}

fun PeriodicContent.toEntity(): PeriodicContentWithEventsEntity {
    return PeriodicContentWithEventsEntity(
        PeriodicContentEntity(0, recurrence.toEntity()), events.map(PeriodicEvent::toEntity)
    )
}

@Entity
data class NonPeriodicContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long
)

data class NonPeriodicContentWithEventsEntity(
    @Embedded val nonPeriodicContentEntity: NonPeriodicContentEntity, @Relation(
        parentColumn = "id", entityColumn = "nonPeriodicContentId"
    ) val events: List<NonPeriodicEventEntity>
)

fun NonPeriodicContentWithEventsEntity.toDomain(): NonPeriodicContent {
    return NonPeriodicContent(events.map(NonPeriodicEventEntity::toDomain))
}

fun NonPeriodicContent.toEntity(): NonPeriodicContentWithEventsEntity {
    return NonPeriodicContentWithEventsEntity(
        NonPeriodicContentEntity(0), events.map(NonPeriodicEvent::toEntity)
    )
}

open class EventEntity(
    @Ignore open val id: Long,
    @Ignore open val name: String,
    @Ignore open val typeName: String,
    @Ignore open val startDatetime: LocalDateTime,
    @Ignore open val endDatetime: LocalDateTime,
    @Ignore open val lecturers: List<String>,
    @Ignore open val rooms: List<String>,
    @Ignore open val groups: List<String>
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = PeriodicContentEntity::class,
        parentColumns = ["id"],
        childColumns = ["periodicContentId"],
        onDelete = CASCADE
    )]
)
data class PeriodicEventEntity(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    override val name: String,
    override val typeName: String,
    override val startDatetime: LocalDateTime,
    override val endDatetime: LocalDateTime,
    override val lecturers: List<String>,
    override val rooms: List<String>,
    override val groups: List<String>,
    @ColumnInfo(index = true) var periodicContentId: Long,
    val timeSlotName: String,
    val periodNumber: Int,
    @Embedded val recurrenceRule: FrequencyRuleEntity
) : EventEntity(id, name, typeName, startDatetime, endDatetime, lecturers, rooms, groups)

fun PeriodicEventEntity.toDomain(): PeriodicEvent {
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
        recurrenceRule.toDomain()
    )
}

fun PeriodicEvent.toEntity(): PeriodicEventEntity {
    return PeriodicEventEntity(
        0,
        name,
        typeName,
        startDatetime,
        endDatetime,
        lecturers,
        rooms,
        groups,
        0,
        timeSlotName,
        periodNumber,
        recurrenceRule.toEntity()
    )
}

@Entity(
    foreignKeys = [ForeignKey(
        entity = NonPeriodicContentEntity::class,
        parentColumns = ["id"],
        childColumns = ["nonPeriodicContentId"],
        onDelete = CASCADE
    )]
)
data class NonPeriodicEventEntity(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    override val name: String,
    override val typeName: String,
    override val startDatetime: LocalDateTime,
    override val endDatetime: LocalDateTime,
    override val lecturers: List<String>,
    override val rooms: List<String>,
    override val groups: List<String>,
    @ColumnInfo(index = true) var nonPeriodicContentId: Long,
) : EventEntity(id, name, typeName, startDatetime, endDatetime, lecturers, rooms, groups)

fun NonPeriodicEventEntity.toDomain(): NonPeriodicEvent {
    return NonPeriodicEvent(name, typeName, lecturers, rooms, groups, startDatetime, endDatetime)
}

fun NonPeriodicEvent.toEntity(): NonPeriodicEventEntity {
    return NonPeriodicEventEntity(
        0, name, typeName, startDatetime, endDatetime, lecturers, rooms, groups, 0
    )
}