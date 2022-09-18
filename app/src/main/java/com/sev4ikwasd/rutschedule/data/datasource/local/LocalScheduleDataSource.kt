package com.sev4ikwasd.rutschedule.data.datasource.local

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.model.Schedule

interface LocalScheduleDataSource {
    suspend fun getSchedule(id: Int): Result<Schedule>
    suspend fun changeSchedule(schedule: Schedule)
}