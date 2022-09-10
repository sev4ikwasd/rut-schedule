package com.sev4ikwasd.rutschedule.data

import android.content.Context
import com.sev4ikwasd.rutschedule.data.impl.JSoupScheduleRepository

interface AppContainer {
    val scheduleRepository: ScheduleRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    override val scheduleRepository: ScheduleRepository by lazy {
        JSoupScheduleRepository()
    }
}