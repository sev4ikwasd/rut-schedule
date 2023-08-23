package com.sev4ikwasd.rutschedule.data.datasource.local

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.model.GroupSchedules

interface LocalScheduleDataSource {
    suspend fun getSchedules(id: Int): Result<GroupSchedules>
    suspend fun changeSchedules(schedules: GroupSchedules)
}