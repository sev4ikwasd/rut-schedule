package com.sev4ikwasd.rutschedule.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ErrorScreen(
    errorMessage: String,
    onRefresh: () -> Unit,
    showNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(topBar = { ErrorTopAppBar(showNavigateBack, onNavigateBack) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding()
                )
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onRefresh) {
                    Text(text = "Перезагрузить")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorTopAppBar(
    showNavigateBack: Boolean,
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(title = {}, navigationIcon = {
        if (showNavigateBack) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, "Вернуться назад")
            }
        }
    })
}

@Preview
@Composable
fun PreviewErrorScreen() {
    ErrorScreen(errorMessage = "Сообщение об ошибке", onRefresh = {}, true, onNavigateBack = {})
}