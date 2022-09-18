package com.sev4ikwasd.rutschedule.data.datasource.remote

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Schedule

interface RemoteScheduleDataSource {
    suspend fun getAllGroups(): Result<List<Group>>
    suspend fun getSchedule(id: Int): Result<Schedule>
}