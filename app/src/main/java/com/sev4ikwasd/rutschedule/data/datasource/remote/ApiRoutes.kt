package com.sev4ikwasd.rutschedule.data.datasource.remote

object ApiRoutes {
    private const val BASE_URL = "https://rut-miit.ru/"
    const val GROUPS = "${BASE_URL}data-service/data/timetable/groups-catalog"
    const val GROUP_SCHEDULE = "${BASE_URL}api/v1b/public/timetable/v2/group/"
}