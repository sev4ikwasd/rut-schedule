package com.sev4ikwasd.rutschedule.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sev4ikwasd.rutschedule.data.Result
import com.sev4ikwasd.rutschedule.data.repository.ScheduleRepository
import com.sev4ikwasd.rutschedule.model.Course
import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Institute
import com.sev4ikwasd.rutschedule.model.Specialty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface GroupsUiState {
    data object Loading : GroupsUiState
    data class Loaded(val institutes: List<Institute>, val filteredInstitutes: List<Institute>) :
        GroupsUiState

    data object Error : GroupsUiState
}

class GroupsViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val uiState: StateFlow<GroupsUiState> = _uiState

    init {
        updateGroups()
    }

    fun updateGroups() {
        viewModelScope.launch {
            when (val result = scheduleRepository.getAllInstitutes()) {
                is Result.Success -> _uiState.value =
                    GroupsUiState.Loaded(result.data, result.data)

                is Result.Error -> _uiState.value = GroupsUiState.Error
            }
        }
    }

    fun filter(query: String) {
        viewModelScope.launch {
            if (_uiState.value is GroupsUiState.Loaded) {
                val loadedUiState = (_uiState.value as GroupsUiState.Loaded)
                val filteredInstituteList = mutableListOf<Institute>()
                for (institute in loadedUiState.institutes) {
                    val filteredCourseList = mutableListOf<Course>()
                    for (course in institute.courses) {
                        val filteredSpecialtyList = mutableListOf<Specialty>()
                        for (specialty in course.specialties) {
                            val filteredGroupList = mutableListOf<Group>()
                            for (group in specialty.groups) {
                                if (compareNames(group.name, query)) {
                                    filteredGroupList.add(group)
                                }
                            }
                            if (compareNames(
                                    specialty.name,
                                    query
                                ) || compareNames(specialty.abbreviation, query)
                            ) {
                                filteredSpecialtyList.add(specialty)
                            } else if (filteredGroupList.isNotEmpty()) {
                                filteredSpecialtyList.add(specialty.copy(groups = filteredGroupList))
                            }
                        }
                        if (compareNames(course.course.toString(), query)) {
                            filteredCourseList.add(course)
                        } else if (filteredSpecialtyList.isNotEmpty()) {
                            filteredCourseList.add(course.copy(specialties = filteredSpecialtyList))
                        }
                    }
                    if (compareNames(institute.name, query) || compareNames(
                            institute.abbreviation,
                            query
                        )
                    ) {
                        filteredInstituteList.add(institute)
                    } else if (filteredCourseList.isNotEmpty()) {
                        filteredInstituteList.add(institute.copy(courses = filteredCourseList))
                    }
                }
                _uiState.value = loadedUiState.copy(filteredInstitutes = filteredInstituteList)
            }
        }
    }

    private fun compareNames(firstName: String, secondName: String): Boolean {
        return firstName.trim().lowercase().contains(secondName.trim().lowercase())
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