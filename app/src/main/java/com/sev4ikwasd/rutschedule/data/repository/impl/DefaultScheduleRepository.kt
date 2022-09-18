package com.sev4ikwasd.rutschedule.data.repository.impl

import com.sev4ikwasd.rutschedule.data.CacheableResult
import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.datasource.local.LocalScheduleDataSource
import com.sev4ikwasd.rutschedule.data.datasource.remote.RemoteScheduleDataSource
import com.sev4ikwasd.rutschedule.data.repository.ScheduleRepository
import com.sev4ikwasd.rutschedule.data.toCacheableResult
import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultScheduleRepository(
    private val remoteScheduleDataSource: RemoteScheduleDataSource,
    private val localScheduleDataSource: LocalScheduleDataSource
) : ScheduleRepository {
    override suspend fun getAllGroups(): Result<List<Group>> {
        return remoteScheduleDataSource.getAllGroups()
    }

    override suspend fun getSchedule(id: Int): StateFlow<CacheableResult<Schedule>> {
        val mutableResult =
            MutableStateFlow(localScheduleDataSource.getSchedule(id).toCacheableResult())
        val schedule = remoteScheduleDataSource.getSchedule(id)
        if (schedule is Result.Success) {
            mutableResult.value = CacheableResult.SuccessfullyUpdated(schedule.data)
            localScheduleDataSource.changeSchedule(schedule.data)
        } else {
            if (mutableResult.value is CacheableResult.SuccessfullyUpdated)
                mutableResult.value =
                    CacheableResult.NotUpdated((mutableResult.value as CacheableResult.SuccessfullyUpdated<Schedule>).data)
        }
        return mutableResult.asStateFlow()
    }
}