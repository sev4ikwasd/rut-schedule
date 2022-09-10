package com.sev4ikwasd.rutschedule.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sev4ikwasd.rutschedule.data.AppContainer
import com.sev4ikwasd.rutschedule.ui.schedule.ScheduleRoute
import com.sev4ikwasd.rutschedule.ui.schedule.ScheduleViewModel

@Composable
fun RUTScheduleNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    startDestination: String = RUTScheduleDestinations.SCHEDULE_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(RUTScheduleDestinations.SCHEDULE_ROUTE) {
            val scheduleViewModel: ScheduleViewModel = viewModel(
                factory = ScheduleViewModel.provideFactory(appContainer.scheduleRepository)
            )
            ScheduleRoute(
                scheduleViewModel = scheduleViewModel
            )
        }
    }
}