package com.sev4ikwasd.rutschedule.data.repository.impl

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.datasource.local.LocalScheduleDataSource
import com.sev4ikwasd.rutschedule.data.datasource.remote.RemoteScheduleDataSource
import com.sev4ikwasd.rutschedule.data.repository.ScheduleRepository
import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Schedule

class DefaultScheduleRepository(
    private val remoteScheduleDataSource: RemoteScheduleDataSource,
    private val localScheduleDataSource: LocalScheduleDataSource
) : ScheduleRepository {
    override suspend fun getAllGroups(): Result<List<Group>> {
        return remoteScheduleDataSource.getAllGroups()
    }

    override suspend fun loadSchedule(id: Int): Result<Schedule> {
        return localScheduleDataSource.getSchedule(id)
    }

    override suspend fun updateSchedule(id: Int): Result<Schedule> {
        val schedule = remoteScheduleDataSource.getSchedule(id)
        if (schedule is Result.Success) {
            localScheduleDataSource.changeSchedule(schedule.data)
        }
        return schedule
    }
}