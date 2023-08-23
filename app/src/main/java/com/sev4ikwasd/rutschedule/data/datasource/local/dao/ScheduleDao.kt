package com.sev4ikwasd.rutschedule.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sev4ikwasd.rutschedule.data.entity.GroupScheduleEntity
import com.sev4ikwasd.rutschedule.data.entity.GroupSchedulesEntity
import com.sev4ikwasd.rutschedule.data.entity.GroupSchedulesWithSchedulesEntity
import com.sev4ikwasd.rutschedule.data.entity.NonPeriodicContentEntity
import com.sev4ikwasd.rutschedule.data.entity.NonPeriodicEventEntity
import com.sev4ikwasd.rutschedule.data.entity.PeriodicContentEntity
import com.sev4ikwasd.rutschedule.data.entity.PeriodicEventEntity
import com.sev4ikwasd.rutschedule.data.entity.TimetableEntity

@Dao
interface ScheduleDao {
    @Transaction
    @Query("SELECT * FROM GroupSchedulesEntity WHERE apiId=:id")
    suspend fun getGroupSchedulesWithSchedules(id: Int): GroupSchedulesWithSchedulesEntity

    @Transaction
    suspend fun changeGroupSchedulesWithSchedules(groupSchedules: GroupSchedulesWithSchedulesEntity) {
        deleteAll()
        insertGroupSchedulesWithSchedules(groupSchedules)
    }

    @Transaction
    suspend fun insertGroupSchedulesWithSchedules(groupSchedules: GroupSchedulesWithSchedulesEntity) {
        val groupSchedulesId = insertGroupSchedules(groupSchedules.groupSchedulesEntity)
        for (groupSchedule in groupSchedules.schedules) {
            groupSchedule.groupScheduleEntity.groupSchedulesId = groupSchedulesId
            groupSchedule.groupScheduleEntity.timetableId =
                insertTimetable(groupSchedule.timetableEntity)
            if (groupSchedule.periodicContentEntity != null) {
                val periodicContentId =
                    insertPeriodicContent(groupSchedule.periodicContentEntity.periodicContentEntity)
                groupSchedule.groupScheduleEntity.periodicContentId = periodicContentId
                for (periodicEvent in groupSchedule.periodicContentEntity.events) {
                    periodicEvent.periodicContentId = periodicContentId
                    insertPeriodicEvent(periodicEvent)
                }
            }
            if (groupSchedule.nonPeriodicContentEntity != null) {
                val nonPeriodicContentId =
                    insertNonPeriodicContent(groupSchedule.nonPeriodicContentEntity.nonPeriodicContentEntity)
                groupSchedule.groupScheduleEntity.nonPeriodicContentId = nonPeriodicContentId
                for (nonPeriodicEvent in groupSchedule.nonPeriodicContentEntity.events) {
                    nonPeriodicEvent.nonPeriodicContentId = nonPeriodicContentId
                    insertNonPeriodicEvent(nonPeriodicEvent)
                }
            }
            insertGroupSchedule(groupSchedule.groupScheduleEntity)
        }
    }

    @Insert
    suspend fun insertGroupSchedules(groupSchedulesEntity: GroupSchedulesEntity): Long

    @Insert
    suspend fun insertGroupSchedule(groupScheduleEntity: GroupScheduleEntity): Long

    @Insert
    suspend fun insertTimetable(timetableEntity: TimetableEntity): Long

    @Insert
    suspend fun insertPeriodicContent(periodicContentEntity: PeriodicContentEntity): Long

    @Insert
    suspend fun insertNonPeriodicContent(nonPeriodicContentEntity: NonPeriodicContentEntity): Long

    @Insert
    suspend fun insertPeriodicEvent(periodicEventEntity: PeriodicEventEntity): Long

    @Insert
    suspend fun insertNonPeriodicEvent(nonPeriodicEventEntity: NonPeriodicEventEntity): Long

    suspend fun deleteAll() {
        deleteGroupSchedules()
        deleteGroupSchedule()
        deleteTimetable()
        deletePeriodicContent()
        deletePeriodicEvent()
        deleteNonPeriodicContent()
        deleteNonPeriodicEvent()
    }

    @Query("DELETE FROM GroupSchedulesEntity")
    suspend fun deleteGroupSchedules()

    @Query("DELETE FROM GroupScheduleEntity")
    suspend fun deleteGroupSchedule()

    @Query("DELETE FROM TimetableEntity")
    suspend fun deleteTimetable()

    @Query("DELETE FROM PeriodicContentEntity")
    suspend fun deletePeriodicContent()

    @Query("DELETE FROM PeriodicEventEntity")
    suspend fun deletePeriodicEvent()

    @Query("DELETE FROM NonPeriodicContentEntity")
    suspend fun deleteNonPeriodicContent()

    @Query("DELETE FROM NonPeriodicEventEntity")
    suspend fun deleteNonPeriodicEvent()
}