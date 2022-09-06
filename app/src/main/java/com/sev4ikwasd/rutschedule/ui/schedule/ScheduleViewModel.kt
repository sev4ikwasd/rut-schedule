package com.sev4ikwasd.rutschedule.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.ScheduleRepository
import com.sev4ikwasd.rutschedule.model.Schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class ScheduleUiState {
    object Loading : ScheduleUiState()
    data class Loaded(val schedule: Schedule, val currentDate: LocalDate) : ScheduleUiState()
    object Error : ScheduleUiState()
}

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState

    init {
        viewModelScope.launch {
            when (val result = scheduleRepository.getSchedule(194657)) {
                is Result.Success -> _uiState.value =
                    ScheduleUiState.Loaded(schedule = result.data, LocalDate.now())
                is Result.Error -> _uiState.value = ScheduleUiState.Error
            }
        }
    }

    fun updateCurrentDate(date: LocalDate) {
        viewModelScope.launch {
            if (_uiState.value is ScheduleUiState.Loaded) {
                _uiState.update { (it as ScheduleUiState.Loaded).copy(currentDate = date) }
            }
        }
    }


    companion object {
        fun provideFactory(
            scheduleRepository: ScheduleRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScheduleViewModel(scheduleRepository) as T
            }
        }
    }
}