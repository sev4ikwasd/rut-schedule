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

sealed interface ScheduleUiState {
    object Loading : ScheduleUiState
    data class Loaded(val isRefreshing: Boolean, val schedule: Schedule) : ScheduleUiState
    object Error : ScheduleUiState
}

class ScheduleViewModel(
    private val groupId: Int,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState

    init {
        updateSchedule()
    }

    fun updateSchedule() {
        viewModelScope.launch {
            if (_uiState.value is ScheduleUiState.Loaded) {
                _uiState.update { (it as ScheduleUiState.Loaded).copy(isRefreshing = true) }
            }
            when (val result = scheduleRepository.getSchedule(groupId)) {
                is Result.Success -> _uiState.value =
                    ScheduleUiState.Loaded(false, result.data)
                is Result.Error -> _uiState.value = ScheduleUiState.Error
            }
        }

    }

    companion object {
        fun provideFactory(
            groupId: Int,
            scheduleRepository: ScheduleRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScheduleViewModel(groupId, scheduleRepository) as T
            }
        }
    }
}