package com.sev4ikwasd.rutschedule

import android.app.Application
import com.sev4ikwasd.rutschedule.data.AppContainer
import com.sev4ikwasd.rutschedule.data.AppContainerImpl

class RUTScheduleApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}