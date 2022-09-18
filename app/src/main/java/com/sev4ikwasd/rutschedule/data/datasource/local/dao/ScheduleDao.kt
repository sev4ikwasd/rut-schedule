package com.sev4ikwasd.rutschedule.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sev4ikwasd.rutschedule.data.entity.ClassEntity
import com.sev4ikwasd.rutschedule.data.entity.ScheduleEntity
import com.sev4ikwasd.rutschedule.data.entity.ScheduleWithClassesEntity


@Dao
interface ScheduleDao {
    @Transaction
    @Query("SELECT * FROM ScheduleEntity WHERE groupId=:groupId")
    fun getScheduleWithClasses(groupId: Int): ScheduleWithClassesEntity

    @Transaction
    fun changeScheduleWithClasses(scheduleWithClassesEntity: ScheduleWithClassesEntity) {
        deleteAll()
        insertScheduleWithClasses(scheduleWithClassesEntity)
    }

    @Transaction
    fun insertScheduleWithClasses(scheduleWithClassesEntity: ScheduleWithClassesEntity) {
        val scheduleId: Long = insertSchedule(scheduleWithClassesEntity.schedule)
        for (classEntity in scheduleWithClassesEntity.classes) {
            classEntity.scheduleId = scheduleId
            insertClass(classEntity)
        }
    }

    @Insert
    fun insertSchedule(scheduleEntity: ScheduleEntity): Long

    @Insert
    fun insertClass(classEntity: ClassEntity)

    @Query("DELETE FROM ScheduleEntity")
    fun deleteAll()
}