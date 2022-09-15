package com.sev4ikwasd.rutschedule.ui

import androidx.navigation.NavHostController

object RUTScheduleDestinations {
    const val GROUPS_ROUTE = "groups"
    const val SCHEDULE_ROUTE = "schedule"
    const val SCHEDULE_GROUP_ARGUMENT = "groupId"
}

class RUTScheduleNavigationActions(navController: NavHostController) {
    val navigateToGroups: () -> Unit = {
        navController.navigate(RUTScheduleDestinations.GROUPS_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToSchedule: (groupId: Int) -> Unit = {
        navController.navigate("${RUTScheduleDestinations.SCHEDULE_ROUTE}/${it}") {
            launchSingleTop = true
        }
    }
}