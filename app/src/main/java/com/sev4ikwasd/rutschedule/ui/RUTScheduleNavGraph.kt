package com.sev4ikwasd.rutschedule.ui

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sev4ikwasd.rutschedule.data.AppContainer
import com.sev4ikwasd.rutschedule.ui.groups.GroupsRoute
import com.sev4ikwasd.rutschedule.ui.groups.GroupsViewModel
import com.sev4ikwasd.rutschedule.ui.schedule.ScheduleRoute
import com.sev4ikwasd.rutschedule.ui.schedule.ScheduleViewModel

@Composable
fun RUTScheduleNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
) {
    val navActions = remember(navController) {
        RUTScheduleNavigationActions(navController)
    }
    val sharedPreferences =
        (LocalContext.current as Activity).getSharedPreferences(
            "groupPreference",
            Context.MODE_PRIVATE
        )
    var groupId = sharedPreferences.getInt("groupId", 0)
    val startDestination: String = if (groupId == 0) {
        RUTScheduleDestinations.GROUPS_ROUTE
    } else {
        "${RUTScheduleDestinations.SCHEDULE_ROUTE}/{${RUTScheduleDestinations.SCHEDULE_GROUP_ARGUMENT}}"
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            "${RUTScheduleDestinations.SCHEDULE_ROUTE}/{${RUTScheduleDestinations.SCHEDULE_GROUP_ARGUMENT}}",
            arguments = listOf(navArgument(RUTScheduleDestinations.SCHEDULE_GROUP_ARGUMENT) {
                type = NavType.IntType; defaultValue = groupId
            })
        ) { backStackEntry ->
            val scheduleViewModel: ScheduleViewModel = viewModel(
                factory = ScheduleViewModel.provideFactory(
                    backStackEntry.arguments?.getInt(RUTScheduleDestinations.SCHEDULE_GROUP_ARGUMENT)!!,
                    appContainer.scheduleRepository
                )
            )
            ScheduleRoute(
                scheduleViewModel = scheduleViewModel,
                onNavigateToGroups = {
                    groupId = 0
                    with(sharedPreferences.edit()) {
                        putInt("groupId", 0)
                        apply()
                    }
                    navActions.navigateToGroups()
                }
            )
        }
        composable(RUTScheduleDestinations.GROUPS_ROUTE) {
            val groupsViewModel: GroupsViewModel = viewModel(
                factory = GroupsViewModel.provideFactory(appContainer.scheduleRepository)
            )
            GroupsRoute(
                groupsViewModel = groupsViewModel,
                onNavigateToSchedule = {
                    groupId = it
                    with(sharedPreferences.edit()) {
                        putInt("groupId", it)
                        apply()
                    }
                    navActions.navigateToSchedule(it)
                }
            )
        }
    }
}