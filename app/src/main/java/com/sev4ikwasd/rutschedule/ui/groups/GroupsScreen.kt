package com.sev4ikwasd.rutschedule.ui.groups

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sev4ikwasd.rutschedule.model.Course
import com.sev4ikwasd.rutschedule.model.Group
import com.sev4ikwasd.rutschedule.model.Institute
import com.sev4ikwasd.rutschedule.model.Specialty
import com.sev4ikwasd.rutschedule.ui.composable.ErrorScreen
import com.sev4ikwasd.rutschedule.ui.composable.LoadingScreen

@Composable
fun GroupsScreen(groupsViewModel: GroupsViewModel, onNavigateToSchedule: (Int) -> Unit) {
    when (val state = groupsViewModel.uiState.collectAsState().value) {
        is GroupsUiState.Loading -> LoadingScreen()
        is GroupsUiState.Loaded -> {
            Scaffold(topBar = {
                GroupsTopAppBar()
            }) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        )
                ) {
                    InstitutesWithSearch(
                        institutes = state.filteredInstitutes,
                        onQueryChanged = { groupsViewModel.filter(it) },
                        onNavigateToSchedule = { onNavigateToSchedule(it) })
                }
            }
        }

        is GroupsUiState.Error -> ErrorScreen(
            errorMessage = "Произошла ошибка при загрузке групп",
            onRefresh = { groupsViewModel.updateGroups() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsTopAppBar() {
    CenterAlignedTopAppBar(title = {
        Text(text = "Выбор группы")
    })
}

@Composable
fun InstitutesWithSearch(
    institutes: List<Institute>,
    onQueryChanged: (String) -> Unit,
    onNavigateToSchedule: (Int) -> Unit
) {
    Column {
        val query = remember { mutableStateOf("") }
        OutlinedTextField(
            value = query.value, onValueChange = {
                query.value = it
                onQueryChanged(it)
            }, modifier = Modifier
                .padding(8.dp, 4.dp)
                .fillMaxWidth(),
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Выбор группы",
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Search)
        )
        InstituteList(institutes = institutes, navigateToSchedule = { onNavigateToSchedule(it) })
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun InstituteList(institutes: List<Institute>, navigateToSchedule: (scheduleId: Int) -> Unit) {
    LazyColumn(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        institutes.forEach { institute ->
            stickyHeader {
                Surface(Modifier.fillParentMaxWidth()) {
                    Text(
                        text = "${institute.name} (${institute.abbreviation})",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            items(institute.courses) { course ->
                Text(
                    text = "${course.course} курс",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillParentMaxWidth()
                ) {
                    course.specialties.forEach { specialty ->
                        Text(
                            text = "${specialty.name} (${specialty.abbreviation})",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Column(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .fillParentMaxWidth()
                        ) {
                            FlowRow(
                                horizontalArrangement = Arrangement.Start
                            ) {
                                repeat(specialty.groups.count()) { groupIndex ->
                                    val group = specialty.groups[groupIndex]
                                    TextButton(onClick = { navigateToSchedule(group.id) }) {
                                        Text(text = group.name)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewGroupsWithSearch() {
    InstitutesWithSearch(
        institutes =
        listOf(
            Institute(
                "Академия \"Высшая инженерная школа\"", "АВИШ", listOf(
                    Course(
                        1, listOf(
                            Specialty(
                                "Инфокоммуникационные технологии и системы связи",
                                "ШМС",
                                listOf(
                                    Group(185411, "ШМС-111")
                                )
                            ),
                            Specialty(
                                "Информатика и вычислительная техника",
                                "ШАД",
                                listOf(
                                    Group(185407, "ШАД-111"),
                                    Group(185407, "ШАД-111"),
                                    Group(185407, "ШАД-111"),
                                    Group(185407, "ШАД-111"),
                                    Group(185407, "ШАД-111"),
                                    Group(185407, "ШАД-111"),
                                    Group(186322, "ШАД-112")
                                )
                            )
                        )
                    ),
                    Course(
                        1, listOf(
                            Specialty(
                                "Инфокоммуникационные технологии и системы связи",
                                "ШМС",
                                listOf(
                                    Group(185411, "ШМС-111")
                                )
                            ),
                            Specialty(
                                "Информатика и вычислительная техника",
                                "ШАД",
                                listOf(
                                    Group(185407, "ШАД-111"),
                                    Group(186322, "ШАД-112")
                                )
                            )
                        )
                    )
                )
            ),
            Institute(
                "Академия \"Высшая инженерная школа\"", "АВИШ", listOf(
                    Course(
                        1, listOf(
                            Specialty(
                                "Инфокоммуникационные технологии и системы связи",
                                "ШМС",
                                listOf(
                                    Group(185411, "ШМС-111")
                                )
                            ),
                            Specialty(
                                "Информатика и вычислительная техника",
                                "ШАД",
                                listOf(
                                    Group(185407, "ШАД-111"),
                                    Group(186322, "ШАД-112")
                                )
                            )
                        )
                    ),
                    Course(
                        1, listOf(
                            Specialty(
                                "Инфокоммуникационные технологии и системы связи",
                                "ШМС",
                                listOf(
                                    Group(185411, "ШМС-111")
                                )
                            ),
                            Specialty(
                                "Информатика и вычислительная техника",
                                "ШАД",
                                listOf(
                                    Group(185407, "ШАД-111"),
                                    Group(186322, "ШАД-112")
                                )
                            )
                        )
                    )
                )
            )
        ),
        {}, {}
    )
}