package com.sev4ikwasd.rutschedule.data.repository

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Schedule

interface ScheduleRepository {
    suspend fun getAllGroups(): Result<List<Group>>
    suspend fun loadSchedule(id: Int): Result<Schedule>
    suspend fun updateSchedule(id: Int): Result<Schedule>
}