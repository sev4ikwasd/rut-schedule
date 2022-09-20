package com.sev4ikwasd.rutschedule.data.datasource.remote.impl

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.datasource.remote.RemoteScheduleDataSource
import com.sev4ikwasd.rutschedule.model.Class
import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class JSoupScheduleDataSource : RemoteScheduleDataSource {
    private val connection = Jsoup.newSession()

    private val stringToDayOfWeek = mapOf(
        "Понедельник" to DayOfWeek.MONDAY,
        "Вторник" to DayOfWeek.TUESDAY,
        "Среда" to DayOfWeek.WEDNESDAY,
        "Четверг" to DayOfWeek.THURSDAY,
        "Пятница" to DayOfWeek.FRIDAY,
        "Суббота" to DayOfWeek.SATURDAY,
        "Воскресенье" to DayOfWeek.SUNDAY,
    )

    override suspend fun getAllGroups(): Result<List<Group>> {
        return withContext(Dispatchers.IO) {
            try {
                connection.newRequest()
                connection.url(Constants.RUT_SITE_BASE_URL)
                val parsedGroups = ArrayList<Group>()
                val document = connection.get()
                val universities =
                    document.getElementsByClass("timetable-catalog")[0].getElementsByClass("info-block info-block_collapse show")
                for (university in universities) {
                    val courses = university.children()[2].children()[0]
                    for (course in courses.children()) {
                        val groups = course.children()[1]
                        for (group in groups.children()) {
                            if (group.child(0).tag().name.equals("a")) {
                                val groupName = group.child(0).text()
                                val groupId = group.child(0).attr("href").substring(11).toInt()
                                parsedGroups.add(Group(groupName, groupId))
                            } else {
                                val collapsedGroups = group.child(0).child(1)
                                for (collapsedGroup in collapsedGroups.children()) {
                                    val groupName = collapsedGroup.text()
                                    val groupId = collapsedGroup.attr("href").substring(11).toInt()
                                    parsedGroups.add(Group(groupName, groupId))
                                }
                            }
                        }
                    }
                }
                Result.Success(parsedGroups)
            } catch (e: Exception) {
                Result.Error(IllegalArgumentException("Error occurred while parsing groups"))
            }
        }
    }

    override suspend fun getSchedule(id: Int): Result<Schedule> {
        return withContext(Dispatchers.IO) {
            try {
                connection.newRequest()
                connection.url(Constants.RUT_SITE_BASE_URL + "/" + id)
                val document = connection.get()
                val classNumberToTimePeriodMap = HashMap<Int, TimePeriod>()
                val classes = ArrayList<Class>()
                val wholeSchedule = document.getElementsByClass("tab-content")[0]
                for (week in wholeSchedule.children()) {
                    val weekNumber = week.id().substring(5).toInt()
                    val row = week.getElementsByTag("table")[0].child(0)
                    val numberToDayOfWeek = HashMap<Int, DayOfWeek>()
                    for (i in 1 until row.child(0).childrenSize()) {
                        val dayOfWeekString = row.child(0).children()[i].ownText()
                        numberToDayOfWeek[i] = stringToDayOfWeek[dayOfWeekString]!!
                    }
                    for (i in 1 until row.childrenSize()) {
                        val column = row.child(i)
                        val classNumberToTimePeriod = column.child(0)
                        val classNumber = classNumberToTimePeriod.text().substring(0, 1).toInt()
                        val timeRegex =
                            Regex("^([0-1]?[0-9]|2[0-3]):([0-5][0-9]) — ([0-1]?[0-9]|2[0-3]):([0-5][0-9])")
                        val timeRegexMatchResult =
                            timeRegex.find(classNumberToTimePeriod.child(0).text())
                        val (hourFrom, minuteFrom, hourTo, minuteTo) = timeRegexMatchResult!!.destructured
                        val timePeriod = TimePeriod(
                            LocalTime.of(hourFrom.toInt(), minuteFrom.toInt()),
                            LocalTime.of(hourTo.toInt(), minuteTo.toInt())
                        )
                        classNumberToTimePeriodMap[classNumber] = timePeriod

                        for (j in 1 until column.childrenSize()) {
                            val cell = column.child(j)
                            if (cell.child(0).text().isBlank())
                                continue
                            val name = cell.child(0).ownText()
                            val type = cell.child(0).child(0).text()
                            val params = cell.getElementsByClass("mb-2")
                            val teachers = ArrayList<String>()
                            val classrooms = ArrayList<String>()
                            for (param in params) {
                                if (param.child(0).classNames().contains("icon-academic-cap")) {
                                    for (teacher in param.children())
                                        teachers.add(teacher.text())
                                } else if (param.child(0).classNames().contains("icon-location")) {
                                    for (classroom in param.children())
                                        classrooms.add(classroom.text().substring(10))
                                }
                            }
                            classes.add(
                                Class(
                                    type,
                                    name,
                                    teachers,
                                    classrooms,
                                    weekNumber,
                                    numberToDayOfWeek[j]!!,
                                    classNumber,
                                    classNumberToTimePeriodMap[classNumber]!!.timeFrom,
                                    classNumberToTimePeriodMap[classNumber]!!.timeTo
                                )
                            )
                        }
                    }
                }
                val group =
                    document.getElementsByClass("page-header-name__title")[0].text().substring(26)
                val dateRegex = Regex("(\\d{2})\\.(\\d{2})\\.(\\d{4})")
                val dates = dateRegex.findAll(
                    document.getElementsByClass("d-inline-block mr-4 order-1")[0].getElementsByTag(
                        "span"
                    )[0].text()
                )
                val localDates = ArrayList<LocalDate>()
                for (date in dates) {
                    val (day, month, year) = date.destructured
                    localDates.add(LocalDate.of(year.toInt(), month.toInt(), day.toInt()))
                }
                Result.Success(
                    Schedule(
                        id,
                        group,
                        classes,
                        localDates[0],
                        localDates[1]
                    )
                )
            } catch (e: Exception) {
                Result.Error(IllegalArgumentException("Error occurred while parsing schedule"))
            }
        }
    }
}

private data class TimePeriod(
    val timeFrom: LocalTime,
    val timeTo: LocalTime
)

object Constants {
    const val RUT_SITE_BASE_URL = "https://miit.ru/timetable"
}