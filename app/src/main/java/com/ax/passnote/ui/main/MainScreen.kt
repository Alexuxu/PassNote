package com.ax.passnote.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ax.passnote.PassNoteApplication
import com.ax.passnote.ui.theme.md_theme_dark_primary

@Composable
fun GradientTitle() {
    val gradientColors = listOf(MaterialTheme.colorScheme.primary, md_theme_dark_primary)
    Text(
        text = "PassNote",
        style = TextStyle(
            brush = Brush.linearGradient(
                colors = gradientColors
            ),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    application: PassNoteApplication,
    onAddPassword: () -> Unit,
    onPasswordClick: (Int) -> Unit,
    onMoreClick: () -> Unit // New callback for more options
) {
    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(application.passwordRepository))
    val passwords by viewModel.passwords.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            PassNoteTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onClearSearch = { viewModel.clearSearch() },
                onMoreClick = onMoreClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPassword) {
                Icon(Icons.Filled.Add, contentDescription = "添加新密码")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(passwords) { password ->
                PasswordListItem(
                    password = password,
                    onClick = { onPasswordClick(password.id) } 
                )
            }
        }
    }
}
