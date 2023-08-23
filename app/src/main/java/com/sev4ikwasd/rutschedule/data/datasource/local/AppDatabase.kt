package com.sev4ikwasd.rutschedule.data.datasource.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.sev4ikwasd.rutschedule.data.datasource.local.dao.ScheduleDao
import com.sev4ikwasd.rutschedule.data.entity.GroupScheduleEntity
import com.sev4ikwasd.rutschedule.data.entity.GroupSchedulesEntity
import com.sev4ikwasd.rutschedule.data.entity.NonPeriodicContentEntity
import com.sev4ikwasd.rutschedule.data.entity.NonPeriodicEventEntity
import com.sev4ikwasd.rutschedule.data.entity.PeriodicContentEntity
import com.sev4ikwasd.rutschedule.data.entity.PeriodicEventEntity
import com.sev4ikwasd.rutschedule.data.entity.TimetableEntity

@Database(
    entities = [GroupSchedulesEntity::class, GroupScheduleEntity::class, TimetableEntity::class, PeriodicContentEntity::class, NonPeriodicContentEntity::class, PeriodicEventEntity::class, NonPeriodicEventEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2, spec = AppDatabase.AutoMigration1to2::class)],
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    @DeleteTable.Entries(
        DeleteTable(
            tableName = "ScheduleEntity"
        ),
        DeleteTable(
            tableName = "ClassEntity"
        )
    )
    class AutoMigration1to2 : AutoMigrationSpec

    abstract fun scheduleDao(): ScheduleDao
}