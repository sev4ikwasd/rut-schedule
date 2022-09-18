package com.sev4ikwasd.rutschedule.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sev4ikwasd.rutschedule.data.datasource.local.dao.ScheduleDao
import com.sev4ikwasd.rutschedule.data.entity.ClassEntity
import com.sev4ikwasd.rutschedule.data.entity.ScheduleEntity

@Database(entities = [ScheduleEntity::class, ClassEntity::class], version = 1, exportSchema = false)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}