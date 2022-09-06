package com.sev4ikwasd.rutschedule.ui

import androidx.compose.runtime.Composable
import com.sev4ikwasd.rutschedule.data.AppContainer
import com.sev4ikwasd.rutschedule.ui.theme.RUTScheduleTheme

@Composable
fun RUTScheduleApp(appContainer: AppContainer) {
    RUTScheduleTheme {
        RUTScheduleNavGraph(appContainer = appContainer)
    }
}