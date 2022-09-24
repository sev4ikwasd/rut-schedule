package com.sev4ikwasd.rutschedule.ui.schedule

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.sev4ikwasd.rutschedule.model.Class
import com.sev4ikwasd.rutschedule.ui.composable.ErrorScreen
import com.sev4ikwasd.rutschedule.ui.composable.LoadingScreen
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ScheduleScreen(scheduleViewModel: ScheduleViewModel, onNavigateToGroups: () -> Unit) {
    when (val state = scheduleViewModel.uiState.collectAsState().value) {
        is ScheduleUiState.Loading -> LoadingScreen()
        is ScheduleUiState.Loaded -> {
            val pagerStartIndex = Int.MAX_VALUE / 2
            val startDate = remember { mutableStateOf(LocalDate.now()) }
            val pagerState = rememberPagerState(initialPage = pagerStartIndex)
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
                val pagerCurrentDate =
                    startDate.value.plusDays((pagerState.currentPage - pagerStartIndex).toLong())
                ScheduleTopAppBar(date = pagerCurrentDate, onCurrentDateChange = {
                    scope.launch {
                        val daysBetween = (ChronoUnit.DAYS.between(it, startDate.value)).toInt()
                        val page = pagerStartIndex - daysBetween
                        pagerState.animateScrollToPage(page)
                    }
                }, onNavigateToGroups = onNavigateToGroups)
            }) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        )
                ) {
                    if (state.isCacheUsed) {
                        LaunchedEffect(snackbarHostState) {
                            snackbarHostState.showSnackbar("Расписание может быть неактуальным")
                        }
                    }
                    PagedDayClasses(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { scheduleViewModel.updateSchedule() },
                        classes = state.schedule.classes,
                        dateFrom = state.schedule.dateFrom,
                        startDate = startDate.value,
                        pagerStartIndex = pagerStartIndex,
                        pagerState = pagerState
                    )
                }
            }
        }
        is ScheduleUiState.Error -> ErrorScreen(
            errorMessage = "Произошла ошибка при загрузке расписания",
            onRefresh = { scheduleViewModel.updateSchedule() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTopAppBar(
    date: LocalDate, onCurrentDateChange: (LocalDate) -> Unit, onNavigateToGroups: () -> Unit
) {
    val datePickerDialog = DatePickerDialog(
        LocalContext.current, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onCurrentDateChange(LocalDate.of(year, month + 1, dayOfMonth))
        }, date.year, date.monthValue - 1, date.dayOfMonth
    )

    var displayMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(title = {
        Button(onClick = {
            datePickerDialog.show()
        }) {
            val formattedDate = date.format(DateTimeFormatter.ofPattern("E, d LLL y"))
            Text(text = formattedDate)
        }
    }, actions = {
        IconButton(onClick = { displayMenu = !displayMenu }) {
            Icon(Icons.Default.MoreVert, "")
        }
        DropdownMenu(
            expanded = displayMenu,
            onDismissRequest = { displayMenu = false }
        ) {
            DropdownMenuItem(onClick = onNavigateToGroups, text = {
                Text(text = "Сменить группу")
            })
        }
    })
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagedDayClasses(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    classes: List<Class>,
    dateFrom: LocalDate,
    startDate: LocalDate,
    pagerStartIndex: Int,
    pagerState: PagerState
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing), onRefresh = onRefresh
    ) {
        HorizontalPager(count = Int.MAX_VALUE, state = pagerState) { index ->
            val page = index - pagerStartIndex
            val dateFromWeekBeginning = dateFrom.minusDays(dateFrom.dayOfWeek.ordinal.toLong())
            val pagerCurrentDate = startDate.plusDays(page.toLong())
            val week = ((ChronoUnit.WEEKS.between(
                dateFromWeekBeginning, pagerCurrentDate
            ) % 2) + 1).toInt()
            DayClasses(classes = classes, dayOfWeek = pagerCurrentDate.dayOfWeek, week = week)
        }
    }
}

@Composable
fun DayClasses(
    classes: List<Class>,
    dayOfWeek: DayOfWeek,
    week: Int,
) {
    val displayedClasses = classes.filter { it.dayOfWeek == dayOfWeek && it.week == week }
    LazyColumn(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (displayedClasses.isNotEmpty()) {
            items(displayedClasses) {
                ClassCard(it)
            }
        } else {
            items(1) { // Swipe refresh not working without list
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "На этот день нет пар", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun ClassCard(classData: Class) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
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
                val classTypeAndName = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.6f
                            )
                        ).toSpanStyle()
                    ) {
                        append(text = classData.type)
                    }
                    append(text = "  ")
                    withStyle(style = MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                        append(text = classData.name)
                    }
                }
                Text(text = classTypeAndName)
            }
            if (classData.classrooms.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(2.dp))
                var classroomsString = "Аудитории: "
                for (classroom in classData.classrooms) {
                    classroomsString += "$classroom, "
                }
                classroomsString = classroomsString.substring(0, classroomsString.length - 2)
                Text(
                    text = classroomsString, style = MaterialTheme.typography.bodySmall
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
                    text = teachersString, style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDayClasses() {
    DayClasses(
        listOf(
            Class(
                "Лекция",
                "Математика",
                listOf("Иванов И.И.", "Андреев А.А."),
                listOf("420"),
                1,
                DayOfWeek.MONDAY,
                1,
                LocalTime.of(8, 30),
                LocalTime.of(10, 0)
            ), Class(
                "Лекция",
                "Математика",
                listOf("Иванов И.И.", "Андреев А.А."),
                listOf("420", "530"),
                1,
                DayOfWeek.MONDAY,
                2,
                LocalTime.of(10, 15),
                LocalTime.of(11, 45)
            ), Class(
                "Лекция",
                "Математика",
                listOf("Иванов И.И.", "Андреев А.А."),
                listOf("420"),
                1,
                DayOfWeek.MONDAY,
                3,
                LocalTime.of(12, 0),
                LocalTime.of(13, 30)
            )
        ), DayOfWeek.MONDAY, 1
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
            listOf("420"),
            1,
            DayOfWeek.MONDAY,
            1,
            LocalTime.of(8, 30),
            LocalTime.of(10, 0)
        )
    )
}