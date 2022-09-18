package com.sev4ikwasd.rutschedule.data.datasource.local.impl

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.Result.Error
import com.sev4ikwasd.rutschedule.data.Result.Success
import com.sev4ikwasd.rutschedule.data.datasource.local.LocalScheduleDataSource
import com.sev4ikwasd.rutschedule.data.datasource.local.dao.ScheduleDao
import com.sev4ikwasd.rutschedule.data.entity.fromDomain
import com.sev4ikwasd.rutschedule.data.entity.toDomain
import com.sev4ikwasd.rutschedule.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseScheduleDataSource internal constructor(
    private val scheduleDao: ScheduleDao
) : LocalScheduleDataSource {
    override suspend fun getSchedule(id: Int): Result<Schedule> = withContext(Dispatchers.IO) {
        try {
            Success(scheduleDao.getScheduleWithClasses(id).toDomain())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun changeSchedule(schedule: Schedule) = withContext(Dispatchers.IO) {
        scheduleDao.changeScheduleWithClasses(schedule.fromDomain())
    }
}