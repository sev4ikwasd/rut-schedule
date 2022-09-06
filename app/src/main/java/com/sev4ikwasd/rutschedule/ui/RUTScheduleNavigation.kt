package com.sev4ikwasd.rutschedule.ui

import androidx.navigation.NavHostController

object RUTScheduleDestinations {
    const val SCHEDULE_ROUTE = "schedule"
}

class RUTScheduleNavigationActions(navController: NavHostController) {
    val navigateToSchedule: () -> Unit = {
        navController.navigate(RUTScheduleDestinations.SCHEDULE_ROUTE) {

        }
    }
}