package com.sev4ikwasd.rutschedule.ui.schedule

import androidx.compose.runtime.Composable

@Composable
fun ScheduleRoute(
    scheduleViewModel: ScheduleViewModel,
    onNavigateToGroups: () -> Unit
) {
    ScheduleScreen(scheduleViewModel, onNavigateToGroups)
}