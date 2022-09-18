package com.sev4ikwasd.rutschedule.data.repository

import com.sev4ikwasd.rutschedule.data.CacheableResult
import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Schedule
import kotlinx.coroutines.flow.StateFlow

interface ScheduleRepository {
    suspend fun getAllGroups(): Result<List<Group>>
    suspend fun getSchedule(id: Int): StateFlow<CacheableResult<Schedule>>
}