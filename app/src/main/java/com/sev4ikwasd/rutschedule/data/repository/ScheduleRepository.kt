package com.sev4ikwasd.rutschedule.data.repository

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.model.GroupSchedules
import com.sev4ikwasd.rutschedule.model.Institute

interface ScheduleRepository {
    suspend fun getAllInstitutes(): Result<List<Institute>>
    suspend fun loadSchedules(id: Int): Result<GroupSchedules>
    suspend fun updateSchedules(id: Int): Result<GroupSchedules>
}