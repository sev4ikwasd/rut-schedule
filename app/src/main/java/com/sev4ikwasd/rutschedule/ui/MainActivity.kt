package com.sev4ikwasd.rutschedule.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sev4ikwasd.rutschedule.RUTScheduleApplication
import com.sev4ikwasd.rutschedule.ui.theme.RUTScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as RUTScheduleApplication).container
        setContent {
            RUTScheduleApp(appContainer)
        }
    }
}