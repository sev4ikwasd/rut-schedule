package com.sev4ikwasd.rutschedule.data.impl

import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.ScheduleRepository
import com.sev4ikwasd.rutschedule.model.Class
import com.sev4ikwasd.rutschedule.model.Schedule
import com.sev4ikwasd.rutschedule.model.TimePeriod
import org.jsoup.Jsoup
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class JSoupScheduleRepository : ScheduleRepository {
    private val connection = Jsoup.newSession()

    override fun getSchedule(id: Int): Result<Schedule> {
        try {
            connection.newRequest()
            connection.url(Constants.RUT_SITE_BASE_UTL + "/" + id)
            val document = connection.get()
            val classNumberToTimePeriodMap = HashMap<Int, TimePeriod>()
            val classes = ArrayList<Class>()
            val wholeSchedule = document.getElementsByClass("tab-content")[0]
            for (week in wholeSchedule.children()) {
                val weekNumber = week.id().substring(5).toInt()
                val row = week.getElementsByTag("table")[0].child(0)
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
                        val name = cell.child(0).text()
                        val type = cell.child(0).child(0).text()
                        val params = cell.getElementsByClass("mb-2")
                        val teachers = ArrayList<String>()
                        for (k in 0..params.size - 2) {
                            teachers.add(params[k].child(0).text())
                        }
                        val classroom = cell.child(params.size).child(0).text()
                        classes.add(
                            Class(
                                type,
                                name,
                                teachers,
                                classroom,
                                weekNumber,
                                DayOfWeek.of(j),
                                classNumber
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
            return Result.Success(
                Schedule(
                    group,
                    classes,
                    classNumberToTimePeriodMap,
                    localDates[0],
                    localDates[1]
                )
            )
        } catch (e: Exception) {
            return Result.Error(IllegalArgumentException("Could not find group with given id"))
        }
    }
}

object Constants {
    const val RUT_SITE_BASE_UTL = "https://miit.ru/timetable"
}