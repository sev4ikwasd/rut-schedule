package com.sev4ikwasd.rutschedule.data.datasource.local.impl

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.Result.Error
import com.sev4ikwasd.rutschedule.data.Result.Success
import com.sev4ikwasd.rutschedule.data.datasource.local.LocalScheduleDataSource
import com.sev4ikwasd.rutschedule.data.datasource.local.dao.ScheduleDao
import com.sev4ikwasd.rutschedule.data.entity.toDomain
import com.sev4ikwasd.rutschedule.data.entity.toEntity
import com.sev4ikwasd.rutschedule.model.GroupSchedules

class DatabaseScheduleDataSource internal constructor(
    private val scheduleDao: ScheduleDao
) : LocalScheduleDataSource {

    override suspend fun getSchedules(id: Int): Result<GroupSchedules> {
        return try {
            Success(scheduleDao.getGroupSchedulesWithSchedules(id).toDomain())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun changeSchedules(schedules: GroupSchedules) {
        scheduleDao.changeGroupSchedulesWithSchedules(schedules.toEntity())
    }
}