package com.sev4ikwasd.rutschedule.data.datasource.remote

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.model.GroupSchedules
import com.sev4ikwasd.rutschedule.model.Institute

interface RemoteScheduleDataSource {
    suspend fun getAllInstitutes(): Result<List<Institute>>
    suspend fun getSchedules(id: Int): Result<GroupSchedules>
}