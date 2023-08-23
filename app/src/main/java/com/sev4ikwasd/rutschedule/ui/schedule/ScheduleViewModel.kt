package com.sev4ikwasd.rutschedule.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.repository.ScheduleRepository
import com.sev4ikwasd.rutschedule.model.GroupSchedules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ScheduleUiState {
    data object Loading : ScheduleUiState
    data class Loaded(
        val isRefreshing: Boolean,
        val isRefreshHidden: Boolean,
        val isCacheUsed: Boolean,
        val groupSchedules: GroupSchedules
    ) :
        ScheduleUiState

    data object Error : ScheduleUiState
}

class ScheduleViewModel(
    private val groupId: Int,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            when (val schedules = scheduleRepository.loadSchedules(groupId)) {
                is Result.Success -> _uiState.value = ScheduleUiState.Loaded(
                    isRefreshing = true,
                    isRefreshHidden = true,
                    isCacheUsed = false,
                    groupSchedules = schedules.data
                )

                is Result.Error -> _uiState.value = ScheduleUiState.Loading
            }
            when (val schedules = scheduleRepository.updateSchedules(groupId)) {
                is Result.Success -> _uiState.value = ScheduleUiState.Loaded(
                    isRefreshing = false,
                    isRefreshHidden = false,
                    isCacheUsed = false,
                    groupSchedules = schedules.data
                )

                is Result.Error -> if (_uiState.value is ScheduleUiState.Loaded) _uiState.value =
                    ScheduleUiState.Loaded(
                        isRefreshing = false,
                        isRefreshHidden = false,
                        isCacheUsed = true,
                        groupSchedules = (_uiState.value as ScheduleUiState.Loaded).groupSchedules
                    )
                else {
                    _uiState.value = ScheduleUiState.Error
                }
            }
        }
    }

    fun updateSchedule() {
        if (_uiState.value is ScheduleUiState.Loaded) {
            _uiState.update {
                (it as ScheduleUiState.Loaded).copy(
                    isRefreshHidden = false
                )
            }
            if (!(_uiState.value as ScheduleUiState.Loaded).isRefreshing) {
                viewModelScope.launch {
                    when (val schedules = scheduleRepository.updateSchedules(groupId)) {
                        is Result.Success -> _uiState.value = ScheduleUiState.Loaded(
                            isRefreshing = false,
                            isRefreshHidden = false,
                            isCacheUsed = false,
                            groupSchedules = schedules.data
                        )

                        is Result.Error -> if (_uiState.value is ScheduleUiState.Loaded) _uiState.value =
                            ScheduleUiState.Loaded(
                                isRefreshing = false,
                                isRefreshHidden = false,
                                isCacheUsed = true,
                                groupSchedules = (_uiState.value as ScheduleUiState.Loaded).groupSchedules
                            )
                    }
                }
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