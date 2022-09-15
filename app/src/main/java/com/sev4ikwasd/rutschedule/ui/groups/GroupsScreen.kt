package com.sev4ikwasd.rutschedule.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sev4ikwasd.rutschedule.model.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(groupsViewModel: GroupsViewModel, onNavigateToSchedule: (Int) -> Unit) {
    when (val state = groupsViewModel.uiState.collectAsState().value) {
        is GroupsUiState.Loading -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
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
                    GroupsWithSearch(
                        groups = state.filteredGroups,
                        onQueryChanged = { groupsViewModel.filterGroups(it) },
                        onNavigateToSchedule = { onNavigateToSchedule(it) })
                }
            }
        }
        is GroupsUiState.Error -> Box(modifier = Modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsTopAppBar() {
    CenterAlignedTopAppBar(title = {
        Text(text = "Выбор группы")
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsWithSearch(
    groups: List<Group>,
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
        GroupList(groups = groups, navigateToSchedule = { onNavigateToSchedule(it) })
    }
}

@Composable
fun GroupList(groups: List<Group>, navigateToSchedule: (scheduleId: Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(groups) { group ->
            TextButton(onClick = { navigateToSchedule(group.groupId) }) {
                Text(text = group.groupName)
            }
        }
    }
}

@Preview
@Composable
fun PreviewGroupsWithSearch() {
    GroupsWithSearch(
        groups = listOf(
            Group("УИС-111", 0),
            Group("УИС-112", 1),
            Group("УИС-113", 2),
            Group("УИС-211", 3),
            Group("УИС-212", 4),
            Group("УИС-213", 5),
            Group("УИС-311", 6),
            Group("УИС-312", 7),
            Group("УИС-313", 8),
        ), {}, {}
    )
}