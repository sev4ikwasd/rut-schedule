package com.sev4ikwasd.rutschedule.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.repository.ScheduleRepository
import com.sev4ikwasd.rutschedule.model.Group
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface GroupsUiState {
    object Loading : GroupsUiState
    data class Loaded(val groups: List<Group>, val filteredGroups: List<Group>) : GroupsUiState
    object Error : GroupsUiState
}

class GroupsViewModel(scheduleRepository: ScheduleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val uiState: StateFlow<GroupsUiState> = _uiState

    init {
        viewModelScope.launch {
            when (val result = scheduleRepository.getAllGroups()) {
                is Result.Success -> _uiState.value =
                    GroupsUiState.Loaded(result.data, result.data)
                is Result.Error -> _uiState.value = GroupsUiState.Error
            }
        }
    }

    fun filterGroups(query: String) {
        viewModelScope.launch {
            if (_uiState.value is GroupsUiState.Loaded) {
                val loadedUiState = (_uiState.value as GroupsUiState.Loaded)
                _uiState.value = loadedUiState.copy(filteredGroups = loadedUiState.groups.filter {
                    it.groupName.trim().lowercase().contains(query.lowercase())
                })
            }
        }
    }

    companion object {
        fun provideFactory(
            scheduleRepository: ScheduleRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GroupsViewModel(scheduleRepository) as T
            }
        }
    }
}