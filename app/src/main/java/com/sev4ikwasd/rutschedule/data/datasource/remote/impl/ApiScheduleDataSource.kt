package com.sev4ikwasd.rutschedule.data.datasource.remote.impl

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.datasource.remote.ApiRoutes
import com.sev4ikwasd.rutschedule.data.datasource.remote.RemoteScheduleDataSource
import com.sev4ikwasd.rutschedule.model.GroupSchedule
import com.sev4ikwasd.rutschedule.model.GroupScheduleSerialized
import com.sev4ikwasd.rutschedule.model.GroupSchedules
import com.sev4ikwasd.rutschedule.model.Institute
import com.sev4ikwasd.rutschedule.model.Institutes
import com.sev4ikwasd.rutschedule.model.Timetables
import com.sev4ikwasd.rutschedule.model.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url

class ApiScheduleDataSource(private val client: HttpClient) : RemoteScheduleDataSource {
    override suspend fun getAllInstitutes(): Result<List<Institute>> {
        return try {
            Result.Success(client.get { url(ApiRoutes.GROUPS) }.body<Institutes>().institutes)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getSchedules(id: Int): Result<GroupSchedules> {
        return try {
            var found = false
            var groupName = ""
            when (val institutes = getAllInstitutes()) {
                is Result.Success -> {
                    for (institute in institutes.data) {
                        for (course in institute.courses) {
                            for (speciality in course.specialties) {
                                for (group in speciality.groups) {
                                    if (group.id == id) {
                                        found = true
                                        groupName = group.name
                                        break
                                    }
                                }
                            }
                        }
                    }
                }

                is Result.Error -> Result.Error(institutes.exception)
            }
            if (!found) {
                Result.Error(IllegalArgumentException("Group with given id not found!"))
            }

            val timetables: Timetables = client.get { url(ApiRoutes.GROUP_SCHEDULE + id) }.body()

            val fullSchedules = mutableListOf<GroupSchedule>()

            for (timetable in timetables.timetables) {
                val fullSchedule: GroupScheduleSerialized =
                    client.get { url(ApiRoutes.GROUP_SCHEDULE + id + "/" + timetable.id) }.body()
                fullSchedules.add(fullSchedule.toDomain())
            }
            Result.Success(GroupSchedules(id, groupName, fullSchedules))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}