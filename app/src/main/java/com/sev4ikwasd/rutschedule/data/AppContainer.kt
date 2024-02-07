package com.sev4ikwasd.rutschedule.data

import android.content.Context
import androidx.room.Room
import com.sev4ikwasd.rutschedule.data.datasource.local.AppDatabase
import com.sev4ikwasd.rutschedule.data.datasource.local.MIGRATION_2_3
import com.sev4ikwasd.rutschedule.data.datasource.local.impl.DatabaseScheduleDataSource
import com.sev4ikwasd.rutschedule.data.datasource.remote.impl.ApiScheduleDataSource
import com.sev4ikwasd.rutschedule.data.repository.ScheduleRepository
import com.sev4ikwasd.rutschedule.data.repository.impl.DefaultScheduleRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface AppContainer {
    val scheduleRepository: ScheduleRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    override val scheduleRepository: ScheduleRepository by lazy {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "rutschedule-db")
            .addMigrations(MIGRATION_2_3)
            .build()
        val httpClient = HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        DefaultScheduleRepository(
            ApiScheduleDataSource(httpClient),
            DatabaseScheduleDataSource(db.scheduleDao())
        )
    }
}