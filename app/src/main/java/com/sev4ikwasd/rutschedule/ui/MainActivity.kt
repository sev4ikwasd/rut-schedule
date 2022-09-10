package com.sev4ikwasd.rutschedule.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sev4ikwasd.rutschedule.RUTScheduleApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as RUTScheduleApplication).container
        setContent {
            RUTScheduleApp(appContainer)
        }
    }
}