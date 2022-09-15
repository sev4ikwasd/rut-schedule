package com.sev4ikwasd.rutschedule.ui.groups

import androidx.compose.runtime.Composable

@Composable
fun GroupsRoute(
    groupsViewModel: GroupsViewModel,
    onNavigateToSchedule: (Int) -> Unit
) {
    GroupsScreen(groupsViewModel) { onNavigateToSchedule(it) }
}
