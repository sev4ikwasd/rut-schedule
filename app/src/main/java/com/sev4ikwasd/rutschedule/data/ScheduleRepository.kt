package com.sev4ikwasd.rutschedule.data

import com.sev4ikwasd.rutschedule.model.Schedule

interface ScheduleRepository {
    suspend fun getSchedule(id: Int): Result<Schedule>
}