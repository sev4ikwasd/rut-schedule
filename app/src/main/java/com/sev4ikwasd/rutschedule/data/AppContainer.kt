package com.sev4ikwasd.rutschedule.data

import android.content.Context
import androidx.room.Room
import com.sev4ikwasd.rutschedule.data.datasource.local.AppDatabase
import com.sev4ikwasd.rutschedule.data.datasource.local.impl.DatabaseScheduleDataSource
import com.sev4ikwasd.rutschedule.data.datasource.remote.impl.JSoupScheduleDataSource
import com.sev4ikwasd.rutschedule.data.repository.ScheduleRepository
import com.sev4ikwasd.rutschedule.data.repository.impl.DefaultScheduleRepository

interface AppContainer {
    val scheduleRepository: ScheduleRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    override val scheduleRepository: ScheduleRepository by lazy {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "rutschedule-db")
            .build()
        DefaultScheduleRepository(
            JSoupScheduleDataSource(),
            DatabaseScheduleDataSource(db.scheduleDao())
        )
    }
}