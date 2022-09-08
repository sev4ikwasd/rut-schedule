package com.sev4ikwasd.rutschedule.ui.schedule

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.sev4ikwasd.rutschedule.model.Class
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(scheduleViewModel: ScheduleViewModel) {
    when (val state = scheduleViewModel.uiState.collectAsState().value) {
        is ScheduleUiState.Loading -> Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        is ScheduleUiState.Loaded ->
            Scaffold(
                topBar = {
                    ScheduleTopAppBar(state.currentDate) { scheduleViewModel.updateCurrentDate(it) }
                }
            ) {
                val dateFromWeekBeginning =
                    state.schedule.dateFrom.minusDays(state.schedule.dateFrom.dayOfWeek.ordinal.toLong())
                val currentDate = state.currentDate
                val week = ((ChronoUnit.WEEKS.between(
                    dateFromWeekBeginning,
                    currentDate
                ) % 2) + 1).toInt()
                DayClasses(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { scheduleViewModel.updateSchedule() },
                    classes = state.schedule.classes,
                    dayOfWeek = state.currentDate.dayOfWeek,
                    week = week,
                    date = state.currentDate,
                    onCurrentDateChange = { scheduleViewModel.updateCurrentDate(it) }
                )
            }
        is ScheduleUiState.Error -> Box(modifier = Modifier)
    }
}

@Composable
fun ScheduleTopAppBar(date: LocalDate, onCurrentDateChange: (LocalDate) -> Unit) {
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onCurrentDateChange(LocalDate.of(year, month + 1, dayOfMonth))
        }, date.year, date.monthValue - 1, date.dayOfMonth
    )

    CenterAlignedTopAppBar(title = {
        Button(onClick = {
            datePickerDialog.show()
        }) {
            val formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            Text(text = formattedDate)
        }
    })
}

@Composable
fun DayClasses(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    classes: List<Class>,
    dayOfWeek: DayOfWeek,
    week: Int,
    date: LocalDate,
    onCurrentDateChange: (LocalDate) -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(classes) {
                if ((it.dayOfWeek == dayOfWeek) && (it.week == week))
                    ClassCard(it)
            }
        }
    }
}

@Composable
fun ClassCard(classData: Class) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp), shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${classData.classNumber} пара",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${classData.timeFrom} - ${classData.timeTo}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.padding(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompositionLocalProvider(
                    LocalContentColor provides LocalContentColor.current.copy(
                        alpha = 0.6f
                    )
                ) {
                    Text(
                        text = classData.type,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = classData.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (classData.classroom.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = classData.classroom,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (classData.teachers.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(2.dp))
                var teachersString = "Преподаватели: "
                for (teacher in classData.teachers) {
                    teachersString += "$teacher, "
                }
                teachersString = teachersString.substring(0, teachersString.length - 2)
                Text(
                    text = teachersString,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDayClasses() {
    DayClasses(false, {},
        listOf(
            Class(
                "Лекция",
                "Математика",
                listOf("Иванов И.И.", "Андреев А.А."),
                "Аудитория 420",
                1,
                DayOfWeek.MONDAY,
                1,
                LocalTime.of(8, 30),
                LocalTime.of(10, 0)
            ),
            Class(
                "Лекция",
                "Математика",
                listOf("Иванов И.И.", "Андреев А.А."),
                "Аудитория 420",
                1,
                DayOfWeek.MONDAY,
                2,
                LocalTime.of(10, 15),
                LocalTime.of(11, 45)
            ),
            Class(
                "Лекция",
                "Математика",
                listOf("Иванов И.И.", "Андреев А.А."),
                "Аудитория 420",
                1,
                DayOfWeek.MONDAY,
                3,
                LocalTime.of(12, 0),
                LocalTime.of(13, 30)
            )
        ), DayOfWeek.MONDAY, 1, LocalDate.now(), {}
    )
}

@Preview
@Composable
fun PreviewClassCard() {
    ClassCard(
        Class(
            "Лекция",
            "Математика",
            listOf("Иванов И.И.", "Андреев А.А."),
            "Аудитория 420",
            1,
            DayOfWeek.MONDAY,
            1,
            LocalTime.of(8, 30),
            LocalTime.of(10, 0)
        )
    )
}