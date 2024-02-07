package com.sev4ikwasd.rutschedule.data.datasource.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sev4ikwasd.rutschedule.data.datasource.local.dao.ScheduleDao
import com.sev4ikwasd.rutschedule.data.entity.GroupScheduleEntity
import com.sev4ikwasd.rutschedule.data.entity.GroupSchedulesEntity
import com.sev4ikwasd.rutschedule.data.entity.NonPeriodicContentEntity
import com.sev4ikwasd.rutschedule.data.entity.NonPeriodicEventEntity
import com.sev4ikwasd.rutschedule.data.entity.PeriodicContentEntity
import com.sev4ikwasd.rutschedule.data.entity.PeriodicEventEntity
import com.sev4ikwasd.rutschedule.data.entity.TimetableEntity

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS GroupSchedulesEntity")
        database.execSQL("DROP TABLE IF EXISTS GroupScheduleEntity")
        database.execSQL("DROP TABLE IF EXISTS TimetableEntity")
        database.execSQL("DROP TABLE IF EXISTS PeriodicContentEntity")
        database.execSQL("DROP TABLE IF EXISTS NonPeriodicContentEntity")
        database.execSQL("DROP TABLE IF EXISTS PeriodicEventEntity")
        database.execSQL("DROP TABLE IF EXISTS NonPeriodicEventEntity")
        database.execSQL("CREATE TABLE IF NOT EXISTS `GroupSchedulesEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `apiId` INTEGER NOT NULL, `groupName` TEXT NOT NULL)")
        database.execSQL("CREATE TABLE IF NOT EXISTS `GroupScheduleEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `groupSchedulesId` INTEGER NOT NULL, `timetableId` INTEGER NOT NULL, `periodicContentId` INTEGER, `nonPeriodicContentId` INTEGER, FOREIGN KEY(`groupSchedulesId`) REFERENCES `GroupSchedulesEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`timetableId`) REFERENCES `TimetableEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`periodicContentId`) REFERENCES `PeriodicContentEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`nonPeriodicContentId`) REFERENCES `NonPeriodicContentEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_GroupScheduleEntity_groupSchedulesId` ON `GroupScheduleEntity` (`groupSchedulesId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_GroupScheduleEntity_timetableId` ON `GroupScheduleEntity` (`timetableId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_GroupScheduleEntity_periodicContentId` ON `GroupScheduleEntity` (`periodicContentId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_GroupScheduleEntity_nonPeriodicContentId` ON `GroupScheduleEntity` (`nonPeriodicContentId`)")
        database.execSQL("CREATE TABLE IF NOT EXISTS `TimetableEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `apiId` TEXT NOT NULL, `type` TEXT NOT NULL, `typeName` TEXT NOT NULL, `startDate` INTEGER NOT NULL, `endDate` INTEGER NOT NULL)")
        database.execSQL("CREATE TABLE IF NOT EXISTS `PeriodicContentEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `frequency` TEXT NOT NULL, `interval` INTEGER NOT NULL, `firstInterval` INTEGER NOT NULL)")
        database.execSQL("CREATE TABLE IF NOT EXISTS `NonPeriodicContentEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        database.execSQL("CREATE TABLE IF NOT EXISTS `PeriodicEventEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `typeName` TEXT NOT NULL, `startDatetime` TEXT NOT NULL, `endDatetime` TEXT NOT NULL, `lecturers` TEXT NOT NULL, `rooms` TEXT NOT NULL, `groups` TEXT NOT NULL, `periodicContentId` INTEGER NOT NULL, `timeSlotName` TEXT NOT NULL, `periodNumber` INTEGER NOT NULL, `frequency` TEXT NOT NULL, `interval` INTEGER NOT NULL, FOREIGN KEY(`periodicContentId`) REFERENCES `PeriodicContentEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_PeriodicEventEntity_periodicContentId` ON `PeriodicEventEntity` (`periodicContentId`)")
        database.execSQL("CREATE TABLE IF NOT EXISTS `NonPeriodicEventEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `typeName` TEXT NOT NULL, `startDatetime` TEXT NOT NULL, `endDatetime` TEXT NOT NULL, `lecturers` TEXT NOT NULL, `rooms` TEXT NOT NULL, `groups` TEXT NOT NULL, `nonPeriodicContentId` INTEGER NOT NULL, FOREIGN KEY(`nonPeriodicContentId`) REFERENCES `NonPeriodicContentEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_NonPeriodicEventEntity_nonPeriodicContentId` ON `NonPeriodicEventEntity` (`nonPeriodicContentId`)")
    }
}

@Database(
    entities = [GroupSchedulesEntity::class, GroupScheduleEntity::class, TimetableEntity::class, PeriodicContentEntity::class, NonPeriodicContentEntity::class, PeriodicEventEntity::class, NonPeriodicEventEntity::class],
    version = 3,
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