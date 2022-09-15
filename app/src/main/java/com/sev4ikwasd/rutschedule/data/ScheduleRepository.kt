package com.sev4ikwasd.rutschedule.data

import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Schedule

interface ScheduleRepository {
    suspend fun getAllGroups(): Result<List<Group>>
    suspend fun getSchedule(id: Int): Result<Schedule>
}