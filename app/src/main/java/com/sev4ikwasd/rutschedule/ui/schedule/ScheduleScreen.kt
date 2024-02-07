package com.sev4ikwasd.rutschedule.ui.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sev4ikwasd.rutschedule.model.Event
import com.sev4ikwasd.rutschedule.model.Frequency
import com.sev4ikwasd.rutschedule.model.FrequencyRule
import com.sev4ikwasd.rutschedule.model.GroupSchedule
import com.sev4ikwasd.rutschedule.model.GroupSchedules
import com.sev4ikwasd.rutschedule.model.NonPeriodicEvent
import com.sev4ikwasd.rutschedule.model.PeriodicEvent
import com.sev4ikwasd.rutschedule.model.TimetableType
import com.sev4ikwasd.rutschedule.ui.composable.ErrorScreen
import com.sev4ikwasd.rutschedule.ui.composable.LoadingScreen
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(scheduleViewModel: ScheduleViewModel, onNavigateToGroups: () -> Unit) {
    when (val state = scheduleViewModel.uiState.collectAsState().value) {
        is ScheduleUiState.Loading -> LoadingScreen()
        is ScheduleUiState.Loaded -> {
            val pagerStartIndex = Int.MAX_VALUE / 2
            val currentDateTime = remember { mutableStateOf(LocalDateTime.now()) }
            val pagerState = rememberPagerState(
                initialPage = pagerStartIndex,
            ) {
                Int.MAX_VALUE
            }
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
                val pagerCurrentDate =
                    currentDateTime.value.toLocalDate()
                        .plusDays((pagerState.currentPage - pagerStartIndex).toLong())
                val changeGroupDialogState = rememberMaterialDialogState()
                ChangeGroupDialog(
                    state = changeGroupDialogState, onNavigateToGroups = onNavigateToGroups
                )
                ScheduleTopAppBar(
                    pagerCurrentDate = pagerCurrentDate,
                    onCurrentDateChange = {
                        scope.launch {
                            val daysBetween = (ChronoUnit.DAYS.between(
                                it,
                                currentDateTime.value.toLocalDate()
                            )).toInt()
                            val page = pagerStartIndex - daysBetween
                            pagerState.animateScrollToPage(page)
                        }
                    },
                    group = state.groupSchedules.groupName,
                    onNavigateToGroups = { changeGroupDialogState.show() },
                    groupSchedules = state.groupSchedules
                )
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
                    PagedDayEvents(
                        isRefreshing = state.isRefreshing && !state.isRefreshHidden,
                        onRefresh = { scheduleViewModel.updateSchedule() },
                        groupSchedules = state.groupSchedules,
                        currentDateTime = currentDateTime.value,
                        pagerStartIndex = pagerStartIndex,
                        pagerState = pagerState
                    )
                }
            }
        }

        is ScheduleUiState.Error -> ErrorScreen(
            errorMessage = "Произошла ошибка при загрузке расписания",
            onRefresh = { scheduleViewModel.updateSchedule() },
            showNavigateBack = true,
            onNavigateBack = onNavigateToGroups
        )
    }
}

@Composable
fun ChangeGroupDialog(state: MaterialDialogState, onNavigateToGroups: () -> Unit) {
    MaterialDialog(
        dialogState = state,
        buttons = {
            positiveButton(
                text = "Да",
                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.primary)
            ) { onNavigateToGroups() }
            negativeButton(
                text = "Нет",
                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.primary)
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.large
    ) {
        title(
            text = "Сменить группу?", color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTopAppBar(
    pagerCurrentDate: LocalDate,
    onCurrentDateChange: (LocalDate) -> Unit,
    group: String,
    onNavigateToGroups: () -> Unit,
    groupSchedules: GroupSchedules
) {
    val datePickerDialogState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = datePickerDialogState,
        buttons = {
            positiveButton(
                "Ок", textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.primary)
            )
            negativeButton(
                "Отмена",
                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.primary)
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.large
    ) {
        datepicker(
            initialDate = pagerCurrentDate,
            title = "ВЫБЕРИТЕ ДАТУ",
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = MaterialTheme.colorScheme.primary,
                headerTextColor = MaterialTheme.colorScheme.onPrimary,
                calendarHeaderTextColor = MaterialTheme.colorScheme.onBackground,
                dateActiveBackgroundColor = MaterialTheme.colorScheme.primary,
                dateInactiveBackgroundColor = MaterialTheme.colorScheme.background,
                dateActiveTextColor = MaterialTheme.colorScheme.onPrimary,
                dateInactiveTextColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            onCurrentDateChange(it)
        }
    }

    var dayInfo = ""
    var currentSchedule: GroupSchedule? = null
    for (groupSchedule in groupSchedules.schedules) {
        if ((pagerCurrentDate.isAfter(groupSchedule.timetable.startDate)
                    && pagerCurrentDate.isBefore(groupSchedule.timetable.endDate)
                    || pagerCurrentDate.isEqual(groupSchedule.timetable.startDate)
                    || pagerCurrentDate.isEqual(groupSchedule.timetable.endDate))
        ) {
            currentSchedule = groupSchedule
        }
    }
    if (currentSchedule != null) {
        when (currentSchedule.timetable.type) {
            TimetableType.SESSION -> dayInfo = "Сессия"
            TimetableType.NON_PERIODIC -> dayInfo = "Разовое"
            TimetableType.PERIODIC -> {
                if (currentSchedule.periodicContent!!.recurrence.frequency == Frequency.WEEKLY) {
                    val dateFromWeekBeginning =
                        currentSchedule.timetable.startDate.minusDays(currentSchedule.timetable.startDate.dayOfWeek.ordinal.toLong())
                    val week = (((ChronoUnit.WEEKS.between(
                        dateFromWeekBeginning, pagerCurrentDate
                    ) + currentSchedule.periodicContent!!.recurrence.firstInterval)
                            % currentSchedule.periodicContent!!.recurrence.interval) + 1).toInt()
                    dayInfo = "Неделя $week"
                }
            }
        }
    }

    CenterAlignedTopAppBar(title = {
        Button(onClick = {
            datePickerDialogState.show()
        }) {
            val formattedDate = pagerCurrentDate.format(DateTimeFormatter.ofPattern("E, d LLL y"))
            Text(text = formattedDate)
        }
    }, navigationIcon = {
        TextButton(onClick = onNavigateToGroups) {
            Text(text = group)
        }
    }, actions = {
        Text(text = dayInfo, Modifier.padding(end = 8.dp))
    })
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PagedDayEvents(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    groupSchedules: GroupSchedules,
    currentDateTime: LocalDateTime,
    pagerStartIndex: Int,
    pagerState: PagerState
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )
    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        HorizontalPager(state = pagerState) { index ->
            val page = index - pagerStartIndex
            val pagerCurrentDate = currentDateTime.toLocalDate().plusDays(page.toLong())
            DayEvents(
                groupSchedules = groupSchedules,
                pagerCurrentDate = pagerCurrentDate,
                currentDateTime = currentDateTime
            )
        }
        PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun DayEvents(
    groupSchedules: GroupSchedules,
    pagerCurrentDate: LocalDate,
    currentDateTime: LocalDateTime
) {
    var currentSchedule: GroupSchedule? = null
    for (groupSchedule in groupSchedules.schedules) {
        if ((pagerCurrentDate.isAfter(groupSchedule.timetable.startDate)
                    && pagerCurrentDate.isBefore(groupSchedule.timetable.endDate)
                    || pagerCurrentDate.isEqual(groupSchedule.timetable.startDate)
                    || pagerCurrentDate.isEqual(groupSchedule.timetable.endDate))
        ) {
            currentSchedule = groupSchedule
        }
    }
    val displayedEvents = mutableListOf<Event>()
    if (currentSchedule != null) {
        if (currentSchedule.nonPeriodicContent != null) {
            for (event in currentSchedule.nonPeriodicContent!!.events) {
                if (event.startDatetime.toLocalDate().equals(pagerCurrentDate)) {
                    displayedEvents.add(event)
                }
            }
        }
        if (currentSchedule.periodicContent != null) {
            if (currentSchedule.periodicContent!!.recurrence.frequency == Frequency.WEEKLY) {
                val dateFromWeekBeginning =
                    currentSchedule.timetable.startDate.minusDays(currentSchedule.timetable.startDate.dayOfWeek.ordinal.toLong())
                for (event in currentSchedule.periodicContent!!.events) {
                    val week = ((ChronoUnit.WEEKS.between(
                        dateFromWeekBeginning, pagerCurrentDate
                    ) % event.recurrenceRule.interval) + 1).toInt()
                    val currentDayOfWeek = pagerCurrentDate.dayOfWeek
                    if ((currentDayOfWeek == event.startDatetime.dayOfWeek) && (week == event.periodNumber)) {
                        displayedEvents.add(event)
                    }
                }
            }
        }
    }

    displayedEvents.sortBy { event: Event -> event.startDatetime.toLocalTime() }
    val groupedDisplayedEvents =
        displayedEvents.groupBy { event: Event -> event.startDatetime }.toList()
    var nextDisplayedEvents = listOf<Event>()

    if (currentDateTime.toLocalDate().equals(pagerCurrentDate)) {
        for (groupDisplayedEventsList in groupedDisplayedEvents.asReversed()) {
            if (currentDateTime.toLocalTime()
                    .isBefore(groupDisplayedEventsList.second[0].endDatetime.toLocalTime())
            ) {
                nextDisplayedEvents = groupDisplayedEventsList.second
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (groupedDisplayedEvents.isNotEmpty()) {
            items(groupedDisplayedEvents) {
                EventCard(
                    it.second,
                    ((nextDisplayedEvents.isNotEmpty()) && (it.second == nextDisplayedEvents))
                )
            }

            item {
                Spacer(modifier = Modifier.padding(0.dp))
            }
        } else {
            item { // Swipe refresh not working without list
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "На этот день нет пар", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun EventCard(eventsData: List<Event>, isNext: Boolean) {
    var colors = CardDefaults.elevatedCardColors()
    if (isNext) {
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.tertiary
        )
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = colors
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val commonEventData = eventsData[0]
                if (commonEventData is PeriodicEvent) {
                    Text(
                        text = commonEventData.timeSlotName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "${commonEventData.startDatetime.toLocalTime()} - ${commonEventData.endDatetime.toLocalTime()}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.padding(2.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                eventsData.forEach { eventData ->
                    SingleEvent(eventData, isNext)
                }
            }
        }
    }
}

@Composable
fun SingleEvent(eventData: Event, isNext: Boolean) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val classTypeAndName = buildAnnotatedString {
                withStyle(
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isNext) MaterialTheme.colorScheme.tertiary.copy(
                            alpha = 0.6f
                        ) else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        )
                    ).toSpanStyle()
                ) {
                    append(text = eventData.typeName)
                }
                append(text = "  ")
                withStyle(style = MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    append(text = eventData.name)
                }
            }
            Text(text = classTypeAndName)
        }
        if (eventData.rooms.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(2.dp))
            var classroomsString = ""
            for (room in eventData.rooms) {
                classroomsString += "$room, "
            }
            classroomsString = classroomsString.substring(0, classroomsString.length - 2)
            Text(
                text = classroomsString, style = MaterialTheme.typography.bodySmall
            )
        }
        if (eventData.lecturers.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(2.dp))
            var teachersString = "Преподаватели: "
            for (lecturer in eventData.lecturers) {
                teachersString += "$lecturer, "
            }
            teachersString = teachersString.substring(0, teachersString.length - 2)
            Text(
                text = teachersString, style = MaterialTheme.typography.bodySmall
            )
        }
        if (eventData.groups.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(2.dp))
            var teachersString = "Группы: "
            for (group in eventData.groups) {
                teachersString += "$group, "
            }
            teachersString = teachersString.substring(0, teachersString.length - 2)
            Text(
                text = teachersString, style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun PreviewPeriodicEventCard() {
    EventCard(
        listOf(
            PeriodicEvent(
                "Математика",
                "Лекция",
                listOf("Иванов И.И.", "Андреев А.А."),
                listOf("Аудитория 420"),
                listOf("гр. ФЫВ-123"),
                LocalDateTime.of(2022, 1, 8, 16, 30),
                LocalDateTime.of(2022, 1, 8, 18, 30),
                "4 пара",
                2,
                recurrenceRule = FrequencyRule(Frequency.WEEKLY, 2)
            )
        ), false
    )
}

@Preview
@Composable
fun PreviewMultiplePeriodicEventCard() {
    EventCard(
        listOf(
            PeriodicEvent(
                "Математика",
                "Лекция",
                listOf("Иванов И.И.", "Андреев А.А."),
                listOf("Аудитория 420"),
                listOf("гр. ФЫВ-123"),
                LocalDateTime.of(2022, 1, 8, 16, 30),
                LocalDateTime.of(2022, 1, 8, 18, 30),
                "4 пара",
                2,
                recurrenceRule = FrequencyRule(Frequency.WEEKLY, 2)
            ), PeriodicEvent(
                "Математика",
                "Лекция",
                listOf("Иванов И.И.", "Андреев А.А."),
                listOf("Аудитория 420"),
                listOf("гр. ФЫВ-123"),
                LocalDateTime.of(2022, 1, 8, 16, 30),
                LocalDateTime.of(2022, 1, 8, 18, 30),
                "4 пара",
                2,
                recurrenceRule = FrequencyRule(Frequency.WEEKLY, 2)
            )
        ), false
    )
}

@Preview
@Composable
fun PreviewNextPeriodicEventCard() {
    EventCard(
        listOf(
            PeriodicEvent(
                "Математика",
                "Лекция",
                listOf("Иванов И.И.", "Андреев А.А."),
                listOf("Аудитория 420"),
                listOf("гр. ФЫВ-123"),
                LocalDateTime.of(2022, 1, 8, 16, 30),
                LocalDateTime.of(2022, 1, 8, 18, 30),
                "4 пара",
                2,
                recurrenceRule = FrequencyRule(Frequency.WEEKLY, 2)
            )
        ), true
    )
}

@Preview
@Composable
fun PreviewNonPeriodicEventCard() {
    EventCard(
        listOf(
            NonPeriodicEvent(
                "Математика",
                "Консультация",
                listOf("Иванов И.И.", "Андреев А.А."),
                listOf("Аудитория 420"),
                listOf("гр. ФЫВ-123"),
                LocalDateTime.of(2022, 1, 8, 16, 30),
                LocalDateTime.of(2022, 1, 8, 18, 30),
            )
        ), false
    )
}

