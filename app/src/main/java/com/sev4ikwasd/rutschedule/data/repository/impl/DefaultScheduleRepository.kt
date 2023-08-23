package com.sev4ikwasd.rutschedule.data.repository.impl

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.datasource.local.LocalScheduleDataSource
import com.sev4ikwasd.rutschedule.data.datasource.remote.RemoteScheduleDataSource
import com.sev4ikwasd.rutschedule.data.repository.ScheduleRepository
import com.sev4ikwasd.rutschedule.model.GroupSchedules
import com.sev4ikwasd.rutschedule.model.Institute

class DefaultScheduleRepository(
    private val remoteScheduleDataSource: RemoteScheduleDataSource,
    private val localScheduleDataSource: LocalScheduleDataSource
) : ScheduleRepository {
    override suspend fun getAllInstitutes(): Result<List<Institute>> {
        return remoteScheduleDataSource.getAllInstitutes()
    }

    override suspend fun loadSchedules(id: Int): Result<GroupSchedules> {
        return localScheduleDataSource.getSchedules(id)
    }

    override suspend fun updateSchedules(id: Int): Result<GroupSchedules> {
        val schedules = remoteScheduleDataSource.getSchedules(id)
        if (schedules is Result.Success) {
            localScheduleDataSource.changeSchedules(schedules.data)
        }
        return schedules
    }
}